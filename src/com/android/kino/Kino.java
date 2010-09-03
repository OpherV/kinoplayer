package com.android.kino;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.android.kino.logic.InputEventTranslator;
import com.android.kino.logic.KinoMediaPlayer;
import com.android.kino.logic.KinoServiceConnection;
import com.android.kino.logic.KinoUser;
import com.android.kino.logic.ServiceUser;
import com.android.kino.logic.TaskMasterService;
import com.android.kino.musiclibrary.Library;

/**
 * The starting activity (that's why it's not in the UI package).
 */
public class Kino extends Application implements ServiceUser {

    private KinoServiceConnection mMediaPlayerConn = new KinoServiceConnection(this);
    private KinoServiceConnection mInputTranslatorConn = new KinoServiceConnection(this);
    private KinoServiceConnection mLibraryConn = new KinoServiceConnection(this);
    private KinoServiceConnection mTaskMasterConn = new KinoServiceConnection(this);
    private KinoMediaPlayer mPlayer = null;
    private InputEventTranslator mInputTranslator = null;
    private Library mLibrary = null;
    private TaskMasterService mTaskMaster = null;
    
    private List<KinoUser> mUsers = new LinkedList<KinoUser>();
    private boolean mIsInitialized = false;
    public final static String ALBUM_DIR="kino/images/albums";
    public final static String ARTIST_DIR="kino/images/artists";
	public static final String KINODIR = "kino";
    
    public static Kino getKino(Activity activity) {
        return (Kino)activity.getApplication();
    }
    
    public KinoMediaPlayer getPlayer() {
        return mPlayer;
    }
    
    public InputEventTranslator getInputTranslator() {
        return mInputTranslator;
    }
    
    public Library getLibrary() {
    	if (mLibrary==null){
    		Log.e("KINO","WARNING! Trying to fetch library when Kino is not up!");
    	}
        return mLibrary;
    }
    
    public TaskMasterService getTaskMaster() {
    	if (mTaskMaster==null){
    		Log.e("KINO","WARNING! Trying to fetch taskmaster when Kino is not up!");
    	}
        return mTaskMaster;
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
                BIND_AUTO_CREATE);
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
        
        success = bindService(
                new Intent(this, InputEventTranslatorService.class),
                mInputTranslatorConn,
                BIND_AUTO_CREATE);
        if (!success) {
            Log.e(getClass().getName(), "Failed to bind to input event translator service");
            // TODO: Do something about this... failed to bind to input event translator
            return;
        }
        Log.d(getClass().getName(), "Kino started OK");
        
        //bind the library service
        boolean libraryBound = bindService(new Intent(this, Library.class), mLibraryConn , Context.BIND_AUTO_CREATE);
        Log.d(this.getClass().toString(), "libraryBound from Kino: "+libraryBound);
             
        
        startService(new Intent(this, TaskMasterService.class));
        boolean taskmasterBound =  bindService(new Intent(this, TaskMasterService.class), mTaskMasterConn , Context.BIND_AUTO_CREATE);
        Log.d(this.getClass().toString(), "taskMaster bound from Kino: "+taskmasterBound);
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
        else if (binder instanceof InputEventTranslatorService.IETBinder) {
            mInputTranslator = ((InputEventTranslatorService.IETBinder) binder).getInputTranslator();
        }
        else if (binder instanceof Library.LibraryBinder){
        	mLibrary = ((Library.LibraryBinder) binder).getLibrary();
        }
        else if (binder instanceof TaskMasterService.TaskMasterBinder){
        	mTaskMaster = ((TaskMasterService.TaskMasterBinder) binder).getTaskMaster();
        }
        mIsInitialized = mPlayer != null && mInputTranslator != null && mLibrary != null && mTaskMaster != null;
        if (mIsInitialized) {
            for (KinoUser user : mUsers) {
                user.onKinoInit(this);
            }
        }
    }
    
    @Override
    public void onTerminate() {
        Log.d(getClass().getName(), "Kino.onDestroy");
        doUnbindMediaPlayerService();
        super.onTerminate();
    }

    public void showNotification() {
        ((MediaPlayerService.MPBinder)mMediaPlayerConn.getBinder()).showNotification();
    }

    public void registerUser(KinoUser user) {
        if (mIsInitialized) {
            user.onKinoInit(this);
        }
        mUsers.add(user);
    }

    /**
     * Shuts down the services.
     * TODO: Use this in some 'ShutDown' button
     */
    public void shutDown() {
        stopService(new Intent(this, InputEventTranslatorService.class));
        stopService(new Intent(this, MediaPlayerService.class));
        stopService(new Intent(this, Library.class));
        stopService(new Intent(this, TaskMasterService.class));        
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
        if (isBoundToInputTranslator()) {
            unbindService(mInputTranslatorConn);
            mInputTranslatorConn = null;
        }
        if (isBoundToLibrary()) {
            unbindService(mLibraryConn);
            mLibrary = null;
        }        
        if (isBoundToTaskMaster()) {
            unbindService(mTaskMasterConn);
            mTaskMaster = null;
        }
    }
    
    private boolean isBoundToMediaPlayer() {
        return mPlayer != null;
    }
    
    private boolean isBoundToInputTranslator() {
        return mInputTranslatorConn != null;
    }
    
    private boolean isBoundToLibrary() {
        return mLibrary != null;
    }
    
    private boolean isBoundToTaskMaster() {
        return mTaskMaster != null;
    }
}