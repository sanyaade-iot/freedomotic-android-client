package es.gpulido.freedomotic.ui;
import it.freedomotic.model.object.EnvObject;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import es.gpulido.freedomotic.R;
import es.gpulido.freedomotic.api.EnvironmentController;
import es.gpulido.freedomotic.api.FreedomController;

public class ZoneObjectListFragment extends ListFragment {
	boolean mDualPane;
    int mCurCheckPosition = 0;        
    int mRoomIndex =0;    
    /**
     * Create a new instance of CountingFragment, providing "num"
     * as an argument.
     */
    static ZoneObjectListFragment newInstance(int roomIndex) {
    	ZoneObjectListFragment f = new ZoneObjectListFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("roomIndex", roomIndex);
        f.setArguments(args);
        return f;
    }

    
    /**
     * When creating, retrieve this instance's number from its arguments.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRoomIndex = getArguments() != null ? getArguments().getInt("roomIndex") : 1;
    }
     
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
	    super.onActivityCreated(savedInstanceState);		    	    	   	    
		//TODO: Think where the refresh must be done
		setData();
		
		//TODO: manage dualpanel
	    // Populate list with our static array of titles.      
		View detailsFrame = getActivity().findViewById(R.id.details);
        mDualPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;

         if (savedInstanceState != null) {
             // Restore last state for checked position.             
        	 mCurCheckPosition = savedInstanceState.getInt("curChoice", 0);
             
         }

         if (mDualPane) {
             // In dual-pane mode, the list view highlights the selected item.
             getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
             // Make sure our UI is in the correct state.
             showDetails(mCurCheckPosition,false);
         }		
		
		
	}
	public void setData()
	{
		ArrayList<EnvObject> envObjectList = (ArrayList<EnvObject>)EnvironmentController.getInstance().getRoom(mRoomIndex).getObjects();			    	   													
		setListAdapter(new ObjectsAdapter(getActivity(), envObjectList));
	}

	@Override
     public void onSaveInstanceState(Bundle outState) {
         super.onSaveInstanceState(outState);
         outState.putInt("curChoice", mCurCheckPosition);
     }
	

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {	  	
		showDetails(position,false);	  	
	}
	
    /**
     * Helper function to show the details of a selected item, either by
     * displaying a fragment in-place in the current UI, or starting a
     * whole new activity in which it is displayed.
     */
	synchronized void showDetails(int index, boolean reload) {
        mCurCheckPosition = index;        

        if (mDualPane) {
            // We can display everything in-place with fragments, so update
            // the list to highlight the selected item and show the data.        	        	
            getListView().setItemChecked(index, true);            

            // Check what fragment is currently shown, replace if needed.
            ObjectViewerFragment details = (ObjectViewerFragment)
                    getFragmentManager().findFragmentById(R.id.details);
            if (details == null || !(details.getObjectIndex()== index && details.getRoomIndex()==mRoomIndex)||reload==true) {
                // Make new fragment to show this selection.
                details = ObjectViewerFragment.newInstance(mRoomIndex,index);

                // Execute a transaction, replacing any existing fragment
                // with this one inside the frame.
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.details, details);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
            }           
        } else {
            // Otherwise we need to launch a new activity to display
            // the dialog fragment with selected text.
            Intent intent = new Intent();
            intent.setClass(getActivity(), ObjectViewerActivity.class);
       
            intent.putExtra("roomIndex", mRoomIndex);
            intent.putExtra("objectIndex", index);
            startActivity(intent);
        }
	}
}
