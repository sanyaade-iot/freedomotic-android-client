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



import java.util.ArrayList;
import java.util.Observer;

import it.freedomotic.model.object.Behavior;
import it.freedomotic.model.object.BooleanBehavior;
import it.freedomotic.model.object.EnvObject;
import it.freedomotic.model.object.ListBehavior;
import it.freedomotic.model.object.RangedIntBehavior;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;
import es.gpulido.freedomotic.R;
import es.gpulido.freedomotic.api.FreedomoticController;

public class BehaviorListView extends LinearLayout{
	ViewHolder holder;
	Behavior m_behavior;
	EnvObject m_obj; // the envobject where the behavior belongs
	public BehaviorListView(Context context,Behavior behavior,EnvObject obj) {
		super(context);
		// TODO Auto-generated constructor stub
		addView(inflate(context, R.layout.row_behavior, null));
		m_obj= obj;
		setBehavior(behavior);
		//FreedomController.getInstance().addObserver(this);
		
    	if (m_behavior instanceof BooleanBehavior)
    	{
    		ToggleButton.OnClickListener myToggleOnClickListener = new OnClickListener() {

    			public void onClick(View v) {					
    				ToggleButton tg = (ToggleButton)v;					  
    				boolean value = tg.isChecked();
    				String valueString = "false";
    				if (value) valueString = "true";

    				String object =m_obj.getName();
    				String behavior = m_behavior.getName();
    				FreedomoticController.getInstance().changeBehavior(object, behavior, valueString);
    				//TODO: Feedback of the change;
    			}
    		};
    		holder.button.setOnClickListener(myToggleOnClickListener);	
		}
		else if (m_behavior instanceof RangedIntBehavior)
		{
			OnSeekBarChangeListener mySeekBarChangeListener = new OnSeekBarChangeListener() {
				
				public void onStopTrackingTouch(SeekBar seekBar) {
					// TODO Auto-generated method stub
					
				}
				
				public void onStartTrackingTouch(SeekBar seekBar) {
					// TODO Auto-generated method stub
					
				}
				
				public void onProgressChanged(SeekBar seekBar, int progress,
						boolean fromUser) {
					if (fromUser){
						RangedIntBehavior beh = (RangedIntBehavior)m_behavior; 
						String value = String.valueOf(progress+beh.getMin());
						String object =m_obj.getName();
						String behavior = m_behavior.getName();
						FreedomoticController.getInstance().changeBehavior(object, behavior, value);
					}

				}
			};
			holder.seekBar.setOnSeekBarChangeListener(mySeekBarChangeListener);
			holder.seekBar.setVisibility(View.VISIBLE);			
		}
		else if (m_behavior instanceof ListBehavior)//it is a Multievaluated Behavior
		{
			OnItemSelectedListener myOnItemSelectedListener = new OnItemSelectedListener() {

				public void onItemSelected(AdapterView<?> parent, View view,
						int pos, long id)  {
					String value = (String) parent.getItemAtPosition(pos);
					String object =m_obj.getName();
					String behavior = m_behavior.getName();
					FreedomoticController.getInstance().changeBehavior(object, behavior, value);
				}

				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub
					
				}
			
			
			
			};
			holder.spinner.setOnItemSelectedListener(myOnItemSelectedListener);
		}						
		
	}
	
	public void setBehavior(Behavior b) {
	    	m_behavior = b;
	    	setData();
			    		    		    		    		    		    
	}
	
	
	
	 public void setData() {    	
	      		
		 if (holder == null) {					 	
				holder = new ViewHolder();
				holder.textView = (TextView) findViewById(R.id.label);
				holder.seekBar=(SeekBar)findViewById(R.id.seekBar1);
				holder.spinner=(Spinner)findViewById(R.id.spinner1);
				holder.button=(ToggleButton)findViewById(R.id.toggleButton1);			
			
			} 
			holder.textView.setText(m_behavior.getName());
			// Modify the layout 		
			holder.seekBar.setVisibility(View.GONE);
			holder.spinner.setVisibility(View.GONE);
			holder.button.setVisibility(View.GONE);
			if (m_behavior instanceof BooleanBehavior)
			{
				holder.button.setVisibility(View.VISIBLE);
				holder.button.setChecked(((BooleanBehavior)m_behavior).getValue());
				//holder.imageView.setImageResource(R.drawable.no);			
			}
			else if (m_behavior instanceof RangedIntBehavior)
			{
				//We should translate the min max to the 0..max of the seekbar
				RangedIntBehavior beh = (RangedIntBehavior)m_behavior; 
				holder.seekBar.setMax(beh.getMax()-beh.getMin());
				holder.seekBar.setVisibility(View.VISIBLE);
				holder.seekBar.setProgress(beh.getValue());
				//holder.imageView.setImageResource(R.drawable.ok);
			}
			else if(m_behavior instanceof ListBehavior) //it is a Multievaluated Behavior
			{				
				holder.spinner.setVisibility(View.VISIBLE);
				ListBehavior lb = (ListBehavior)m_behavior;
				ArrayList<String> spinnerArray = lb.getList();				
				ArrayAdapter<String>spinnerArrayAdapter = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_spinner_dropdown_item, spinnerArray);										
				holder.spinner.setAdapter(spinnerArrayAdapter);
				holder.spinner.setSelection(lb.indexOfSelection());
			}				
	 }
	
	// static to save the reference to the outer class and to avoid access to
	// any members of the containing class
	private static class ViewHolder {
		public ToggleButton button;
		public SeekBar seekBar;
		public Spinner spinner;
		public TextView textView;
	}

	private class SetBehaviorValue extends AsyncTask<Behavior, Void, Behavior> {
	    	
	    	//private final ProgressDialog dialog = new ProgressDialog(Main.this);
	    	// can use UI thread here    	
//	    	protected void onPreExecute() {    	
//	    		this.dialog.setMessage("Selecting data...");    	
//	    	    this.dialog.show();    	
//	    	    }

	    	protected Behavior doInBackground(Behavior... params) {
	    		m_behavior = params[0];
	    		return m_behavior;	    
	  	      }
	    	 
		      // can use UI thread here
		      protected void onPostExecute(final Behavior result) {
		         setBehavior(result);	         
		      }
	
		   }

//	@Override
//	public void update(Observable observable, Object data) {
//		// TODO Auto-generated method stub
//		if (((Behavior)data).equals((Behavior)m_behavior))
//			new SetBehaviorValue().execute((Behavior)data);
//			//setBehavior((Behavior)data);
//			
//	}   
	
	
	
}
