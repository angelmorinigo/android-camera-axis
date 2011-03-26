package com.myapps.utils;

import android.content.SharedPreferences;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;

/**
 * 
 * Manages textual values used in preferences
 *
 */
public class preferenceManagerUtils {
	/**
	 * Apply a textual value for a specific preference
	 * @param activity The current activity
	 * @param preferences The SharedPreferences object to use
	 * @param objectKey The key in preferences
	 * @param defaultVal The default value for preferences key
	 */
    public static void setEditTextPreferenceValueFromPreference(
	    PreferenceActivity activity, SharedPreferences preferences,
	    String objectKey, String defaultVal) {
	EditTextPreference tomd = (EditTextPreference) activity
		.findPreference(objectKey);
	String value = preferences.getString(objectKey, defaultVal);
	tomd.setText(value);
    }

    /**
     * Apply a default value for a preference if a null value has
     * been entered
     * @param preferences The SharedPreferences object to use
     * @param objectKey The key in preferences
     * @param defaultVal The default value for preference key
     */
    public static void ifNullChangeAndCommit(SharedPreferences preferences,
	    String objectKey, String defaultVal) {
	/* Get limitFPS value to prevent value = "" */
	String value = preferences.getString(objectKey, defaultVal);

	/* Change value and commit it */
	if (value.equalsIgnoreCase("")) {
	    SharedPreferences.Editor editor = preferences.edit();
	    editor.putString(objectKey, defaultVal);
	    editor.commit();
	}
    }
}
