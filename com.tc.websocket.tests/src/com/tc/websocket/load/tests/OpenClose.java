/*
 * © Copyright Tek Counsel LLC 2016
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

package com.tc.websocket.load.tests;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.tc.websocket.tests.client.NettyTestClient;
import com.tc.websocket.tests.config.NettyClientFactory;
import com.tc.websocket.tests.config.TestConfig;

public class OpenClose implements Runnable {

	@SuppressWarnings("unused")
	private static OpenClose loader;

	protected static final TestConfig cfg = TestConfig.getInstance();
	protected List<NettyTestClient> clients = new ArrayList<NettyTestClient>();
	private static Scanner scanner;
	private static ScheduledExecutorService scheduled = Executors.newScheduledThreadPool(4);

	public static void main(String[] args) throws InterruptedException {
		cfg.overrideProperty("number.of.clients", "500");
		cfg.overrideProperty("print.on.count", "1000");
	
		List<OpenClose> list = new ArrayList<OpenClose>();
		for(int i=0;i <= 4;i++){
			OpenClose openClose = new OpenClose();
			scheduled.scheduleAtFixedRate(openClose, 0, 1, TimeUnit.SECONDS);
			list.add(openClose);
		}

		scanner = new Scanner(System.in);
		while (scanner.hasNext()) {
			String cmd = scanner.next();
			if (cmd.equals("runningcount")) {
				System.out.println("Running count is : " + NettyTestClient.counter.get());
			} else if (cmd.equals("stop")) {

				System.exit(0);
			} else if (cmd.equals("resetcounter")) {
				NettyTestClient.resetCounter();
			} else if ("gc".equals(cmd)) {
				System.gc();
			}
		}

	}

	public OpenClose() {
		
		

	}



	public List<NettyTestClient> getClients() {
		return clients;
	}

	@Override
	public void run() {
		try {

			NettyClientFactory factory = new NettyClientFactory();
			List<NettyTestClient> list = factory.buildNewClients(TestConfig.getInstance().getMaxPayload(), false);

			System.out.println("closing clients " + list.size());
		     factory.closeClients(list, 100);
		     
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
