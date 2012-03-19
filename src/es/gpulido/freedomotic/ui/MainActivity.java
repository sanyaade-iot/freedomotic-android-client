package es.gpulido.freedomotic.ui;

import org.restlet.resource.Post;

import it.freedomotic.model.object.EnvObject;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;

import es.gpulido.freedomotic.R;
import es.gpulido.freedomotic.api.EnvironmentController;
import es.gpulido.freedomotic.api.FreedomoticController;
import es.gpulido.freedomotic.ui.SelectableObjectListFragment.OnFragmentItemSelectedListener;
import es.gpulido.freedomotic.ui.base.BaseMultiPanelActivity;
import es.gpulido.freedomotic.ui.preferences.EditPreferences;
import es.gpulido.freedomotic.ui.preferences.Preferences;

//Main activity that holds the first screen fragments
public  class MainActivity extends  BaseMultiPanelActivity implements 
	ActionBar.OnNavigationListener, OnFragmentItemSelectedListener{
	//TODO: move to a constants class
	private static final int ACTIVITY_PREFERENCES = 0;
	
	public static int THEME = R.style.Theme_Sherlock_Light_DarkActionBar;
	/** The alert dialog box. */
	private AlertDialog alertDialog;	

	Fragment mRoomsFragment;
	Fragment mHousingPlanFragment;	
	boolean refreshing = true;
								
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		super.onCreate(savedInstanceState);
		
		//Ipv6 fix
		System.setProperty("java.net.preferIPv6Addresses", "false");		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD)
		{
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
	
	
		//Set the navigation mode in the actionbar		
		Context context = getSupportActionBar().getThemedContext();
		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		ArrayAdapter<CharSequence> list = ArrayAdapter.createFromResource(context, R.array.locations,
				R.layout.sherlock_spinner_item);							
        list.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);						
        
	    getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
	    getSupportActionBar().setListNavigationCallbacks(list, this);
	     		
		//ActionBar features
		setSupportProgressBarIndeterminateVisibility(false);

		//	Initializes the alert dialog to be used later.
		alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle(getString(R.string.error_dialog_title));
		alertDialog.setButton(getString(R.string.error_dialog_button),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						return;
					}
				});	

		readSettings();
		update();		
		mRoomsFragment=Fragment.instantiate(this, RoomsFragment.class.getName());
		mHousingPlanFragment=Fragment.instantiate(this, HousingPlanFragment.class.getName());		
	}
	
	
		
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		//TODO: Review how the new intent could be done to save / restore
		if (itemPosition == 0)
		{						
			setMasterFragment(mRoomsFragment,1,false);
		}
		else
		{			
			setMasterFragment(mHousingPlanFragment,0,false);
		}
		return false;
	}

	private void update() {
		Toast.makeText(this, R.string.retrieve_object_data, Toast.LENGTH_LONG)
				.show();
		new RefreshObjects().execute();
	}


	private void readSettings() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		Preferences.create(prefs);		
		// Init RestResource
		switch (EnvironmentController.getInstance().init()) {
//		case EnvironmentController.STOMP_ERROR:
//			Toast.makeText(
//					this,
//					"There is a problem with the Broker settings. Review your preferences and/or network",
//					Toast.LENGTH_LONG).show();
//			break;
		case EnvironmentController.REST_ERROR:
			Toast.makeText(
					this,
					"There is a problem with the Server settings. Review your preferences and/or network",
					Toast.LENGTH_LONG).show();
			break;
		case EnvironmentController.CONNECTED:
			break;
					
		}
//		if(!FreedomoticController.getInstance().initStompClient())
//		{	
//			Toast.makeText(
//					this,
//					"There is a problem with the Broker settings. Review your preferences and/or network",
//					Toast.LENGTH_LONG).show();				
//		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    outState.putInt("tab", getSupportActionBar().getSelectedNavigationIndex());	    
	}
	
	@Override	
	public boolean onCreateOptionsMenu(Menu menu) {		
		MenuInflater menuInflater= getSupportMenuInflater();
		menuInflater.inflate(R.menu.main, menu);
		if (refreshing)
		{
			menu.findItem(R.id.menu_refresh).setVisible(false);			
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:	    
            // app icon in action bar clicked; go Location selection
            return true;
		case R.id.menu_refresh:						
			update();
			break;

//		case R.id.menu_search:
//			Toast.makeText(this, "Tapped search", Toast.LENGTH_SHORT).show();
//			break;

		case R.id.menu_preferences:
			startActivityForResult(new Intent(this, EditPreferences.class),
					ACTIVITY_PREFERENCES);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case ACTIVITY_PREFERENCES:			
			// Preference screen was called: use new preferences to connect
			readSettings();
			update();
			break;
		}
	}
 

	// Updates asynchronly the Objects
	// TODO: this must be in the
	private class RefreshObjects extends AsyncTask<Object, Void, Message> {
		// private final ProgressDialog dialog = new ProgressDialog(Main.this);
		// can use UI thread here
		protected void onPreExecute() {
			setSupportProgressBarIndeterminateVisibility(true);
			refreshing = true;
			invalidateOptionsMenu();
		}

		// automatically done on worker thread (separate from UI thread)
		protected Message doInBackground(Object... params) {
			Message msg;
			msg = Message.obtain();
			msg.what = 0;
			try {			
				EnvironmentController.getInstance().retrieve();
				if(!FreedomoticController.getInstance().initStompClient())
				{	
					msg = Message.obtain();
					msg.what = 2;
					Bundle data = new Bundle();				
						data.putString("msg",
								"There is a problem with the Broker settings. Review your preferences and/or network");					
				}
									
			} catch (Exception e) {
				if (EnvironmentController.getInstance().getEnvironment()==null)
				{
					msg = Message.obtain();
					msg.what = 2;
					Bundle data = new Bundle();				
						data.putString("msg",
							"Cannot get the data due to: " + e.getMessage());				
					
					msg.setData(data);
				}
			}
			return msg;
		}
		// can use UI thread here
		protected void onPostExecute(final Message msg) {
			switch (msg.what) {
			case 0:
				//refreshMasterFragmentView();
				break;
			case 1:								
				break;
			case 2:
				// Error.
				// TODO:
				if (msg.getData().getString("msg")!="")
					alertDialog.setMessage(msg.getData().getString("msg"));
				else
					alertDialog.setMessage("Review your preferences and/or network");
				alertDialog.show();							
				break;
			default:			
				break;

			}
			setSupportProgressBarIndeterminateVisibility(false);
			refreshing = false;
			invalidateOptionsMenu();
		}
	}

	public void onObjectSelected(String objName, Fragment sender) {		
		Fragment fragment= ObjectViewerFragment.newInstance(objName);
    	setDetailsOrMasterFragment(fragment,true);    										
	}
	
	
	//not needed
	public boolean isDualPanel()
	{
		return super.isDualPanel();		
	}


	   
}
