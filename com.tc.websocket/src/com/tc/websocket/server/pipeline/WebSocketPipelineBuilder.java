/*
 * 
 */
package com.tc.websocket.server.pipeline;

import com.google.inject.Inject;
import com.tc.di.guicer.IGuicer;
import com.tc.websocket.Config;
import com.tc.websocket.server.handler.WebSocketServerHandler;
import com.tc.websocket.server.handler.WebSocketValidationHandler;

import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;


// TODO: Auto-generated Javadoc
/**
 * The Class WebSocketPipelineBuilder.
 */
public class WebSocketPipelineBuilder implements IPipelineBuilder {
	
	/** The guicer. */
	@Inject
	private IGuicer guicer;
	
	
	

	/* (non-Javadoc)
	 * @see com.tc.websocket.server.pipeline.IPipelineBuilder#apply(io.netty.channel.ChannelPipeline)
	 */
	@Override
	public void apply(ChannelPipeline pipeline){
		pipeline.channel().config().setAutoRead(true);
		pipeline.addLast(new HttpServerCodec());
		pipeline.addLast(new HttpObjectAggregator(65536));
		if(Config.getInstance().isCompressionEnabled()){
			pipeline.addLast(new WebSocketServerCompressionHandler());
		}
		pipeline.addLast(guicer.inject(new WebSocketValidationHandler()));
		pipeline.addLast(guicer.inject(new WebSocketServerHandler()));
	}
	

}
