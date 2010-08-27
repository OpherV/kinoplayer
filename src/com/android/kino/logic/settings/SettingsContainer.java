package com.android.kino.logic.settings;

import java.util.Collection;

import com.android.kino.logic.action.KinoAction;

public interface SettingsContainer {

    public String getName();

    public KinoAction getConfiguredAction(int eventId);

    public void setConfiguredAction(int eventId, int actionId);

    /**
     * TODO(remember) To be used to populate settings window.
     */
    public Collection<Integer> getConfiguredEvents();
    
    public boolean isConfigured(int eventId);
    
}
