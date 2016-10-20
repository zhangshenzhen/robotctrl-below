package com.brick.robotctrl;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.widget.Button;

import com.kjn.ftpabout.FTPAsk;
import com.zhangyt.log.LogUtil;

import org.apache.commons.net.ftp.FTPFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by li on 2016/8/8.
 */
public class AboutActivity extends BaseActivity {
    public final String TAG = "AboutActivity";
    public static Handler contextHandler = null;
    private String robotName = null;

    private Button uploadButton;
//    public Intent intentM = new Intent(Intent.ACTION_VIEW);
//    public Intent intentA = new Intent(Intent.ACTION_VIEW);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        Intent intent = getIntent();
        robotName = intent.getStringExtra("robotName");

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

        Thread t = new MyThread(robotName);
        t.start();
    }

    public class  MyThread extends Thread{
        public FTPAsk ftp = null ;
        public String hostName = "218.2.191.50";
        public String userName = "test1";
        public String password = "test1";
        public String MLOCAL_PATH = null;
        public String ALOCAL_PATH = null;
        private String robotName = null;

        boolean downloadSuccessFlag = false;
        List<FTPFile> remoteFile;
        List<File> localFile;
        String AfileNameDown [] =new String[2];
        boolean isAPK = false;

        public MyThread(String robotName) {
            this.robotName = robotName;
        }

        @Override
        public void run() {
            remoteFile = new ArrayList<FTPFile>();//remote file arry
            localFile = new ArrayList<File>();//local file arry
            String REMOTE_PATH = "\\" + robotName;                     //"\\东南\\更新\\";

            ftp = new FTPAsk(hostName, userName, password);
            MLOCAL_PATH = Environment.getExternalStorageDirectory().getPath()+"/Movies";//local
            ALOCAL_PATH = Environment.getExternalStorageDirectory().getPath()+"/Download";//local
            LogUtil.d(TAG, "onCreate: 789");
            try {
//            if (ftp != null) {
//                // 关闭FTP服务
//                ftp.closeConnect();
//            }
                // 打开FTP服务
                LogUtil.d(TAG, "onCreate: 开始打开");
                ftp.openConnect();
                LogUtil.d(TAG, "onCreate: 123");
                File mfile = new File(MLOCAL_PATH);//local movies path
                File[] mfiles = mfile.listFiles();//local download path
                File afile = new File(ALOCAL_PATH);
                File[] afiles = afile.listFiles();
                LogUtil.d(TAG, "run: " + REMOTE_PATH);
                try {
                    remoteFile = ftp.listFiles(REMOTE_PATH);
                }catch (Exception e){
                    e.printStackTrace();
                    REMOTE_PATH = "\\common\\";             // 和之前的目录兼容
                    remoteFile = ftp.listFiles(REMOTE_PATH);
                }

                if (remoteFile.size() <=0 ) {
                    REMOTE_PATH = "\\common\\";             // 目录不存在不会报异常，这里采用判断文件个数来决定目录位置
                    remoteFile = ftp.listFiles(REMOTE_PATH);
                }
                LogUtil.d(TAG, "run: " + REMOTE_PATH);
                if (remoteFile.size() > 0) {
                    for (int i = 0; i < remoteFile.size(); i++) {
                        LogUtil.d(TAG, "remoteFile: " + remoteFile.get(i).getName());
                        if (remoteFile.get(i).getName().endsWith(".mp4") || remoteFile.get(i).getName().endsWith(".3gp")
                                || remoteFile.get(i).getName().endsWith(".mp3") || remoteFile.get(i).getName().endsWith(".jpg") ) {
                            isAPK = false;
                            try {
                                // 下载
                                downloadSuccessFlag = ftp.download(REMOTE_PATH, remoteFile.get(i).getName(), MLOCAL_PATH);//下载格式(远程路径字符串，远程文件名，本地电影文件夹)
                                if ( downloadSuccessFlag ) {
                                    LogUtil.d(TAG, "Movies " + remoteFile.get(i).getName() + " download success");
                                } else {
                                    LogUtil.d(TAG, "Movies " + remoteFile.get(i).getName() + " no need to download!!");
                                }
                            } catch (Exception e) {
                                System.out.println(e.toString());
                                System.out.println(e.getMessage());
                                e.printStackTrace();
                            }
                        } else if (remoteFile.get(i).getName().endsWith(".apk")) {
                            try {                                               //Modified by jiangly  ,同时更新两个apk
                                // 下载
                                downloadSuccessFlag = ftp.download(REMOTE_PATH, remoteFile.get(i).getName(), ALOCAL_PATH);
                                if ( downloadSuccessFlag ) {
                                    LogUtil.d(TAG, "Apk " + remoteFile.get(i).getName() + " download success");
                                } else {
                                    LogUtil.d(TAG, "Apk " + remoteFile.get(i).getName() + " no need to download!!");
                                }
                            } catch (IOException e) {
                                System.out.println(e.toString());
                                System.out.println(e.getMessage());
                                e.printStackTrace();
                            }
                        }
                    }
                    checkFile(mfiles, remoteFile);                                      //删除本地Movies多余文件
                    contextHandler.sendEmptyMessage(SSDBTask.Key_VideoPlayList);       // 重新上传视频列表
                    checkFile(afiles, remoteFile);                                      //删除本地APK多余文件
                }else{
                    LogUtil.d(TAG, "暂无更新");
                }
                ftp.closeConnect();
                contextHandler.sendEmptyMessage(SSDBTask.key_ApkUpdate);

                // replace finish()?
                Intent intent = new Intent();
                intent.setClass(AboutActivity.this, MainActivity.class);
                startActivity(intent);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public void checkFile(File[] files, List<FTPFile> remoteFile){

        LogUtil.d(TAG, "checkFile: 开始");
        for (int j = 0; j < files.length; j++) {
            boolean flag = true;
            for(int i = 0; i < remoteFile.size(); i++) {
                if (files[j].getAbsolutePath().endsWith(remoteFile.get(i).getName())) {
                    LogUtil.d(TAG, "checkFile: 存在");
                    flag = false;
                    break;
                }
            }
            if(flag){
                LogUtil.d(TAG, "checkFile: 删除");
                files[j].delete();
            }
        }
    }

    public static void setHandler(Handler handler){
        AboutActivity.contextHandler = handler;
    }

    @Override
    protected void onStop() {
        LogUtil.i(TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onRestart() {
        LogUtil.i(TAG, "onRestart");
        clearTimerCount();
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        LogUtil.i(TAG, "onDestroy");
        super.onDestroy();
    }

    public static void startAction(Context context, String robotName) {
        Intent aboutIntent = new Intent();
        aboutIntent.setClass(context, AboutActivity.class);
        aboutIntent.putExtra("robotName", robotName);
        context.startActivity(aboutIntent);
    }
}
