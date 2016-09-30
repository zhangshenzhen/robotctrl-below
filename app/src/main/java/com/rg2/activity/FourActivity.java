package com.rg2.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.brick.robotctrl.R;
import com.rg2.utils.StringUtils;
import com.rg2.utils.ToastUtil;


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
    }

    @Override
    protected void initEvent()
    {
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
            String mRelativesName = mRelativesNameEt.getText().toString();
            String mRelativesPhone = mRelativesPhoneEt.getText().toString();
            String mContactName = mContactNameEt.getText().toString();
            String mContactPhone = mContactPhoneEt.getText().toString();


            if (StringUtils.stringIsEmpty(mRelativesName))
            {
                ToastUtil.show(FourActivity.this, "请输入亲属姓名");
                return;
            }

            if (StringUtils.isMobileNO(mRelativesPhone))
            {
                ToastUtil.show(FourActivity.this, "请输入正确的亲属移动电话");
                return;
            }
            if (StringUtils.stringIsEmpty(mContactName))
            {
                ToastUtil.show(FourActivity.this, "请输入紧急联系人");
                return;
            }
            if (StringUtils.isMobileNO(mContactPhone))
            {
                ToastUtil.show(FourActivity.this, "请输入正确的紧急联系人移动电话");
                return;
            }
            startActivityForResult(new Intent(this,FiveActivity.class),1);
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
