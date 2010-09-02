package com.android.kino.logic.interceptor;

public interface InputEventInterceptor {
    
    public void setListener(InputEventListener listener);
    
    public void startIntercepting();
    
    public void stopIntercepting();

}
