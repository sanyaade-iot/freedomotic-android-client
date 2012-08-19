package es.gpulido.freedomotic.ui.base;

import com.actionbarsherlock.app.SherlockFragment;


public class SelectableObjectFragment extends SherlockFragment{
	
	   protected IFragmentItemSelectedListener mListener;
	    
	    @Override
	    public void onAttach(android.app.Activity activity) {
			 super.onAttach(activity);
			 try {
		        mListener = (IFragmentItemSelectedListener) activity;               
		    } catch (ClassCastException e) {
		        throw new ClassCastException(activity.toString() + " must implement IFragmentItemSelectedListener");
		    }
	    	
	    };
	      		

}
