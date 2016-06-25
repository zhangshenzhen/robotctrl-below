package com.brick.robotctrl;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ant.liao.GifView;
import com.jly.expression.expression;
import com.jly.idcard.IDcard;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by kjnijk on 2016-06-24.
 */
public class NoQueryActivity extends BaseActivity {
    private static final String TAG = "NoQueryActivity";
    private String mp3Url = "/sdcard/Movies/record4.m4a";         //播放的MP3文件
    private GifView gf;

    private TextView text;
    private String showText;
    private Button humanButton;
    private Button askButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);

        text = (TextView) findViewById(R.id.showText);
        Intent intent = getIntent();
        showText = intent.getStringExtra("extra_showResult");
        Log.d("extra_showResult", showText);
        text.setText(showText);

        gf =(GifView)findViewById(R.id.gif3);
        gf.setGifImage(R.drawable.smile);
        gf.setGifImageType(GifView.GifImageType.COVER);
        gf.setShowDimension(640,400);

        humanButton = (Button) findViewById(R.id.humanButton);
        humanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expression.startExpressionActivity(NoQueryActivity.this, "0");
            }
        });

        askButton = (Button) findViewById(R.id.askButton);
        askButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent().setClass(NoQueryActivity.this, QuestTestActivity.class));
            }
        });


/////////////////////////////MP3播放
//        Intent playIntent = new Intent();
//        playIntent.putExtra("url", mp3Url);
////        intent.putExtra("MSG", 0);
//        Log.d(TAG, "onCreate: starting PlayService");
//        playIntent.setClass(NoQueryActivity.this, PlayerService.class);
//        startService(playIntent);       //启动服务
//
//        Timer timer = new Timer(true);
//        timer.schedule(queryTask, 200, 200); //延时1000ms后执行，1000ms执行一次
        PlayerService.startPlayerService(NoQueryActivity.this, mp3Url);

/////////////////////////////



    }






    private int countForPlayer = 0;
    TimerTask queryTask = new TimerTask() {
        @Override
        public void run() {
            ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
//            Log.d(TAG, "pkg:"+cn.getPackageName());
//            Log.d(TAG, "cls:"+cn.getClassName());

            if ( cn.getClassName().equals("com.brick.robotctrl.NoQueryActivity") ) {
                countForPlayer++;
                Log.d(TAG, "run: countForPlayer:" + countForPlayer);
                if ( countForPlayer == 30*1000/200 ) {
                    PlayerService.startPlayerService(NoQueryActivity.this, mp3Url);
                    countForPlayer = 0;
                }
            } else if (!cn.getClassName().equals("com.brick.robotctrl.ADActivity")) {
                userTimer.addTimerCount();
//                Log.d(TAG, "run: userTimer" + userTimer.getTimerCount());
            }
            if(userTimer.getTimerCount() > (10*60*1000/200)) {
//                Log.d(TAG, "Timeout to play video");
                startActivity(new Intent().setClass(NoQueryActivity.this, ADActivity.class));
                userTimer.clearTimerCount();
            }
        }
    };
    @Override
    protected void onStop() {
        Log.i(TAG, "onStop");
        Intent stopIntent = new Intent();
        stopIntent.putExtra("url", mp3Url);
//        intent.putExtra("MSG", 0);
        Log.d(TAG, "onCreate: starting PlayService");
        stopIntent.setClass(NoQueryActivity.this, PlayerService.class);
        stopService(stopIntent);
        super.onStop();
    }

    @Override
    protected void onRestart() {
        Log.i(TAG, "onRestart");
        countForPlayer = 0;
        PlayerService.startPlayerService(NoQueryActivity.this, mp3Url);
        super.onRestart();
    }
}
