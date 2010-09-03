package com.android.kino.logic;

import java.util.ListIterator;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.util.Log;

import com.android.kino.utils.TimeSpan;

/**
 * Contains all the capabilities of media playback.
 */
public class KinoMediaPlayer implements OnErrorListener, OnCompletionListener {
    // Seeking to less than MIN_SEEK_THRESHOLD milliseconds from the end will
    // be considered as seek to end and will result in playing the next media
    private static final int MIN_SEEK_THRESHOLD = 1000;
    
    /**
     * Implementation notes:
     * See the diagram in this link -
     *  http://developer.android.com/reference/android/media/MediaPlayer.html
     * This implementation makes sure the state of the media player can only be
     * at -
     * Idle - after creation or an error.
     * Prepared - after setCurrentMedia.
     * Started - while playing.
     * Paused / Stopped.
     * End - after disposed.
     */
    private MediaPlayer mMp;
    private Playlist mPlaylist = null;
    private ListIterator<MediaProperties> mPlayOrder = null;
    private MediaProperties mCurrentMedia = null;
    
    private RepeatMode mRepeatMode = RepeatMode.OFF;
    private boolean mIsShuffleOn = false;

    public enum RepeatMode { OFF, ONE, ALL };
    
    public KinoMediaPlayer() {
        mMp = new MediaPlayer();
        mMp.setOnErrorListener(this);
        mMp.setOnCompletionListener(this);
    }
    
    /**
     * Call this method when done using the media player.
     */
    public void dispose() {
        mMp.release();
        mPlaylist = null;
        mPlayOrder = null;
        mCurrentMedia = null;
    }
    
    public boolean isPlaying() {
        try {
            return mMp.isPlaying();
        }
        catch (Exception e) {
            Log.e(getClass().getName(), "Not playing - probably in Error state");
            return false;
        }
    }
    
    public boolean isPaused() {
        return !isPlaying() && safeGetPosition() > 0;
    }
    
    public boolean isStopped() {
        return !isPlaying() && safeGetPosition() == 0;
    }
    
    /**
     * Seek to a place in the current track.
     * If seeking to the end of the track, the player will try to play the next
     * media.
     * 
     * @param percentOfTrack The percent of the track duration to seek to.
     */
    public void seek(float percentOfTrack) {
        Log.d(getClass().getName(), "Seeking to " + percentOfTrack);
        int duration = safeGetDuration();
        if (duration == 0) {
            // Failed to get duration
            return;
        }
        int seekPosition = (int) (duration * percentOfTrack);
        // Checking for seek to end (here 'cause comparing floats is inaccurate)
        if (seekPosition >= duration - MIN_SEEK_THRESHOLD) {
            Log.d(getClass().getName(), "Seeked to end");
            // TODO(test) Check what happens if there is no next song to play
            //      (with and without repeat one/all)
            playNextMedia();
            return;
        }
        mMp.seekTo(seekPosition);
    }
    
    /**
     * Returns the current position in the playlist.
     * 
     * @return A zero based index of the playlist position. If there is no
     *         playlist returns 0.
     */
    public int getCurrentPlaylistIndex() {
        if (mPlayOrder == null) {
            return 0;
        }
        return mPlayOrder.nextIndex() - 1;
    }

    /**
     * Returns a container of the elapsed and remaining playing time.
     * @return A container of the elapsed and remaining playing time.
     */
    public PlaybackPosition getPlaybackPosition() {
        int duration = safeGetDuration();
        int position = safeGetPosition();
        return new PlaybackPosition(duration, position);
    }

    /**
     * Returns the duration of the current file.
     * @return The duration of the current file.
     */
    public TimeSpan getCurrentTrackDuration() {
        try {
            return new TimeSpan(safeGetDuration());
        }
        catch (Exception e) {
            return new TimeSpan(0);
        }
    }
    
    public int getCurrentTrackDurationInSeconds(){
    	int duration=getCurrentTrackDuration().getDays()*24*60*60+getCurrentTrackDuration().getHours()*60*60+getCurrentTrackDuration().getMinutes()*60+getCurrentTrackDuration().getSeconds();
    	return duration;
    }
    
    public MediaProperties getCurrentMedia() {
        return mCurrentMedia;
    }

    public Playlist getCurrentPlaylist() {
        return mPlaylist;
    }

