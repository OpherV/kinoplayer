package com.android.kino.logic;

import com.android.kino.utils.TimeSpan;

public class PlaybackPosition {

    private int mDuration;
    private int mPosition;
    
    /**
     * Constructs a new playback position container.
     * 
     * @param duration The duration of the played media in milliseconds.
     * @param position The current position of the played media in milliseconds.
     */
    public PlaybackPosition(int duration, int position) {
        mDuration = duration;
        mPosition = position;
    }
    
    public int getDuration() {
        return mDuration;
    }
    
    public int getPosition() {
        return mPosition;
    }
    
    public TimeSpan getElapsed() {
        return new TimeSpan(mPosition);
    }
    
    public TimeSpan getRemaining() {
        int remaining = mDuration - mPosition;
        return new TimeSpan(remaining);
    }
    
    public float getPositionPercent() {
        if (mDuration == 0) {
            return 0;
        }
        return ((float)mPosition) / mDuration;
    }
    
}
