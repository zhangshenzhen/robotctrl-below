package com.brick.robotctrl;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.fragment.FunctionActivity;
import com.jly.idcard.IDcardActivity;


public class MenuActivity extends BaseActivity implements View.OnClickListener {
    private final String TAG = "MenuActivity";
    TextView IDButton = null;
    TextView ADButton = null;
    TextView prodButton = null;
    TextView busButton = null;
    TextView aboutButton = null;
    TextView function = null;

    private RelativeLayout menuActivity = null;
    private RelativeLayout reback1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        initData();
    }

    @Override
    protected void updatePresentation() {
    }

    //初始化控件
    private void initData() {
        IDButton = (TextView) findViewById(R.id.textView2);
        ADButton = (TextView) findViewById(R.id.textView3);
        prodButton = (TextView) findViewById(R.id.textView4);
        busButton = (TextView) findViewById(R.id.textView5);
        aboutButton = (TextView) findViewById(R.id.textView6);
        function = (TextView) findViewById(R.id.textView7);
        reback1 = (RelativeLayout) findViewById(R.id.reback);

        IDButton.setOnClickListener(this);
        ADButton.setOnClickListener(this);
        prodButton.setOnClickListener(this);
        busButton.setOnClickListener(this);
        aboutButton.setOnClickListener(this);
        function.setOnClickListener(this);
        reback1.setOnClickListener(this);

    }
    //点击事件;
    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.textView2:
                clearTimerCount();
                startActivity(new Intent(MenuActivity.this, IDcardActivity.class));
                break;
            case R.id.textView3:
                clearTimerCount();
                Log.d(TAG, "onClick: starting ADActivity");
                startActivity(new Intent(MenuActivity.this, SettingsActivity.class));
                break;
            case R.id.textView4:
                clearTimerCount();
                startActivity(new Intent(MenuActivity.this, ManyQueryActivity.class));
                break;
            case R.id.textView5:
                clearTimerCount();
                startActivity(new Intent(MenuActivity.this, QuestTestActivity.class));
                break;
            case R.id.textView6:
                clearTimerCount();
                startActivity(new Intent(MenuActivity.this, AboutActivity.class));
                break;
            case R.id.textView7:
                clearTimerCount();
                startActivity(new Intent(MenuActivity.this, FunctionActivity.class));
                break;
            case R.id.reback:
                finish();//退出当前的Activity;
                break;
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
        clearTimerCount();
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy");
         super.onDestroy();
    }


}
