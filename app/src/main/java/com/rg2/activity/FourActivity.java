package com.rg2.activity;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaRouter;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
    private RadioGroup mkinrelatives,mcardholderrelatives;
    private EditText mRelativesNo,mRelativespho,emergencyName;
    private String mRelativesName;
    private String mRelativesPhone;
    private String mContactName;
    private String mContactPhone;
    private RadioButton buttonrelatives;
    private RadioButton buttonholderrelatives;


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
        mkinrelatives = (RadioGroup)findViewById(R.id.rg_marital_status);
        //获取RadioGroup中RadioButton控件
        buttonrelatives = (RadioButton) findViewById(mkinrelatives.getCheckedRadioButtonId());

        mcardholderrelatives = (RadioGroup)findViewById(R.id.rg_relationship_status);
        //获取RadioGroup中RadioButton控件
        buttonholderrelatives = (RadioButton) findViewById(mcardholderrelatives.getCheckedRadioButtonId());
        mRelativesNo = (EditText) findViewById(R.id.tv_areaNo);
        mRelativespho = (EditText) findViewById(R.id.et_residential_telephone);
        emergencyName = (EditText) findViewById(R.id.et_contact_name);

        useInstance();
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
    protected void onStop() {
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
            mRelativesName = mRelativesNameEt.getText().toString();
            mRelativesPhone = mRelativesPhoneEt.getText().toString();
            mContactName = mContactNameEt.getText().toString();
            mContactPhone = mContactPhoneEt.getText().toString();
                useInstance();
              startActivityForResult(new Intent(this,FiveActivity.class),1);
        }
        else if(v == mBackTv)
        {
            finish();
        }
    }
        //存储用户信息
       private void useInstance() {
           instance.setKinName("亲属姓名："+mRelativesName);
           instance.setKinTel("亲属电话："+mRelativesNo.getText().toString().trim()+mRelativespho.getText().toString().trim());
           instance.setKinPhone("亲属手机号："+mRelativesPhone);
           //获取获取RadioGroup中RadioButton控件的值
           instance.setRelatives("亲属关系："+buttonrelatives.getText().toString().trim());
           instance.setEmergencyContact("紧急联系人："+mContactName);
           instance.setEmergencyContactphone("紧急联系人号码："+mContactPhone);
           //获取获取RadioGroup中RadioButton控件的值
           instance.setCardholderrelatives("与持卡人的关系："+buttonholderrelatives.getText().toString().trim());

       }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("ActivityResult","第四个");
        if(requestCode==1 &&resultCode== Activity.RESULT_OK)
        {
            Log.d("ActivityResult","第四—个");
            setResult(Activity.RESULT_OK);
            finish();
        }
    }
}
