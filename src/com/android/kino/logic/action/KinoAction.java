package com.android.kino.logic.action;

import com.android.kino.logic.KinoMediaPlayer;

/**
 * An action that manipulates the playback in some way.
 */
public interface KinoAction extends Comparable<KinoAction> {
    
    /**
     * Performs the action.
     * @param player The media player to act on.
     */
    public void performAction(KinoMediaPlayer player);
    
    /**
     * A unique action ID.
     */
    public int getActionID();
    
    /**
     * The name of the action.
     * To be used in the settings UI.
     */
    public CharSequence getActionName();
    
    /**
     * Description to use in a tooltip, for example.
     */
    public CharSequence getActionDescription();
    
    public boolean equals(KinoAction other);
}
