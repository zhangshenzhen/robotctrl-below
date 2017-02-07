package com.card;

import android.content.Intent;
import android.media.MediaRouter;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.brick.robotctrl.R;

import com.presentation.presentionui.CardPresentation;
import com.rg2.activity.BaseActivity;



import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CardActivity extends BaseActivity {



    @Bind(R.id.tv_back_card)
    TextView tvBackCard;
    @Bind(R.id.tv_card_applyfor)
    TextView tvCardApplyfor;
    @Bind(R.id.tv_card_business)
    TextView tvCardBusiness;
    @Bind(R.id.tv_card_activate)
    TextView tvCardActivate;
    @Bind(R.id.activity_finance_introduce_list)
    LinearLayout activityFinanceIntroduceList;

    private CardPresentation mCardPresentation;
    @Override
    protected void initViews(Bundle savedInstanceState) {
        setContentView(R.layout.activity_card);
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

    /*开启副屏的*/
    @Override
    protected void updatePresentation() {
        // Log.d(TAG, "updatePresentation: ");
        //得到当前route and its presentation display
        MediaRouter.RouteInfo route = mMediaRouter.getSelectedRoute(
                MediaRouter.ROUTE_TYPE_LIVE_VIDEO);
        Display presentationDisplay = route != null ? route.getPresentationDisplay() : null;
        // 注释 : Dismiss the current presentation if the display has changed.
        if (mCardPresentation != null && mCardPresentation.getDisplay() != presentationDisplay) {
            mCardPresentation.dismiss();
            mCardPresentation = null;
        }
        if (mCardPresentation == null && presentationDisplay != null) {
            // Initialise a new Presentation for the Display
            mCardPresentation = new CardPresentation(this, presentationDisplay);
            //把当前的对象引用赋值给BaseActivity中的引用;
            mPresentation = mCardPresentation;
            mCardPresentation.setOnDismissListener(mOnDismissListener);
            try {
                mCardPresentation.show();
            } catch (WindowManager.InvalidDisplayException ex) {
                mCardPresentation = null;
            }
        }
     }


    @OnClick({R.id.tv_back_card, R.id.tv_card_applyfor, R.id.tv_card_business, R.id.tv_card_activate})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back_card:
                finish();                   // 返回上一层;
                break;
            case R.id.tv_card_applyfor:
                startActivity(new Intent(this, ApplyForActivity.class));
                break;
            case R.id.tv_card_business:
                startActivity(new Intent(this, CardBusinessActivity.class));
                break;
            case R.id.tv_card_activate:
                startActivity(new Intent(this, CardActivataActivity.class));
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }


}
