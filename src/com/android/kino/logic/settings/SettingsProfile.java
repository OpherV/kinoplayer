package com.android.kino.logic.settings;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

import com.android.kino.logic.action.KinoAction;

/**
 * A settings profile contains a mapping of supported events to their desired
 * action and a default mapping to use in case some event is not mapped.
 */
public class SettingsProfile extends DefaultSettings {
    
    private String mName;
    private SettingsContainer mDefaults;
    
    public SettingsProfile(SettingsContainer defaultSettings, String profileName) {
        this(defaultSettings, profileName, null);
    }
    
    public SettingsProfile(SettingsContainer defaultSettings, String profileName, Map<Integer, Integer> settings) {
        super(settings);
        mName = profileName;
        mDefaults = defaultSettings;
    }

    @Override
    public String getName() {
        return mName;
    }

    @Override
    public KinoAction getConfiguredAction(int eventId) {
        if (super.isConfigured(eventId)) {
            return super.getConfiguredAction(eventId);
        }
        return mDefaults.getConfiguredAction(eventId);
    }

    @Override
    public Collection<Integer> getConfiguredEvents() {
        Collection<Integer> profileSet = super.getConfiguredEvents();
        Collection<Integer> defaultSet = mDefaults.getConfiguredEvents();
        // Join collections
        Collection<Integer> result = new LinkedList<Integer>(profileSet);
        for (Integer event : defaultSet) {
            if (!result.contains(event)) {
                result.add(event);
            }
        }
        return result;
    }

    @Override
    public boolean isConfigured(int eventId) {
        return super.isConfigured(eventId) || mDefaults.isConfigured(eventId);
    }

}
