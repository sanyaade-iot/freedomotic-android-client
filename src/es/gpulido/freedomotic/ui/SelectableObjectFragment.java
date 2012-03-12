package es.gpulido.freedomotic.ui;

import com.actionbarsherlock.app.SherlockFragment;

import es.gpulido.freedomotic.ui.SelectableObjectListFragment.OnFragmentItemSelectedListener;

public class SelectableObjectFragment extends SherlockFragment{
	
	   OnFragmentItemSelectedListener mListener;
	    
	    @Override
	    public void onAttach(android.app.Activity activity) {
			 super.onAttach(activity);
			 try {
		        mListener = (OnFragmentItemSelectedListener) activity;               
		    } catch (ClassCastException e) {
		        throw new ClassCastException(activity.toString() + " must implement OnFragmentItemSelectedListener");
		    }
	    	
	    };
	      		

}
