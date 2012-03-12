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

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockPreferenceActivity;

import es.gpulido.freedomotic.R;
import es.gpulido.freedomotic.ui.MainActivity;

public class EditPreferences extends SherlockPreferenceActivity {
	 @Override
    protected void onCreate(Bundle savedInstanceState) {
		 setTheme(MainActivity.THEME);		 
		 super.onCreate(savedInstanceState);
		 //TODO move the string to a resource
		 getSupportActionBar().setTitle("Preferences");
		 addPreferencesFromResource(R.xml.preferences);
    }
}
