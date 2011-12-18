package es.gpulido.freedom;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import es.gpulido.freedom.api.EnvironmentController;
import es.gpulido.freedom.api.FreedomController;
import es.gpulido.freedom.ui.actionbar.ActionBarActivity;
import es.gpulido.freedom.ui.preferences.EditPreferences;
import es.gpulido.freedom.ui.preferences.EditPreferencesHC;
import es.gpulido.freedom.ui.preferences.Preferences;

//Main activity that holds the first screen fragments
public class MainActivity extends ActionBarActivity {
	//TODO: move to a constants class
	private static final int ACTIVITY_PREFERENCES = 0;

	private boolean mAlternateTitle = false;

	/** The alert dialog box. */
	private AlertDialog alertDialog;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		System.setProperty("java.net.preferIPv6Addresses", "false");
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);

		// checkFirstRun();
		readSettings();
		setContentView(R.layout.activity_home);
		update();

		// findViewById(R.id.toggle_title).setOnClickListener(new
		// View.OnClickListener() {
		// @Override
		// public void onClick(View view) {
		// if (mAlternateTitle) {
		// setTitle(R.string.app_name);
		// } else {
		// setTitle(R.string.alternate_title);
		// }
		// mAlternateTitle = !mAlternateTitle;
		// }
		// });

		// Initializes the alert dialog to be used later.
		alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle(getString(R.string.error_dialog_title));
		alertDialog.setButton(getString(R.string.error_dialog_button),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						return;
					}
				});
	}

	private void update() {
		// TODO: move the string to resources
		Toast.makeText(this, "Retrieving Object data", Toast.LENGTH_LONG)
				.show();
		new RefreshObjects().execute();
	}

	private void checkFirstRun() {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(this);
		if (!sp.getBoolean("first_run", true)) {
			launchPreferences();
		}
		sp.edit().putBoolean("first_run", false).commit();
	}

	private void readSettings() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		Preferences.create(prefs);
		// Init RestResource
		switch (FreedomController.getInstance().init()) {
		case FreedomController.STOMP_ERROR:
			Toast.makeText(
					this,
					"There is a problem with the Broker settings. Review your preferences and/or network",
					Toast.LENGTH_LONG).show();
			break;
		case FreedomController.REST_ERROR:
			Toast.makeText(
					this,
					"There is a problem with the Server settings. Review your preferences and/or network",
					Toast.LENGTH_LONG).show();
			break;
		case FreedomController.CONNECTED:
			break;
		}
		// Init RestResource
		switch (EnvironmentController.getInstance().init()) {
		case EnvironmentController.REST_ERROR:
			Toast.makeText(
					this,
					"There is a problem with the Server settings. Review your preferences and/or network",
					Toast.LENGTH_LONG).show();
			break;
		case EnvironmentController.CONNECTED:
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.main, menu);
		// Calling super after populating the menu is necessary here to ensure
		// that the action bar helpers have a chance to handle this event.
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			//Toast.makeText(this, "Tapped home", Toast.LENGTH_SHORT).show();
			break;

		case R.id.menu_refresh:
			update();
			break;

//		case R.id.menu_search:
//			Toast.makeText(this, "Tapped search", Toast.LENGTH_SHORT).show();
//			break;

		case R.id.menu_preferences:
			launchPreferences();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case ACTIVITY_PREFERENCES:
			// If available, backup the new preferences data (Android 2.2+)
			// dataChanged(getPackageName());
			// Preference screen was called: use new preferences to connect
			readSettings();
			update();
			break;
		}
	}

	// TODO: Move to a PreferencesHelper class like the ActionBarHelper
	// Launch the correct preferences window.
	private void launchPreferences() {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			startActivityForResult(new Intent(this, EditPreferences.class),
					ACTIVITY_PREFERENCES);
		} else {
			startActivityForResult(new Intent(this, EditPreferencesHC.class),
					ACTIVITY_PREFERENCES);
		}

	}

	// Updates asynchronly the Objects
	// TODO: this must be in the
	private class RefreshObjects extends AsyncTask<Object, Void, Message> {
		// private final ProgressDialog dialog = new ProgressDialog(Main.this);
		// can use UI thread here
		protected void onPreExecute() {
			getActionBarHelper().setRefreshActionItemState(true);
		}

		// automatically done on worker thread (separate from UI thread)
		protected Message doInBackground(Object... params) {
			Message msg;
			try {
				FreedomController.getInstance().retrieve();
				EnvironmentController.getInstance().retrieve();
				msg = Message.obtain();
				msg.what = 0;
			} catch (Exception e) {
				msg = Message.obtain();
				msg.what = 2;
				Bundle data = new Bundle();
				data.putString("msg",
						"Cannot get the data due to: " + e.getMessage());
				msg.setData(data);
			}
			return msg;
		}

		// can use UI thread here
		protected void onPostExecute(final Message msg) {
			switch (msg.what) {
			case 0:
				break;
			case 1:
				// Update the interface once the object has been updated
				break;
			case 2:
				// Error.
				// TODO:
				alertDialog.setMessage(msg.getData().getString("msg"));
				alertDialog.show();
				getActionBarHelper().setRefreshActionItemState(false);
				break;
			default:
				getActionBarHelper().setRefreshActionItemState(false);
				break;

			}
			getActionBarHelper().setRefreshActionItemState(false);
		}
	}

}
