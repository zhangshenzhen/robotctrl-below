package com.financial;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.brick.robotctrl.R;
import com.rg2.activity.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FinanceIntroduceList extends BaseActivity {


    @Bind(R.id.tv_back_select)
    TextView tvBackSelect;
    @Bind(R.id.activity_finance_introduce_list)
    LinearLayout activityFinanceIntroduceList;


    @Override
    protected void initViews(Bundle savedInstanceState) {
        setContentView(R.layout.activity_finance_list);
        ButterKnife.bind(this);

    }
    @Override
    protected void initEvent() {
        tvBackSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    @Override
    protected void updatePresentation() {

    }

    @Override
    protected void initData() {

    }




    @Override
    protected void initViewData() {

    }

}
