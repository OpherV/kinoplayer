package com.android.kino.logic.interceptor;

import android.content.Context;

import com.android.kino.logic.InputEventTranslator;
import com.android.kino.logic.event.DoubleTapEvent;

public class TapInterceptor extends InterceptorBase {
    
    public TapInterceptor(Context context, InputEventTranslator inputTranslator) {
        super(inputTranslator, DoubleTapEvent.ID);
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        
    }
}
