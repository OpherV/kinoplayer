package com.android.kino.ui;

import java.io.File;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceClickListener;

import com.android.kino.Kino;
import com.android.kino.R;
import com.android.kino.logic.settings.SettingsLoader;

public class KinoPreferences extends PreferenceActivity {
	
	public final static int PICKMUSICDIR = 1;
	
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
        
        Preference pickMusicDir = (Preference) findPreference("musicDir");
        pickMusicDir.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                
        		File rootDir = Environment.getExternalStorageDirectory();
        		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(KinoPreferences.this);
        		String mediaPath = prefs.getString("musicDir", "mp3");
        		File mp3dir = new File(rootDir, mediaPath);
            	
            	Intent pickFolder = new Intent();
                pickFolder.setAction(Intent.ACTION_PICK );
                Uri theUri = Uri.parse("folder://" + mp3dir.getPath() ) ;
                pickFolder.setData(theUri);
                startActivityForResult(pickFolder,PICKMUSICDIR);
                return true;
            }
        });
    }
    
    // Listen for results.
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        // See which child activity is calling us back.
        switch (requestCode) {
            case PICKMUSICDIR:
                // This is the standard resultCode that is sent back if the
                // activity crashed or didn't doesn't supply an explicit result.
                if (resultCode == RESULT_CANCELED){
                    //
                } 
                else if (resultCode == RESULT_OK) {             
                	String mp3dir = data.getData().getPath();
                    
                    //TODO or - is this the right way to save a preference? i think not. (it doesn't show if the user clicks the music folder again)
                	//Toast.makeText(this, mp3dir ,Toast.LENGTH_LONG).show();
                	SharedPreferences.Editor prefs = PreferenceManager.getDefaultSharedPreferences(this).edit();
                	prefs.putString("musicDir", mp3dir).commit();
                }
            default:
                break;
        }
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        SettingsLoader.updateCurrentSettings(this);
        Kino.getKino(this).showNotification();
    }
}
