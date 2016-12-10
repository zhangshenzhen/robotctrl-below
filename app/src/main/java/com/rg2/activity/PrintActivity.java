package com.rg2.activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import com.brick.robotctrl.*;
import com.rg2.utils.StringUtils;

import java.io.UnsupportedEncodingException;
import java.util.Date;

/**
 * Created by Brick on 2016/12/10.
 */
public class PrintActivity  extends BaseActivity{

    private Button mSubmitBnt1,mSubmitBnt2,mSubmitBnt3;
    SerialCtrl serialCtrlPrinter=null;
    @Override
    protected void initData() {

    }

    @Override
    protected void initViews(Bundle savedInstanceState) {


        setContentView(R.layout.activity_print);
        mSubmitBnt1= (Button)findViewById(R.id.btn_submit1);
        mSubmitBnt2= (Button)findViewById(R.id.btn_submit2);
        mSubmitBnt3= (Button)findViewById(R.id.btn_submit3);
    }

    @Override
    protected void initEvent() {
        mSubmitBnt1.setOnClickListener(this);
        mSubmitBnt2.setOnClickListener(this);
        mSubmitBnt3.setOnClickListener(this);
    }

    @Override
    protected void initViewData() {
        serialCtrlPrinter=new SerialCtrl(PrintActivity.this, new Handler(),"ttyUSB1",9600,"printer");
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        if(v == mSubmitBnt1 )
        {
            print1();
        }
        else if(v == mSubmitBnt2 )
        {
            print2();
        }
        else if(v == mSubmitBnt3 )
        {
            print3();
        }
    }


    private  void print1()
    {
       //  String str ="1234567890ABCDEFGHIJ中华人民共和";
        //  String str="中华人民共和";
        String time = StringUtils.getDateToString(new Date());

        String str0 ="                                ";
        String str1 ="                      2016-12-12";
        String str2 ="                                ";
        String str3 ="                                ";
        String str4 ="               8号              ";
        String str5 ="                                ";
        String str6 ="                                ";
        String str7 ="对公业务    柜台032             ";
        String str8 ="                                ";
        String str9 ="                                ";
        sendPortText(str9);
        sendPortText(str9);
        sendPortText(str9);
        sendPortText(str9);
        sendPortText(str9);
        sendPortText(str8);
        sendPortText(str7);
        sendPortText(str6);
        sendPortText(str5);
        sendPortText(str4);
        sendPortText(str3);
        sendPortText(str2);
        sendPortText(str1);
        sendPortText(str0);
        sendPortText(str0);
        sendPortText(str0);
        sendPortText(str0);
        sendPortText(str0);
    }

    private  void print2()
    {
        //  String str ="1234567890ABCDEFGHIJ中华人民共和";
        String time = StringUtils.getDateToString(new Date());

        String str0 ="                                ";
        String str1 ="                      2016-12-12";
        String str2 ="                                ";
        String str3 ="                                ";
        String str4 ="               8号              ";
        String str5 ="                                ";
        String str6 ="                                ";
        String str7 ="对私业务    柜台032             ";
        String str8 ="                                ";
        String str9 ="                                ";
        sendPortText(str9);
        sendPortText(str9);
        sendPortText(str9);
        sendPortText(str9);
        sendPortText(str9);
        sendPortText(str8);
        sendPortText(str7);
        sendPortText(str6);
        sendPortText(str5);
        sendPortText(str4);
        sendPortText(str3);
        sendPortText(str2);
        sendPortText(str1);
        sendPortText(str0);
        sendPortText(str0);
        sendPortText(str0);
        sendPortText(str0);
        sendPortText(str0);
    }

    private  void print3()
    {
        //  String str ="1234567890ABCDEFGHIJ中华人民共和";
        String time = StringUtils.getDateToString(new Date());

        String str0 ="                                ";
        String str1 ="                      2016-12-12";
        String str2 ="                                ";
        String str3 ="                                ";
        String str4 ="               8号              ";
        String str5 ="                                ";
        String str6 ="                                ";
        String str7 ="理财业务    柜台032             ";
        String str8 ="                                ";
        String str9 ="                                ";
        sendPortText(str9);
        sendPortText(str9);
        sendPortText(str9);
        sendPortText(str9);
        sendPortText(str9);
        sendPortText(str8);
        sendPortText(str7);
        sendPortText(str6);
        sendPortText(str5);
        sendPortText(str4);
        sendPortText(str3);
        sendPortText(str2);
        sendPortText(str1);
        sendPortText(str0);
        sendPortText(str0);
        sendPortText(str0);
        sendPortText(str0);
        sendPortText(str0);
    }


    private void sendPortText(String content)
    {
        byte[] temp = null;
        try {
            temp=content.getBytes("gbk");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        serialCtrlPrinter.sendPortText(serialCtrlPrinter.ComA,temp);
    }


}
