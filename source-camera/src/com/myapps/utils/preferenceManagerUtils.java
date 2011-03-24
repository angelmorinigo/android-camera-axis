package com.myapps.utils;

import android.content.SharedPreferences;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;

public class preferenceManagerUtils {
    public static void setEditTextPreferenceValueFromPreference(
	    PreferenceActivity activity, SharedPreferences preferences,
	    String objectKey, String defaultVal) {
	EditTextPreference tomd = (EditTextPreference) activity
		.findPreference(objectKey);
	String value = preferences.getString(objectKey, defaultVal);
	tomd.setText(value);
    }

    public static void ifNullChangeAndCommit(SharedPreferences preferences,
	    String objectKey, String defaultVal) {
	/* Get limitFPS value to bloc value = "" */
	String value = preferences.getString(objectKey, defaultVal);

	/* Change value and commited it */
	if (value.equalsIgnoreCase("")) {
	    SharedPreferences.Editor editor = preferences.edit();
	    editor.putString(objectKey, defaultVal);
	    editor.commit();
	}
    }
}
