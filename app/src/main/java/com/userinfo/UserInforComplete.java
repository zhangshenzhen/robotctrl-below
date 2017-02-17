package com.userinfo;

import android.media.MediaRouter;
import android.os.Bundle;
import android.view.Display;
import android.view.WindowManager;

import com.brick.robotctrl.R;
import com.presentation.SelecttPresentation;
import com.presentation.presentionui.InfoCompletePresentaion;
import com.rg2.activity.BaseActivity;

/**
 * Created by lx on 2017/2/17.
 */

public class UserInforComplete extends BaseActivity {

     private  InfoCompletePresentaion minfoCompletePresentaion;

    @Override
    protected void initData() {

    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
      setContentView(R.layout.activity_userinfo_complete);
    }

    @Override
    protected void initEvent() {

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
