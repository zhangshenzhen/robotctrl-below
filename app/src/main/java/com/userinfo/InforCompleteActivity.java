package com.userinfo;

import android.media.MediaRouter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.brick.robotctrl.R;
import com.brick.robotctrl.SerialCtrl;
import com.presentation.SelecttPresentation;
import com.presentation.presentionui.InfoCompletePresentaion;
import com.rg2.activity.BaseActivity;

/**
 * Created by lx on 2017/2/17.
 */

public class InforCompleteActivity extends BaseActivity {

     private  InfoCompletePresentaion minfoCompletePresentaion;
  private Button start,initialize,paperable,readcard,outcard;
    public SerialCtrl serialCtrlcard ;
    @Override
    protected void initData() {
        //初始化发卡机
        serialCtrlcard = new SerialCtrl(this, new Handler(), "ttymxc0", 9600, "robotctrl");
        start = (Button) findViewById(R.id.start);
        initialize = (Button) findViewById(R.id.initialize);
        paperable = (Button) findViewById(R.id.paperable);
        readcard = (Button) findViewById(R.id.readcard);
        outcard = (Button) findViewById(R.id.outcard);
        start.setOnClickListener(this);
        initialize.setOnClickListener(this);
        paperable.setOnClickListener(this);
        readcard.setOnClickListener(this);
        outcard.setOnClickListener(this);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
      setContentView(R.layout.activity_userinfo_complete);
    }

    @Override
    protected void initEvent() {

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.start:
                serialCtrlcard.sendPortData(serialCtrlcard.ComA, "55AA7E0004020100840D");//开始
                break;
            case R.id.initialize:
                serialCtrlcard.sendPortData(serialCtrlcard.ComA, "55AA7E0004020300860D");//初始化
                break;
            case R.id.paperable:
                serialCtrlcard.sendPortData(serialCtrlcard.ComA, "55AA7E0004020400870D");//准备
                break;
            case R.id.readcard:
                String st = String.valueOf(serialCtrlcard.getBattery());//读取
                Log.d("serialCtrlcard:",""+st);
                break;
            case R.id.outcard:
                serialCtrlcard.sendPortData(serialCtrlcard.ComA, "55AA7E0004020500880D");//吐卡
                break;
        }
    }



    @Override
    protected void initViewData() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        updatePresentation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (minfoCompletePresentaion != null) {
            minfoCompletePresentaion.dismiss();
            minfoCompletePresentaion = null;
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
        if (minfoCompletePresentaion != null && minfoCompletePresentaion.getDisplay() != presentationDisplay) {
            minfoCompletePresentaion.dismiss();
            minfoCompletePresentaion = null;
        }
        if (minfoCompletePresentaion == null && presentationDisplay != null) {
            // Initialise a new Presentation for the Display
            minfoCompletePresentaion = new InfoCompletePresentaion(this, presentationDisplay);
            //把当前的对象引用赋值给BaseActivity中的引用;
            mPresentation = minfoCompletePresentaion;
            minfoCompletePresentaion.setOnDismissListener(mOnDismissListener);
            try {
                minfoCompletePresentaion.show();
            } catch (WindowManager.InvalidDisplayException ex) {
                minfoCompletePresentaion = null;
            }
        }
    }
}