    /**
     * Set playlist. Set <code>null</code> to clear the playlist.
     */
    public void setCurrentPlaylist(Playlist playlist) {
        Log.d(getClass().getName(), "Setting playlist - " + playlist);
        mPlaylist = playlist;
    }
    
    public boolean setCurrentMedia(int position) {
        // First stop whatever we're doing
        mMp.reset();
        if (mPlaylist == null) {
            Log.d(getClass().getName(), "Can't find media - no playlist");
            return false;
        }
        setPlayOrderIterator(position);
        if (mPlayOrder.hasNext()) {
            MediaProperties next = mPlayOrder.next();
            if (!setCurrentMedia(next)) {
                mPlayOrder = null;
                return false;
            }
            return true;
        }
        else {
            Log.d(getClass().getName(), "Can't find media - iterator is empty");
            return false;
        }
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
        Log.d(getClass().getName(), "Setting current media");
        String filename = media.Filename;
        try {
            // Must reset before loading another track
            mMp.reset();
            mMp.setDataSource(filename);
            mMp.prepare();
        }
        catch (Exception e) {
            // Failed loading file
            Log.e(getClass().getName(), "Failed setting current media", e);
            // TODO(check-log) This might be redundant
            resetMediaPlayer();
            return false;
        }
        mCurrentMedia = media;
        return true;
    }

    /**
     * Note that the passed volume value is raw scalar. UI controls should be
     * scaled logarithmically.
     * 
     * @param volume The volume scalar.
     */
    public void setVolume(float volume) {
        Log.d(getClass().getName(), "Setting volume - " + volume);
        mMp.setVolume(volume, volume);
    }

    /**
     * Set repeat mode.
     */
    public void setRepeatMode(RepeatMode repeat) {
        Log.d(getClass().getName(), "Setting repeat mode - " + repeat);
        mRepeatMode = repeat;
    }

    /**
     * Set shuffle mode on/off.
     */
    public void setShuffle(boolean isOn) {
        if (mIsShuffleOn != isOn) {
            Log.d(getClass().getName(), "Changing shuffle - " + isOn);
            mIsShuffleOn = isOn;
            setPlayOrderIterator(getCurrentPlaylistIndex());
        }
    }

    /**
     * Toggle shuffle mode on/off.
     */
    public void toggleShuffle() {
        setShuffle(!mIsShuffleOn);
    }
    
    /**
     * Toggles between playing and pausing the current media.
     * If there is no media to play, does nothing.
     */
    public void togglePlayPause() {
        if (mCurrentMedia == null) {
            if (!playNextMedia()) {
                // No media to play - do nothing
                Log.d(getClass().getName(), "No media to play");
                return;
            }
        }
        if (mMp.isPlaying()) {
            Log.d(getClass().getName(), "Play->Pause");
            mMp.pause();
        }
        else {
            Log.d(getClass().getName(), "Pause(?)->Play");
            mMp.start();
        }
    }
    
    /**
     * Stops playback.
     */
    public void stop() {
        Log.d(getClass().getName(), "Stopping playback");
        try {
            mMp.stop();
            // Prepare so we could start playing again
            mMp.prepare();
        }
        catch (Exception e) {
            Log.e(getClass().getName(), "Failed to stop", e);
        }
    }
    
    /**
     * Plays the next media - even if repeat one is on.
     * 
     * @return <code>true</code> if moved to the next media, <code>false</code>
     *         if failed or if there is no next media to play. Can fail if there
     *         is no current playlist or if set current media failed.
     */
    public boolean next() {
        // Play only if was playing before
        if (isPlaying()) {
            return playNextMedia();
        }
        else {
            return setNextMedia();
        }
    }

