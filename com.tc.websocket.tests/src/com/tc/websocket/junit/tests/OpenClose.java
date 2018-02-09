package com.tc.websocket.junit.tests;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.tc.websocket.tests.client.NettyTestClient;
import com.tc.websocket.tests.config.NettyClientFactory;
import com.tc.websocket.tests.config.TestConfig;

public class OpenClose {
	
	
	private TestConfig cfg = TestConfig.getInstance();
	private List<NettyTestClient> clients = new ArrayList<NettyTestClient>();
	
	
	
	@Before
	public void setUp() throws Exception {
		
		//cfg.overrideProperty("print.on.count", "10000"); //we don't want it to print
		cfg.overrideProperty("number.of.clients", "10");
		//cfg.overrideProperty("compression.enabled", "false");
		//cfg.overrideProperty("message.delay", "100");
		//cfg.overrideProperty("print.data", "true");//we want to print data.
		

		
	}

	@After
	public void tearDown() throws Exception {
		
	}

	@Test
	public void testOpenClose() throws InterruptedException, JsonParseException, JsonMappingException, IOException, URISyntaxException {
		NettyClientFactory factory = new NettyClientFactory();
		for(int i=0;i < 1000;i++){
			
			this.clients.addAll(factory.buildClients(TestConfig.getInstance().getMaxPayload(), false));
			
			for(NettyTestClient client : this.clients){
				if(client.isOpen()){
				client.disconnect();
				}
			}
			
			this.clients.clear();
			Thread.sleep(2000);
		}
		
			
	}
	
	public void print(Object o){
		System.out.println(o);
	}

}
