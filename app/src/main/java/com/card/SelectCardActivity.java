package com.card;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.brick.robotctrl.R;
import com.rg2.activity.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by shenzhen on 2017/2/10.
 */

public class SelectCardActivity extends BaseActivity {
    @Bind(R.id.tv_card1)
    TextView tvCard1;
    @Bind(R.id.tv_card2)
    TextView tvCard2;
    @Bind(R.id.tv_card3)
    TextView tvCard3;
    @Bind(R.id.tv_card4)
    TextView tvCard4;
    @Bind(R.id.tv_card5)
    TextView tvCard5;
    @Bind(R.id.tv_card6)
    TextView tvCard6;
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
        setContentView(R.layout.activity_select_card);
        ButterKnife.bind(this);
    }

    @Override
    protected void initEvent() {

    }

    @Override
    protected void initViewData() {

    }

    @OnClick({R.id.tv_card1, R.id.tv_card2, R.id.tv_card3, R.id.tv_card4,
            R.id.tv_card5, R.id.tv_card6,R.id.btn_back, R.id.btn_next})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_card1:
                break;
            case R.id.tv_card2:
                break;
            case R.id.tv_card3:
                break;
            case R.id.tv_card4:
                break;
            case R.id.tv_card5:
                break;
            case R.id.tv_card6:
                break;
            case R.id.btn_back:
                break;
            case R.id.btn_next:
                break;
        }
    }

}
