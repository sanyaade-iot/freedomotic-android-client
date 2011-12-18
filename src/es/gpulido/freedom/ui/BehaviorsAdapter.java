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



import it.freedom.model.object.Behavior;
import it.freedom.model.object.EnvObject;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import es.gpulido.freedom.R;


public class BehaviorsAdapter extends ArrayAdapter<Behavior> {

	private final EnvObject envObject;
	//private final int index;

	public BehaviorsAdapter(Activity context, EnvObject object) {		
		super(context, R.layout.row_behavior,object.getBehaviors());
		//this.index = FreedomController.getInstance().getObjectNumber(object);
		this.envObject=object;		
	}

	// static to save the reference to the outer class and to avoid access to
	// any members of the containing class
//	static class ViewHolder {
//		public ToggleButton button;
//		public SeekBar seekBar;
//		public Spinner spinner;
//		public TextView textView;
//	}

	public View getView(final int position, View convertView, ViewGroup parent) 
    {
            if (convertView == null) {
                    // Create a new view
            	View output = new BehaviorListView(getContext(), getItem(position),envObject); 	
                return output;
            } else {
                // Reuse view
            	BehaviorListView setView = (BehaviorListView) convertView;
            	setView.setBehavior(getItem(position));                               
                return setView;
            }
    }		
}
