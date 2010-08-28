package com.android.kino.utils;

public abstract class ConvertUtils {
    
    public static int tryParse(String str) {
        return tryParse(str, 0);
    }
    
    public static int tryParse(String str, int failValue) {
        try {
            return Integer.parseInt(str);
        }
        catch (Exception ex) {
            return failValue;
        }
    }
    
    public static String safeFileName(String str){
    	return str.replaceAll(" ", "_").toLowerCase();
    }
    
    public static int getMins(int totalSecs){
    	return (int) Math.floor(totalSecs/60);
    }
    
    public static int getSecs(int totalSecs){
    	return (totalSecs % 60);
    }
    
    public static String formatTime(int totalSecs){
    	String time = String.format("%02d:%02d", getMins(totalSecs), getSecs(totalSecs));
    	return time;
    }
}
