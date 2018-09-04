package com.tc.websocket.clients;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketClientCompressionHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;

import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.SSLEngine;

import com.tc.websocket.Config;
import com.tc.websocket.Const;
import com.tc.websocket.SSLFactory;

public abstract class AbstractClient implements IWebSocketClient{

	private static final Logger logger = Logger.getLogger(AbstractClient.class.getName());


	//netty objects.
	private EventLoopGroup group = new NioEventLoopGroup(1);
	private WebSocketClientHandler handler;
	private Channel ch;
	private URI uri;
	private int maxPayload=65536;
	private boolean compress;
	private SslContext sslContext;
	
	


	public AbstractClient( URI uri ) throws InterruptedException {
		this.uri = uri;

	}



	public void setSSLContext(SslContext sslContext){
		this.sslContext = sslContext;
	}


	
	public void connect() throws InterruptedException{
		// Connect with V13 (RFC 6455 aka HyBi-17). You can change it to V08 or V00.
		// If you change it to V00, ping is not supported and remember to change
		// HttpResponseDecoder to WebSocketHttpResponseDecoder in the pipeline.
		handler =
				new WebSocketClientHandler(
						WebSocketClientHandshakerFactory.newHandshaker(uri, WebSocketVersion.V13, null, true, new DefaultHttpHeaders(),this.getMaxPayload()));


		//make sure the handler has a refernce to this object.
		handler.setClient(this);

		Bootstrap clientBoot = new Bootstrap();
		clientBoot.group(group)
		.channel(NioSocketChannel.class)
		.handler(new ChannelInitializer<SocketChannel>() {

			
			protected void initChannel(SocketChannel ch) {
				ChannelPipeline p = ch.pipeline();
				SSLEngine sslEngine=null;
				if(AbstractClient.this.isEncrypted()){
					if(sslContext == null){
						sslEngine = new SSLFactory().createClientSslCtx(Config.getInstance()).newEngine(ch.alloc(), uri.getHost(),uri.getPort());
					}else{
						sslEngine = sslContext.newEngine(ch.alloc(),uri.getHost(),uri.getPort());
					}
					
					sslEngine.setEnabledProtocols(Const.TLS_PROTOCOLS);
					sslEngine.setUseClientMode(true);
					p.addLast(new SslHandler(sslEngine));
				}

				p.addLast( new HttpClientCodec());
				p.addLast(new HttpObjectAggregator(8192));
				if(AbstractClient.this.isCompress()){
					p.addLast(WebSocketClientCompressionHandler.INSTANCE);
				}
				p.addLast(handler);


			}
		});


		this.ch = clientBoot.connect(uri.getHost(), uri.getPort()).sync().channel();
		handler.handshakeFuture().sync();	

	}

	public boolean isEncrypted() {
		return uri.toString().startsWith(Const.WSS);
	}



	
	public void onOpen(WebSocketClientHandshaker handShaker) {
		logger.log(Level.INFO, "onOpen");
	}

	
	public void onError(Throwable cause) {
		logger.log(Level.SEVERE, null, cause);
	}

	
	public void onClose() {
		logger.log(Level.FINE, "onClose");
	}

	
	public void disconnect() {
		ch.writeAndFlush(new CloseWebSocketFrame());
        try {
			ch.closeFuture().sync();
		} catch (InterruptedException e) {
			logger.log(Level.SEVERE,null, e);
		}finally{
			group.shutdownGracefully();
		}
	
	}

	
	public boolean isOpen() {
		return this.ch.isOpen();
	}


	
	public void send(String text) {
		ch.writeAndFlush(new TextWebSocketFrame(text));
	}

	public URI getUri(){
		return this.uri;
	}

	public String getRemoteHost(){
		return this.uri.getHost();
	}

	public int getMaxPayload() {
		return maxPayload;
	}


	public void setMaxPayload(int maxPayload) {
		this.maxPayload = maxPayload;
	}

	public boolean isCompress() {
		return compress;
	}


	public void setCompress(boolean compress) {
		this.compress = compress;
	}

}
