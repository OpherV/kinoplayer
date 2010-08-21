package com.android.kino.logic.action;

import com.android.kino.logic.KinoMediaPlayer;

/**
 * Toggles the playback between play and pause.
 */
public class PlayPauseToggle implements KinoAction {

    private KinoMediaPlayer mPlayer;
    
    public PlayPauseToggle(KinoMediaPlayer player) {
        mPlayer = player;
    }
    
    /* (non-Javadoc)
     * @see com.android.kino.logic.actions.KinoAction#performAction()
     */
    @Override
    public void performAction() {
        mPlayer.togglePlayPause();
    }

}
