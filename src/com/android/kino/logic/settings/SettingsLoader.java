package com.android.kino.logic.settings;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.kino.Kino;
import com.android.kino.logic.InputEventTranslator;
import com.android.kino.logic.action.KinoAction;
import com.android.kino.logic.action.NextTrack;
import com.android.kino.logic.action.PlayPauseToggle;
import com.android.kino.logic.event.DoubleTapEvent;
import com.android.kino.logic.settings.SettingsContainer.Setting;

public abstract class SettingsLoader {
    private static final String TAG = "SettingsLoader";

    private static final String ACTION_PLAY_PAUSE = "playpause";
    private static final String ACTION_NEXT_TRACK = "nexttrack";

    private static final String IMAGE_LAZY_DOWNLOAD = "lazyload";
    private static final String IMAGE_ON_UPDATE = "atupdate";
    
    private static final String DEFAULT_MUSIC_DIR = "mp3";
    private static final boolean DEFAULT_IS_IMG_LAZY_LOAD = true;
    private static final boolean DEFAULT_ALLOW_TAP = true;

    
    private static SettingsContainer mDefaultSettings = null;
    private static Map<String, SettingsProfile> mUserProfiles =
        new HashMap<String, SettingsProfile>();
    private static SettingsContainer mCurrentSettings = null;
    
    public static SettingsContainer loadDefaultSettings(Context context) {
        if (mDefaultSettings != null) {
            return mDefaultSettings;
        }
        // TODO(implement) Load setting from DB or something - currently use hardcoded default settings
        SettingsContainer defaultSettings = new DefaultSettings();
        defaultSettings.setConfiguredString(Setting.MEDIA_DIRECTORY, DEFAULT_MUSIC_DIR);
        defaultSettings.setConfiguredBoolean(Setting.LAZY_LOAD_IMAGES, DEFAULT_IS_IMG_LAZY_LOAD);
        defaultSettings.setConfiguredBoolean(Setting.ENABLE_DOUBLE_TAP, DEFAULT_ALLOW_TAP);
        defaultSettings.setConfiguredAction(DoubleTapEvent.ID, PlayPauseToggle.ID);
        mDefaultSettings = defaultSettings;
        
        return mDefaultSettings;
    }
    
    public static SettingsContainer loadProfile(String profileName, Context context) {
        if (mUserProfiles.containsKey(profileName)) {
            return mUserProfiles.get(profileName);
        }
        // TODO(implement) Load setting from DB or something
        return null;
    }
    
    public static SettingsContainer loadCurrentSettings(Context context) {
        Log.d(TAG, "loadCurrentSettings - " + context);
        if (mCurrentSettings != null) {
            return mCurrentSettings;
        }
        mCurrentSettings = loadDefaultSettings(context);
        if (context != null) {
            setPreferences(mCurrentSettings, context);
        }
        return mCurrentSettings;
    }
    
    public static void setCurrentSettings(SettingsContainer settings) {
        mCurrentSettings = settings;
    }
    
    /**
     * Update the current Kino settings according to the Android preferences.
     */
    public static void updateCurrentSettings(Activity context) {
        Log.d(TAG, "updateCurrentSettings");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        
        String musicDir = prefs.getString("musicDir", DEFAULT_MUSIC_DIR);
        String imgDownload = prefs.getString("imgDownload", IMAGE_LAZY_DOWNLOAD);
        boolean allowDoubleTap = prefs.getBoolean("allowDoubleTap", DEFAULT_ALLOW_TAP);
        String doubleTapAction = prefs.getString("doubleTapAction", ACTION_PLAY_PAUSE);
        
        SettingsContainer settings = loadCurrentSettings(null);
        settings.setConfiguredString(Setting.MEDIA_DIRECTORY, musicDir);
        settings.setConfiguredBoolean(Setting.LAZY_LOAD_IMAGES, imgDownload.equals(IMAGE_LAZY_DOWNLOAD));
        settings.setConfiguredBoolean(Setting.ENABLE_DOUBLE_TAP, allowDoubleTap);
    
        if (doubleTapAction.equals(ACTION_PLAY_PAUSE)) {
            settings.setConfiguredAction(DoubleTapEvent.ID, PlayPauseToggle.ID);
        }
        else if (doubleTapAction.equals(ACTION_NEXT_TRACK)) {
            settings.setConfiguredAction(DoubleTapEvent.ID, NextTrack.ID);
        }
    
        InputEventTranslator inputTranslator = Kino.getKino(context).getInputTranslator();
        if (inputTranslator != null) {
            if (allowDoubleTap) {
                // Enable double tap
                inputTranslator.enableDoubleTap();
            }
            else {
                // Disable double tap
                inputTranslator.disableDoubleTap();
            }
        }
    }

    /**
     * Updates Android preferences according to a Kino container.
     */
    private static void setPreferences(SettingsContainer settings, Context context) {
        Log.d(TAG, "setPreferences");
        String musicDir = settings.getConfiguredString(Setting.MEDIA_DIRECTORY);
        boolean imgLazyLoad = settings.getConfiguredBoolean(Setting.LAZY_LOAD_IMAGES);
        boolean allowDoubleTap = settings.getConfiguredBoolean(Setting.ENABLE_DOUBLE_TAP);
        KinoAction doubleTapAction = settings.getConfiguredAction(DoubleTapEvent.ID);
        
        SharedPreferences.Editor prefs = PreferenceManager.getDefaultSharedPreferences(context).edit();
        prefs.putString("musicDir", musicDir);
        if (imgLazyLoad) {
            prefs.putString("imgDownload", IMAGE_LAZY_DOWNLOAD);
        }
        else {
            prefs.putString("imgDownload", IMAGE_ON_UPDATE);
        }
        prefs.putBoolean("allowDoubleTap", allowDoubleTap);
        switch (doubleTapAction.getActionID()) {
        case PlayPauseToggle.ID: {
            prefs.putString("doubleTapAction", ACTION_PLAY_PAUSE);
            break;
            }
        case NextTrack.ID: {
            prefs.putString("doubleTapAction", ACTION_PLAY_PAUSE);
            break;
        }
        default:
            Log.e(TAG, "Unsupported action - " + doubleTapAction.getActionID());
        }
        
        if (context instanceof Activity) {
            updateCurrentSettings((Activity) context);
        }
    }
}
