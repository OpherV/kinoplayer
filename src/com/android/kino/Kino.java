package com.android.kino;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.android.kino.logic.KinoMediaPlayer;
import com.android.kino.logic.KinoServiceConnection;
import com.android.kino.logic.ServiceUser;
import com.android.kino.musiclibrary.Library;

/**
 * The starting activity (that's why it's not in the UI package).
 */
public class Kino extends Application implements ServiceUser {

    private KinoServiceConnection mMediaPlayerConn = new KinoServiceConnection(this);
    private KinoServiceConnection mLibraryConn = new KinoServiceConnection(this);
    private KinoMediaPlayer mPlayer = null;
    private Library mLibrary = null;
    
    public static Kino getKino(Activity activity) {
        return (Kino)activity.getApplication();
    }
    
    public KinoMediaPlayer getPlayer() {
        return mPlayer;
    }
    
    public Library getLibrary() {
        return mLibrary;
    }

    /**
     * Entry point of the application.
     * Starts the media player service and the input event translator service.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        // Ignores the 'savedInstanceState' - the state will be restored by
        // onRestoreInstanceState.        
        Log.d(getClass().getName(), "Kino.onCreate");

        // Create media player service and get media player
        Object result = startService(new Intent(this, MediaPlayerService.class));
        if (result == null) {
            Log.e(getClass().getName(), "Failed to start media player service");
            return;
        }
        
        boolean success = bindService(
                new Intent(this, MediaPlayerService.class),
                mMediaPlayerConn,
                Context.BIND_AUTO_CREATE);
        if (!success) {
            Log.e(getClass().getName(), "Failed to bind to media player service");
            // TODO: Do something about this... failed to bind to media player
            return;
        }

        // Create input event translator service
        result = startService(new Intent(this, InputEventTranslatorService.class));
        if (result == null) {
            Log.e(getClass().getName(), "Failed to start input event translator service");
            return;
        }
        Log.d(getClass().getName(), "Kino started OK");
        
        //bind the library service
        boolean libraryBound = bindService(new Intent(this, Library.class), mLibraryConn , Context.BIND_AUTO_CREATE);
        Log.d(this.getClass().toString(), "libraryBound from Kino: "+libraryBound);
    }

    /* (non-Javadoc)
     * @see com.android.kino.logic.ServiceUser#onConnected(android.os.IBinder)
     */
    @Override
    public void onConnected(IBinder binder) {
        if (binder == null) {
            Log.e(getClass().getName(), "Failed to get binder");
            return;
        }
        
        //switch between the different services
        if (binder instanceof MediaPlayerService.MPBinder) {
        	mPlayer = ((MediaPlayerService.MPBinder) binder).getPlayer();
        }
        else if (binder instanceof Library.LibraryBinder){
        	mLibrary = ((Library.LibraryBinder) binder).getLibrary();
        }
    }
    
    @Override
    public void onTerminate() {
        Log.d(getClass().getName(), "Kino.onDestroy");
        doUnbindMediaPlayerService();
        super.onTerminate();
    }

    /**
     * Shuts down the services.
     * TODO: Use this in some 'ShutDown' button
     */
    public void shutDown() {
        stopService(new Intent(this, InputEventTranslatorService.class));
        stopService(new Intent(this, MediaPlayerService.class));
    }

    /**
     * Disconnects from the media player service.
     */
    private void doUnbindMediaPlayerService() {
        // Detach our existing connections
        if (isBoundToMediaPlayer()) {
            unbindService(mMediaPlayerConn);
            mPlayer = null;
        }
        if (isBoundToLibrary()) {
            unbindService(mLibraryConn);
            mLibrary = null;
        }
    }
    
    private boolean isBoundToMediaPlayer() {
        return mPlayer != null;
    }
    
    private boolean isBoundToLibrary() {
        return mLibrary != null;
    }
}