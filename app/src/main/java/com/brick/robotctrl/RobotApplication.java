package com.brick.robotctrl;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Debug;
import android.os.Environment;
import android.util.Log;

import com.kjn.crashlog.CrashHandler;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.text.AttributedCharacterIterator;
import java.text.DateFormat;
import java.text.Format;
import java.util.Date;

import zime.ui.ZIMEAVDemoService;

import static android.R.attr.x;

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
       CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);
 //x.Ext.init(this);//Xutils初始化
        RobotApplication.context = getApplicationContext();

        //初始化程序崩溃调用；
         Thread.currentThread().setUncaughtExceptionHandler(new MyexceptionHandler());

    }

    private class MyexceptionHandler implements Thread.UncaughtExceptionHandler {

        @Override//等发现了为捕获的异常的时候调用的方法；
        public void uncaughtException(Thread thread, Throwable ex) {
            Log.e("MyexceptionHandler", "......程序发现了异常，被哥们捕获了");
            StringBuffer sb = new StringBuffer();
            Date date = new Date();
            //格式化时间
            String time = DateFormat.getInstance().format(date);
            sb.append("Time:");
            sb.append(time+"\n");//当前的系统时间；
            Field[] fields = Build.class.getDeclaredFields();
           /* for(Field field : fields){
                try {
                   String name = field.getName();
                    String value =  (String)field.get(null);
                    sb.append(name+"="+value+"\n"); //追加信息；
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }*/

            //错误日志, 吧异常写到文件中；
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            sb.append(sw.toString());//追加；

            try {
                File file = new File(Environment.getExternalStorageDirectory(),"Robort_error.log");
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(sb.toString().getBytes());
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
//            android.os.Process.killProcess(android.os.Process.myPid());
//            Log.e("MyexceptionHandler", "......启动自杀方式，再次激活程序");

        }
    }
}
