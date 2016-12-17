package com.rg2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.brick.robotctrl.R;
import com.rg2.utils.LogUtil;
import com.rg2.utils.ToastUtil;

/**
 * 作者：王先云 on 2016/12/17 22:45
 * 邮箱：wangxianyun1@163.com
 * 描述：一句话简单描述
 */
public class FingerInputActivity extends BaseActivity
{

    private TextView mBackTv;
    Handler mHander = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case 1:
                    ToastUtil.show(FingerInputActivity.this, "请输入正确的指纹");
                    break;
            }
        }
    };

    @Override
    protected void initData()
    {

    }

    @Override
    protected void initViews(Bundle savedInstanceState)
    {
        setContentView(R.layout.activity_finger_input);
        mBackTv = (TextView) findViewById(R.id.tv_back);
    }

    @Override
    protected void initEvent()
    {
        mBackTv.setOnClickListener(this);
    }

    @Override
    protected void initViewData()
    {
        mHander.sendEmptyMessageAtTime(1, 10 * 1000);
    }

    @Override
    public void onClick(View v)
    {
        super.onClick(v);
        if (v == mBackTv)
        {
            finish();
        }
    }



    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            LogUtil.e("TAG","onKeyDown");
            mHander.removeMessages(1);
            startActivity(new Intent(FingerInputActivity.this, FingerprintActivity.class));
            finish();
            return false;
        }
        else
        {
            return super.onKeyDown(keyCode, event);
        }

    }
}
