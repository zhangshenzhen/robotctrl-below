package com.presentation.presentionui;

import android.content.Context;
import android.os.Bundle;
import android.view.Display;

import com.brick.robotctrl.R;
import com.presentation.BasePresentation;

/**
 * Created by shenzhen on 2017/1/13.
 */

public class SelectCardPresentation extends  BasePresentation {



    public SelectCardPresentation(Context outerContext, Display display) {
        super(outerContext, display);
    }
    @Override
      protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.persentation_selece_card);
      }
     public void initViewData(){}




}
