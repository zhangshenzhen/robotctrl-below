package com.brick.robotctrl;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

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
    private String MLOCAL_PATH = null;
    private String ALOCAL_PATH = null;
    private List<FTPFile> remoteFile;
    public static String fileNameDown;
    private boolean isAPK = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ftp = new FTPAsk(hostName, userName, password);
        remoteFile = new ArrayList<FTPFile>();
        MLOCAL_PATH = Environment.getExternalStorageDirectory().getPath()+"/Movies";
        ALOCAL_PATH = Environment.getExternalStorageDirectory().getPath()+"/Download";
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
                    File mfile = new File(MLOCAL_PATH);
                    File[] mfiles = mfile.listFiles();
                    File afile = new File(ALOCAL_PATH);
                    File[] afiles = afile.listFiles();
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
                                    result = ftp.download(REMOTE_PATH, fileNameDown, MLOCAL_PATH);
                                } catch (IOException e) {
                                    System.out.println(e.toString());
                                    System.out.println(e.getMessage());
                                    e.printStackTrace();
                                }
                                if (result.isSucceed()) {
                                    Log.e(TAG, "download ok...time:" + result.getTime()
                                            + " and size:" + result.getResponse());
                                } else {
                                    Log.e(TAG, "Movies download fail");
                                }
                            }
                        }
                        checkFile(mfiles, remoteFile);                              //删除本地Movies多余文件
                        checkFile(afiles, remoteFile);                              //删除本地APK多余文件
                        for (int i = 0; i < remoteFile.size(); i++) {
                            if (remoteFile.get(i).getName().endsWith(".apk")) {
                                isAPK = true;
                                fileNameDown = remoteFile.get(i).getName();
                                break;
                            }
                        }
                        if(isAPK) {
                            Result result = null;
                            try {
                                // 下载
                                result = ftp.download(REMOTE_PATH, fileNameDown, ALOCAL_PATH);
                            } catch (IOException e) {
                                System.out.println(e.toString());
                                System.out.println(e.getMessage());
                                e.printStackTrace();
                            }
                            if (result.isSucceed()) {
                                Log.e(TAG, "download ok...time:" + result.getTime()
                                        + " and size:" + result.getResponse());
                                ftp.closeConnect();
                                String str = ALOCAL_PATH + "/" + fileNameDown;
                                Log.d(TAG, "str: " + str);
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setDataAndType(Uri.fromFile(new File(str)), "application/vnd.android.package-archive");
                                startActivity(intent);
                            } else {
                                Log.e(TAG, "APK download fail");
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

    public void checkFile(File[] files, List<FTPFile> remoteFile){
        boolean flag = true;
        for (int j = 0; j < files.length; j++) {
            for(int i = 0; i < remoteFile.size(); i++) {
                if (files[j].getAbsolutePath().endsWith(remoteFile.get(i).getName())) {
                    flag = false;
                    break;
                }
            }
            if(flag){
                files[j].delete();
            }
        }
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
