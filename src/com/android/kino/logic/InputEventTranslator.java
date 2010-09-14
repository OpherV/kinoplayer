package com.android.kino.logic;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.android.kino.logic.action.KinoAction;
import com.android.kino.logic.interceptor.DoubleTapInterceptor;
import com.android.kino.logic.interceptor.InputEventInterceptor;
import com.android.kino.logic.interceptor.InputEventListener;
import com.android.kino.logic.settings.SettingsContainer;
import com.android.kino.logic.settings.SettingsLoader;
import com.android.kino.logic.settings.SettingsContainer.Setting;

/**
 * Receives input from the input interceptors and communicates it to the media
 * player.
 */
public class InputEventTranslator implements InputEventListener {
    private KinoMediaPlayer mPlayer;
    
    private List<InputEventInterceptor> mInterceptors = new LinkedList<InputEventInterceptor>();
    
    public InputEventTranslator(KinoMediaPlayer player, Context context) {
        mPlayer = player;
        
        SettingsContainer settings = SettingsLoader.loadCurrentSettings(context);
        if (settings.getConfiguredBoolean(Setting.ENABLE_DOUBLE_TAP)) {
            mInterceptors.add(new DoubleTapInterceptor());
        }
    }
    
    /**
     * Call this method to pass input to the translator.
     * The translator will perform the appropriate action.
     * 
     * @param eventId The event to handle.
     */
    @Override
    public void onEventTriggered(int eventId) {
        KinoAction action = SettingsLoader.loadCurrentSettings(null).getConfiguredAction(eventId);
        
        if (action != null) {
            action.performAction(mPlayer);
        }
    }

    public void startIntercepting() {
        for (InputEventInterceptor interceptor : mInterceptors) {
            Log.i("InputTranslator", "intercepting " + interceptor);
            interceptor.setListener(this);
            interceptor.startIntercepting();
        }
    }

    public void stopIntercepting() {
        for (InputEventInterceptor interceptor : mInterceptors) {
            Log.i("InputTranslator", "stoping interception " + interceptor);
            interceptor.stopIntercepting();
        }
    }

    public void enableDoubleTap() {
        Log.i("InputTranslator", "enableDoubleTap");
        for (InputEventInterceptor interceptor : mInterceptors) {
            if (interceptor instanceof DoubleTapInterceptor) {
                // Interceptor exists
                interceptor.startIntercepting();
                return;
            }
        }
        // Interceptor not found
        InputEventInterceptor interceptor = new DoubleTapInterceptor();
        mInterceptors.add(interceptor);
        interceptor.startIntercepting();
    }

    public void disableDoubleTap() {
        Log.i("InputTranslator", "disableDoubleTap");
        for (InputEventInterceptor interceptor : mInterceptors) {
            if (interceptor instanceof DoubleTapInterceptor) {
                // Interceptor exists
                interceptor.stopIntercepting();
                return;
            }
        }
    }
}
