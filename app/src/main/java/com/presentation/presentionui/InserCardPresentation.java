package com.presentation.presentionui;

import android.content.Context;
import android.os.Bundle;
import android.view.Display;

import com.brick.robotctrl.R;
import com.presentation.BasePresentation;

/**
 * Created by shenzhen on 2017/1/13.
 */

public class InserCardPresentation extends  BasePresentation {



    public InserCardPresentation(Context outerContext, Display display) {
        super(outerContext, display);
    }
    @Override
      protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.persentation_insercard);
      }
     public void initViewData(){}




}
