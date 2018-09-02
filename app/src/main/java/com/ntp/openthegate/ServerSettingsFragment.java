package com.ntp.openthegate;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.util.Log;

/**
 * A simple {@link Fragment} subclass.
 */
public class ServerSettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        //Log.w("Debug","onCreatePreferences Fragment");
        setPreferencesFromResource(R.xml.server_account, rootKey);
        setSummary();
    }

    @Override
    public void onResume() {
        //Log.w("Debug","onResume Fragment");
        super.onResume();

        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        //Log.w("Debug","onPause Fragment");
        super.onPause();

        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,String key)
    {
        //Log.w("Debug","onSharedPreferenceChanged Fragment");
        setSummary();
    }

    public void setSummary(){
        OTGStatus.readSettings();
        EditTextPreference editTextPref = (EditTextPreference) findPreference("mqServerUri");
        editTextPref.setSummary(OTGStatus.mqServerUri);
        editTextPref = (EditTextPreference) findPreference("mqUsername");
        editTextPref.setSummary(OTGStatus.mqUsername);
        editTextPref = (EditTextPreference) findPreference("mqPassword");
        editTextPref.setSummary(OTGStatus.mqPassword);
        editTextPref = (EditTextPreference) findPreference("mqPublishTopic");
        editTextPref.setSummary(OTGStatus.mqPublishTopic);
        editTextPref = (EditTextPreference) findPreference("mqPort");
        editTextPref.setSummary(OTGStatus.mqPort);
    }
}
