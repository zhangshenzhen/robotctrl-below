package com.brick.robotctrl;


import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.IBinder;
import android.util.Log;
import java.io.File;

@SuppressLint("NewApi")
public class PlayerService extends Service {
    private static final String TAG = "PlayerService";
    private MediaPlayer mediaPlayer = new MediaPlayer();       //媒体播放器对象
    private String path;                        //音乐文件路径
    private boolean isPause;                    //暂停状态

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mediaPlayer.isPlaying()) {
            stop();
        }
        path = intent.getStringExtra("url");
        Log.d(TAG, "onStartCommand: " + path);
//        int msg = intent.getIntExtra("MSG", 0);
//        if(msg == AppConstant.PlayerMsg.PLAY_MSG) {
        play(0);
//        } else if(msg == AppConstant.PlayerMsg.PAUSE_MSG) {
//            pause();
//        } else if(msg == AppConstant.PlayerMsg.STOP_MSG) {
//            stop();
//        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void play(int position) {
        try {
            mediaPlayer.reset();//把各项参数恢复到初始状态
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();  //进行缓冲
            mediaPlayer.setOnPreparedListener(new PreparedListener(position));//注册一个监听器
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 暂停音乐
     */
    private void pause() {
        Log.d(TAG, "pause: ");
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPause = true;
        }
    }

    /**
     * 停止音乐
     */
    private void stop() {
        Log.d(TAG, "stop: ");
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            try {
                mediaPlayer.prepare(); // 在调用stop后如果需要再次通过start进行播放,需要之前调用prepare函数
            } catch (Exception e) {
                e.printStackTrace();
            }
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
     * 实现一个OnPrepareLister接口,当音乐准备好的时候开始播放
     */
    private final class PreparedListener implements OnPreparedListener {
        private int positon;

        public PreparedListener(int positon) {
            this.positon = positon;
        }

        @Override
        public void onPrepared(MediaPlayer mp) {
            mediaPlayer.start();    //开始播放
            if (positon > 0) {    //如果音乐不是从头播放
                mediaPlayer.seekTo(positon);
            }
        }
    }


    public static void startPlayerService(Context context, String url) {
        File file = new File(url);
        if ( !file.exists() ) {
            Log.d(TAG, "startPlayerService: File:" + url + " not exist! startPlayerService no effective");
            return;
        }
        stopPlayerService(context);

        Intent playIntent = new Intent();
        playIntent.putExtra("url", url);
//        intent.putExtra("MSG", 0);
        Log.d("", "startPlayerService: starting PlayService");
        playIntent.setClass(context, PlayerService.class);
        context.startService(playIntent);       //启动服务
    }

    public static void stopPlayerService(Context context) {
        Intent stopIntent = new Intent();
        Log.d("", "stopPlayerService: starting stopService");
        stopIntent.setClass(context, PlayerService.class);
        context.stopService(stopIntent);       //关闭服务
    }
}