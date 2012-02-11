package es.gpulido.freedomotic.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import es.gpulido.freedomotic.R;


//TODO: unify with ObjectsActivity
public class ZoneObjectsActivity extends FragmentActivity {	
	   @Override
	   protected void onCreate(Bundle savedInstanceState) {
	       super.onCreate(savedInstanceState);

	       if (savedInstanceState == null) {
	           // During initial setup, plug in the details fragment.
	    	   setContentView(R.layout.fragment_layout);    	           
	       }
	   }
				
}
