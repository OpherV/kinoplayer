package com.android.kino.logic.settings;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import android.util.Log;

import com.android.kino.logic.action.ActionFactory;
import com.android.kino.logic.action.KinoAction;

/**
 * A default settings container contains a mapping of supported events to their
 * desired action.
 * For example, it is possible to configure that shaking the device will cause
 * the playlist to shuffle if ShakeEvent is mapped to ShufflePlaylist.
 */
public class DefaultSettings implements SettingsContainer {
    
    private String TAG = this.toString();
    private Map<Integer, Integer> mSettings;
    private Map<Setting, String> mStringSettings;
    private Map<Setting, Boolean> mBooleanSettings;
    
    public DefaultSettings() {
        this(null);
    }
    
    public DefaultSettings(Map<Integer, Integer> settings) {
        mSettings = new HashMap<Integer, Integer>();
        if (settings != null) {
            // Add settings one by one rather than assign - we don't want to
            // change the given container
            for (Entry<Integer, Integer> entry : settings.entrySet()) {
                setConfiguredAction(entry.getKey(), entry.getValue());
            }
        }
        mStringSettings = new HashMap<Setting, String>();
        mBooleanSettings = new HashMap<Setting, Boolean>();
    }

    @Override
    public String getName() {
        return "Default";
    }

    @Override
    public String getConfiguredString(Setting setting) {
        Log.d(TAG, "Got " + mStringSettings.get(setting) + " of " + setting);
        return mStringSettings.get(setting);
    }

    @Override
    public void setConfiguredString(Setting setting, String value) {
        Log.d(TAG, "Setting " + setting + " to " + value);
        mStringSettings.put(setting, value);
    }

    @Override
    public boolean getConfiguredBoolean(Setting setting) {
        Log.d(TAG, "Got " + mBooleanSettings.get(setting) + " of " + setting);
        return mBooleanSettings.get(setting);
    }

    @Override
    public void setConfiguredBoolean(Setting setting, boolean value) {
        Log.d(TAG, "Setting " + setting + " to " + value);
        mBooleanSettings.put(setting, value);
    }

    @Override
    public KinoAction getConfiguredAction(int eventId) {
        Log.d(TAG, "Got action " + mSettings.get(eventId) + " of event " + eventId);
        return ActionFactory.create(mSettings.get(eventId));
    }

    @Override
    public void setConfiguredAction(int eventId, int actionId) {
        Log.d(TAG, "Setting action " + actionId + " to event " + eventId);
        mSettings.put(eventId, actionId);
    }

    @Override
    public Collection<Integer> getConfiguredEvents() {
        return mSettings.keySet();
    }

    @Override
    public boolean isConfigured(int eventId) {
        return mSettings.containsKey(eventId);
    }

    @Override
    public boolean isConfigured(Setting setting) {
        return mStringSettings.containsKey(setting) || mBooleanSettings.containsKey(setting);
    }

}
