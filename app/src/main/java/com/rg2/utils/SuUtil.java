package com.rg2.utils;

/**
 * Created by lx on 2018-08-28.
 */

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Intent;

import com.brick.robotctrl.RobotApplication;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import static android.content.Context.ACTIVITY_SERVICE;

/**
 * 执行android命令
 *
 * @author Yuedong Li
 */
public class SuUtil {

    private static Process process;

    /**
     * 结束进程,执行操作调用即可
     */
    public static void kill(String packageName) {
        initProcess();
        killProcess(packageName);
        close();
    }
   /*
   * 开启指定报名的应用
   * //封装成工具时 context 不是activity
   * 需要application全局的context 时要设置setFlags
   * */
    public static void startApp(String packagename,String packageAppname) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ComponentName cname = new ComponentName(packagename, packageAppname);
        intent.setComponent(cname);//cmp=com.tmri.app.main/com.tmri.app.ui.activity.TmriActivity
        RobotApplication.getAppContext().startActivity(intent);
    }
    /*
    * 关闭指定报名的应用*/
    public static void killAppPackageName(String packagename) {
        ActivityManager amg = (ActivityManager) RobotApplication.getAppContext().getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> myappprocess = amg.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info : myappprocess) {
            int pid = info.pid;
            Method method = null;
            try {
                method = Class.forName("android.app.ActivityManager").getMethod("forceStopPackage", String.class);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            try {
                method.invoke(amg, packagename);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 初始化进程
     */
    private static void initProcess() {
        if (process == null)
            try {
                process = Runtime.getRuntime().exec("su");
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    /**
     * 结束进程
     */
    private static void killProcess(String packageName) {
        OutputStream out = process.getOutputStream();
        String cmd = "am force-stop " + packageName + " \n";
        try {
            out.write(cmd.getBytes());
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭输出流
     */
    private static void close() {
        if (process != null)
            try {
                process.getOutputStream().close();
                process = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
}