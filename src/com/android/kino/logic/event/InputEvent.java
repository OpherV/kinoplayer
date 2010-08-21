package com.android.kino.logic.event;

/**
 * An interface to represent different types of input.
 */
public interface InputEvent {
    /**
     * A unique event ID.
     */
    public int getEventID();
    
    /**
     * The name of the event.
     * To be used in the settings UI.
     */
    public CharSequence getEventName();
    
    /**
     * Description to use in a tooltip, for example.
     */
    public CharSequence getEventDescription();
}
