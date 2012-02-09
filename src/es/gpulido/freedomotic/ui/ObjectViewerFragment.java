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
package es.gpulido.freedomotic.ui;

import it.freedomotic.model.object.EnvObject;

import java.util.Observable;
import java.util.Observer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import es.gpulido.freedomotic.R;
import es.gpulido.freedomotic.api.EnvironmentController;
import es.gpulido.freedomotic.api.FreedomController;

public class ObjectViewerFragment extends Fragment implements Observer{
	
	 /**
     * Create a new instance of ObjectViewerFragment, initialized to
     * show the text at 'index'.
     */
    public static ObjectViewerFragment newInstance(int roomIndex, int objectIndex) {
    	ObjectViewerFragment f = new ObjectViewerFragment();

        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putInt("roomIndex", roomIndex);
        args.putInt("objectIndex", objectIndex);
        f.setArguments(args);
        return f;
    }

    public int getRoomIndex() {
        return getArguments().getInt("roomIndex", 0);
    }
    
    public int getObjectIndex() {
        return getArguments().getInt("objectIndex", 0);
    }
           
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
   
    	if (container == null) {
            // We have different layouts, and in one of them this
            // fragment's containing frame doesn't exist.  The fragment
            // may still be created from its saved state, but there is
            // no reason to try to create its view hierarchy because it
            // won't be displayed.  Note this is not needed -- we could
            // just run the code below, where we would create and return
            // the view hierarchy; it would just never be used.
            return null;
        }
     		    
        // Inflate the layout for this fragment
     	if( EnvironmentController.getInstance().getRoom(getRoomIndex()).getObjects().size()!=0)
     	{
	    	EnvObject object = EnvironmentController.getInstance().getRoom(getRoomIndex()).getObjects().get(getObjectIndex()); 
	    	getActivity().setTitle(object.getName());
	    	View vi = inflater.inflate(R.layout.view_object, container, false);
	        ListView view = (ListView)vi.findViewById(R.id.list_behaviors);
	    	view.setAdapter(new BehaviorsAdapter(getActivity(), object));
	    	return vi;
     	}
     	else
     	{
     		return null;
     		
     	}    	
    }
      
    @Override
    public void onResume() {
    	super.onResume();    	
    	FreedomController.getInstance().addObserver(this); 
    };
    
    @Override
    public void onPause() {    	
    	FreedomController.getInstance().deleteObserver(this);
    	super.onPause();    	
    };
    
	@Override
	public void update(Observable observable, Object data) {		
			
		if( getActivity() instanceof ObjectViewerActivity)
		{			
			ListView view = (ListView)getActivity().findViewById(R.id.list_behaviors);
			view.post(new Runnable(){
				@Override
				public void run() {
					ListView view = (ListView)getActivity().findViewById(R.id.list_behaviors);
					((BehaviorsAdapter)view.getAdapter()).notifyDataSetChanged();
					
				}});
		}
		else //is dual panel
		{
			ObjectViewerFragment details2 = (ObjectViewerFragment)
                    getFragmentManager().findFragmentById(R.id.details);
			if (details2!=null)
			{				
				View view = details2.getView();
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
