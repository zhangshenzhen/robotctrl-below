package com.card;

import android.content.Intent;
import android.media.MediaRouter;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;

import com.brick.robotctrl.R;
import com.presentation.presentionui.ApplyforPresentation;
import com.rg2.activity.BaseActivity;
import com.rg2.utils.ToastUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CardBusinessActivity extends BaseActivity {
    public final static String TAG = "CardBusinessActivity";
    @Bind(R.id.cb_agree)
    CheckBox cbAgree;
    @Bind(R.id.btn_back)
    Button btnBack;
    @Bind(R.id.btn_next)
    Button btnNext;


    private ApplyforPresentation mApplyforPresentation;


    @Override
    protected void initViews(Bundle savedInstanceState) {
        setContentView(R.layout.activity_business_card);
        ButterKnife.bind(this);
    }


    @Override
    protected void initData() {

    }


    @Override
    protected void initEvent() {

    }

    @Override
    protected void initViewData() {

    }

    @Override
    protected void updatePresentation() {
        // Log.d(TAG, "updatePresentation: ");
        //得到当前route and its presentation display
        MediaRouter.RouteInfo route = mMediaRouter.getSelectedRoute(
                MediaRouter.ROUTE_TYPE_LIVE_VIDEO);
        Display presentationDisplay = route != null ? route.getPresentationDisplay() : null;
        // 注释 : Dismiss the current presentation if the display has changed.
        if (mApplyforPresentation != null && mApplyforPresentation.getDisplay() != presentationDisplay) {
            mApplyforPresentation.dismiss();
            mApplyforPresentation = null;
        }
        if (mApplyforPresentation == null && presentationDisplay != null) {
            // Initialise a new Presentation for the Display
            mApplyforPresentation = new ApplyforPresentation(this, presentationDisplay);
            //把当前的对象引用赋值给BaseActivity中的引用;
            mPresentation = mApplyforPresentation;
            mApplyforPresentation.setOnDismissListener(mOnDismissListener);
            try {
                mApplyforPresentation.show();
            } catch (WindowManager.InvalidDisplayException ex) {
                mApplyforPresentation = null;
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        updatePresentation();

    }


    @OnClick({R.id.btn_back,R.id.btn_next})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                Log.e(TAG, "退出卡片办理业务");
                finish();
                break;
            case R.id.btn_next:
                if (cbAgree.isChecked()) { //同意用户协议;
                    Log.e(TAG, "同意用户协议，进入下一页");
                    startActivity(new Intent(CardBusinessActivity.this,
                            BusinessSelectCardActivity.class));
                } else {
                    ToastUtil.show(CardBusinessActivity.this, "请先阅读用户协议,并同意");
                }
                break;
        }
    }

}
