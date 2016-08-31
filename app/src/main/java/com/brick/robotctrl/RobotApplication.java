package com.brick.robotctrl;

import android.app.Application;

import com.kjn.crashlog.CrashHandler;

/**
 * Created by kjnijk on 2016-08-31.
 */
public class RobotApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);
    }
}
