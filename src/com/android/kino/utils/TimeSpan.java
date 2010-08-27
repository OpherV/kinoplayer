package com.android.kino.utils;

public class TimeSpan {
    
    private static final int MILLIES_IN_SEC = 1000;
    private static final int SECS_IN_MINUTE = 60;
    private static final int MINUTES_IN_HOUR = 60;
    private static final int HOURS_IN_DAY = 24;
    
    private int mMillies;
    private int mSeconds;
    private int mMinutes;
    private int mHours;
    private int mDays;
    
    public TimeSpan(int totalMillies) {
        mMillies = totalMillies % MILLIES_IN_SEC;
        totalMillies /= MILLIES_IN_SEC;
        mSeconds = totalMillies % SECS_IN_MINUTE;
        totalMillies /= SECS_IN_MINUTE;
        mMinutes = totalMillies % MINUTES_IN_HOUR;
        totalMillies /= MINUTES_IN_HOUR;
        mHours = totalMillies % HOURS_IN_DAY;
        totalMillies /= HOURS_IN_DAY;
        mDays = totalMillies;
    }
    
    public int getDays() {
        return mDays;
    }
    
    public int getHours() {
        return mHours;
    }
    
    public int getMinutes() {
        return mMinutes;
    }
    
    public int getSeconds() {
        return mSeconds;
    }
    
    public int getMillies() {
        return mMillies;
    }
}
