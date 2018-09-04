package com.tc.websocket.tests.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.IOUtils;

import com.tc.websocket.IConfig;

public class TestConfig implements IConfig {

	private static TestConfig config = new TestConfig();

	private Properties props = new Properties();



	public static TestConfig getInstance(){
		return config;
	}

	private TestConfig(){
		InputStream in =null;
		try{
			in= TestConfig.class.getResourceAsStream("config.properties");
			props.load(in);
		}catch(IOException io){
			io.printStackTrace();
		}finally{
			IOUtils.closeQuietly(in);
		}
	}

	public int getNumberOfClients(){
		return Integer.parseInt(props.getProperty("number.of.clients"));
	}

	public String getWebSocketUrl(){
		return props.getProperty("websocket.url");
	}

	public String getWebSocketUrl2(){
		return props.getProperty("websocket.url2");
	}

	public int getConnectionDelay(){
		return Integer.parseInt(props.getProperty("connection.delay"));
	}


	public int getMaxPayload(){
		return Integer.parseInt(props.getProperty("max.frame.payload.length"));
	}
	
	
	public int getMessageDelay(){
		return Integer.parseInt(props.getProperty("message.delay"));
	}
	
	public String getSampleDataDir(){
		return props.getProperty("sample.data.dir");
	}
	
	public void overrideProperty(String key, String value){
		this.props.setProperty(key, value);
	}
	
	
	public String getHttpUsername(){
		return props.getProperty("http.username");
	}

	public String getHttpPassword(){
		return props.getProperty("http.password");
	}
	
	public String getRegisterUserUrl(){
		return props.getProperty("rest.registeruser");
	}
	
	public String getRemoveUserUrl(){
		return props.getProperty("rest.removeuser");
	}
	
	public String getOnlineUsersUrl(){
		return props.getProperty("rest.onlineusers");
	}
	
	public String getRestWebSocketUrl(){
		return props.getProperty("rest.websocketurl");
	}
	
	public String getSendMessageUrl(){
		return props.getProperty("rest.sendmessage");
	}
	
	public String getSendSimpleUrl(){
		return props.getProperty("rest.sendsimple");
	}
	
	public String getLatestMessageUrl(){
		return props.getProperty("rest.latestmessage");
	}
	
	public String getMessagesUrl(){
		return props.getProperty("rest.messages");
	}
	
	
	public int getPrintOnCount(){
		return Integer.parseInt(props.getProperty("print.on.count"));
	}
	
	public int getGcOnCount(){
		return Integer.parseInt(props.getProperty("gc.on.count"));
	}
	
	private int stopOnCount = 0;
	public int getStopOnCount(){
		if(stopOnCount > 0) return stopOnCount;
		stopOnCount= Integer.parseInt(props.getProperty("stop.on.count"));
		return stopOnCount;
	}
	
	
	public boolean isCompressionEnabled(){
		return new Boolean(props.getProperty("compression.enabled"));
	}

	
	public boolean isAllowAnonymous() {
		// TODO Auto-generated method stub
		return false;
	}

	
	public boolean isBroadcastServer() {
		// TODO Auto-generated method stub
		return false;
	}

	
	public boolean isEncrypted() {
		return props.getProperty("websocket.url").startsWith("wss://");
	}

	
	public int getPort() {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public String getKeyStore() {
		return props.getProperty("key.store");
	}

	
	public String getKeyStorePassword() {
		return props.getProperty("keystore.password");
	}

	
	public String getKeyPassword() {
		return props.getProperty("key.password");
	}

	
	public boolean isDebug() {
		// TODO Auto-generated method stub
		return false;
	}

	
	public String getKeyStoreType() {
		return props.getProperty("key.store.type");
	}

	
	public String getWebsocketFilter() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public boolean isTestMode() {
		// TODO Auto-generated method stub
		return false;
	}

	
	public void print(Object o) {
		System.out.println(o);
	}

	
	public void run() {
		// TODO Auto-generated method stub
		
	}

	
	public int getEventLoopThreads() {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public boolean isValid() {
		// TODO Auto-generated method stub
		return false;
	}

	
	public boolean isOnServer() {
		// TODO Auto-generated method stub
		return false;
	}

	
	public void setOnServer(boolean onServer) {
		// TODO Auto-generated method stub
		
	}

	
	public String getBroadcastServer() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public String getClustermateMonitor() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public int getClustermateExpiration() {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public boolean isClustered() {
		// TODO Auto-generated method stub
		return false;
	}

	
	public String getError() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public long getMaxSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public int getMaxConnections() {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public void setMaxConnections(int maxConnections) {
		// TODO Auto-generated method stub
		
	}

	
	public int getPingInterval() {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public int getThreadCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public void setThreadCount(int threadCount) {
		// TODO Auto-generated method stub
		
	}

	
	public int getPurgeInterval() {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public void setPurgeInterval(int purgeInterval) {
		// TODO Auto-generated method stub
		
	}

	
	public List<String> getAllowedOrigins() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public boolean isAllowedOrigin(String origin) {
		// TODO Auto-generated method stub
		return false;
	}

	
	public boolean isProfiled() {
		// TODO Auto-generated method stub
		return false;
	}

	
	public String getUsername() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public String getPassword() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public int getClientCacheMax() {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public int getSendBuffer() {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public int getReceiveBuffer() {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public boolean isNativeTransport() {
		// TODO Auto-generated method stub
		return false;
	}


	
	public int getProxyBackendPort() {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public String getProxyBackendHost() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public boolean isProxy() {
		// TODO Auto-generated method stub
		return false;
	}

	
	public boolean isLeakDetector() {
		// TODO Auto-generated method stub
		return false;
	}


	
	public List<Integer> getRedirectPorts() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public String getKeyFile() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public String getCertFile() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public boolean isKeyStore() {

		return false;
	}

	
	public boolean isEmptyCredentials() {
		// TODO Auto-generated method stub
		return false;
	}

	
	public String getVersion() {
		return "N/A";
	}

	
	public String property(String key) {
		return this.props.getProperty(key);
	}

	
	public Charset getCharSet() {
		return Charset.defaultCharset();
	}

	
	public boolean isCertAuth() {
		// TODO Auto-generated method stub
		return false;
	}

}
