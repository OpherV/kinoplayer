package com.android.kino.utils;

public abstract class CompareUtils {
    
    public static <T extends Comparable<T>> boolean equalsWithNulls(T rhs, T lhs) {
        if (rhs == null || lhs == null) {
            return rhs == lhs;
        }
        return rhs.compareTo(lhs) == 0;
    }
    
    public static <T extends Comparable<T>> int compareWithNulls(T rhs, T lhs) {
        if (rhs == null || lhs == null) {
            if (rhs == lhs) {
                return 0;
            }
            if (rhs == null) {
                return - Math.abs(lhs.hashCode());
            }
            return Math.abs(rhs.hashCode());
        }
        return rhs.compareTo(lhs);
    }
    
    public static boolean isNullOrEmpty(String str) {
        if (str == null) {
            return true;
        }
        return str.trim().equals("");
    }
    
}
