package com.card;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.brick.robotctrl.R;
import com.rg2.activity.BaseActivity;

public class InserCard extends BaseActivity {

    private static final String TAG = "InserCard";


    @Override
    protected void initViews(Bundle savedInstanceState) {
        setContentView(R.layout.activity_inser_card);
        //需要相关检测功能检测是否插卡.
        }



    @Override
    protected void updatePresentation() {

    }

    @Override
    protected void initData() {

    }



    @Override
    protected void initEvent() {
        //模拟读卡操作;
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    sleep(3000);
                    startActivity(new Intent(InserCard.this,SettingPasswordActivity.class));
                    Log.d(TAG,"开启新的界面");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }

    @Override
    protected void initViewData() {

    }
}
