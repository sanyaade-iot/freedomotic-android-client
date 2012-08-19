package es.gpulido.freedomotic.ui.base;


import com.actionbarsherlock.app.SherlockFragmentActivity;

import es.gpulido.freedomotic.R;
import es.gpulido.freedomotic.ui.ObjectViewerFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

public abstract class BaseActivity  extends SherlockFragmentActivity 
{
					
		public void replaceFragment(int targetSlot,Fragment fragment,boolean addToBackstack)
		{			
		 	// TODO: Only replace when needed
//	    	ObjectViewerFragment details = (ObjectViewerFragment)
//	    			getFragmentManager().findFragmentById(R.id.details);
			
			
			// Execute a transaction, replacing any existing fragment
	        // with this one inside the frame.
	        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
	        ft.replace(targetSlot, fragment);	        
	        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
	        if (addToBackstack)
	        	ft.addToBackStack(null);
	        ft.commit();
					
		}		
		
		public void addFragment(int targetSlot,Fragment fragment)
		{			
			// Execute a transaction, replacing any existing fragment
	        // with this one inside the frame.
	        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
	        ft.add(targetSlot, fragment);	        
	        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
	        ft.commit();
					
		}		
		
}
