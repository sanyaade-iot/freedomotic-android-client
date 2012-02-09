package es.gpulido.freedomotic.ui;

import android.os.Bundle;
import es.gpulido.freedomotic.R;
import es.gpulido.freedomotic.ui.actionbar.ActionBarActivity;


//TODO: unify with ObjectsActivity
public class ZoneObjectsActivity extends ActionBarActivity {	
	   @Override
	   protected void onCreate(Bundle savedInstanceState) {
	       super.onCreate(savedInstanceState);

	       if (savedInstanceState == null) {
	           // During initial setup, plug in the details fragment.
	    	   setContentView(R.layout.fragment_layout);    	           
	       }
	   }
				
}
