package com.farsitel.android.qiblacompass.activities;

import com.farsitel.android.qiblacompass.R;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class Prefs extends PreferenceActivity {
    public static final String GPS_STATUS = "gps_status";
    public static final boolean GPS_STATUS_DEF =true;
    
    public static final String NETWORK_STATUS = "network_status";
    public static final boolean NETWORK_STATUS_DEF = true;
    
    public static final String SONI_STATUS = "soni_status";
    public static final boolean SONI_STATUS_DEF = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }
    public static boolean getGPSStatus(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context)
        .getBoolean(GPS_STATUS, GPS_STATUS_DEF);

    }
    public static boolean getNetworkStatus(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context)
        .getBoolean(NETWORK_STATUS, NETWORK_STATUS_DEF);

    }
    public static boolean getSoniStatus(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context)
        .getBoolean(SONI_STATUS, SONI_STATUS_DEF);

    }
}
