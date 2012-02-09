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
package es.gpulido.freedomotic.ui;

import android.content.res.Configuration;
import android.os.Bundle;
import es.gpulido.freedomotic.ui.actionbar.ActionBarActivity;

/*
* This is a secondary activity, to show what the user has selected
* when the screen is not large enough to show it all in one activity.
*/
public class ObjectViewerActivity extends ActionBarActivity {

   @Override
   protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);

       if (getResources().getConfiguration().orientation
               == Configuration.ORIENTATION_LANDSCAPE) {
           // If the screen is now in landscape mode, we can show the
           // dialog in-line with the list so we don't need this activity.
           finish();
           return;
       }

       if (savedInstanceState == null) {
           // During initial setup, plug in the details fragment.
           ObjectViewerFragment details = new ObjectViewerFragment();
           details.setArguments(getIntent().getExtras());
           getSupportFragmentManager().beginTransaction().add(
                   android.R.id.content, details).commit();
           
       }
   }
}
