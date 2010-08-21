package com.android.kino.logic;

import com.android.kino.logic.action.KinoAction;
import com.android.kino.logic.action.PlayPauseToggle;
import com.android.kino.logic.event.DoubleTapEvent;
import com.android.kino.logic.event.InputEvent;

/**
 * Receives input from the input interceptors and communicates it to the media
 * player.
 */
public class InputEventTranslator {
    private KinoMediaPlayer mPlayer;
    
    public InputEventTranslator(KinoMediaPlayer player) {
        mPlayer = player;
    }
    
    /**
     * Call this method to pass input to the translator.
     * The translator will perform the appropriate action.
     * 
     * @param event The event to handle.
     */
    public void handleInput(InputEvent event) {
        KinoAction action = null;

        // TODO: Use the configuration to get the KinoAction - for now it's
        //       a static mapping
        switch (event.getEventID()) {
        case DoubleTapEvent.ID: {
            action = new PlayPauseToggle(mPlayer);
        }
        }
        
        if (action != null) {
            action.performAction();
        }
    }
}
