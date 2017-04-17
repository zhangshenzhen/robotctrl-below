package com.receiver;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.util.Log;

import com.google.android.gms.appdatasearch.GetRecentContextCall;

/**
 * Created by lx on 2017/3/29.
 */

public class NetSwitchReceiver extends BroadcastReceiver {
    public  static final  String TAG = "NetSwitchReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG,"这里开启广播。。。");
       //修改当前的IP
        ContentResolver resolver = context.getContentResolver();
        Settings.System.putInt(resolver, Settings.System.WIFI_USE_STATIC_IP,1);
        Settings.System.putString( resolver, Settings.System.WIFI_STATIC_IP, "192.168.0.107");
        //获取当前ip
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ip = wifiInfo.getIpAddress();
        Log.d(TAG,"IP wifiInfo="+wifiInfo);
         Log.d(TAG,"IP="+int2ip(ip));
    }
    /**
     * 将ip的整数形式转换成ip形式
     *
     * @param ipInt
     * @return
     */
    public static String int2ip(int ipInt) {
        StringBuilder sb = new StringBuilder();
        sb.append(ipInt & 0xFF).append(".");
        sb.append((ipInt >> 8) & 0xFF).append(".");
        sb.append((ipInt >> 16) & 0xFF).append(".");
        sb.append((ipInt >> 24) & 0xFF);
        return sb.toString();
    }




    //检查当前网络是否可用;
     public  static boolean cheakEnable(Context context){
            boolean i = false;
         NetworkInfo localNetworkInfo = ((ConnectivityManager) context
                 .getSystemService("connectivity")).getActiveNetworkInfo();
         if ((localNetworkInfo != null) && (localNetworkInfo.isAvailable()))
             return true;
         return   false;
    }

}
