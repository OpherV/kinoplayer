package com.android.kino.ui;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceClickListener;

import com.android.kino.R;
import com.android.kino.logic.settings.SettingsLoader;

public class KinoPreferences extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        // Get the custom preference
        Preference libraryActions = (Preference) findPreference("libraryActions");
        libraryActions.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                Intent libActionsIntent = new Intent(KinoPreferences.this,LibraryActionsMenu.class);                        
                startActivity(libActionsIntent);
                return true;
            }
        });
    }
    
    @Override
    protected void onDestroy() {
        SettingsLoader.updateCurrentSettings(this);
        super.onDestroy();
    }
}
