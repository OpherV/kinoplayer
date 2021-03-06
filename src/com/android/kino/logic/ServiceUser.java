package com.android.kino.logic;

import android.os.IBinder;

/**
 * Used together with {@link KinoServiceConnection}.
 */
public interface ServiceUser {
    /**
     * Called when the connection to the service is established.
     * 
     * @param binder The binder from the service.
     */
    public void onConnected(IBinder binder);
}
