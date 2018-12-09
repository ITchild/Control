package com.fei.control;

import android.app.Application;

public class ContorlApplication extends Application {
    private static ContorlApplication mIntent;
    private boolean isDebug = true;

    @Override
    public void onCreate() {
        super.onCreate();
        mIntent = this;
    }

    public static ContorlApplication getMIntent(){
        return mIntent;
    }

    public boolean isDebug() {
        return isDebug;
    }

    public void setDebug(boolean debug) {
        isDebug = debug;
    }

}
