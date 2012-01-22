/*******************************************************************************
 * Copyright (c) 2011 Gabriel Pulido.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Gabriel Pulido - initial API and implementation
 ******************************************************************************/
package es.gpulido.freedom.ui;


import it.freedom.model.object.EnvObject;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;
import es.gpulido.freedom.R;
import es.gpulido.freedom.api.FreedomController;

public class ObjectListFragment extends ListFragment implements Observer {
	boolean mDualPane;
    int mCurCheckPosition = 0;    

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);	
	    FreedomController.getInstance().addObserver(this);
		setEmptyText("No data");
		//TODO: Think where the refresh must be done
		setData();
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
		ArrayList<EnvObject> envObjectList = (ArrayList<EnvObject>)FreedomController.getInstance().getObjects();			    	   													
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
	synchronized void showDetails(int index,boolean reload) {
        mCurCheckPosition = index;

        if (mDualPane) {
            // We can display everything in-place with fragments, so update
            // the list to highlight the selected item and show the data.        	        	
            getListView().setItemChecked(index, true);            

            // Check what fragment is currently shown, replace if needed.
            ObjectViewerFragment details = (ObjectViewerFragment)
                    getFragmentManager().findFragmentById(R.id.details);
            if (details == null || details.getShownIndex() != index ||reload==true) {
                // Make new fragment to show this selection.
                details = ObjectViewerFragment.newInstance(index);

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
            intent.putExtra("index", index);
            startActivity(intent);
        }
	}
	
	@Override
	public void update(Observable observable, Object data) {		
		
		if (mDualPane)
		{
			ObjectViewerFragment details = (ObjectViewerFragment)
	                    getFragmentManager().findFragmentById(R.id.details);
			if (details!=null)
			{
				View view = details.getView();
				view.post(new Runnable(){
					@Override
					public void run() {
						ListView view2 = (ListView)getActivity().findViewById(R.id.list_behaviors);
						((BehaviorsAdapter)view2.getAdapter()).notifyDataSetChanged();						
					}});
								
			}
						
		}	
	}	

}
	

	

