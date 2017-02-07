package com.card;

import android.media.MediaRouter;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.brick.robotctrl.R;
import com.presentation.SelecttPresentation;
import com.presentation.presentionui.CardPresentation;
import com.rg2.activity.BaseActivity;

public class CardBusinessActivity extends BaseActivity {

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setContentView(R.layout.activity_business_card);
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
}
