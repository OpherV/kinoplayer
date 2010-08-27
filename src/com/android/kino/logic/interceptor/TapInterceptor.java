package com.android.kino.logic.interceptor;

import android.content.Context;
import android.content.Intent;

import com.android.kino.logic.InputEventTranslator;
import com.android.kino.logic.event.DoubleTapEvent;

public class TapInterceptor extends InterceptorBase {
    public TapInterceptor(InputEventTranslator inputTranslator) {
        super(inputTranslator, DoubleTapEvent.ID);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        
    }
}
