package es.gpulido.freedomotic.ui;

import android.support.v4.app.Fragment;

import com.actionbarsherlock.app.SherlockListFragment;

public class SelectableObjectListFragment extends SherlockListFragment{
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
      	
	public interface OnFragmentItemSelectedListener {
		public void onObjectSelected(String objectName,Fragment sender);
		public boolean isDualPanel();
	}
}
