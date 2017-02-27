package com.card;

import android.content.Intent;
import android.media.MediaRouter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.brick.robotctrl.R;
import com.presentation.presentionui.CardinfoPresentation;
import com.presentation.presentionui.InserCardPresentation;
import com.rg2.activity.BaseActivity;

public class InserCard extends BaseActivity {

    private static final String TAG = "InserCard";
private InserCardPresentation mInserCardPresentation;

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setContentView(R.layout.activity_inser_card);
        //需要相关检测功能检测是否插卡.
        }





    @Override
    protected void initData() {

    }



    @Override
    protected void initEvent() {
        //模拟读卡操作;
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    sleep(3000);
                    startActivity(new Intent(InserCard.this,SettingPasswordActivity.class));
                    Log.d(TAG,"开启新的界面");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }.start();
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
        if (mInserCardPresentation != null && mInserCardPresentation.getDisplay() != presentationDisplay) {
            mInserCardPresentation.dismiss();
            mInserCardPresentation = null;
        }
        if (mInserCardPresentation == null && presentationDisplay != null) {
            // Initialise a new Presentation for the Display
            mInserCardPresentation = new InserCardPresentation(this, presentationDisplay);
            //把当前的对象引用赋值给BaseActivity中的引用;
            mPresentation = mInserCardPresentation;
            mInserCardPresentation.setOnDismissListener(mOnDismissListener);
            try {
                mInserCardPresentation.show();
            } catch (WindowManager.InvalidDisplayException ex) {
                mInserCardPresentation = null;
            }
        }
    }

}
