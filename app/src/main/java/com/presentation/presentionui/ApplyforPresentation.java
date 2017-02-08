package com.presentation.presentionui;

import android.content.Context;
import android.os.Bundle;
import android.view.Display;

import com.brick.robotctrl.R;
import com.presentation.BasePresentation;

/**
 * Created by shenzhen on 2017/1/13.
 */

public class ApplyforPresentation extends  BasePresentation {



    public ApplyforPresentation(Context outerContext, Display display) {
        super(outerContext, display);
    }
    @Override
      protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.persentation_applyfor);
      }
     public void initViewData(){}




}
