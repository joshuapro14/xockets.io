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


package com.tc.websocket.services;

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.google.inject.Module;
import com.tc.di.guicer.Guicer;
import com.tc.websocket.services.lib.ServicesModule;



// TODO: Auto-generated Javadoc
/**
 * The Class Activator.
 * Activator for the OSGi environment.
 * Starts and Stops the xockets.io server.
 */
public class Activator extends Plugin {

	/** The plugin id. */
	public static Bundle bundle;

	/** The version. */
	public static String VERSION;
	
	
	
	/** The command line reg. */
	@SuppressWarnings("unused")
	private ServiceRegistration commandLineReg;




	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(final BundleContext context) {
		bundle=context.getBundle(); 
		Guicer.createGuicer(bundle, new ArrayList<Module>(Arrays.asList(new ServicesModule())));
	}


	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(final BundleContext context) {
		Guicer.removeGuicer(bundle);
	}


}
