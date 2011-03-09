package com.myapps;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import com.myapps.utils.preferenceManagerUtils;

public class MesPreferences extends PreferenceActivity {
    SharedPreferences preferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	addPreferencesFromResource(R.xml.preferences);
	preferences = PreferenceManager.getDefaultSharedPreferences(this);

	/* Show current delay */
	preferenceManagerUtils.setEditTextPreferenceValueFromPreference(this,
		preferences, getString(R.string.TimeOut),
		getString(R.string.defaultTimeOut));
	/* Show current FPS delay */
	preferenceManagerUtils.setEditTextPreferenceValueFromPreference(this,
		preferences, getString(R.string.limitFPS),
		getString(R.string.defaultlimitFPS));
	/* Show current seuil */
	preferenceManagerUtils.setEditTextPreferenceValueFromPreference(this,
		preferences, getString(R.string.SeuilDM),
		getString(R.string.defaultSeuilDM));
	/* Show current to interval for motion detection */
	preferenceManagerUtils.setEditTextPreferenceValueFromPreference(this,
		preferences, getString(R.string.NotifTO),
		getString(R.string.defaultNotifTO));
    }

    public void onDestroy() {
	SharedPreferences preferences = PreferenceManager
		.getDefaultSharedPreferences(this);

	preferenceManagerUtils
		.ifNullChangeAndCommit(preferences,
			getString(R.string.TimeOut),
			getString(R.string.defaultTimeOut));
	preferenceManagerUtils.ifNullChangeAndCommit(preferences,
		getString(R.string.limitFPS),
		getString(R.string.defaultlimitFPS));
	preferenceManagerUtils
		.ifNullChangeAndCommit(preferences,
			getString(R.string.NotifTO),
			getString(R.string.defaultNotifTO));
	preferenceManagerUtils
		.ifNullChangeAndCommit(preferences,
			getString(R.string.SeuilDM),
			getString(R.string.defaultSeuilDM));

	super.onDestroy();

    }

}
