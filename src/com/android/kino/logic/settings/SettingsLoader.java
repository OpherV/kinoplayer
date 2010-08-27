package com.android.kino.logic.settings;

import java.util.HashMap;
import java.util.Map;

import com.android.kino.logic.action.PlayPauseToggle;
import com.android.kino.logic.event.DoubleTapEvent;

public abstract class SettingsLoader {
    
    private static SettingsContainer mDefaultSettings = null;
    private static Map<String, SettingsProfile> mUserProfiles =
        new HashMap<String, SettingsProfile>();
    
    public static SettingsContainer loadDefaultSettings() {
        if (mDefaultSettings != null) {
            return mDefaultSettings;
        }
        // TODO(implement) Load setting from DB or something - currently use hardcoded default settings
        SettingsContainer defaultSettings = new DefaultSettings();
        defaultSettings.setConfiguredAction(DoubleTapEvent.ID, PlayPauseToggle.ID);
        return null;
    }
    
    public static SettingsContainer loadProfile(String profileName) {
        if (mUserProfiles.containsKey(profileName)) {
            return mUserProfiles.get(profileName);
        }
        // TODO(implement) Load setting from DB or something
        return null;
    }
}
