package com.android.kino.logic;

import com.android.kino.logic.action.KinoAction;
import com.android.kino.logic.interceptor.InputEventListener;
import com.android.kino.logic.settings.SettingsLoader;

/**
 * Receives input from the input interceptors and communicates it to the media
 * player.
 */
public class InputEventTranslator implements InputEventListener {
    private KinoMediaPlayer mPlayer;
    
    public InputEventTranslator(KinoMediaPlayer player) {
        mPlayer = player;
    }
    
    /**
     * Call this method to pass input to the translator.
     * The translator will perform the appropriate action.
     * 
     * @param eventId The event to handle.
     */
    @Override
    public void onEventTriggered(int eventId) {
        KinoAction action = SettingsLoader.loadCurrentSettings().getConfiguredAction(eventId);
        
        if (action != null) {
            action.performAction(mPlayer);
        }
    }
}
