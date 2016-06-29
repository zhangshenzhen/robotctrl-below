package com.brick.robotctrl;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.ant.liao.GifView;

/**
 * Created by kjnijk on 2016-06-24.
 */
public class NoAnswerQueryActivity extends BaseActivity {
    private static final String TAG = "NoAnswerQueryActivity";
    private String mp3Url = "/sdcard/Movies/record2.m4a";         //播放的MP3文件
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
        PlayerService.startPlayerService(NoAnswerQueryActivity.this, mp3Url);

        gf =(GifView)findViewById(R.id.gif3);
        gf.setGifImage(R.drawable.smile);
        gf.setGifImageType(GifView.GifImageType.COVER);
        gf.setShowDimension(640,400);

        humanButton = (Button) findViewById(R.id.humanButton);
        humanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearTimerCount();
                ExpressionActivity.startExpressionActivity(NoAnswerQueryActivity.this, "0");
            }
        });

        askButton = (Button) findViewById(R.id.askButton);
        askButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearTimerCount();
                startActivity(new Intent().setClass(NoAnswerQueryActivity.this, QuestTestActivity.class));
            }
        });
    }

    @Override
    protected void onStop() {
        Log.i(TAG, "onStop");
        Intent stopIntent = new Intent();
        stopIntent.putExtra("url", mp3Url);
        Log.d(TAG, "onCreate: starting PlayService");
        stopIntent.setClass(NoAnswerQueryActivity.this, PlayerService.class);
        stopService(stopIntent);
        super.onStop();
    }

    @Override
    protected void onRestart() {
        Log.i(TAG, "onRestart");
        super.onRestart();
    }
}
