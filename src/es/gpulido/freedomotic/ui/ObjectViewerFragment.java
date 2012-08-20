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

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

import es.gpulido.freedomotic.R;
import es.gpulido.freedomotic.api.EnvironmentController;

/**
 * Fragment that shows the object list of behaviors for a given object
 * 
 * @author gpt
 * 
 */
public class ObjectViewerFragment extends SherlockFragment implements Observer {

	TextView objectName;

	Handler uiHandler;

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
		EnvironmentController.getInstance().addObserver(this);
	}

	@Override
	public void onPause() {
		EnvironmentController.getInstance().deleteObserver(this);
		// getSherlockActivity().getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		// getSherlockActivity().getSupportActionBar().setDisplayShowTitleEnabled(false);
		// getSherlockActivity().getSupportActionBar().setSubtitle(null);
		super.onPause();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		uiHandler = new Handler();
		if (container == null) {
			return null;
		}
		EnvObject object = EnvironmentController.getInstance().getObject(
				getObjName());

		//
		// Test for changing the actionbar style when on object detail
		// IFragmentItemSelectedListener sherlock =
		// (IFragmentItemSelectedListener)getSherlockActivity();
		// if (!sherlock.isDualPanel())
		// {
		// getSherlockActivity().getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		// getSherlockActivity().getSupportActionBar().setDisplayShowTitleEnabled(true);
		// getSherlockActivity().getSupportActionBar().setTitle(object.getName());
		//
		// }

		View vi = inflater.inflate(R.layout.view_object, container, false);
		objectName = (TextView) vi.findViewById(R.id.txt_name);
		objectName.setText(object.getName());
		ListView view = (ListView) vi.findViewById(R.id.list_behaviors);
		view.setAdapter(new BehaviorsAdapter(getActivity(), object));
		return vi;
	}

	// update the Fragment when the object data has changed externally
	@Override
	public void update(Observable observable, final Object data) {
		ListView view = (ListView) getActivity().findViewById(
				R.id.list_behaviors);

		// I don't know why this doesn't work on <= gingerbread
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
		{
			view.post(new Runnable() {
				public void run() {		
					ListView view = (ListView) getActivity().findViewById(
							R.id.list_behaviors);
					// Check if the update affects the object
					if (data != null) {
						EnvObject updatedObject = (EnvObject) data;
						if (updatedObject.getName().equals(getObjName())) {
						((BehaviorsAdapter) view.getAdapter())
								.notifyDataSetChanged();
						}
					} else {
						EnvObject object = EnvironmentController.getInstance()
								.getObject(getObjName());
						view.setAdapter(new BehaviorsAdapter(getActivity(),object));
					}
				}
			});
		} else {
			view.post(new Runnable() {
				public void run() {
					ListView view = (ListView) getActivity().findViewById(
							R.id.list_behaviors);
					EnvObject object = EnvironmentController.getInstance()
							.getObject(getObjName());
					view.setAdapter(new BehaviorsAdapter(getActivity(), object));
				}
			});
		}
	}
}
