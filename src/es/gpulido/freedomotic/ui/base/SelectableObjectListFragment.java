package es.gpulido.freedomotic.ui.base;

import com.actionbarsherlock.app.SherlockListFragment;

public class SelectableObjectListFragment extends SherlockListFragment{
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
