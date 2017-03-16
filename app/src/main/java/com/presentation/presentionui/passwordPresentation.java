package com.presentation.presentionui;

import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.widget.TextView;

import com.brick.robotctrl.R;
import com.presentation.BasePresentation;

/**
 * Created by shenzhen on 2017/1/13.
 */

public class PasswordPresentation extends  BasePresentation {


    private TextView password;

    public PasswordPresentation(Context outerContext, Display display) {
        super(outerContext, display);
    }
    @Override
      protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.persentation_password);
        password = (TextView) findViewById(R.id.tv_password);
      }
      public void initViewData(){

     }
    public void passWordError(){
        password.setText("您输入的密码有误,请重新输入");
    }
    public void passWordDiff(){
        password.setText("您两次输入的密码不一致,请重新输入");
    }
    public void passWord(){
        password.setText("您的密码已设置完成");
    }

}
