package com.card;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.brick.robotctrl.R;
import com.rg2.activity.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by shenzhen on 2017/2/10.
 */

public class BusinessCarInfo extends BaseActivity {

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
        setContentView(R.layout.activity_business_cardinfo);
        ButterKnife.bind(this);
    }
    @Override
    protected void initViewData() {
        Intent intent = getIntent();
        String cardDetails = intent.getStringExtra("cardDetails");
        tvCardDetails.setText(cardDetails);

    }

    @Override
    protected void initEvent() {

    }



}
