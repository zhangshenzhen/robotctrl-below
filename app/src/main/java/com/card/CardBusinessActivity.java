package com.card;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;

import com.brick.robotctrl.R;
import com.rg2.activity.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CardBusinessActivity extends BaseActivity {
    public final static String TAG = "CardBusinessActivity";
    @Bind(R.id.cb_agree)
    CheckBox cbAgree;
    @Bind(R.id.btn_back)
    Button btnBack;
    @Bind(R.id.btn_next)
    Button btnNext;


    @Override
    protected void initViews(Bundle savedInstanceState) {
        setContentView(R.layout.activity_business_card);
        ButterKnife.bind(this);
    }


    @Override
    protected void initData() {

    }


    @Override
    protected void initEvent() {

    }

    @Override
    protected void initViewData() {

    }

    @Override
    protected void updatePresentation() {


    }

    @Override
    protected void onResume() {
        super.onResume();
        updatePresentation();

    }


    @OnClick({/*R.id.cb_agree,*/ R.id.btn_back, R.id.btn_next})
    public void onClick(View view) {
        switch (view.getId()) {
        /*    case R.id.cb_agree:
                break;*/
            case R.id.btn_back:
                Log.e(TAG, "退出卡片办理业务");
                finish();
                break;
            case R.id.btn_next:
                 if(cbAgree.isChecked()){ //同意用户协议;
                  Log.e(TAG,"同意用户协议，进入下一页");
                 }

                break;
        }
    }
}
