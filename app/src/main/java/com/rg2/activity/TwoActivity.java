package com.rg2.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.brick.robotctrl.R;
import com.rg2.utils.StringUtils;
import com.rg2.utils.ToastUtil;


/**
 * 作者：王先云 on 2016/9/1 15:49
 * 邮箱：wangxianyun1@163.com
 * 描述：一句话简单描述
 */
public class TwoActivity extends BaseActivity
{
    private Button mSubmitBtn;
    private EditText mResidentialAreaNoEt;//住宅区号
    private EditText mResidentialTelEt;//住宅电话
    private EditText mCompanyAreaNoEt;//公司区号
    private EditText mCompanyTelEt;//公司电话
    private EditText mExtensionEt;//公司分机电话
    private EditText mPhoneEt;//用户手机
    private TextView mBackTv;

    @Override
    protected void initData()
    {

    }

    @Override
    protected void initViews(Bundle savedInstanceState)
    {
        setContentView(R.layout.activity_two);
        mSubmitBtn = (Button) findViewById(R.id.btn_submit);

        mResidentialAreaNoEt = (EditText) findViewById(R.id.et_residential_areaNo);
        mResidentialTelEt = (EditText) findViewById(R.id.et_residential_tel);
        mCompanyAreaNoEt = (EditText) findViewById(R.id.et_company_areaNo);
        mCompanyTelEt = (EditText) findViewById(R.id.et_company_tel);
        mExtensionEt = (EditText) findViewById(R.id.et_extension);
        mPhoneEt = (EditText) findViewById(R.id.et_phone);
        mBackTv = (TextView) findViewById(R.id.tv_back);
    }

    @Override
    protected void initEvent()
    {
        mBackTv.setOnClickListener(this);
        mSubmitBtn.setOnClickListener(this);
    }

    @Override
    protected void initViewData()
    {

    }

    @Override
    public void onClick(View v)
    {
        super.onClick(v);
        if (v == mSubmitBtn)
        {

            String mResidentialAreaNo = mResidentialAreaNoEt.getText().toString();
            String mResidentialTel = mResidentialTelEt.getText().toString();
            String mCompanyAreaNo = mCompanyAreaNoEt.getText().toString();
            String mCompanyTel = mCompanyTelEt.getText().toString();
            String mExtension = mExtensionEt.getText().toString();
            String mPhone = mPhoneEt.getText().toString();


//            if (StringUtils.stringIsEmpty(mCompanyAreaNo))
//            {
//                ToastUtil.show(TwoActivity.this, "请输入公司电话区号");
//                return;
//            }
//
//            if (StringUtils.stringIsEmpty(mCompanyTel))
//            {
//                ToastUtil.show(TwoActivity.this, "请输入公司电话");
//                return;
//            }
//            if (StringUtils.isMobileNO(mPhone))
//            {
//                ToastUtil.show(TwoActivity.this, "请输入正确手机号码");
//                return;
//            }

            startActivityForResult(new Intent(this,ThreeActivity.class),1);
        }
        else if(v == mBackTv)
        {
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1 &&resultCode== Activity.RESULT_OK)
        {
            setResult(Activity.RESULT_OK);
            finish();
        }
    }

}
