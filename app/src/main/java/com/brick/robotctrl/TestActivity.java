package com.brick.robotctrl;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import java.util.Timer;
import java.util.TimerTask;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        // relative timer
        Timer timer = new Timer(true);
        timer.schedule(timeOutTask, 1*60*1000, 1*60*1000);  //60min
        // timer.cancel(); //退出计时器
    }

    TimerTask timeOutTask = new TimerTask() {
        @Override
        public void run() {
            startActivity(new Intent().setClass(TestActivity.this, MenuActivity.class));
        }
    };
}
