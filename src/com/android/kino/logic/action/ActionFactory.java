package com.android.kino.logic.action;


public abstract class ActionFactory {

    public static KinoAction create(int id) {
        switch (id) {
        case PlayPauseToggle.ID:
            return new PlayPauseToggle();
        default:
            return null;
        }
    }
    
    public static int[] getAllActionIds() {
        return new int[] {
                PlayPauseToggle.ID
        };
    }

}
