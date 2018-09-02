package com.ntp.openthegate;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class AccountSettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, new AccountSettingsFragment())
                .commit();
    }
}