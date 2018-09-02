package com.ntp.openthegate;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.PreferenceFragmentCompat;

/**
 * A simple {@link Fragment} subclass.
 */
public class AccountSettingsFragment extends PreferenceFragmentCompat  implements SharedPreferences.OnSharedPreferenceChangeListener  {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey)
    {
        setPreferencesFromResource(R.xml.user_account, rootKey);
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

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
    {
        //Log.w("Debug","onSharedPreferenceChanged Fragment");
        setSummary();
    }

    public void setSummary(){
        OTGStatus.readSettings();
        EditTextPreference editTextPref = (EditTextPreference) findPreference("gateUserID");
        editTextPref.setSummary(OTGStatus.gateUserID);
        editTextPref = (EditTextPreference) findPreference("gatePassword");
        editTextPref.setSummary(OTGStatus.gatePassword);
        editTextPref = (EditTextPreference) findPreference("mqClientId");
        editTextPref.setSummary(OTGStatus.mqClientId);
    }
}
