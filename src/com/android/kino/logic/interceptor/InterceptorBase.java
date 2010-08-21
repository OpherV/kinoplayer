package com.android.kino.logic.interceptor;

import android.content.BroadcastReceiver;

import com.android.kino.logic.InputEventTranslator;
import com.android.kino.logic.event.InputEvent;

public abstract class InterceptorBase extends BroadcastReceiver {
    private InputEventTranslator mInputTranslator;
    private InputEvent mInputEvent;
    
    public InterceptorBase(InputEventTranslator inputTranslator, InputEvent inputEvent) {
        mInputTranslator = inputTranslator;
        mInputEvent = inputEvent;
    }
    
    /**
     * Uses the default event.
     */
    protected void eventTriggered() {
        eventTriggered(mInputEvent);
    }
    
    /**
     * Allows using several events.
     */
    protected void eventTriggered(InputEvent inputEvent) {
        mInputTranslator.handleInput(inputEvent);
    }
}
