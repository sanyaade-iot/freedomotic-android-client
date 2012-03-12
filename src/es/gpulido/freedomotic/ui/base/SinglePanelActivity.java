package es.gpulido.freedomotic.ui.base;

import es.gpulido.freedomotic.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;


public abstract class SinglePanelActivity  extends BaseActivity{
	
	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_singlepane_empty);	   
	    }	   
	    
	    public void setPanelFragment(Fragment fragment,boolean addToBackstack)
	    {
	    	replaceFragment(R.id.panel, fragment,addToBackstack);	    	
	    }


}
