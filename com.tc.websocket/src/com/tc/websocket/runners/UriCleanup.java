/*
 * 
 */
package com.tc.websocket.runners;

import com.google.inject.Inject;
import com.tc.di.guicer.IGuicer;
import com.tc.websocket.server.IDominoWebSocketServer;



// TODO: Auto-generated Javadoc
/*
 * cleanup connections / sessions that end abruptly
 */

/**
 * The Class UserCleanup.
 */
public class UriCleanup implements Runnable {

	/** The server. */
	@Inject
	IDominoWebSocketServer server;

	/** The guicer. */
	@Inject
	IGuicer guicer;
	

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {	
		System.out.println("cleaning up uris");
		this.server.getUriUserMap().cleanup();

	}

}
