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

package com.tc.websocket.services.lib;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.tc.di.guicer.Guicer;
import com.tc.di.guicer.IGuicer;
import com.tc.websocket.Activator;
import com.tc.websocket.factories.ISocketMessageFactory;
import com.tc.websocket.factories.IUserFactory;
import com.tc.websocket.factories.SocketMessageFactory;
import com.tc.websocket.factories.UserFactory;
import com.tc.websocket.guice.DominoWebSocketModule;
import com.tc.websocket.server.IDominoWebSocketServer;
import com.tc.websocket.services.rest.IRestWebSocket;
import com.tc.websocket.services.rest.RestWebSocket;
import com.tc.websocket.services.rest.RestWebSocketBean;



// TODO: Auto-generated Javadoc
/**
 * The Class DominoWebSocketModule.
 */
public class ServicesModule extends AbstractModule {

	/** The Constant LOG. */
	private static final Logger LOG = Logger.getLogger(ServicesModule.class.getName());




	/* (non-Javadoc)
	 * @see com.google.inject.AbstractModule#configure()
	 */
	@Override
	protected void configure() {

		try{

			bind(IUserFactory.class).to(UserFactory.class).in(Singleton.class);
			bind(ISocketMessageFactory.class).to(SocketMessageFactory.class).in(Singleton.class);
			
			//bind the rest bean (not singleton... per request);
			bind(RestWebSocketBean.class);

			//setup the restful bindings
			bind(IRestWebSocket.class).to(RestWebSocket.class);
			


		}catch(Exception e){
			LOG.log(Level.SEVERE,null,e);
		}

	}
	
	@Provides
	public IGuicer provideGuicer(){
		return Guicer.getInstance(Activator.bundle);
	}
	
	@Provides
	public IDominoWebSocketServer provideServer(){
		return DominoWebSocketModule.serverInstance();
	}



	

}
