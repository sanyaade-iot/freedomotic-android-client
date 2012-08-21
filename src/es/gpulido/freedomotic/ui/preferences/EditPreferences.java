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

import java.util.List;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.actionbarsherlock.app.SherlockPreferenceActivity;

import es.gpulido.freedomotic.R;
import es.gpulido.freedomotic.ui.MainActivity;

public class EditPreferences extends SherlockPreferenceActivity {
	
	@SuppressWarnings("deprecation")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
		 setTheme(MainActivity.THEME);		 
		 super.onCreate(savedInstanceState);		 
		 getSupportActionBar().setTitle("Preferences");
		 if (Build.VERSION.SDK_INT<Build.VERSION_CODES.HONEYCOMB) {
			 addPreferencesFromResource(R.xml.preferences_preics);
		 }
    }
	 	 	 
	  @Override
	  public void onBuildHeaders(List<Header> target) {
	    loadHeadersFromResource(R.xml.preference_headers, target);
	  }
	  
	  
}
