package com.card;

import android.media.MediaRouter;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.brick.robotctrl.R;
import com.presentation.SelecttPresentation;
import com.presentation.presentionui.ApplyforPresentation;
import com.rg2.activity.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

/*卡片申请*/

public class ApplyForActivity extends BaseActivity {
   private ApplyforPresentation mapplyforPresentation;
    @Bind(R.id.tv_back)
    TextView tvBack;

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setContentView(R.layout.activity_apply_for);
        ButterKnife.bind(this);
    }


    @Override
    protected void initData() {
        tvBack.setOnClickListener(this);
     }
    @Override
    public void onClick(View v) {
       finish();
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
        if (mapplyforPresentation != null){
            mapplyforPresentation.dismiss();
            mapplyforPresentation = null;
        }
    }
}
