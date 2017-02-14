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
import android.widget.TextView;

import com.brick.robotctrl.R;
import com.presentation.presentionui.ApplyforPresentation;
import com.rg2.activity.BaseActivity;
import com.rg2.utils.ToastUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


/*卡片申请*/

public class ApplyForActivity extends BaseActivity {

    private static final String TAG = "ApplyForActivity";
    @Bind(R.id.text)
    TextView text;
    @Bind(R.id.cb_agree)
    CheckBox cbAgree;
    @Bind(R.id.btn_back)
    Button btnBack;
    @Bind(R.id.btn_next)
    Button btnNext;
    private ApplyforPresentation mapplyforPresentation;


    @Override
    protected void initViews(Bundle savedInstanceState) {
        setContentView(R.layout.activity_apply_for);
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
    @OnClick({R.id.btn_back, R.id.btn_next})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_next:
                if(cbAgree.isChecked()){ //同意用户协议;
                   startActivity(new Intent(ApplyForActivity.this,
                            ApplyForSelectCardActivity.class ));
                }else {
                    ToastUtil.show(ApplyForActivity.this, "请先阅读用户协议,并同意");
                }
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
        if (mapplyforPresentation != null && mapplyforPresentation.getDisplay() != presentationDisplay) {
            mapplyforPresentation.dismiss();
            mapplyforPresentation = null;
        }
        if (mapplyforPresentation == null && presentationDisplay != null) {
            // Initialise a new Presentation for the Display
            mapplyforPresentation = new ApplyforPresentation(this, presentationDisplay);
            //把当前的对象引用赋值给BaseActivity中的引用;
            mPresentation = mapplyforPresentation;
            mapplyforPresentation.setOnDismissListener(mOnDismissListener);
            try {
                mapplyforPresentation.show();
            } catch (WindowManager.InvalidDisplayException ex) {
                mapplyforPresentation = null;
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        updatePresentation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mapplyforPresentation != null) {
            mapplyforPresentation.dismiss();
            mapplyforPresentation = null;
        }
    }

}
