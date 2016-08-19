package com.brick.robotctrl;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.kjn.ftpabout.Result;

import java.io.File;
import java.io.IOException;

/**
 * Created by kjnijk on 2016-08-14.
 */
public class DownLoadService extends Service {
    private static final String TAG = "DownLoadService";
    private String LOCAL_PATH = null;
    
    
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LOCAL_PATH = Environment.getExternalStorageDirectory()
                .getPath()+"/Movies";
        Log.d(TAG, "onStartCommand: 开始");

        new Thread() {
            @Override
            public void run() {
                Result result = null;
                LOCAL_PATH = Environment.getExternalStorageDirectory()
                        .getPath()+"/Movies";
                try {
                    // 下载
                    Log.d(TAG, "REMOTE_PATH: " + AboutActivity.REMOTE_PATH);
                    Log.d(TAG, "fileNameDown: " + AboutActivity.fileNameDownLoad);
                    Log.d(TAG, "LOCAL_PATH: " + LOCAL_PATH);
                    result = AboutActivity.ftp.download(AboutActivity.REMOTE_PATH, AboutActivity.fileNameDownLoad, LOCAL_PATH);
                    Log.d(TAG, "result: " );
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (result.isSucceed()) {
                    Log.e(TAG, "download ok...time:" + result.getTime()
                            + " and size:" + result.getResponse());
                } else {
                    Log.e(TAG, "download fail");
                }
            }
        }.start();
        return super.onStartCommand(intent, flags, startId);
    }

    public static void startAction(Context context) {
        Intent playIntent = new Intent();
        Log.d("", "startDownLoadService: starting DownloadService");
        playIntent.setClass(context, DownLoadService.class);
        context.startService(playIntent);       //启动服务
    }

    public static void stopAction(Context context) {
        Intent stopIntent = new Intent();
        Log.d("", "stopPlayerService: starting stopService");
        stopIntent.setClass(context, PlayerService.class);
        context.stopService(stopIntent);       //关闭服务
    }
    
}

