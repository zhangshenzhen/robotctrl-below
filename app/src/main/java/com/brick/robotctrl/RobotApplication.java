package com.brick.robotctrl;

import android.app.Application;
import android.content.Intent;

import com.kjn.crashlog.CrashHandler;

import zime.ui.ZIMEAVDemoService;

/**
 * Created by kjnijk on 2016-08-31.
 */
public class RobotApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Intent stopIntent = new Intent(this, ZIMEAVDemoService.class);
        stopService(stopIntent);
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);
    }
}
