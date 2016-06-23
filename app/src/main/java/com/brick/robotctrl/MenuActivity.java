package com.brick.robotctrl;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.os.Bundle;
import com.jly.idcard.IDcard;


public class MenuActivity extends BaseActivity implements View.OnTouchListener{
    private final String TAG = "MenuActivity";

    Button IDButton = null;
    Button ADButton = null;
    Button testButton = null;
    Button busButton = null;
    UserTimer userTimer = null;

    private RelativeLayout menuActivity = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);


        IDButton = (Button) findViewById(R.id.IDButton);
        IDButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                startActivity(new Intent().setClass(MenuActivity.this, ...));
                userTimer.clearTimerCount();
               // Toast.makeText(MenuActivity.this, "No ID Detector", Toast.LENGTH_SHORT).show();
                startActivity(new Intent().setClass(MenuActivity.this, IDcard.class));

            }
        });

        ADButton = (Button) findViewById(R.id.ADButton);
        ADButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userTimer.clearTimerCount();
                startActivity(new Intent().setClass(MenuActivity.this, ADActivity.class));
            }
        });

        testButton = (Button) findViewById(R.id.testButton);
        testButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    userTimer.clearTimerCount();
                    startActivity(new Intent().setClass(MenuActivity.this, TestActivity.class));
                }
            }
        );

        busButton = (Button) findViewById(R.id.busButton);
        busButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    userTimer.clearTimerCount();
                    startActivity(new Intent().setClass(MenuActivity.this, QuestActivity.class));
                }
            }
        );

        userTimer = new UserTimer();

        menuActivity = (RelativeLayout) findViewById(R.id.menuActivity);
        menuActivity.setOnTouchListener(this);
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
    @Override
    public boolean onTouch(View v, MotionEvent event){
        Log.d(TAG, "OnTouch: Touch Screen");
        userTimer.clearTimerCount();
        return true;
    }
}
