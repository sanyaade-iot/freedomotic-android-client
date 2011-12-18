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
package es.gpulido.freedom.ui;



import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import es.gpulido.freedom.R;
import es.gpulido.freedom.api.FreedomController;
import es.gpulido.freedom.ui.actionbar.ActionBarActivity;
import es.gpulido.freedom.ui.preferences.Preferences;

public class FreedomActivity extends ActionBarActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.setProperty("java.net.preferIPv6Addresses", "false");
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        ReadSettings();                       
        setContentView(R.layout.fragment_layout);
        //setContentView(R.layout.main);
        
    }

	private void ReadSettings() {
	       
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);        
        Preferences.create(prefs);                       
       	// Init RestResource
        FreedomController.getInstance().init();
        
		
	}
}
