package com.fragment;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.view.View;

/**
 * Created by lx on 2017/4/13.
 */

public class CameraFragment extends BaseFragment {
    @Override
    public View initView() {
        return null;
    }

    @Override
    public void initData() {
        //设置为横屏幕;
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//横屏
        getActivity().startActivity(new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE));
    }
}
