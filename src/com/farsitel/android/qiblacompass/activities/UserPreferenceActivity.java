package com.farsitel.android.qiblacompass.activities;

/*
 * This activity is responsible to construct the preference screen from xml resource
 * Preference screen is combined of:
 * 1. GPS On/Off (Boolean)
 * 2. Setting default location (Integer)
 * 
 * 
 * Required files:
 * res/xml/settings.xml
 */

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.Window;

import com.farsitel.android.qiblacompass.R;

public class UserPreferenceActivity extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        boolean b = requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        Log.d("qibla", "b: " + b);
        addPreferencesFromResource(R.xml.settings);

    }
}
