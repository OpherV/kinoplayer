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

}
