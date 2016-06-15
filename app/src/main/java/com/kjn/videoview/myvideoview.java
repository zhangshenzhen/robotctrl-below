package com.kjn.videoview;

import android.media.MediaPlayer;
import android.os.Environment;
import android.util.Log;
import android.widget.VideoView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ${kang} on 2016/6/15.
 */

public class myvideoview {
    private VideoView videoView;
    private List<String> list;
    private int index = 0;
    private    String videopath;

    public myvideoview (VideoView videoView){
        this.videoView = videoView;
        list = new ArrayList<String>();
        videopath = Environment.getExternalStorageDirectory()
              .getPath()+"/data";
        getFiles(videopath);
        videoView.setVideoPath(list.get(index));             //获得第一个video的路径
        videoView.start();                                   //开始播放
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {  //监听视频播放块结束时，做next操作
          @Override
          public void onCompletion(MediaPlayer mp) {
              next();
          }
        });
    }


    private void getFiles(String url) {
        try {
            File file = new File(url);
            File[] files = file.listFiles();

            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    getFiles(files[i].getAbsolutePath());
                } else {
                    if (files[i].getAbsolutePath().endsWith(".3gp")
                            || files[i].getAbsolutePath().endsWith(".mp4")) {
                        list.add(files[i].toString());
                        System.out.println(files[i].toString());
                    }
                }
            }
        } catch (Exception e) {
            Log.d("getfile", "查找异常!");
        }

    }

    private void next(){
        if (++index >= list.size()) {
            index = 0;
            videoView.setVideoPath(list.get(index));
            videoView.start();
        } else {
            videoView.setVideoPath(list.get(index));
            videoView.start();
            //System.out.println("111111111111111");
        }
    }
}
