package com.android.kino.logic.action;

import com.android.kino.logic.KinoMediaPlayer;

/**
 * Toggles the playback between play and pause.
 */
public class PlayPauseToggle extends BaseKinoAction {
    public static final int ID = 1;
    public static final CharSequence NAME = "Play/Pause";
    public static final CharSequence DESCRIPTION =
        "Toggle betweem play and pause";
    
    /* (non-Javadoc)
     * @see com.android.kino.logic.action.KinoAction#performAction(com.android.kino.logic.KinoMediaPlayer)
     */
    @Override
    public void performAction(KinoMediaPlayer player) {
        player.togglePlayPause();
    }

    /* (non-Javadoc)
     * @see com.android.kino.logic.action.KinoAction#getActionID()
     */
    @Override
    public int getActionID() {
        return ID;
    }

    /* (non-Javadoc)
     * @see com.android.kino.logic.action.KinoAction#getActionName()
     */
    @Override
    public CharSequence getActionName() {
        return NAME;
    }

    /* (non-Javadoc)
     * @see com.android.kino.logic.action.KinoAction#getActionDescription()
     */
    @Override
    public CharSequence getActionDescription() {
        return DESCRIPTION;
    }

}
