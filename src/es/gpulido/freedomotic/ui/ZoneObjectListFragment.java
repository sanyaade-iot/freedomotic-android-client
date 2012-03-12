package es.gpulido.freedomotic.ui;
import it.freedomotic.model.object.EnvObject;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import com.actionbarsherlock.app.SherlockListFragment;

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
import es.gpulido.freedomotic.ui.base.BaseMultiPanelActivity;



public class ZoneObjectListFragment extends SelectableObjectListFragment {
	//TODO: abstract to a "selectablelistfragment"
	boolean mDualPane;
    int mCurCheckPosition;
    int mRoomIndex;
    String mObjectSelectedName = "";
    ArrayList<EnvObject> mEnvObjectList;
      
    /**
     * Create a new instance of CountingFragment, providing "num"
     * as an argument.
     */
     static ZoneObjectListFragment newInstance(int roomIndex) {
    	ZoneObjectListFragment f = new ZoneObjectListFragment();    	 
        // Supply num input as an argument.    	
        Bundle args = new Bundle();
        args.putInt("roomIndex", roomIndex);
        args.putInt("curChoice", -1);
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
        mCurCheckPosition = getArguments() != null ? getArguments().getInt("curChoice"): -1;        
    }
    @Override
	public void onActivityCreated(Bundle savedInstanceState) {
    	super.onActivityCreated(savedInstanceState);		    	    	   	    
		//TODO: Think where the refresh must be done
    	if (mListener.isDualPanel())
    	{    		
    		getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    	}
    	setEmptyText("No data");
	    setData();
        
	    if (savedInstanceState != null) {
            // Restore last state for checked position.             
       	 	mCurCheckPosition = savedInstanceState.getInt("curChoice", -1); 
            mRoomIndex = savedInstanceState.getInt("curRoomIndex", 0);            
        }
	    
    }
    
    @Override
    public void onResume() { 
    	super.onResume();       	    	    
    	getView().invalidate();    	
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("curChoice", mCurCheckPosition);
        outState.putInt("curRoomIndex", mRoomIndex);
    }
	
	public void setData()
	{
		mEnvObjectList = (ArrayList<EnvObject>)EnvironmentController.getInstance().getRoom(mRoomIndex).getObjects();			    	   													
		setListAdapter(new ObjectsAdapter(getActivity(), mEnvObjectList));
	}
	
	//Throws again the objectselected to allow the listener to update the data
	public void selectItem()
	{		
		if(mCurCheckPosition != -1)
		{	
			mListener.onObjectSelected(((EnvObject)getListAdapter().getItem(mCurCheckPosition)).getName(),this);
		}
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {				                	      		
		mCurCheckPosition = position;
		getListView().setItemChecked(mCurCheckPosition, true);
		selectItem();
	}

	

}
