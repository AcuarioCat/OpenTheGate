package com.ntp.openthegate;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class ServerSettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, new ServerSettingsFragment())
                .commit();
    }
}