package com.myapps;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.SeekBar;

public class MesPreferences extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	addPreferencesFromResource(R.xml.preferences);
	SharedPreferences preferences = PreferenceManager
		.getDefaultSharedPreferences(this);

	/* Show current delay */
	EditTextPreference to = (EditTextPreference) findPreference(getString(R.string.TimeOut));
	String valueTo = preferences.getString(getString(R.string.TimeOut),
		getString(R.string.defaultTimeOut));
	to.setText(valueTo);
	
	/* Show current delay */
	EditTextPreference fps = (EditTextPreference) findPreference(getString(R.string.limitFPS));
	String valueFps = preferences.getString(getString(R.string.limitFPS),
		getString(R.string.defaultlimitFPS));
	fps.setText(valueFps);
	
	/* Show current sensibility */

    }

    public void onDestroy() {
	SharedPreferences preferences = PreferenceManager
		.getDefaultSharedPreferences(this);

	/* Get HTTP TO value to bloc value = "" */
	String value = preferences.getString(getString(R.string.TimeOut),
		getString(R.string.defaultTimeOut));
	Log.i(getString(R.string.logTag), "timeoutsave=" + value + ";");
	
	/* Change value and commited it */
	if (value.equalsIgnoreCase("")) {
	    Log.i(getString(R.string.logTag), "onDestroy change value");
	    SharedPreferences.Editor editor = preferences.edit();
	    editor.putString(getString(R.string.TimeOut),
		    getString(R.string.defaultTimeOut));
	    editor.commit();
	    Log.i(getString(R.string.logTag),
		    "onDestroy commit"
			    + preferences.getString(
				    getString(R.string.TimeOut),
				    getString(R.string.defaultTimeOut)) + ";");

	}
	
	/* Get limitFPS value to bloc value = "" */
	String valueFPS = preferences.getString(getString(R.string.limitFPS),
		getString(R.string.defaultlimitFPS));
	Log.i(getString(R.string.logTag), "limitFPS save=" + valueFPS + ";");
	
	/* Change value and commited it */
	if (valueFPS.equalsIgnoreCase("")) {
	    Log.i(getString(R.string.logTag), "onDestroy change value");
	    SharedPreferences.Editor editor = preferences.edit();
	    editor.putString(getString(R.string.limitFPS),
		    getString(R.string.defaultlimitFPS));
	    editor.commit();
	    Log.i(getString(R.string.logTag),
		    "onDestroy commit"
			    + preferences.getString(
				    getString(R.string.limitFPS),
				    getString(R.string.defaultlimitFPS)) + ";");

	}
	super.onDestroy();

    }
}
