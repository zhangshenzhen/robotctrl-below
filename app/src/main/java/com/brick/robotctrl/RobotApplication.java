package com.brick.robotctrl;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Debug;

import com.kjn.crashlog.CrashHandler;



import java.text.AttributedCharacterIterator;

import zime.ui.ZIMEAVDemoService;

/**
 * Created by kjnijk on 2016-08-31.
 */
public class RobotApplication extends Application {

  /*  protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        Debug.startMethodTracing("zhang_san");
    }*/

    //获取上下文
    private static Context context;
    public static Context getAppContext() {
        return RobotApplication.context;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        Intent stopIntent = new Intent(this, ZIMEAVDemoService.class);
        stopService(stopIntent);
//        CrashHandler crashHandler = CrashHandler.getInstance();
//        crashHandler.init(this);
 //       x.Ext.init(this);//Xutils初始化

        RobotApplication.context = getApplicationContext();
    }
}
