package com.card;

import android.content.Intent;
import android.media.MediaRouter;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.brick.robotctrl.R;
import com.jly.idcard.IDcardActivity;
import com.presentation.presentionui.CardinfoPresentation;
import com.rg2.activity.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by shenzhen on 2017/2/10.
 */

public class BusinessCarInfo extends BaseActivity {

    @Bind(R.id.tv_card_details)
    TextView tvCardDetails;
    @Bind(R.id.btn_back)
    Button btnBack;
    @Bind(R.id.btn_next)
    Button btnNext;
    private CardinfoPresentation mCardinfoPresentation;

    @Override
    protected void initData() {

    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setContentView(R.layout.activity_business_cardinfo);
        ButterKnife.bind(this);
    }

    @Override
    protected void initViewData() {
        Intent intent = getIntent();
        String cardDetails = intent.getStringExtra("cardDetails");
        tvCardDetails.setText("本卡为XX银行" + cardDetails + "年费为26/年");

    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mCardinfoPresentation != null){
            mCardinfoPresentation.dismiss();
            mCardinfoPresentation = null;
        }
    }

    @Override
    protected void initEvent() {
    }
    @OnClick({R.id.btn_back, R.id.btn_next})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_next:
                //激活信息采集页面，
                startActivity(new Intent(BusinessCarInfo.this, IDcardActivity.class));
                break;
        }
    }
    @Override
    protected void updatePresentation() {
        // Log.d(TAG, "updatePresentation: ");
        //得到当前route and its presentation display
        MediaRouter.RouteInfo route = mMediaRouter.getSelectedRoute(
                MediaRouter.ROUTE_TYPE_LIVE_VIDEO);
        Display presentationDisplay = route != null ? route.getPresentationDisplay() : null;
        // 注释 : Dismiss the current presentation if the display has changed.
        if (mCardinfoPresentation != null && mCardinfoPresentation.getDisplay() != presentationDisplay) {
            mCardinfoPresentation.dismiss();
            mCardinfoPresentation = null;
         }
        if (mCardinfoPresentation == null && presentationDisplay != null) {
            // Initialise a new Presentation for the Display
            mCardinfoPresentation = new CardinfoPresentation(this, presentationDisplay);
            //把当前的对象引用赋值给BaseActivity中的引用;

            mCardinfoPresentation.setOnDismissListener(mOnDismissListener);
            try {
                mCardinfoPresentation.show();
            } catch (WindowManager.InvalidDisplayException ex) {
                mCardinfoPresentation = null;
            }
        }
    }
}
