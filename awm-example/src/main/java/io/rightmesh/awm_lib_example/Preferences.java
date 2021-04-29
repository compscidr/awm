package io.rightmesh.awm_lib_example;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.util.Log;

public class Preferences extends PreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = Preferences.class.getCanonicalName();
    private MainActivity mainActivity;

    @Override
    public void onCreatePreferences(Bundle bundle, String rootKey) {
        Activity activity = getActivity();
        if(activity != null) {
            mainActivity = (MainActivity) activity;
        }
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);
        Log.d(TAG, "Shared preference changed: " + preference);

        if (mainActivity != null) {
           mainActivity.stop();
           mainActivity.start();
        }
    }
}
