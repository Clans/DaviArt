package com.github.clans.daviart;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public final class PreferenceHelper {

    private static final String KEY_CREDENTIALS = "key_credentials";

    private static SharedPreferences mPrefs;

    private PreferenceHelper() {
    }

    public static void init(Context applicationContext) {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(applicationContext);
    }

    /**
     * Update an int valued shared preference. This method induces file system
     * access. Validate values in setters before calling this method.
     *
     * @param key   to associate with value
     * @param value to store
     */
    private static void updatePref(String key, int value) {
        SharedPreferences.Editor edit = mPrefs.edit();
        edit.putInt(key, value);
        edit.apply();
    }

    /**
     * Update a boolean valued shared preference. This method induces file system
     * access. Validate values in setters before calling this method.
     *
     * @param key   to associate with value
     * @param value to store
     */
    private static void updatePref(String key, boolean value) {
        SharedPreferences.Editor edit = mPrefs.edit();
        edit.putBoolean(key, value);
        edit.apply();
    }

    /**
     * Update a string valued shared preference. This method induces file system
     * access. Validate values in setters before calling this method.
     *
     * @param key   to associate with value
     * @param value to store
     */
    private static void updatePref(String key, String value) {
        SharedPreferences.Editor edit = mPrefs.edit();
        edit.putString(key, value);
        edit.apply();
    }

    /**
     * Update a long valued shared preference. This method induces file system
     * access. Validate values in setters before calling this method.
     *
     * @param key   to associate with value
     * @param value to store
     */
    private static void updatePref(String key, long value) {
        SharedPreferences.Editor edit = mPrefs.edit();
        edit.putLong(key, value);
        edit.apply();
    }

    /**
     * Update a float valued shared preference. This method induces file system
     * access. Validate values in setters before calling this method.
     *
     * @param key   to associate with value
     * @param value to store
     */
    private static void updatePref(String key, float value) {
        SharedPreferences.Editor edit = mPrefs.edit();
        edit.putFloat(key, value);
        edit.apply();
    }

	public static void clearAll() {
        mPrefs.edit().clear().apply();
	}

    public static void setCredentials(String credentials) {
        updatePref(KEY_CREDENTIALS, credentials);
    }

    public static String getCredentials() {
        return mPrefs.getString(KEY_CREDENTIALS, "");
    }
}
