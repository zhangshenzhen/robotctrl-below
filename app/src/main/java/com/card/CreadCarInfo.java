package com.card;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.brick.robotctrl.R;
import com.jly.idcard.IDcardActivity;
import com.rg2.activity.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by shenzhen on 2017/2/10.
 */

public class CreadCarInfo extends BaseActivity {

    private static final String TAG ="CreadCarInfo";
    @Bind(R.id.tv_card_details)
    TextView tvCardDetails;
    @Bind(R.id.btn_back)
    Button btnBack;
    @Bind(R.id.btn_next)
    Button btnNext;

    @Override
    protected void updatePresentation() {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setContentView(R.layout.activity_creadcardinfo);
        ButterKnife.bind(this);
    }

    @Override
    protected void initEvent() {

    }

    @Override
    protected void initViewData() {

    }


    @OnClick({R.id.btn_back, R.id.btn_next})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_next:
                //激活信息采集软件；
                Log.d(TAG,"激活信息采集软件1");
                startActivity(new Intent(CreadCarInfo.this, IDcardActivity.class));
                Log.d(TAG,"激活信息采集软件2");
                break;
        }
    }
}
