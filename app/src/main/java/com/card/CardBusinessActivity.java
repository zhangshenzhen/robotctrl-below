package com.card;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.brick.robotctrl.R;
import com.rg2.activity.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CardBusinessActivity extends BaseActivity {

    @Bind(R.id.tv_back)
    TextView tvBack;

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setContentView(R.layout.activity_business_card);
        ButterKnife.bind(this);
    }


    @Override
    protected void initData() {
    tvBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
      finish();
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


}
