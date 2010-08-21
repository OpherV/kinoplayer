package com.android.kino.logic;


import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

/**
 * Used to bind to services. The service user will have it's onConnected
 * callback called when the service is connected.
 */
public class KinoServiceConnection implements ServiceConnection {
    
    private IBinder mBinder;
    private ServiceUser mConnectionUser;
    
    /**
     * @param connectionUser Usually 'this' - the object that creates this
     *     connection.
     */
    public KinoServiceConnection(ServiceUser connectionUser) {
        mConnectionUser = connectionUser;
    }
    
    /**
     * Should be used only after 'onConnected' was called.
     */
    public IBinder getBinder() {
        return mBinder;
    }

    /**
     * This is called when the connection with the service has been established,
     * giving us the service object we can use to interact with the service.
     */
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        mBinder = service;
        mConnectionUser.onConnected(mBinder);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        mBinder = null;
    }

}
