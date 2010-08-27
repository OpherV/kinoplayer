package com.android.kino;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.android.kino.logic.KinoMediaPlayer;

/**
 * This service is only used to contain the KinoMediaPlayer running in the
 * background.
 * To use it, you need to bind to this service and get it from the binder.
 */
public class MediaPlayerService extends Service{
    
    // This is the object that receives interactions from clients
    private final IBinder mBinder = new MPBinder();
    
    private NotificationManager mNotificationMngr;
    private KinoMediaPlayer mMediaPlayer = null;
    
    /* (non-Javadoc)
     * @see android.app.Service#onCreate()
     */
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(getClass().getName(), "MediaPlayerService.onCreate");
        if (mMediaPlayer == null) {
            mMediaPlayer = new KinoMediaPlayer();
        }
        
        mNotificationMngr = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        // Display a notification about us starting. We put an icon in the status bar.
        showNotification();               
    }

    /* (non-Javadoc)
     * @see android.app.Service#onDestroy()
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(getClass().getName(), "MediaPlayerService.onDestroy");
    }

    /* (non-Javadoc)
     * @see android.app.Service#onStartCommand(android.content.Intent, int, int)
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(getClass().getName(), "MediaPlayerService.onStartCommand");
        Log.i("MediaPlayerService", "Received start id " + startId + ": " + intent);
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }
    
    /* (non-Javadoc)
     * @see android.app.Service#onBind(android.content.Intent)
     */
    @Override
    public IBinder onBind(Intent arg0) {
        Log.d(getClass().getName(), "MediaPlayerService.onBind");
        return mBinder;
    }

    /* (non-Javadoc)
     * @see android.app.Service#onRebind(android.content.Intent)
     */
    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        Log.d(getClass().getName(), "MediaPlayerService.onRebind");
    }

    /* (non-Javadoc)
     * @see android.app.Service#onUnbind(android.content.Intent)
     */
    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(getClass().getName(), "MediaPlayerService.onUnbind");
        return super.onUnbind(intent);
    }

    
    /**
     * Show a notification while this service is running.
     */
    private void showNotification() {
        CharSequence tickerText = getText(R.string.mp_service_started);

        // Set the icon, scrolling text and timestamp
        Notification notification = new Notification(R.drawable.icon, tickerText,
                System.currentTimeMillis());

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, Kino.class), 0);
        
        CharSequence notificationText = getText(R.string.mp_service_notification);

        // Set the info for the views that show in the notification panel.
        notification.setLatestEventInfo(this, getText(R.string.mp_service_label),
                notificationText, contentIntent);

        // Send the notification.
        // We use a layout id because it is a unique number.  We use it later to cancel.
        mNotificationMngr.notify(R.string.mp_service_notification, notification);
    }
    
    
    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class MPBinder extends Binder {
        public KinoMediaPlayer getPlayer() {
            return mMediaPlayer;
        }
    }

}
