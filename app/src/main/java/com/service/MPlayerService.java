package com.service;


import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import com.brick.robotctrl.ADActivity;
import com.brick.robotctrl.MainActivity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SuppressLint("NewApi")
public class MPlayerService extends Service {
    private static final String TAG = "MPlayerService";

    public static String pathfile;                        //音乐文件路径
    private boolean isPause = false;                    //暂停状态

    private static String mp3Url= null;
    public int duration ;
    int  currentPosition ;
    public MediaPlayer mediaPlayer;


    @Override
    public IBinder onBind(Intent arg0) {
        return new SetPlayerBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mediaPlayer.isPlaying()) {
            stop();
        }
        pathfile = mp3Url;
        Log.d(TAG, "onStartCommand: " + pathfile);
        play(0);
        return super.onStartCommand(intent, flags, startId);
    }

    public void initData() {
        //媒体播放器对象
        if(mediaPlayer == null){
          mediaPlayer = new MediaPlayer();
          mediaPlayer.reset();//把各项参数恢复到初始状态
          }
          mediaPlayer.reset();
        Log.d(TAG, "开始准备音乐资源 " + pathfile);
        try {
            mediaPlayer.setDataSource(pathfile);
            mediaPlayer.prepare();  //进行缓冲
            duration = mediaPlayer.getDuration();//最大时长
            mediaPlayer.setOnCompletionListener(new CompleteListener());

        }catch (IOException e){
            e.printStackTrace();
        }
    }
    String Mode;
    public  class  SetPlayerBinder extends Binder {

        public void initMediaPlayer(){
            initData();
        }

        public void setPsuse(){
            pause();
        }
        public void setstop(){
            stop();
        }

        public void setstart( String path ,String mode){
            Mode = mode;
            Log.d(TAG,"点击了开始2");
            pathfile = path;
            play(0);
        }
        /*暂停*/
        public void setPause(){
            pause();
        }
        /*继续*/
        public void setContinue(){
           mContinue();
        }
        public int  durationMax(){
            return duration;
        }
        public int getCurrentposition(){
            currentPosition = mediaPlayer.getCurrentPosition();
            return currentPosition;
        }
        public void seekprogress(int posion){
             pause();
            isPause = false;
            mediaPlayer.seekTo(posion);
            mediaPlayer.start();
        }
    }

    private void play(int position) {
        initData();//初始化媒体播放器
        if (mediaPlayer.isPlaying()){//如果正在播放就直接返回
            return;
        }
        Log.d(TAG,"开始播放音乐start");
        if (position > 0) {    //如果音乐不是从头播放
            mediaPlayer.seekTo(position);
        }else {
            mediaPlayer.seekTo(0);
        }
        mediaPlayer.start();

    }


    /**
     * 暂停音乐
     */
    private void pause() {
        Log.d(TAG, "pause: ");
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            Log.d(TAG, "pause: "+duration);
            isPause = true;
        }
    }

    /*
    * 继续*/
    public void mContinue(){
        //mediaPlayer.seekTo(duration-10);
        if (isPause){
           mediaPlayer.start();
            isPause = false;
         }

    }

    /**
     * 停止音乐
     */
    private void stop() {
        Log.d(TAG, "stop:... ");
        currentPosition = 0;
        if (mediaPlayer != null&&mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            Log.d(TAG, "stop:... "+mediaPlayer);
        }
    }


    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }


    /**
     * 实现一个OnCompletionListener接口,当音乐播放完的时候调用
     */
    private class CompleteListener implements MediaPlayer.OnCompletionListener {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer2) {
            Log.d(TAG,"开始重复播放音乐start : "+Mode);
            currentPosition = 0;
            if (Mode.equals("Single")){
                MainActivity.isMp3 = false;
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
                Log.d(TAG,"停止播放音乐: " + mediaPlayer);
                return;
            }else if (Mode.equals("SingleCycle")){
              play(0);
            }else if (Mode.equals("Cycle")){
                MainActivity.isMp3 = false; //如果是MP4格式mp3就关闭
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
                Log.d(TAG,"开始辨别音乐和视频: " + mediaPlayer);
                next();
                return;
            }
        }
    }

   boolean isInitList = true;//只是为了初始化 list 一次
    private void next() {
        Log.d(TAG, "next: 下一首播放 是否初始化:" + isInitList );
        if (isInitList){
        if(getFiles(FileDir)){   //循环列表播放是用的 初始化List集合
            isInitList = false;
           }
        }
        index = findIndexOfStringInvideoList(pathfile);//判断刚才已经播放过的index
        Log.d(TAG, "next: 刚才播放的 index " + index );
        if (++index >= playerList.size()) {
            index = 0;
        }
        nextFilepath = playerList.get(index);
        nextFileName =nextFilepath.substring(nextFilepath.lastIndexOf("/") + 1, nextFilepath .length());
        Log.d(TAG, " next: 下一首播放 : "+index+ " : " + nextFileName);
       if (playerList.get(index).endsWith("mp4")){//如果是MP4格式
           MainActivity.isMp3 = false; //如果是MP4格式mp3就关闭
           Log.d(TAG,"开始循环视频");
          ADActivity.startAction(MainActivity.mcontext, "Cycle",nextFileName);
         return;
        }else {
           MainActivity.isMp3 = true; //如果是MP4格式mp3就关闭
           start(nextFilepath ,"Cycle");
       }
    }
    public void start( String path ,String mode){
        Mode = mode;
        Log.d(TAG,"开始循环音乐");
        pathfile = path;
        play(0);
    }
   // public ADVideo adVideo = new ADVideo()
    public String nextFilepath;
    public String nextFileName;
    public List<String> playerList;
    public int index = 0;
    String FileDir = Environment.getExternalStorageDirectory().getPath()+"/Movies/";
   /*播放下一首音乐或者视频*/
    public boolean getFiles(String url) {
        playerList = new ArrayList<String>();
        try {
            File file = new File(url);
            File[] files = file.listFiles();
            for (int i = 0; i <files.length ; i++) {
                if (files[i].isDirectory()) {
                    Log.i(TAG, "getFiles find Directory");
                    getFiles(files[i].getAbsolutePath());
                }else {
                    if (
                        files[i].getAbsolutePath().endsWith(".mp4")||
                        files[i].getAbsolutePath().endsWith(".avi")||
                        files[i].getAbsolutePath().endsWith(".3gp")||
                        files[i].getAbsolutePath().endsWith(".mp3")
                            ) {
                        playerList.add(files[i].toString());
                  }
              }
           }
                Log.d("getfile", "playerList next 顺序 ："+playerList);
                if (playerList.isEmpty()){
                    return false;
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
     }

   public int findIndexOfStringInvideoList(String str){
        for(int i=0;i<playerList.size();i++) {
            if(playerList.get(i).equals(str)) {
                return i;
            }
        }
        return -1;
    }

    public static void startAction(Context context, String url) {
        File file = new File(url);
        Log.d(TAG, "--"+file.exists());
        if ( !file.exists() ) {
            Log.d(TAG, "startPlayerService: File:" + url + " not exist! startPlayerService no effective");
            return;
        }
        stopAction(context);//先停止在开启;
        Intent playIntent = new Intent();
        mp3Url = url;
        Log.d(TAG, "startPlayerService: starting PlayService");
        playIntent.setClass(context, MPlayerService.class);
        context.startService(playIntent);       //启动服务
    }

    public static void stopAction(Context context) {
        Intent stopIntent = new Intent();
        Log.d(TAG, "stopPlayerService: starting stopService");
        stopIntent.setClass(context, MPlayerService.class);
        context.stopService(stopIntent);       //关闭服务
    }
}