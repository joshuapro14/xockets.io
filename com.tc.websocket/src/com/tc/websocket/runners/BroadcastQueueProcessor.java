/*
 * � Copyright Tek Counsel LLC 2016
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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.tc.guice.domino.module.ServerInfo;
import com.tc.utils.StringCache;
import com.tc.websocket.Config;
import com.tc.websocket.Const;
import com.tc.xpage.profiler.Stopwatch;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.DocumentCollection;
import lotus.domino.NotesException;
import lotus.domino.Session;
import lotus.domino.View;


// TODO: Auto-generated Javadoc
/**
 * The Class BroadcastQueueProcessor.
 */
public class BroadcastQueueProcessor extends AbstractQueueProcessor implements Runnable {
	
	/** The Constant LOG. */
	private static final Logger LOG = Logger.getLogger(BroadcastQueueProcessor.class.getName());
	
	/** The cluster mates. */
	private List<String> clusterMates = new ArrayList<String>();


	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	@Stopwatch
	public void run() {

		if(TaskRunner.getInstance().isClosing()){
			return;
		}
		
		//exit if nobody on.
		if(server.getWebSocketCount() == 0) return;

		Session session = super.openSession();
		try {

			if(ServerInfo.getInstance().isCurrentServer(Config.getInstance().getBroadcastServer())){
				Database db = session.getDatabase(StringCache.EMPTY, Const.WEBSOCKET_PATH);
				View view = db.getView(Const.VIEW_BROADCAST_QUEUE);
				view.setAutoUpdate(false);
				Document doc = view.getFirstDocument();
				Document temp = null;
				while(doc!=null){
					if(doc.isValid() && !doc.hasItem(StringCache.FIELD_CONFLICT)){
						this.buildDirectMessages(doc);
						doc.replaceItemValue(Const.FIELD_SENTFLAG, Const.FIELD_SENTFLAG_VALUE_SENT);
						doc.save();
					}
					temp = view.getNextDocument(doc);
					doc.recycle();
					doc = temp;
				}
				
				
				view.setAutoUpdate(true);
			}
		} catch (NotesException e) {
			LOG.log(Level.SEVERE,null,e);

		}finally{
			super.closeSession(session);
		}
	}


	/**
	 * Gets the cluster mates.
	 *
	 * @param s the s
	 * @return the cluster mates
	 * @throws NotesException the notes exception
	 */
	private List<String> getClusterMates(Session s) throws NotesException{
		if(clusterMates.isEmpty()){
			synchronized(clusterMates){
				if(clusterMates.isEmpty()){

					if(!Config.getInstance().isClustered()){
						clusterMates.add(ServerInfo.getInstance().getServerName());
						return clusterMates;
					}

					System.out.println("loading clustermates");
					/*
					 * all below should get recycled after session is recycled.
					 */
					final Database db = s.getDatabase(StringCache.EMPTY, StringCache.NAMES_DOT_NSF);
					final View view = db.getView(Const.VIEW_SERVERS);
					final Document docServer = view.getDocumentByKey(ServerInfo.getInstance().getServerName(), true);
					final String clusterName = docServer.getItemValueString(Const.FIELD_CLUSTERNAME);
					final DocumentCollection col = db.getView(Const.VIEW_CLUSTERS).getAllDocumentsByKey(clusterName,true);
					Document doc = col.getFirstDocument();
					while(doc!=null){
						clusterMates.add(doc.getItemValueString(Const.FIELD_SERVERNAME));
						doc = col.getNextDocument(doc);
					}
				}
			}
		}
		return clusterMates;
	}



	/**
	 * Builds the direct messages.
	 *
	 * @param doc the doc
	 */
	@Stopwatch
	private void buildDirectMessages(Document doc) {
		Document directMessage=null;
		try{
			for(String server : this.getClusterMates(doc.getParentDatabase().getParent())){
				directMessage = doc.copyToDatabase(doc.getParentDatabase());
				directMessage.replaceItemValue(Const.FIELD_TO, server);
				directMessage.save();
				directMessage.recycle();
			}
		}catch(Exception e){
			LOG.log(Level.SEVERE,null, e);

			try {
				doc.replaceItemValue(Const.FIELD_ERROR, e.getLocalizedMessage());
				doc.replaceItemValue(Const.FIELD_SENTFLAG, Const.FIELD_SENTFLAG_VALUE_ERROR);
				doc.save();

				directMessage.replaceItemValue(Const.FIELD_ERROR, e.getLocalizedMessage());
				directMessage.replaceItemValue(Const.FIELD_SENTFLAG, Const.FIELD_SENTFLAG_VALUE_ERROR);
				directMessage.save();

			} catch (NotesException e1) {
				LOG.log(Level.SEVERE,null, e);
			}


		}


	}



}
