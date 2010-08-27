package com.android.kino.logic.interceptor;

import android.content.BroadcastReceiver;

import com.android.kino.logic.InputEventTranslator;

public abstract class InterceptorBase extends BroadcastReceiver {
    private InputEventTranslator mInputTranslator;
    private int mEventId;
    
    public InterceptorBase(InputEventTranslator inputTranslator, int eventId) {
        mInputTranslator = inputTranslator;
        mEventId = eventId;
    }
    
    /**
     * Uses the default event.
     */
    protected void eventTriggered() {
        eventTriggered(mEventId);
    }
    
    /**
     * Allows using several events.
     */
    protected void eventTriggered(int eventId) {
        mInputTranslator.handleInput(eventId);
    }
}
