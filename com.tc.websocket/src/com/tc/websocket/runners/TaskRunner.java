/*
 * Copyright Tek Counsel LLC 2016
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at:
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied. See the License for the specific language governing 
 * permissions and limitations under the License.
 */

package com.tc.websocket.runners;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.Inject;
import com.tc.di.guicer.Guicer;
import com.tc.di.guicer.IGuicer;
import com.tc.guice.domino.module.ServerInfo;
import com.tc.websocket.Activator;
import com.tc.websocket.Config;
import com.tc.websocket.Const;
import com.tc.websocket.scripts.Script;
import com.tc.websocket.server.IDominoWebSocketServer;



// TODO: Auto-generated Javadoc
/**
 * The Class TaskRunner.
 */
public class TaskRunner implements Runnable {

	/** The runner. */
	//singleton instance of this class
	private static TaskRunner RUNNER = new TaskRunner();

	/** The Constant LOG. */
	//LOG... thank you captain obvious :)
	private static final Logger LOG = Logger.getLogger(TaskRunner.class.getName());

	/** The run queue. */
	//in memory data structure for queued up runnables.
	private final Queue<IFutureRunnable> RUN_QUEUE = new ConcurrentLinkedQueue<IFutureRunnable>();
	
	/** The scheduler. */
	//service to process state changes for users, and other future tasks.
	private ScheduledExecutorService scheduler;

	/** The closing. */
	private AtomicBoolean closing = new AtomicBoolean(false);	


	/** The guicer. */
	@Inject
	IGuicer guicer;
	
	/** The server. */
	@Inject
	IDominoWebSocketServer server;


	/**
	 * Gets the single instance of TaskRunner.
	 *
	 * @return single instance of TaskRunner
	 */
	public static TaskRunner getInstance(){
		return RUNNER;
	}

	/**
	 * Instantiates a new task runner.
	 */
	private TaskRunner(){
			
	}


	/**
	 * Adds the.
	 *
	 * @param runMe the run me
	 */
	public void add(Runnable runMe){
		guicer.inject(runMe);//make sure all dependencies are there.
		RUN_QUEUE.add(new FutureRunnable(runMe));//queue it up to run as a future runnable with zero seconds.
	}
	

	/**
	 * Adds the.
	 *
	 * @param runMe the run me
	 * @param seconds the seconds
	 */
	//run sometime in the future.
	public void add(Runnable runMe, int seconds){
		guicer.inject(runMe);//make sure all dependencies are there.
		IFutureRunnable future = new FutureRunnable(runMe); //decorate runnable with a futurerunnable
		future.setSeconds(seconds);//set the time it should run
		this.add(future);
	}



	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		while (!RUN_QUEUE.isEmpty()) {
			IFutureRunnable future = RUN_QUEUE.poll();
			scheduler.schedule(future, future.getSeconds(), TimeUnit.SECONDS);
		}
		
		//make sure the scheduled scripts fire.
		Batch batch = new Batch();
		for(Script script : server.getIntervaled()){
			if(script.shouldRun()){
				batch.addRunner(script);
			}
		}
		
		if(!batch.isEmpty()){
			this.add(batch);
		}
	}


	/**
	 * Start.
	 */
	public void start(){

		try{
			this.closing.set(false);//make sure we reset to false.
			
			scheduler = Executors.newScheduledThreadPool(Config.getInstance().getThreadCount());
			
			Guicer.getInstance(Activator.bundle).inject(this);//inject the dependencies
			
			scheduler.scheduleAtFixedRate(this, 5, 100, TimeUnit.MILLISECONDS); 
			

			if(Config.getInstance().isClustered()){
				//monitor users coming in from a different server and add them to the local in memory ConcurrentHashMap
				UserMonitor userMonitor = guicer.createObject(UserMonitor.class);
				scheduler.scheduleAtFixedRate(userMonitor, 0, Const.USER_MONITOR_INTERVAL, TimeUnit.SECONDS);


				//monitor if the other servers in the cluster are up.
				ClustermateMonitor clusterMate = guicer.createObject(ClustermateMonitor.class);
				scheduler.scheduleAtFixedRate(clusterMate, 0, Const.CLUSTERMATE_MONITOR_INTERVAL, TimeUnit.SECONDS);


			}
			
			
			UserQueueProcessor userQs = guicer.createObject(UserQueueProcessor.class);
			scheduler.scheduleAtFixedRate(userQs, 0, 10, TimeUnit.SECONDS);
			

			// setup the queueprocessor to process standard messages.
			QueueProcessor qp = guicer.createObject(QueueProcessor.class);
			scheduler.scheduleAtFixedRate(qp, 0, Const.QUEUE_PROCESSOR_INTERVAL, TimeUnit.MILLISECONDS);
			
			
			//setup the user cleanup to remove abruptly disconnected users
			UserCleanup cleanup= guicer.createObject(UserCleanup.class);
			scheduler.scheduleAtFixedRate(cleanup, 0, Const.USER_CLEANUP_INTERVAL, TimeUnit.SECONDS);
			


			//separate thread to process broadcast messages.
			if(ServerInfo.getInstance().isCurrentServer(Config.getInstance().getBroadcastServer())){
				BroadcastQueueProcessor broadcastProcessor = guicer.createObject(BroadcastQueueProcessor.class);
				scheduler.scheduleAtFixedRate(broadcastProcessor, 0, Const.BROADCAST_QUEUE_PROCESSOR_INTERVAL, TimeUnit.MILLISECONDS);
			}
			
			//last thing, setup the ping operation to run every 30 seconds, to keep the connections alive.
			Ping ping = guicer.createObject(Ping.class);
			scheduler.scheduleAtFixedRate(ping, 0, Config.getInstance().getPingInterval(), TimeUnit.SECONDS);
			
			
			//setup cleanup for the broadcast server
			if(Config.getInstance().isBroadcastServer()){
				PurgeDocuments purge = new PurgeDocuments();
				scheduler.scheduleAtFixedRate(purge, 0, Config.getInstance().getPurgeInterval(), TimeUnit.SECONDS);
				
				FlagForDeletion flag = new FlagForDeletion();
				scheduler.scheduleAtFixedRate(flag, 0, Config.getInstance().getPurgeInterval(), TimeUnit.SECONDS);
			}
			
			
		}catch(Exception e){
			LOG.log(Level.SEVERE, null, e);
		}
	}


	/**
	 * Stop.
	 */
	public void stop(){
		closing.set(true);		
		try {
			scheduler.shutdown();
			if(!scheduler.awaitTermination(30, TimeUnit.SECONDS)){
				scheduler.shutdownNow();
			}
			
			LOG.log(Level.INFO,"TaskRunner scheduler shutdown");
			
		} catch (InterruptedException e) {
			LOG.log(Level.SEVERE,null,e);
		}

		RUN_QUEUE.clear();
		scheduler=null;
		System.runFinalization();
		System.gc();

	}

	/**
	 * Checks if is closing.
	 *
	 * @return true, if is closing
	 */
	public boolean isClosing(){
		return closing.get();
	}

}
