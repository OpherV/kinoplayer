package com.android.kino.logic.action;

/**
 * A base class for actions.
 */
public abstract class BaseKinoAction implements KinoAction {
    
    @Override
    public boolean equals(KinoAction other) {
        return compareTo(other) == 0;
    }

    @Override
    public int compareTo(KinoAction another) {
        return getActionID() - another.getActionID();
    }

}
