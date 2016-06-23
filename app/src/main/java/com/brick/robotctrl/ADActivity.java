package com.brick.robotctrl;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

import com.kjn.videoview.ADVideo;

public class ADActivity extends BaseActivity  {
    private final String TAG = "ADActivity";

    private VideoView videoView;
    ADVideo adVideo = null;
    private String videoPath;
    private boolean flag = true;



//    private View mVolumeBrightnessLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad);

        // videoview 实现
        videoView = (VideoView) findViewById(R.id.videoView);
//      mVolumeBrightnessLayout = findViewById(R.id.operation_volume_brightness);






//        videoView.setOnTouchListener(this);
//        videoView.setMediaController(new MediaController(this));  //不需要注释掉即可
        adVideo = new ADVideo(videoView);
        videoPlay();



        View decorView = getWindow().getDecorView();
//        Hide both the navigation bar and the status bar.
//        SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
//        a general rule, you should design your app to hide the status bar whenever you
//        hide the navigation bar.
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE;
        decorView.setSystemUiVisibility(uiOptions);
    }

    private void showVideoDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(ADActivity.this);
        builder.setTitle("提示");
        builder.setMessage("路径中无视频文件");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

//    @Override
//    public boolean onTouch(View v, MotionEvent event){
//        Log.d(TAG, "onTouch: to MainActivity");
//        startActivity(new Intent().setClass(ADActivity.this, MainActivity.class));
//        return true;
//    }

    public void videoPlay(){
        videoPath = Environment.getExternalStorageDirectory()
                .getPath()+"/Movies";
        flag = adVideo.getFiles(videoPath);
        if (flag) {
            new Thread() {
                @Override
                public void run() {
                    adVideo.play();
                }
            }.start();
        }
        else {
            showVideoDialog();
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
        View decorView = getWindow().getDecorView();
//        Hide both the navigation bar and the status bar.
//        SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
//        a general rule, you should design your app to hide the status bar whenever you
//        hide the navigation bar.
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE;
        decorView.setSystemUiVisibility(uiOptions);
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy");
        super.onDestroy();
    }

    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Log.d(TAG, "onTouch: to MainActivity");
            startActivity(new Intent().setClass(ADActivity.this, MainActivity.class));
            return true;

        }
    }
}
