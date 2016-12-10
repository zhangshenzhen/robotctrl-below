package com.rg2.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * 作者：王先云 on 2016/7/1 10:23
 * 邮箱：wangxianyun1@163.com
 * 描述：一句话简单描述
 */
public abstract class BaseActivity extends Activity implements View.OnClickListener
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        initData();
        initViews(savedInstanceState);
        initEvent();
        initViewData();
    }


    /**
     * 从intent中获取数据
     */
    protected abstract void initData();

    /**
     * 存放从xml中获取ui,例如 findViewById
     */
    protected abstract void initViews(Bundle savedInstanceState);


    /**
     * 初始化页面UI事件,例如 setOnClickListener
     */
    protected abstract void initEvent();

    /**
     * 存放刷新页面的代码和初始化数据
     */
    protected abstract void initViewData();

    @Override
    public void onClick(View v)
    {

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        float x,y;
        x = event.getX();
        //250
        y=event.getY();
        Log.d("TAG", "dispatchTouchEvent: "+x+":"+" "+y);
        event.setLocation(x*1280/1024,y*750/768);

        return super.dispatchTouchEvent(event);
    }
}
