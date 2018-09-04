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

package com.tc.websocket.runners;

import com.tc.websocket.Config;
import com.tc.xpage.profiler.Stopwatch;


// TODO: Auto-generated Javadoc
/**
 * The Class FlagForDeletion.
 */
public class FlagForDeletion extends NotesOperation {
	

	/** The Constant SRCH. */
	private static final String SRCH="Form=\"fmSocketMessage\" & sentFlag=1";


	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	@Stopwatch
	public void run() {
		
		if(TaskRunner.getInstance().isClosing()){
			return;
		
		}else if(!Config.getInstance().isBroadcastServer()){
			return;
		}
		
		super.stampDocuments(SRCH, "Form", "delete");

	}



	






}
