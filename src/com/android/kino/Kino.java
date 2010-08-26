package com.android.kino;

import com.android.kino.logic.KinoMediaPlayer;
import com.android.kino.logic.KinoServiceConnection;
import com.android.kino.logic.ServiceUser;
import com.android.kino.musiclibrary.Library;
import com.android.kino.musiclibrary.LibraryConnector;
import com.android.kino.ui.MenuMain;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

/**
 * The starting activity (that's why it's not in the UI package).
 */
public class Kino extends Activity implements ServiceUser {

    private KinoServiceConnection mMediaPlayerConn = new KinoServiceConnection(this);
    private KinoMediaPlayer mPlayer = null;
    private Library library = null;

    /**
     * Entry point of the application.
     * Starts the media player service and the input event translator service.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);                       
        // Ignores the 'savedInstanceState' - the state will be restored by
        // onRestoreInstanceState.        
        Toast.makeText(this, "Kino.onCreate", Toast.LENGTH_SHORT).show();

        // Create media player service and get media player
        Object result = startService(new Intent(this, MediaPlayerService.class));
        if (result == null) {
            Toast.makeText(this, "Failed to start media player service", Toast.LENGTH_SHORT).show();
            return;
        }
        
        boolean success = bindService(
                new Intent(this, MediaPlayerService.class),
                mMediaPlayerConn,
                Context.BIND_AUTO_CREATE);
        if (!success) {
            Toast.makeText(this, "Failed to bind to media player service", Toast.LENGTH_SHORT).show();
            // TODO: Do something about this... failed to bind to media player
            return;
        }

        // Create input event translator service
        result = startService(new Intent(this, InputEventTranslatorService.class));
        if (result == null) {
            Toast.makeText(this, "Failed to start input event translator service", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, "Kino started OK", Toast.LENGTH_SHORT).show();
        
        if (savedInstanceState != null) {
            return;
        }
        
        //bind the library service
        boolean libraryBound = bindService(new Intent(this, Library.class), new LibraryConnector(this) , Context.BIND_AUTO_CREATE);
        Log.d(this.getClass().toString(), "libraryBound: "+libraryBound);
        
        
        //start the UI thread
        startActivity(new Intent(this, MenuMain.class));
               
        
    }

    /* (non-Javadoc)
     * @see com.android.kino.logic.ServiceUser#onConnected(android.os.IBinder)
     */
    @Override
    public void onConnected(IBinder binder) {
        if (binder == null) {
        	
        	//TODO now that we have several services this is not accurate
            Toast.makeText(this, "Failed to get media player binder", Toast.LENGTH_LONG).show();
            return;
        }
        
        //switch between the different services
        if (binder.getClass()==MediaPlayerService.class)
        {
        	mPlayer = ((MediaPlayerService.MPBinder) binder).getPlayer();
        }
        else if (binder.getClass()==Library.class){
        	library = (Library) binder;
        }
    }
    
    /* (non-Javadoc)
     * @see android.app.Activity#onDestroy()
     */
    @Override
    protected void onDestroy() {
        Toast.makeText(this, "Kino.onDestroy", Toast.LENGTH_SHORT).show();
        super.onDestroy();
        doUnbindMediaPlayerService();
    }

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        /*
         * TODO: Save the state - maybe the current screen?
         * // Save UI state changes to the savedInstanceState.
         * // This bundle will be passed to onCreate if the process is
         * // killed and restarted.
         * savedInstanceState.putBoolean("MyBoolean", true);
         * savedInstanceState.putDouble("myDouble", 1.9);
         * savedInstanceState.putInt("MyInt", 1);
         * savedInstanceState.putString("MyString",
         * "Welcome back to Android");
         * // etc.
         */
        super.onSaveInstanceState(savedInstanceState);
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onRestoreInstanceState(android.os.Bundle)
     */
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState == null) {
            return;
        }
        /*
         * TODO: Restore the state - maybe the last screen?
         * // Restore UI state from the savedInstanceState.
         * // This bundle has also been passed to onCreate.
         * boolean myBoolean = savedInstanceState.getBoolean("MyBoolean");
         * double myDouble = savedInstanceState.getDouble("myDouble");
         * int myInt = savedInstanceState.getInt("MyInt");
         * String myString = savedInstanceState.getString("MyString");
         */
    }

    /**
     * Shuts down the services.
     * TODO: Use this in some 'ShutDown' button
     */
    private void shutDown() {
        stopService(new Intent(this, InputEventTranslatorService.class));
        stopService(new Intent(this, Library.class));
        stopService(new Intent(this, MediaPlayerService.class));
    }

    /**
     * Disconnects from the media player service.
     */
    private void doUnbindMediaPlayerService() {
        if (isBoundToMediaPlayer()) {
            // Detach our existing connection.
            unbindService(mMediaPlayerConn);
            mPlayer = null;
        }
    }
    
    private boolean isBoundToMediaPlayer() {
        return mPlayer != null;
    }
}