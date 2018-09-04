
/*
 * � Copyright Tek Counsel LLC 2013
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

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.tc.websocket.valueobjects.IPrompt;
import com.tc.websocket.valueobjects.Prompt;


// TODO: Auto-generated Javadoc
/**
 * The Class RestArgumentException.
 */
@Provider
public class RestArgumentException implements ExceptionMapper<IllegalArgumentException> {
    
	/** The Constant LOG. */
	private static final Logger LOG = Logger.getLogger(RestArgumentException.class.getName());


 

    /* (non-Javadoc)
     * @see javax.ws.rs.ext.ExceptionMapper#toResponse(java.lang.Throwable)
     */
    @Override
	public Response toResponse(IllegalArgumentException exception) {
        IPrompt prompt = new Prompt();
        String msg  = exception.getMessage();
        prompt.setMessage(IPrompt.EXCEPTION,msg);
        LOG.log(Level.SEVERE,null, exception);
        return Response.status(Response.Status.OK).entity(prompt).type(MediaType.APPLICATION_JSON).build();
    }
}