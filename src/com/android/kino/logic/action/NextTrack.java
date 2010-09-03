package com.android.kino.logic.action;

import com.android.kino.logic.KinoMediaPlayer;

/**
 * Toggles the playback between play and pause.
 */
public class NextTrack extends BaseKinoAction {
    public static final int ID = 2;
    public static final CharSequence NAME = "Next Track";
    public static final CharSequence DESCRIPTION =
        "Play next track";
    
    /* (non-Javadoc)
     * @see com.android.kino.logic.action.KinoAction#performAction(com.android.kino.logic.KinoMediaPlayer)
     */
    @Override
    public void performAction(KinoMediaPlayer player) {
        player.next();
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
