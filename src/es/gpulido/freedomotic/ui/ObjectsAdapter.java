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

import java.util.ArrayList;

import es.gpulido.freedomotic.R;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ObjectsAdapter extends ArrayAdapter<EnvObject> {
	private final Activity context;
	private final ArrayList<EnvObject> items;

	public ObjectsAdapter(Activity context, ArrayList<EnvObject> objects) {
		super(context, R.layout.row_object, objects);
		this.context = context;
		this.items = objects;		
	}

	// static to save the reference to the outer class and to avoid access to
	// any members of the containing class
	static class ViewHolder {
		//public ImageView imageView;
		public TextView textView;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// ViewHolder will buffer the assess to the individual fields of the row
		// layout

		ViewHolder holder;
		// Recycle existing view if passed as parameter
		// This will save memory and time on Android
		// This only works if the base layout for all classes are the same
		View rowView = convertView;	
		if (rowView == null) {
			LayoutInflater inflater = context.getLayoutInflater();
			rowView = inflater.inflate(R.layout.row_object, null, true);
			holder = new ViewHolder();
			holder.textView = (TextView) rowView.findViewById(R.id.label);
			rowView.setTag(holder);
		} else {
			holder = (ViewHolder) rowView.getTag();
		}
		
		holder.textView.setText(items.get(position).getName());
		// Change the icon depending of the object type
		EnvObject obj =  items.get(position);
		String type = obj.getType();
		//TODO: Substitute this for a hot load of images from a directory
		Drawable img;
		if (type.endsWith("Light"))			
			img = getContext().getResources().getDrawable(R.drawable.ic_list_object_light);			
		else if(type.endsWith("TV"))
			img = getContext().getResources().getDrawable(R.drawable.ic_list_object_tv);			
		else if(type.endsWith("ElectricDevice"))
			img = getContext().getResources().getDrawable(R.drawable.ic_list_object_electricdevice);						
		else if(type.endsWith("Door"))
			img = getContext().getResources().getDrawable(R.drawable.ic_list_object_door);
		else if(type.endsWith("Clock"))
			img = getContext().getResources().getDrawable(R.drawable.ic_list_object_clock);			
		else
			img = getContext().getResources().getDrawable(R.drawable.ic_list_object_default);
		holder.textView.setCompoundDrawablesWithIntrinsicBounds(img,null,null,null);	
		return rowView;
	}
}
