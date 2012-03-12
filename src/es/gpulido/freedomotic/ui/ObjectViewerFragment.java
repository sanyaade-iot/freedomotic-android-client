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

import it.freedomotic.model.object.Behavior;
import it.freedomotic.model.object.EnvObject;

import java.util.Observable;
import java.util.Observer;

import com.actionbarsherlock.app.SherlockFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import es.gpulido.freedomotic.R;
import es.gpulido.freedomotic.api.EnvironmentController;
import es.gpulido.freedomotic.api.FreedomoticController;


public class ObjectViewerFragment extends SherlockFragment implements Observer{
	
	 /**
     * Create a new instance of ObjectViewerFragment, initialized to
     * show the text at 'index'.
     */
    public static ObjectViewerFragment newInstance(String objName) {
    	ObjectViewerFragment f = new ObjectViewerFragment();

        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putString("objName", objName);        
        f.setArguments(args);
        return f;
    }

    public String getObjName() {
        return getArguments().getString("objName");
    }
        
	@Override
    public void onResume() {
    	super.onResume();    	
    	FreedomoticController.getInstance().addObserver(this);    	
    };
    
    @Override
    public void onPause() {    	
    	FreedomoticController.getInstance().deleteObserver(this);
    	getSherlockActivity().getSupportActionBar().setSubtitle(null);
    	super.onPause();    	
    };
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
   
    	if (container == null) { return null;}
    	EnvObject object = EnvironmentController.getInstance().getObject(getObjName());    	
    	getSherlockActivity().getSupportActionBar().setSubtitle(object.getName());
    	View vi = inflater.inflate(R.layout.view_object, container, false);
        ListView view = (ListView)vi.findViewById(R.id.list_behaviors);
    	view.setAdapter(new BehaviorsAdapter(getActivity(), object));
    	return vi;
    }

	public void update(Observable observable, Object data) {
		// TODO Auto-generated method stub
		ListView view = (ListView)getActivity().findViewById(R.id.list_behaviors);
		view.post(new Runnable(){
			public void run() {			
				ListView view = (ListView)getActivity().findViewById(R.id.list_behaviors);
				((BehaviorsAdapter)view.getAdapter()).notifyDataSetChanged();													
			}});				
	}
	
	
       
//    @Override
//    public void onResume() {
//    	super.onResume();    	
//    	FreedomController.getInstance().addObserver(this); 
//    };
//    
  
//    
//	@Override
//	public void update(Observable observable, Object data) {		
//			
//		if( getActivity() instanceof ObjectViewerActivity)
//		{			
//			ListView view = (ListView)getActivity().findViewById(R.id.list_behaviors);
//			view.post(new Runnable(){
//				@Override
//				public void run() {
//					ListView view = (ListView)getActivity().findViewById(R.id.list_behaviors);
//					((BehaviorsAdapter)view.getAdapter()).notifyDataSetChanged();
//					
//				}});
//		}
//		else //is dual panel
//		{
//			ObjectViewerFragment details2 = (ObjectViewerFragment)
//                    getFragmentManager().findFragmentById(R.id.details);
//			if (details2!=null)
//			{				
//				View view = details2.getView();
//				view.post(new Runnable(){
//					@Override
//					public void run() {
//						ListView view2 = (ListView)getActivity().findViewById(R.id.list_behaviors);
//						((BehaviorsAdapter)view2.getAdapter()).notifyDataSetChanged();						
//					}});
//								
//			}
//		
//			
//			
//		}
//    		
//	}	
}
