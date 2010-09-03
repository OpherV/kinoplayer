package com.android.kino.logic.settings;

import java.util.Collection;

import com.android.kino.logic.action.KinoAction;

public interface SettingsContainer {
    
    public enum Setting {
        MEDIA_DIRECTORY,
        LAZY_LOAD_IMAGES,
        ENABLE_DOUBLE_TAP,
        DOUBLE_TAP_ACTION
    };

    public String getName();
    
    /**
     * Get a string assigned to a setting. For example, the path of the media
     * directory.
     */
    public String getConfiguredString(Setting setting);
    
    /**
     * Assign a string to a setting. For example, the path of the media
     * directory.
     */
    public void setConfiguredString(Setting setting, String value);
    
    /**
     * Get a boolean assigned to a setting.
     */
    public boolean getConfiguredBoolean(Setting setting);
    
    /**
     * Assign a boolean to a setting.
     */
    public void setConfiguredBoolean(Setting setting, boolean value);

    public KinoAction getConfiguredAction(int eventId);

    /**
     * @param event TODO An event object because some events may require parameters
     * @param actionId TODO An action ID because an action is predefined - might be changed in the future
     */
    public void setConfiguredAction(int eventId, int actionId);

    public Collection<Integer> getConfiguredEvents();
    
    public boolean isConfigured(int eventId);
    
    public boolean isConfigured(Setting setting);
    
}
