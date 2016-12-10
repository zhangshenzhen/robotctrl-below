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
           String str ="                      "+"2016-12-12";
           String str1 ="                                ";
           String str2 ="                                ";

          String str3 ="            7号            ";
          String str4 ="                                ";
           String str5 ="                                ";
           String str6="对公业务    柜台031         ";
           String str7 ="                                ";
           String str8 ="                                ";
           sendPortText(str0);
           sendPortText(str);
        sendPortText(str1);
        sendPortText(str2);
        sendPortText(str3);
        sendPortText(str4);
        sendPortText(str5);
        sendPortText(str6);
        sendPortText(str7);
        sendPortText(str8);


    }

    private  void print2()
    {
        //  String str ="1234567890ABCDEFGHIJ中华人民共和";
        String time = StringUtils.getDateToString(new Date());
        String str ="                      "+time;
        String str1 ="            8号            ";
        String str2="对私业务    柜台032         ";
        byte[] temp=null;
        try {
            temp=str.getBytes("gbk");//这里写原编码方式
        }catch (Exception E){
            E.printStackTrace();
        }
        serialCtrlPrinter.sendPortText(serialCtrlPrinter.ComA,temp);
        try {
            temp=str1.getBytes("gbk");//这里写原编码方式
        }catch (Exception E){
            E.printStackTrace();
        }
        serialCtrlPrinter.sendPortText(serialCtrlPrinter.ComA,temp);
        try {
            temp=str2.getBytes("gbk");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        serialCtrlPrinter.sendPortText(serialCtrlPrinter.ComA,temp);
    }

    private  void print3()
    {
        //  String str ="1234567890ABCDEFGHIJ中华人民共和";
        String time = StringUtils.getDateToString(new Date());
        String str ="                      "+time;
        String str1 ="            9号            ";
        String str2="理财业务    柜台033         ";
        byte[] temp=null;
        try {
            temp=str.getBytes("gbk");//这里写原编码方式
        }catch (Exception E){
            E.printStackTrace();
        }
        serialCtrlPrinter.sendPortText(serialCtrlPrinter.ComA,temp);
        try {
            temp=str1.getBytes("gbk");//这里写原编码方式
        }catch (Exception E){
            E.printStackTrace();
        }
        serialCtrlPrinter.sendPortText(serialCtrlPrinter.ComA,temp);
        try {
            temp=str2.getBytes("gbk");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        serialCtrlPrinter.sendPortText(serialCtrlPrinter.ComA,temp);
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
