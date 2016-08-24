package com.brick.robotctrl;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.kjn.ftpabout.FTPAsk;
import com.kjn.ftpabout.Result;

import org.apache.commons.net.ftp.FTPFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by li on 2016/8/8.
 */
public class AboutActivity extends BaseActivity {
    private final String TAG = "AboutActivity";
    public static FTPAsk ftp = null;
    public static final String REMOTE_PATH = "\\东南\\更新\\";
    public static String fileNameDownLoad;
    private String hostName = "218.2.191.50";
    private String userName = "user_seu";
    private String password = "seu23456";
    private String LOCAL_PATH = null;
    private List<FTPFile> remoteFile;
    public static String fileNameDown;
    private boolean isAPK = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        LOCAL_PATH = Environment.getExternalStorageDirectory()
                .getPath()+"/Movies";
        ftp = new FTPAsk(hostName, userName, password);
        remoteFile = new ArrayList<FTPFile>();
        new Thread() {
            @Override
            public void run() {
                try {
//            if (ftp != null) {
//                // 关闭FTP服务
//                ftp.closeConnect();
//            }
                    // 打开FTP服务
                    Log.d(TAG, "onCreate: 开始打开");
                    ftp.openConnect();
                    remoteFile = ftp.listFiles(REMOTE_PATH);
                    if (remoteFile.size() > 0) {
                        for (int i = 0; i < remoteFile.size(); i++) {
                            Log.d(TAG, "remoteFile: " + remoteFile.get(i).getName());
                            if (remoteFile.get(i).getName().endsWith(".mp4") || remoteFile.get(i).getName().endsWith(".3gp") || remoteFile.get(i).getName().endsWith(".mp3")) {
                                isAPK = false;
                                fileNameDown = remoteFile.get(i).getName();
                                Result result = null;
                                try {
                                    // 下载
                                    result = ftp.download(REMOTE_PATH, fileNameDown, LOCAL_PATH);
                                } catch (IOException e) {
                                    System.out.println(e.toString());
                                    System.out.println(e.getMessage());
                                    e.printStackTrace();
                                }
                                if (result.isSucceed()) {
                                    Log.e(TAG, "download ok...time:" + result.getTime()
                                            + " and size:" + result.getResponse());
                                } else {
                                    Log.e(TAG, "download fail");
                                }
                            }
                        }
                        for (int i = 0; i < remoteFile.size(); i++) {
                            if (remoteFile.get(i).getName().endsWith(".apk")) {
                                isAPK = true;
                                fileNameDown = remoteFile.get(i).getName();
                                break;
                            }
                        }
//                        File file = new File(LOCAL_PATH);
//                        File[] files = file.listFiles();
//                        for (int i = 0; i < files.length; i++) {
//                            if (files[i].getAbsolutePath().endsWith(fileNameDown)) {
//                                Log.d(TAG, "file exit: ");
//                                boolean flag = (files[i].length() >= remoteFile.get(2).getSize());
//                                Log.d(TAG, "flag: " + flag);
//                            }
//                        }
                        if(isAPK) {
                            Result result = null;
                            try {
                                // 下载
                                result = ftp.download(REMOTE_PATH, fileNameDown, LOCAL_PATH);
                            } catch (IOException e) {
                                System.out.println(e.toString());
                                System.out.println(e.getMessage());
                                e.printStackTrace();
                            }
                            if (result.isSucceed()) {
                                Log.e(TAG, "download ok...time:" + result.getTime()
                                        + " and size:" + result.getResponse());
                                ftp.closeConnect();
                                String str = LOCAL_PATH + "/" + fileNameDown;
                                Log.d(TAG, "str: " + str);
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setDataAndType(Uri.fromFile(new File(str)), "application/vnd.android.package-archive");
                                startActivity(intent);
                            } else {
                                Log.e(TAG, "download fail");
                            }
                        }else{
                            ftp.closeConnect();
                            Log.d(TAG, "multvideo:download over ");
                            Intent intent = new Intent();
                            intent.setClass(AboutActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    }else{
                        ftp.closeConnect();
                        Log.d(TAG, "暂无更新");
                        Intent intent = new Intent();
                        intent.setClass(AboutActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                }catch(Exception e){
                        e.printStackTrace();
                    }
                }
        }.start();
    }

    @Override
    protected void onStop() {
        Log.i(TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onRestart() {
        Log.i(TAG, "onRestart");
        clearTimerCount();
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy");
        super.onDestroy();
    }
}
