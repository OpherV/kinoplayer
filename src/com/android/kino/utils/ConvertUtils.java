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
}
