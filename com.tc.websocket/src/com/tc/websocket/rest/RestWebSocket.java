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

package com.tc.websocket.rest;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import com.google.inject.Inject;
import com.tc.di.guicer.IGuicer;
import com.tc.utils.DateUtils;
import com.tc.utils.StringCache;
import com.tc.utils.XSPUtils;
import com.tc.websocket.Const;
import com.tc.websocket.factories.ISocketMessageFactory;
import com.tc.websocket.jsf.IWebSocketBean;
import com.tc.websocket.server.IDominoWebSocketServer;
import com.tc.websocket.valueobjects.IUser;
import com.tc.websocket.valueobjects.SocketMessage;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.NotesException;
import lotus.domino.View;
import lotus.domino.ViewEntry;
import lotus.domino.ViewEntryCollection;


// TODO: Auto-generated Javadoc
/**
 * The Class RestWebSocket.
 */
public class RestWebSocket implements IRestWebSocket {

	/** The Constant LOG. */
	private static final Logger LOG = Logger.getLogger(RestWebSocket.class.getName());

	/** The guicer. */
	@Inject
	IGuicer guicer;

	/** The msg factory. */
	@Inject
	ISocketMessageFactory msgFactory;

	/** The req. */
	@Context
	HttpServletRequest req;

	/** The server. */
	@Inject
	IDominoWebSocketServer server;


	/**
	 * New bean.
	 *
	 * @return the i web socket bean
	 */
	private IWebSocketBean newBean(){
		IWebSocketBean bean = guicer.createObject(IWebSocketBean.class, Const.GUICE_REST_WEBSOCKET);
		bean.setRequest(req);
		return bean;
	}


	/**
	 * Builds the message.
	 *
	 * @param text the text
	 * @param nameValuePairs the name value pairs
	 * @return the response
	 * @throws NotesException the notes exception
	 */
	private Response buildMessage(String text, Object ... nameValuePairs) throws NotesException{

		//first param name, second param value (i.e. websocketUrl,http://bla)
		Map<String,Object> map = new HashMap<String,Object>();
		String name = StringCache.EMPTY;
		int cntr = 0;
		for(Object obj : nameValuePairs){
			
			
			//even are the names
			if((cntr % 2) ==0){
				name = String.valueOf(obj);
			}else{
				//odds the value
				map.put(name,obj);
			}
			cntr ++;
		}

		SocketMessage msg = new SocketMessage()
				.to(XSPUtils.session().getEffectiveUserName())
				.from(Const.FROM_SERVER)
				.text(text)
				.data(map);
		
		
		return this.buildResponse(msg);

	}

	/**
	 * Builds the response.
	 *
	 * @param o the o
	 * @return the response
	 */
	private Response buildResponse(Object o){
		ResponseBuilder builder = Response.ok(o, MediaType.APPLICATION_JSON_TYPE);
		CacheControl cacheControl = new CacheControl();
		cacheControl.setNoCache(true);
		builder.cacheControl(cacheControl);
		builder.expires(DateUtils.adjustDay(new Date(), -1));
		return builder.build();
	}




	/* (non-Javadoc)
	 * @see com.tc.websocket.rest.IRestWebSocket#register()
	 */
	@Override
	public Response register() throws NotesException {
		Response res = null;
		try{
			IWebSocketBean bean = newBean();
			bean.registerCurrentUser();
			List<SelectItem> list = bean.getOnlineUsers();
			res = this.buildMessage(Const.INFO, "websocketUrl",bean.getWebSocketUrl(), "onlineUsers",list,"sessionId", req.getSession().getId());
		}catch(Exception e){
			LOG.log(Level.SEVERE,null,e);
			res =  this.buildMessage(Const.EXCEPTION, Const.EXCEPTION.toLowerCase(),e.getMessage());
		}
		return res;
	}



	/* (non-Javadoc)
	 * @see com.tc.websocket.rest.IRestWebSocket#unregister()
	 */
	@Override
	public Response unregister() throws NotesException {
		Response res = null;
		try{
			IWebSocketBean bean = newBean();
			bean.removeCurrentUser();
			res= this.buildMessage(Const.INFO, "unregister","true");
		}catch(Exception e){
			LOG.log(Level.SEVERE,null,e);
			res =  this.buildMessage(Const.EXCEPTION, Const.EXCEPTION.toLowerCase(),e.getMessage());
		}
		return res;
	}



