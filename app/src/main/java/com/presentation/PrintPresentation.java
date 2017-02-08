package com.presentation;

import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.TextView;

import com.brick.robotctrl.R;

/**
 * Created by shenzhen on 2017/1/13.
 */

public class PrintPresentation extends  BasePresentation {

    private TextView mprint;
    private TextView mpaper;

    public PrintPresentation(Context outerContext, Display display) {
        super(outerContext, display);
    }

    @Override
      protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.persentation_print);
      }
    public void initViewData(){
         mprint = (TextView) findViewById(R.id.print);
        mpaper = (TextView) findViewById(R.id.paper);

        boolean ispaper = false;//模拟判断机器内部是否有纸质小票;
        if(ispaper ){
          mpaper.setVisibility(View.GONE);
        }else {
            mprint.setVisibility(View.GONE);
            mpaper.setVisibility(View.VISIBLE);
        }

    };
}
