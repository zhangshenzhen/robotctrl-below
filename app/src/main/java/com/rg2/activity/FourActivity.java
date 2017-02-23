package com.rg2.activity;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaRouter;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.brick.robotctrl.R;
import com.presentation.presentionui.UserInfoPresentation;


/**
 * 作者：王先云 on 2016/9/1 15:49
 * 邮箱：wangxianyun1@163.com
 * 描述：一句话简单描述
 */
public class FourActivity extends BaseActivity
{
    private Button mSubmitBtn;

    private EditText mRelativesNameEt;
    private EditText mRelativesPhoneEt;
    private EditText mContactNameEt;
    private EditText mContactPhoneEt;
    private Button mBackTv;
    private UserInfoPresentation mUserInfoPresentation;



    @Override
    protected void initData()
    {

    }

    @Override
    protected void initViews(Bundle savedInstanceState)
    {
        setContentView(R.layout.activity_four);
        mSubmitBtn = (Button) findViewById(R.id.btn_submit);
        mRelativesNameEt = (EditText) findViewById(R.id.et_relatives_name);
        mRelativesPhoneEt = (EditText) findViewById(R.id.et_relatives_phone);
        mContactNameEt = (EditText) findViewById(R.id.et_contact_name);
        mContactPhoneEt = (EditText) findViewById(R.id.et_contact_phone);
        mBackTv=(Button)findViewById(R.id.tv_back);
    }

    @Override
    protected void initEvent()
    {
        mSubmitBtn.setOnClickListener(this);
        mBackTv.setOnClickListener(this);
    }

    @Override
    protected void initViewData()
    {

    }
    @Override
    protected void onResume() {
        super.onResume();
        updatePresentation();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUserInfoPresentation != null){
            mUserInfoPresentation.dismiss();
            mUserInfoPresentation= null;
        }
    }

    @Override
    protected void updatePresentation() {
        // Log.d(TAG, "updatePresentation: ");
        //得到当前route and its presentation display
        MediaRouter.RouteInfo route = mMediaRouter.getSelectedRoute(
                MediaRouter.ROUTE_TYPE_LIVE_VIDEO);
        Display presentationDisplay =  route  !=  null ? route.getPresentationDisplay() : null;
        // 注释 : Dismiss the current presentation if the display has changed.
        if (mUserInfoPresentation != null && mUserInfoPresentation.getDisplay() !=  presentationDisplay) {
            mUserInfoPresentation.dismiss();
            mUserInfoPresentation = null;
        }
        if (mUserInfoPresentation == null &&  presentationDisplay != null) {
            // Initialise a new Presentation for the Display
            mUserInfoPresentation = new UserInfoPresentation(this,  presentationDisplay);
            //把当前的对象引用赋值给BaseActivity中的引用;
            mPresentation  =  mUserInfoPresentation  ;
            // Log.d(TAG, "updatePresentation: this: "+ this.toString());
            mUserInfoPresentation.setOnDismissListener(mOnDismissListener);

            // Try to show the presentation, this might fail if the display has
            // gone away in the mean time
            try {
                mUserInfoPresentation.show();
            } catch (WindowManager.InvalidDisplayException ex) {
                // Couldn't show presentation - display was already removed
                // Log.d(TAG, "updatePresentation: failed");
                mUserInfoPresentation = null;
            }
        }
    }
    @Override
    public void onClick(View v)
    {
        super.onClick(v);
        if (v == mSubmitBtn)
        {
            String mRelativesName = mRelativesNameEt.getText().toString();
            String mRelativesPhone = mRelativesPhoneEt.getText().toString();
            String mContactName = mContactNameEt.getText().toString();
            String mContactPhone = mContactPhoneEt.getText().toString();

            startActivityForResult(new Intent(this,FiveActivity.class),1);
        }
        else if(v == mBackTv)
        {
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 &&resultCode== Activity.RESULT_OK)
        {
            setResult(Activity.RESULT_OK);
            finish();
        }
    }
}