	/* (non-Javadoc)
	 * @see com.tc.websocket.rest.IRestWebSocket#getOnlineUsers()
	 */
	@Override
	public Response getOnlineUsers() throws NotesException {
		Response res = null;
		try{
			IWebSocketBean bean = newBean();
			List<SelectItem> list = bean.getOnlineUsers();
			res = this.buildResponse(list);
		}catch(Exception e){
			LOG.log(Level.SEVERE,null,e);
			res =  this.buildMessage(Const.EXCEPTION, Const.EXCEPTION.toLowerCase(),e.getMessage());
		}
		return res;

	}



	/* (non-Javadoc)
	 * @see com.tc.websocket.rest.IRestWebSocket#getWebSocketUrl()
	 */
	@Override
	public Response getWebSocketUrl() throws NotesException {
		Response res = null;
		try{
			IWebSocketBean bean = newBean();
			res = this.buildMessage(Const.INFO, "websocketUrl", bean.getWebSocketUrl());
		}catch(Exception e){
			LOG.log(Level.SEVERE,null,e);
			res =  this.buildMessage(Const.EXCEPTION, Const.EXCEPTION.toLowerCase(),e.getMessage());
		}
		return res;

	}


	/* (non-Javadoc)
	 * @see com.tc.websocket.rest.IRestWebSocket#sendMessage(com.tc.websocket.valueobjects.SocketMessage)
	 */
	@Override
	public Response sendMessage(SocketMessage msg) throws NotesException {
		Response res= null;
		try{
			IWebSocketBean bean = newBean();
			bean.sendMessage(msg);
			res= this.buildMessage(Const.INFO, "sendMessage","true");
		}catch(Exception e){
			LOG.log(Level.SEVERE,null,e);
			res =  this.buildMessage(Const.EXCEPTION, Const.EXCEPTION.toLowerCase(),e.getMessage());
		}
		return res;

	}



	/* (non-Javadoc)
	 * @see com.tc.websocket.rest.IRestWebSocket#sendMessage(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public Response sendMessage(String from, String to, String text) throws NotesException {
		SocketMessage msg = new SocketMessage();
		msg.setFrom(XSPUtils.session().createName(from).getCanonical());
		msg.setTo(XSPUtils.session().createName(to).getCanonical());
		msg.setText(text);
		msg.setDate(new Date());
		return this.sendMessage(msg);
	}



	/* (non-Javadoc)
	 * @see com.tc.websocket.rest.IRestWebSocket#getMessages()
	 */
	@Override
	public Response getMessages() throws NotesException {

		if(this.hasWebSocketConnection()){
			throw new RuntimeException("This RESTful API can only be used when no websocket connection is open.");
		}

		Database db = XSPUtils.session().getDatabase(StringCache.EMPTY, Const.WEBSOCKET_PATH);
		View view = db.getView(Const.VIEW_MESSAGES_BY_USER);
		ViewEntryCollection col = view.getAllEntriesByKey(XSPUtils.session().getEffectiveUserName(),true);
		List<SocketMessage> list = msgFactory.buildMessages(col);

		//mark the collection as sent
		col.stampAll(Const.FIELD_SENTFLAG, Const.FIELD_SENTFLAG_VALUE_SENT);

		//cleanup db should cleanup view, and col
		db.recycle();

		return this.buildResponse(list);
	}



	/* (non-Javadoc)
	 * @see com.tc.websocket.rest.IRestWebSocket#getLatestMessage()
	 */
	@Override
	public Response getLatestMessage() throws NotesException {

		if(this.hasWebSocketConnection()){
			throw new RuntimeException("This RESTful API can only be used when no websocket connection is open.");
		}

		SocketMessage msg = null;
		Database db = XSPUtils.session().getDatabase(StringCache.EMPTY, Const.WEBSOCKET_PATH);
		View view = db.getView(Const.VIEW_MESSAGES_BY_USER);
		ViewEntry entry = view.getEntryByKey(XSPUtils.session().getEffectiveUserName(),true);

		if(entry!=null){
			Document doc = entry.getDocument();
			if(doc.isValid()){
				msg = msgFactory.buildMessage(entry.getDocument());

				//mark as sent.
				doc.replaceItemValue(Const.FIELD_SENTFLAG, Const.FIELD_SENTFLAG_VALUE_SENT);
				doc.save();
			}
		}

		//cleanup db should cleanup view, entry, and doc.
		db.recycle();

		return this.buildResponse(msg);

	}

	/**
	 * Checks for web socket connection.
	 *
	 * @return true, if successful
	 * @throws NotesException the notes exception
	 */
	private boolean hasWebSocketConnection() throws NotesException{
		IUser user = server.resolveUser(XSPUtils.session().getEffectiveUserName());
		return user.getConn()!=null && user.getConn().isOpen();
	}



}
