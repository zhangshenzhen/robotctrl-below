package com.kjn.videoview;

import android.media.MediaPlayer;
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

    private void next() {
        if (++index >= videoList.size()) {
            index = 0;
        }
        Log.d(TAG, "next: 正在播放" + videoList.get(index));
        videoView.setVideoPath(videoList.get(index));
        videoView.start();
    }

    public void play(){
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
