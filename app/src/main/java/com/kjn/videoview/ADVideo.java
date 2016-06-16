package com.kjn.videoview;

import android.media.MediaPlayer;
import android.util.Log;
import android.widget.VideoView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ${kang} on 2016/6/16.
 */

public class ADVideo {
    private VideoView videoView;
    private List<String> videoList;
    private int index = 0;


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
                        System.out.println(files[i].toString());
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
        return flag;
    }

    private void next(){
        if (++index >= videoList.size()) {
            index = 0;
        }
            videoView.setVideoPath(videoList.get(index));
            videoView.start();
    }

    public void play(){
        videoView.setVideoPath(videoList.get(index));             //获得第一个video的路径
        videoView.start();                                   //开始播放
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {  //监听视频播放块结束时，做next操作
            @Override
            public void onCompletion(MediaPlayer mp) {
                next();
            }
        });
    }
}
