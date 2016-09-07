package com.brick.robotctrl;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;

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


    private static Handler contextHandler = null;
    private Button uploadButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);



     /*   uploadButton = (Button)findViewById(R.id.uploadButton);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    ftp.openConnect();
                    File mfile = new File(MLOCAL_PATH);
                    File[] mfiles = mfile.listFiles();
                    File afile = new File(ALOCAL_PATH);
                    File[] afiles = afile.listFiles();
                    *//*if (mfiles.() > 0) {
                        for (int i = 0; i < mfile.length(); i++) {
                            if (remoteFile.get(i).getName().endsWith(".mp4") || remoteFile.get(i).getName().endsWith(".3gp") || remoteFile.get(i).getName().endsWith(".mp3"))
                        }
                    }*//*
                    fflag = ftp.upload(REMOTE_PATH, MLOCAL_PATH);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });*/

        Thread t = new MyThread();
        t.start();
    }

    public class  MyThread extends Thread{
        public FTPAsk ftp = null ;
        public String hostName = "218.2.191.50";
        public String userName = "user_seu";
        public String password = "seu23456";
        public String MLOCAL_PATH = null;
        public String ALOCAL_PATH = null;


        final String REMOTE_PATH = "\\东南\\更新\\";
        String fileNameDownLoad;


        boolean fflag = false;
        List<FTPFile> remoteFile;
        List<File> localFile;
        String fileNameDown;
        boolean isAPK = false;

        @Override
        public void run() {
            remoteFile = new ArrayList<FTPFile>();
            localFile = new ArrayList<File>();

            ftp = new FTPAsk(hostName, userName, password);
            MLOCAL_PATH = Environment.getExternalStorageDirectory().getPath()+"/Movies";
            ALOCAL_PATH = Environment.getExternalStorageDirectory().getPath()+"/Download";
            Log.d(TAG, "onCreate: 789");
            try {
//            if (ftp != null) {
//                // 关闭FTP服务
//                ftp.closeConnect();
//            }
                // 打开FTP服务
                Log.d(TAG, "onCreate: 开始打开");
                ftp.openConnect();
                Log.d(TAG, "onCreate: 123");
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
                                fflag = ftp.download(REMOTE_PATH, fileNameDown, MLOCAL_PATH);

                            } catch (Exception e) {
                                System.out.println(e.toString());
                                System.out.println(e.getMessage());
                                e.printStackTrace();
                            }
                            if (fflag) {
                                Log.e(TAG, "download ok...time:"
                                        + " and size:" );
                            } else {
                                Log.e(TAG, "Movies download fail");
                            }
                        }
                    }
                    checkFile(mfiles, remoteFile);                              //删除本地Movies多余文件
                    contextHandler.sendEmptyMessage(SSDBTask.Key_VideoPlayList);
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
                            fflag = ftp.download(REMOTE_PATH, fileNameDown, ALOCAL_PATH);
                        } catch (IOException e) {
                            System.out.println(e.toString());
                            System.out.println(e.getMessage());
                            e.printStackTrace();
                        }
                        if (fflag) {
                            Log.e(TAG, "download ok...time:"
                                    + " and size:" );
                            ftp.closeConnect();
                            String str = ALOCAL_PATH + "/" + fileNameDown;
                            Log.d(TAG, "str: " + str);
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setDataAndType(Uri.fromFile(new File(str)), "application/vnd.android.package-archive");
                            startActivity(intent);
                        } else {
                            Log.e(TAG, "APK download fail");
                            ftp.closeConnect();
                            /*ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);//获得运行activity
                            ComponentName an = am.getRunningTasks(1).get(0).topActivity;//得到某一活动
                           // Log.d(TAG, "run: "+an.getClassName());
                            if ( !an.getClassName().equals("com.brick.robotctrl.MainActivity") ){*/
                            Intent intent = new Intent();
                            intent.setClass(AboutActivity.this, MainActivity.class);
                            startActivity(intent);
                            //}
                        }
                    }else{
                        ftp.closeConnect();
                        Log.d(TAG, "multvideo:download over ");
                        /*ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);//获得运行activity
                        ComponentName an = am.getRunningTasks(1).get(0).topActivity;//得到某一活动
                        if ( !an.getClassName().equals("com.brick.robotctrl.MainActivity") ){*/
                            Intent intent = new Intent();
                            intent.setClass(AboutActivity.this, MainActivity.class);
                            startActivity(intent);//}
                    }
                }else{
                    ftp.closeConnect();
                    Log.d(TAG, "暂无更新");
                   /* ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);//获得运行activity
                    ComponentName an = am.getRunningTasks(1).get(0).topActivity;//得到某一活动
                    if ( !an.getClassName().equals("com.brick.robotctrl.MainActivity") ){*/
                        Intent intent = new Intent();
                        intent.setClass(AboutActivity.this, MainActivity.class);
                        startActivity(intent);
             //   }
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public void checkFile(File[] files, List<FTPFile> remoteFile){

        Log.d(TAG, "checkFile: 开始");
        for (int j = 0; j < files.length; j++) {
            boolean flag = true;
            for(int i = 0; i < remoteFile.size(); i++) {
                if (files[j].getAbsolutePath().endsWith(remoteFile.get(i).getName())) {
                    Log.d(TAG, "checkFile: 存在");
                    flag = false;
                    break;
                }
            }
            if(flag){
                Log.d(TAG, "checkFile: 删除");
                files[j].delete();
            }
        }
    }

    public static void setHandler(Handler handler){
        AboutActivity.contextHandler = handler;
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
