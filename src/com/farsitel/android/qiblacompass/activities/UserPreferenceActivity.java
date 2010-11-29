package com.farsitel.android.qiblacompass.activities;



import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.farsitel.android.qiblacompass.R;

public class UserPreferenceActivity extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      addPreferencesFromResource(R.xml.settings);
    }


}
