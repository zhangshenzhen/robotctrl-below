package com.kjn.videoview;

import android.media.MediaPlayer;
import android.util.Log;
import android.widget.VideoView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ADVideo {
    private static final String TAG = "ADVideo";
    private static VideoView videoView;
    private static List<String> videoList;
    private static int index = 0;
    private int per;

    public ADVideo (VideoView videoView){
        this.videoView = videoView;
    }

    public boolean getFiles(String url) {
        boolean flag = true;
        videoList = new ArrayList<String>();
        try {
            File file = new File(url);
            File[] files = file.listFiles();

            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    getFiles(files[i].getAbsolutePath());
                } else {
                    if (files[i].getAbsolutePath().endsWith(".3gp")
                            || files[i].getAbsolutePath().endsWith(".mp4")) {
                        videoList.add(files[i].toString());
//                        System.out.println(files[i].toString());
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
        for(int i = 0; i<videoList.size(); i++) {
            Log.d(TAG, "getFiles: " + videoList.get(i));
        }
        return flag;
    }

    static int  findIndexOfStringInvideoList(String  str)
    {

        for(int i=0;i<videoList.size();i++) {
            if(videoList.get(i).equals(str)) {
                return i;
            }
        }
        return -1;
    }
     public static void playcyclewhat(String str)//从那个字符串表示的视频开始播放并循环
    {
        index=findIndexOfStringInvideoList(str);
        play();
    }
    public static void playsinglecyclewhat(String str)
    {
        index=findIndexOfStringInvideoList(str);

        videoView.setVideoPath(videoList.get(index));             //获得第一个video的路径
        Log.d(TAG, "play: starting play: " + videoList.get(index));
        videoView.start();                                   //开始播放
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {  //监听视频播放块结束时，做next操作
            @Override
            public void onCompletion(MediaPlayer mp) {//这是一个匿名类，对该父类mediaplayer.oncompletionlistener中的oncompletion进行了重写
                videoView.setVideoPath(videoList.get(index));
                Log.d(TAG, "play: starting play: " + videoList.get(index));
                videoView.start();
            }
        });
    }
    public static void playsinglewhat(String str)/////////////////////////
    {
        index=findIndexOfStringInvideoList(str);
        videoView.setVideoPath(videoList.get(index));             //获得第一个video的路径
        Log.d(TAG, "play: starting play: " + videoList.get(index));
        videoView.start();                                   //开始播放
    }
    private static void next() {//播放videolist中下一首音乐
        if (++index >= videoList.size()) {
            index = 0;
        }
        Log.d(TAG, "next: 正在播放" + videoList.get(index));
        videoView.setVideoPath(videoList.get(index));
        videoView.start();
    }

    public static void  play(){//从已经检索到的音乐列表之中中挑选一首音乐来播放,播完后下一首
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

    public void  stopPlayBack(){
        videoView.stopPlayback();
    }

    public void start(){
        videoView.seekTo(per);
        videoView.start();
    }
}
