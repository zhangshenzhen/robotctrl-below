package com.presentation.presentionui;

import android.content.Context;
import android.os.Bundle;
import android.view.Display;

import com.brick.robotctrl.R;
import com.presentation.BasePresentation;

/**
 * Created by lx on 2017/2/17.
 */

public class InfoCompletePresentaion extends BasePresentation {

    public InfoCompletePresentaion(Context outerContext, Display display) {
        super(outerContext, display);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.persentation_info_complete);
    }

    public void initViewData(){}
}
