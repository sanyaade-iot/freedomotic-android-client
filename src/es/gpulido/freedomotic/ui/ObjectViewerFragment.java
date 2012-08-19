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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

import es.gpulido.freedomotic.R;
import es.gpulido.freedomotic.api.EnvironmentController;
import es.gpulido.freedomotic.api.FreedomoticController;

/**
 * Fragment that shows the object list of behaviors for a given object
 * @author gpt
 *
 */
public class ObjectViewerFragment extends SherlockFragment implements Observer {

	TextView objectName;
	/**
	 * Create a new instance of ObjectViewerFragment, initialized to show the
	 * text at 'index'.
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
		//DELETE: FreedomoticController.getInstance().addObserver(this);
		EnvironmentController.getInstance().addObserver(this);
	};

	@Override
	public void onPause() {
		//DELETE: FreedomoticController.getInstance().deleteObserver(this);
		EnvironmentController.getInstance().deleteObserver(this);
//		getSherlockActivity().getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
//		getSherlockActivity().getSupportActionBar().setDisplayShowTitleEnabled(false);		
//		getSherlockActivity().getSupportActionBar().setSubtitle(null);
		super.onPause();
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		if (container == null) {
			return null;
		}
		EnvObject object = EnvironmentController.getInstance().getObject(
				getObjName());

		//	
//		Test for changing the actionbar style when on object detail
//		IFragmentItemSelectedListener sherlock = (IFragmentItemSelectedListener)getSherlockActivity();
//		if (!sherlock.isDualPanel())
//		{
//			getSherlockActivity().getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
//			getSherlockActivity().getSupportActionBar().setDisplayShowTitleEnabled(true);
//			getSherlockActivity().getSupportActionBar().setTitle(object.getName());
//			
//		}
		
		View vi = inflater.inflate(R.layout.view_object, container, false);
		objectName = (TextView)vi.findViewById(R.id.txt_name);
		objectName.setText(object.getName());
		ListView view = (ListView) vi.findViewById(R.id.list_behaviors);
		view.setAdapter(new BehaviorsAdapter(getActivity(), object));		
		return vi;
	}

	//update the Fragment when the object data has changed externally
	public void update(Observable observable, final Object data) {
		ListView view = (ListView) getActivity().findViewById(
				R.id.list_behaviors);
		view.post(new Runnable() {
			public void run() {
				ListView view = (ListView) getActivity().findViewById(
						R.id.list_behaviors);
				//Check if the update affects the object
				if (data != null) {
					EnvObject updatedObject = (EnvObject) data;
					if (updatedObject.getName().equals(getObjName())) {
						((BehaviorsAdapter) view.getAdapter())
								.notifyDataSetChanged();
					}
				}
				else
				{
					EnvObject object = EnvironmentController.getInstance()
						.getObject(getObjName());
					view.setAdapter(new BehaviorsAdapter(getActivity(), object));
				}				
			}
		});
	}

	// @Override
	// public void onResume() {
	// super.onResume();
	// FreedomController.getInstance().addObserver(this);
	// };
	//

	//
	// @Override
	// public void update(Observable observable, Object data) {
	//
	// if( getActivity() instanceof ObjectViewerActivity)
	// {
	// ListView view =
	// (ListView)getActivity().findViewById(R.id.list_behaviors);
	// view.post(new Runnable(){
	// @Override
	// public void run() {
	// ListView view =
	// (ListView)getActivity().findViewById(R.id.list_behaviors);
	// ((BehaviorsAdapter)view.getAdapter()).notifyDataSetChanged();
	//
	// }});
	// }
	// else //is dual panel
	// {
	// ObjectViewerFragment details2 = (ObjectViewerFragment)
	// getFragmentManager().findFragmentById(R.id.details);
	// if (details2!=null)
	// {
	// View view = details2.getView();
	// view.post(new Runnable(){
	// @Override
	// public void run() {
	// ListView view2 =
	// (ListView)getActivity().findViewById(R.id.list_behaviors);
	// ((BehaviorsAdapter)view2.getAdapter()).notifyDataSetChanged();
	// }});
	//
	// }
	//
	//
	//
	// }
	//
	// }
}
