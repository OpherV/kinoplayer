package com.android.kino.logic;

import java.util.Iterator;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.util.Log;

/**
 * Contains all the capabilities of media playback.
 */
public class KinoMediaPlayer implements OnErrorListener, OnCompletionListener {
    // TODO: Implement
    private MediaPlayer mMp;
    private Playlist mPlaylist = null;
    private Iterator<MediaProperties> mPlayOrder = null;
    private MediaProperties mCurrentTrack = null;
    
    public KinoMediaPlayer() {
        mMp = new MediaPlayer();
        mMp.setOnErrorListener(this);
        mMp.setOnCompletionListener(this);
    }
    
    public void setCurrentPlaylist(Playlist playlist) {
        mPlaylist = playlist;
    }
    
    public Playlist getCurrentPlaylist() {
        return mPlaylist;
    }
    
    /**
     * Sets the current media to play.
     * You should usually use setCurrentPlaylist.
     * Doesn't actually starts the playback.
     * (To delete the current playlist you should call setCurrentPlaylist(null))
     * 
     * @param media The media to use.
     * 
     * @return <code>true</code> if successful.
     */
    public boolean setCurrentMedia(MediaProperties media) {
        String filename = media.Filename;
        try {
            mMp.setDataSource(filename);
            mMp.prepare();
        }
        catch (Exception e) {
            // Failed loading file
            mMp.reset();
            mCurrentTrack = null;
            return false;
        }
        mCurrentTrack = media;
        return true;
    }

    /**
     * Toggles between playing and pausing the current song.
     * If there is no song to play, does nothing.
     */
    public void togglePlayPause() {
        if (mCurrentTrack == null) {
            if (!setNextSong()) {
                // No song to play - do nothing
                return;
            }
        }
        if (mMp.isPlaying()) {
            mMp.pause();
        }
        else {
            mMp.start();
        }
    }

    private boolean setNextSong() {
        if (mPlaylist != null) {
            if (mPlayOrder == null) {
                // TODO Get playing iterator according to settings or something
                mPlayOrder = mPlaylist.iterator();
            }
            if (mPlayOrder.hasNext()) {
                MediaProperties next = mPlayOrder.next();
                return setCurrentMedia(next);
            }
        }
        return false;
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.e(getClass().getName(), "onError(" + what + "," + extra + ")");
        mMp.reset();
        // TODO Maybe we should return false here to continue to the next track?
        return true;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        setNextSong();
    }
}
