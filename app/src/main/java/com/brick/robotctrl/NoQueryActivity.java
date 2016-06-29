package com.brick.robotctrl;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ant.liao.GifView;

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
        PlayerService.startPlayerService(NoQueryActivity.this, mp3Url);

        gf =(GifView)findViewById(R.id.gif3);
        gf.setGifImage(R.drawable.smile);
        gf.setGifImageType(GifView.GifImageType.COVER);
       // gf.setShowDimension(640,400);

        humanButton = (Button) findViewById(R.id.humanButton);
        humanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ExpressionActivity.startExpressionActivity(NoQueryActivity.this, "0");
            }
        });

        askButton = (Button) findViewById(R.id.askButton);
        askButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent().setClass(NoQueryActivity.this, QuestTestActivity.class));
            }
        });
    }

    @Override
    protected void onStop() {
        Log.i(TAG, "onStop");
        Intent stopIntent = new Intent();
        stopIntent.putExtra("url", mp3Url);
        Log.d(TAG, "onCreate: starting PlayService");
        stopIntent.setClass(NoQueryActivity.this, PlayerService.class);
        stopService(stopIntent);
        super.onStop();
    }

    @Override
    protected void onRestart() {
        Log.i(TAG, "onRestart");
        super.onRestart();
    }
}
