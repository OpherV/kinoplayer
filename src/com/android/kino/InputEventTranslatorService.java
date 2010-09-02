package com.android.kino;

import java.util.LinkedList;
import java.util.List;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.android.kino.logic.InputEventTranslator;
import com.android.kino.logic.KinoMediaPlayer;
import com.android.kino.logic.KinoServiceConnection;
import com.android.kino.logic.ServiceUser;
import com.android.kino.logic.interceptor.DoubleTapInterceptor;
import com.android.kino.logic.interceptor.InputEventInterceptor;

/**
 * This service is only used to contain the InputEventTranslator running in the
 * background.
 * To use it, you need to bind to this service and get it from the binder.
 */
public class InputEventTranslatorService extends Service implements ServiceUser {
    
    // This is the object that receives interactions from clients
    private final IBinder mBinder = new IETBinder();
    private InputEventTranslator mInputTranslator = null;
    private KinoServiceConnection mMediaPlayerConn = new KinoServiceConnection(this);
    
    private List<InputEventInterceptor> mInterceptors = new LinkedList<InputEventInterceptor>();

    
    /* (non-Javadoc)
     * @see android.app.Service#onCreate()
     */
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(getClass().getName(), "InputEventTranslatorService.onCreate");

        // Bind to media player service and get media player
        boolean success = bindService(
                new Intent(this, MediaPlayerService.class),
                mMediaPlayerConn,
                Context.BIND_AUTO_CREATE);
        if (!success) {
            // TODO: Do something about this... failed to bind to media player
            return;
        }
        
        mInterceptors.add(new DoubleTapInterceptor());
    }

    /* (non-Javadoc)
     * @see com.android.kino.logic.ServiceUser#onConnected(android.os.IBinder)
     */
    @Override
    public void onConnected(IBinder binder) {
        if (binder == null) {
            Log.d(getClass().getName(), "Failed to get media player binder");
            return;
        }
        if (mInputTranslator == null) {
            KinoMediaPlayer player =
                ((MediaPlayerService.MPBinder) binder).getPlayer();
            mInputTranslator = new InputEventTranslator(player);
            for (InputEventInterceptor interceptor : mInterceptors) {
                interceptor.setListener(mInputTranslator);
                interceptor.startIntercepting();
            }
        }
        Log.d(getClass().getName(), "InputEventTranslatorService bound to media player");
    }

    /* (non-Javadoc)
     * @see android.app.Service#onDestroy()
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        for (InputEventInterceptor interceptor : mInterceptors) {
            interceptor.stopIntercepting();
        }
        doUnbindMediaPlayerService();
        Log.d(getClass().getName(), "InputEventTranslatorService.onDestroy");
    }

    /* (non-Javadoc)
     * @see android.app.Service#onStartCommand(android.content.Intent, int, int)
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(getClass().getName(), "InputEventTranslatorService.onStartCommand");
        Log.i("InputEventTranslatorService", "Received start id " + startId + ": " + intent);
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }
    
    /* (non-Javadoc)
     * @see android.app.Service#onBind(android.content.Intent)
     */
    @Override
    public IBinder onBind(Intent arg0) {
        Log.d(getClass().getName(), "InputEventTranslatorService.onBind");
        return mBinder;
    }

    /* (non-Javadoc)
     * @see android.app.Service#onRebind(android.content.Intent)
     */
    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        Log.d(getClass().getName(), "InputEventTranslatorService.onRebind");
    }

    /* (non-Javadoc)
     * @see android.app.Service#onUnbind(android.content.Intent)
     */
    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(getClass().getName(), "InputEventTranslatorService.onUnbind");
        return super.onUnbind(intent);
    }

    /**
     * Disconnects from the media player service.
     */
    private void doUnbindMediaPlayerService() {
        if (isBoundToMediaPlayer()) {
            // Detach our existing connection.
            unbindService(mMediaPlayerConn);
            mInputTranslator = null;
        }
    }
    
    private boolean isBoundToMediaPlayer() {
        return mInputTranslator != null;
    }
    
    
    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class IETBinder extends Binder {
        public InputEventTranslator getInputTranslator() {
            return mInputTranslator;
        }
    }

}
