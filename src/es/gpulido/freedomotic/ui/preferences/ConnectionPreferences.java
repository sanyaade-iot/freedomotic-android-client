package es.gpulido.freedomotic.ui.preferences;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.preference.PreferenceFragment;

@SuppressLint("NewApi") 
public class ConnectionPreferences extends PreferenceFragment{
	 @SuppressLint("NewApi") 
	 @Override
	  public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);

	    int res=
	        getActivity().getResources()
	                     .getIdentifier(getArguments().getString("resource"),
	                                    "xml",
	                                    getActivity().getPackageName());

	    addPreferencesFromResource(res);
	  }

}
