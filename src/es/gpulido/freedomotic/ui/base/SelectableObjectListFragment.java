package es.gpulido.freedomotic.ui.base;

import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;

import com.actionbarsherlock.app.SherlockListFragment;

import es.gpulido.freedomotic.R;

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
