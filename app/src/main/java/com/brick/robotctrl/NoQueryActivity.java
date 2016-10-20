package com.brick.robotctrl;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ant.liao.GifView;
import com.zhangyt.log.LogUtil;

public class NoQueryActivity extends BaseActivity {
    private static final String TAG = "NoQueryActivity";
    private String mp3Url = Environment.getExternalStorageDirectory().getPath() + "/Movies/record4.m4a";         //播放的MP3文件
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
        LogUtil.d("extra_showResult", showText);
        text.setText(showText);
        PlayerService.startAction(NoQueryActivity.this, mp3Url);

        gf =(GifView)findViewById(R.id.gif3);
        gf.setGifImage(R.drawable.smile);
        gf.setGifImageType(GifView.GifImageType.COVER);
       // gf.setShowDimension(640,400);

        humanButton = (Button) findViewById(R.id.humanButton);
        humanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                humanButton.setClickable(false);
                clearTimerCount();
                ExpressionActivity.startAction(NoQueryActivity.this, 0);
            }
        });

        askButton = (Button) findViewById(R.id.askButton);
        askButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askButton.setClickable(false);
                clearTimerCount();
                startActivity(new Intent().setClass(NoQueryActivity.this, QuestTestActivity.class));
            }
        });
    }

    @Override
    protected void onStop() {
        LogUtil.i(TAG, "onStop");
        Intent stopIntent = new Intent();
        stopIntent.putExtra("url", mp3Url);
        LogUtil.d(TAG, "onCreate: starting PlayService");
        stopIntent.setClass(NoQueryActivity.this, PlayerService.class);
        stopService(stopIntent);
        super.onStop();
    }

    @Override
    protected void onRestart() {
        LogUtil.i(TAG, "onRestart");
        askButton.setClickable(true);
        humanButton.setClickable(true);
        super.onRestart();
    }
}
