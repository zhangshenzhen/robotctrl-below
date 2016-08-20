package com.kjn.videoview;

import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.VideoView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ADVideo {
    private final String TAG = "ADVideo";
    private VideoView videoView;
    private List<String> videoList;
    private int index = 0;
    private static int per = 0;
    private Handler contextHandler = null;
    private final int singleOver = 1;
    private final int PROGRESS = 2;

    public ADVideo (VideoView videoView, Handler handler){
        this.videoView = videoView;
        this.contextHandler = handler;
    }

    public boolean getFiles(String url) {
        boolean flag = true;
      videoList = new ArrayList<String>();
        try {
            File file = new File(url);
            File[] files = file.listFiles();

            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    Log.i(TAG, "getFiles find Directory");
                    getFiles(files[i].getAbsolutePath());
                } else {
                    if (
                        files[i].getAbsolutePath().endsWith(".avi")||
                        files[i].getAbsolutePath().endsWith(".mp4")||
                        files[i].getAbsolutePath().endsWith(".3gp")
 //                       files[i].getAbsolutePath().endsWith(".flv")||
//                        files[i].getAbsolutePath().endsWith(".gif")||
//                        files[i].getAbsolutePath().endsWith(".mkv")||
//                        files[i].getAbsolutePath().endsWith(".mov")||
//                        files[i].getAbsolutePath().endsWith(".mpg")||
 //                       files[i].getAbsolutePath().endsWith(".rmvb")
//                        files[i].getAbsolutePath().endsWith(".swf")||
//                        files[i].getAbsolutePath().endsWith(".vob")||
//                        files[i].getAbsolutePath().endsWith(".wmv")
                         ) {
                        videoList.add(files[i].toString());
                    }
                }
            }
            if (videoList.isEmpty()){
                flag = false;
            }
        } catch (Exception e) {
            Log.d("getfile", "查找异常!");
            System.out.println(e.toString());
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
//        for(int i = 0; i<videoList.size(); i++) {
//            Log.d(TAG, "getFiles: " + videoList.get(i));
//        }
        return flag;
    }

    int findIndexOfStringInvideoList(String str)
    {
        for(int i=0;i<videoList.size();i++) {
            if(videoList.get(i).endsWith(str)) {
                return i;
            }
        }
        return -1;
    }
    public void playCycleWhat(String str)//从那个字符串表示的视频开始播放并循环
    {
        index=findIndexOfStringInvideoList(str);
        Log.d(TAG, "playCycleWhat: " + index);
        play();
    }
    public void playSingleCycleWhat(String str)
    {
        index=findIndexOfStringInvideoList(str);

        videoView.setVideoPath(videoList.get(index));             //获得第一个video的路径
        Log.d(TAG, "play: starting play: " + videoList.get(index));
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                videoView.start();                                   //开始播放
//                contextHandler.sendEmptyMessage(PROGRESS);
//                Log.d(TAG, "onPrepared: PROGRESS");
            }
        });

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {  //监听视频播放块结束时，做next操作
            @Override
            public void onCompletion(MediaPlayer mp) {//这是一个匿名类，对该父类mediaplayer.oncompletionlistener中的oncompletion进行了重写
                videoView.setVideoPath(videoList.get(index));
                Log.d(TAG, "over play: starting play: " + videoList.get(index));
                videoView.start();
            }
        });
    }
    public void playSingleWhat(String str) {
        index=findIndexOfStringInvideoList(str);
        videoView.setVideoPath(videoList.get(index));             //获得第一个video的路径
        Log.d(TAG, "play: starting play: " + videoList.get(index));
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                videoView.start();                                   //开始播放
                contextHandler.sendEmptyMessage(PROGRESS);
                Log.d(TAG, "onPrepared: PROGRESS");
            }
        });
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {  //监听视频播放块结束时，做next操作
            @Override
            public void onCompletion(MediaPlayer mp) {//这是一个匿名类，对该父类mediaplayer.oncompletionlistener中的oncompletion进行了重写
                stopPlayBack();
                Message message = new Message();
                message.what = singleOver;
                message.obj = "nihao";
                contextHandler.sendMessage(message);
//                contextHandler.sendEmptyMessage(PROGRESS);
//                Log.d(TAG, "onPrepared: PROGRESS over");
            }
        });
    }
    private void next() {//播放videolist中下一首音乐
        if (++index >= videoList.size()) {
            index = 0;
        }
        Log.d(TAG, "next: 正在播放" + videoList.get(index));
        videoView.setVideoPath(videoList.get(index));
        videoView.start();
    }

    public void  play(){                     //从已经检索到的音乐列表之中中挑选一首音乐来播放,播完后下一首
        videoView.setVideoPath(videoList.get(index));             //获得第一个video的路径
        Log.d(TAG, "play: starting play: " + videoList.get(index));
        videoView.start();                                   //开始播放
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {  //监听视频播放块结束时，做next操作
            @Override
            public void onCompletion(MediaPlayer mp) {
                next();
            }
        });
    }

    public void pause(){
        videoView.pause();
        per = videoView.getCurrentPosition();
    }

    public void resume(){
        //videoView.seekTo(per);
        videoView.resume();
        videoView.start();

    }

    public void stopPlayBack(){
        videoView.stopPlayback();
    }

    public void start(){
        videoView.seekTo(per);
        videoView.start();
    }
}
