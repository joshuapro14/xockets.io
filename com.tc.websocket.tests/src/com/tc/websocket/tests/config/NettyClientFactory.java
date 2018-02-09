package com.tc.websocket.tests.config;

import io.netty.handler.ssl.SslContext;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLException;

import com.tc.websocket.SSLFactory;
import com.tc.websocket.tests.client.NettyTestClient;

public class NettyClientFactory {

	private List<NettyTestClient> clients = new ArrayList<NettyTestClient>();

	public List<NettyTestClient> buildClients(int maxPayload, boolean anonymous)
			throws URISyntaxException, InterruptedException, SSLException {
		
		

		if (!this.clients.isEmpty()) {
			return this.clients;
		}
		this.clients.addAll(this.createNew(maxPayload, anonymous));

		return this.clients;

	}

	public List<NettyTestClient> buildNewClients(int maxPayload, boolean anonymous)
			throws URISyntaxException, InterruptedException, SSLException {
		
		
		return this.createNew(maxPayload, anonymous);
	}

	private List<NettyTestClient> createNew(int maxPayload, boolean anonymous)
			throws URISyntaxException, InterruptedException, SSLException {

		TestConfig cfg = TestConfig.getInstance();

		List<NettyTestClient> myclients = new ArrayList<NettyTestClient>();

		if (cfg.getWebSocketUrl().contains("wss://")) {
			System.out.println("building websocket clients over TLS/SSL...");
		} else {
			System.out.println("building websocket clients...");
		}

		SslContext sslContext = null;
		if (cfg.isEncrypted()) {
			sslContext = new SSLFactory().createClientSslCtx(cfg);

		}

		String url = null;

		for (int i = 0; i < cfg.getNumberOfClients(); i++) {

			String username = null;
			if (i < 10) {
				username = anonymous ? "Anonymous" : "username00" + i;
			} else if (i < 100) {
				username = anonymous ? "Anonymous" : "username0" + i;
			} else {
				username = anonymous ? "Anonymous" : "username" + i;
			}

			String url1 = cfg.getWebSocketUrl().replace("{uri}", "uri" + i) + "/" + username;
			String url2 = cfg.getWebSocketUrl2().replace("{uri}", "uri" + i) + "/" + username;

			NettyTestClient c = null;

			if ((i % 2) == 0) {
				url = url1;
			} else {
				url = url2;
			}

			c = new NettyTestClient(new URI(url));

			// set the ssl engine if needed.

			if (sslContext != null) {
				c.setSSLContext(sslContext);
			}

			c.setMaxPayload(maxPayload);
			c.setUsername(username);
			c.setUuid(username);
			c.setCompress(cfg.isCompressionEnabled());
			c.connect();
			myclients.add(c);

			//Thread.sleep(cfg.getConnectionDelay());
		}


		return myclients;
	}


	public void closeClients(List<NettyTestClient> clients, int sleep) throws InterruptedException {
		for (NettyTestClient client : clients) {
			client.disconnect();
			//Thread.sleep(sleep);
		}
	}

	public void closeClients() throws InterruptedException {
		for (NettyTestClient client : clients) {
			client.disconnect();
			//Thread.sleep(500);
		}
	}

}
