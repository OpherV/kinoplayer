package com.android.kino.logic.settings;

import java.util.Collection;

import com.android.kino.logic.action.KinoAction;

public interface SettingsContainer {

    public String getName();

    public KinoAction getConfiguredAction(int eventId);

    /**
     * @param event TODO An event object because some events may require parameters
     * @param actionId TODO An action ID because an action is predefined - might be changed in the future
     */
    public void setConfiguredAction(int eventId, int actionId);

    public Collection<Integer> getConfiguredEvents();
    
    public boolean isConfigured(int eventId);
    
}
