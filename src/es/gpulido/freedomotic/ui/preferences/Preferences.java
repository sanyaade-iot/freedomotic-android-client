/*******************************************************************************
 * Copyright (c) 2011 Gabriel Pulido.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Gabriel Pulido - initial API and implementation
 ******************************************************************************/
package es.gpulido.freedomotic.ui.preferences;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Preferences {

	// class attributes
	private static Preferences _instance = null; // Singleton reference
	static SharedPreferences prefs = null;

	protected Preferences() {
	}

	public static synchronized Preferences instance() {
		if (null == _instance)
			_instance = new Preferences();
		return _instance;

	}

	public static void create(SharedPreferences prefers) {
		prefs = prefers;
	}

	public static String getServerString() {
		String serverIP = prefs.getString("server_ip", "");
		if (serverIP == "")
			return "";
		String serverPort = "8111";
		if (!prefs.getBoolean("use_default", true))
			serverPort = prefs.getString("server_port", "");
		if (serverPort == "")
			return "http://" + serverIP;

		return "http://" + serverIP + ":" + serverPort;
	}

	public static String getBrokerIP() {
		if (prefs.getBoolean("use_default",true))
			return prefs.getString("server_ip", "");
		else
			return prefs.getString("broker_ip", "");

	}

	public static int getBrokerPort() {
		if (prefs.getBoolean("use_default", true))
			return 61666;
		else
		{
			int port;
			try {
				port = Integer.parseInt(prefs.getString("broker_port", "-1"));
			} catch (NumberFormatException ex) {
				port = -1;
			}
			return port;			
		}
		
	}

	public static String getBrokerString() {
		String brokerIp = getBrokerIP();
		int brokerPort = getBrokerPort();		
		return "http://" + brokerIp + ":" + brokerPort;
	}

	public static String getResourcesURL() {
		return getServerString() + "/v1/environment/resources/";
	}

	public static String getEnvironmentURL() {
		return getServerString() + "/v1/environment/";
	}

}