    /**
     * Plays the previous media - even if repeat one is on.
     * 
     * @return <code>true</code> if moved to the previous media,
     *         <code>false</code> if failed or if there is no previous media to
     *         play. Can fail if there is no current playlist or if set current
     *         media failed.
     */
    public boolean previous() {
        Log.d(getClass().getName(), "Playing previous media");
        boolean shouldPlay = isPlaying();
        // TODO(test) Test playing an empty playlist
        if (mPlaylist == null) {
            Log.d(getClass().getName(), "Can't previous - no playlist");
            return false;
        }
        MediaProperties next;
        if (mPlayOrder == null) {
            // No play order - play the last track
            setPlayOrderIterator(mPlaylist.size() - 1);
            next = mPlayOrder.next();
        }
        else if (mPlayOrder.hasPrevious()) {
            next = mPlayOrder.previous();
            // Note: The list iterator will return the last extracted item when 
            //       the iteration direction changes (from forward to backwards
            //       and vice versa) so we call the method again
            if (next == mCurrentMedia) {
                return previous();
            }
        }
        else if (mRepeatMode == RepeatMode.ALL) {
            // We are at the first track - play the last track
            // TODO(functionality) It might be better to push the iterator to
            //      the end to preserve the current order (e.g. in a random
            //      iterator)
            mPlayOrder = null;
            return previous();
        }
        else {
            // Repeat is off - seek to start
            Log.d(getClass().getName(), "Can't previous - repeat is off");
            seek(0);
            mMp.start();
            return true;
        }
        if (!setCurrentMedia(next)) {
            mPlayOrder = null;
            return false;
        }
        // Play only if was playing before
        if (shouldPlay) {
            mMp.start();
        }
        return true;
    }
    
    /**
     * Sets the next media in the playlist as the current media.
     * 
     * @return <code>true</code> if moved to the next media, <code>false</code>
     *         if failed or if there is no next media to play. Can fail if there
     *         is no current playlist or if set current media failed.
     */
    private boolean setNextMedia() {
        // First stop whatever we're doing
        mMp.reset();
        if (mPlaylist == null) {
            Log.d(getClass().getName(), "Can't next - no playlist");
            return false;
        }
        if (mPlayOrder == null) {
            // No play order - play the first track
            setPlayOrderIterator(0);
        }
        if (mPlayOrder.hasNext()) {
            MediaProperties next = mPlayOrder.next();
            // Note: The list iterator will return the last extracted item when 
            //       the iteration direction changes (from forward to backwards
            //       and vice versa) so we call the method again
            if (next == mCurrentMedia) {
                return setNextMedia();
            }
            if (!setCurrentMedia(next)) {
                mPlayOrder = null;
                return false;
            }
            return true;
        }
        else if (mRepeatMode == RepeatMode.ALL) {
            // We are at the last track - play the first track
            // TODO(functionality) It might be better to rewind the iterator to
            //      the start to preserve the current order (e.g. in a random
            //      iterator)
            mPlayOrder = null;
            return setNextMedia();
        }
        else {
            // Repeat is off - don't do anything
            Log.d(getClass().getName(), "Can't next - repeat is off");
            return false;
        }
    }

    /**
     * Play the next media in the playlist.
     */
    private boolean playNextMedia() {
        Log.d(getClass().getName(), "Playing next media");
        if (!setNextMedia()) {
            return false;
        }
        // Start playing
        mMp.start();
        return true;
    }

    private void setPlayOrderIterator(int position) {
        if (mIsShuffleOn) {
            Log.d(getClass().getName(), "Setting random play order");
            mPlayOrder = mPlaylist.randomIterator(position);
        }
        else {
            Log.d(getClass().getName(), "Setting normal play order");
            mPlayOrder = mPlaylist.playlistIterator(position);
        }
    }
    
    private int safeGetDuration() {
        try {
            return mMp.getDuration();
        }
        catch (Exception e) {
            Log.e(getClass().getName(), "Failed to get duration", e);
            return 0;
        }
    }
    
    private int safeGetPosition() {
        try {
            return mMp.getCurrentPosition();
        }
        catch (Exception e) {
            Log.e(getClass().getName(), "Failed to get position", e);
            return 0;
        }
    }
    
    /**
     * Resets the media player - nothing is currently playing and the current
     * track is set to null.
     * Playlist and play order remains the same.
     */
    private void resetMediaPlayer() {
        Log.d(getClass().getName(), "Reseting media player");
        mMp.reset();
        mCurrentMedia = null;
    }

    /* (non-Javadoc)
     * @see android.media.MediaPlayer.OnErrorListener#onError(android.media.MediaPlayer, int, int)
     */
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.e(getClass().getName(), "onError(" + what + "," + extra + ")");
        resetMediaPlayer();
        return true;
    }

    /* (non-Javadoc)
     * @see android.media.MediaPlayer.OnCompletionListener#onCompletion(android.media.MediaPlayer)
     */
    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.d(getClass().getName(), "Playback completed");
        if (mRepeatMode == RepeatMode.ONE) {
            seek(0);
            mMp.start();
        }
        else {
            playNextMedia();
        }
    }
}
