package com.presentation.presentionui;

import android.content.Context;
import android.os.Bundle;
import android.view.Display;

import com.brick.robotctrl.R;
import com.presentation.BasePresentation;

/**
 * Created by shenzhen on 2017/1/13.
 */

public class UserInfoPresentation extends  BasePresentation {



    public UserInfoPresentation(Context outerContext, Display display) {
        super(outerContext, display);
    }
    @Override
      protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.persentation_userinfo);
      }
     public void initViewData(){}

    public void setText(){

    }

}
