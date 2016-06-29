package com.brick.robotctrl;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.os.Bundle;
import com.jly.idcard.IDcard;


public class MenuActivity extends BaseActivity {
    private final String TAG = "MenuActivity";

    ImageButton IDButton = null;
    ImageButton ADButton = null;
    ImageButton testButton = null;
    ImageButton busButton = null;
    UserTimer userTimer = null;

    private RelativeLayout menuActivity = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);


        IDButton = (ImageButton) findViewById(R.id.IDButton);
        IDButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userTimer.clearTimerCount();
                startActivity(new Intent().setClass(MenuActivity.this, IDcard.class));

            }
        });

        ADButton = (ImageButton) findViewById(R.id.ADButton);
        ADButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userTimer.clearTimerCount();
                Log.d(TAG, "onClick: starting ADActivity");
                startActivity(new Intent().setClass(MenuActivity.this, ADActivity.class));
            }
        });

        testButton = (ImageButton) findViewById(R.id.testButton);
        testButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    userTimer.clearTimerCount();
                    startActivity(new Intent().setClass(MenuActivity.this, TestActivity.class));
                }
            }
        );

        busButton = (ImageButton) findViewById(R.id.busButton);
        busButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    userTimer.clearTimerCount();
                    startActivity(new Intent().setClass(MenuActivity.this, QuestTestActivity.class));
                }
            }
        );

        userTimer = new UserTimer();

    }



    @Override
    protected void onStop() {
        Log.i(TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onRestart() {
        Log.i(TAG, "onRestart");
        userTimer.clearTimerCount();
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy");
        super.onDestroy();
    }
}
