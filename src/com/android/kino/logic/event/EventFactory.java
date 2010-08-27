package com.android.kino.logic.event;


public abstract class EventFactory {

    public static InputEvent create(int id) {
        switch (id) {
        case DoubleTapEvent.ID:
            return new DoubleTapEvent();
        default:
            return null;
        }
    }

    /**
     * TODO(remember) To be used to populate settings window.
     */
    public static int[] getAllEventIds() {
        return new int[] {
                DoubleTapEvent.ID
        };
    }

}
