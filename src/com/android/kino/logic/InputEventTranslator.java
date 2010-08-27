package com.android.kino.logic;

import com.android.kino.logic.action.KinoAction;
import com.android.kino.logic.settings.SettingsContainer;
import com.android.kino.logic.settings.SettingsLoader;

/**
 * Receives input from the input interceptors and communicates it to the media
 * player.
 */
public class InputEventTranslator {
    private KinoMediaPlayer mPlayer;
    private SettingsContainer mSettings;
    
    public InputEventTranslator(KinoMediaPlayer player) {
        mPlayer = player;
        mSettings = SettingsLoader.loadDefaultSettings();
    }
    
    public void setSettings(SettingsContainer settings) {
        mSettings = settings;
    }
    
    /**
     * Call this method to pass input to the translator.
     * The translator will perform the appropriate action.
     * 
     * @param eventId The event to handle.
     */
    public void handleInput(int eventId) {
        KinoAction action = mSettings.getConfiguredAction(eventId);
        
        if (action != null) {
            action.performAction(mPlayer);
        }
    }
}
