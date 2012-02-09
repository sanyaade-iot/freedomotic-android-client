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

import android.os.Bundle;
import es.gpulido.freedomotic.R;
import es.gpulido.freedomotic.ui.actionbar.ActionBarActivity;

//Just a class to inflate the Objects Fragments 
public class ObjectsActivity  extends ActionBarActivity{
	
	   @Override
	   protected void onCreate(Bundle savedInstanceState) {
	       super.onCreate(savedInstanceState);

	       if (savedInstanceState == null) {
	           // During initial setup, plug in the details fragment.
	    	   setContentView(R.layout.fragment_layout);    	           
	       }
	   }
	
}
