package com.fragment;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.brick.robotctrl.R;
import com.rg2.activity.BaseActivity;

/**
 * Created by lx on 2017/4/6.
 */

public class ScreenFragment extends BaseFragment {

    private Paint paint;
    /**
     * 一个可以被修改的图片
     */
    private Bitmap alterBitmap;
    /**
     * 画板
     */
    private Canvas canvas;
    private RelativeLayout main;
    private  int point;
    @Override
    public View initView() {
        return View.inflate(getActivity(),R.layout.screen_fragment,null);
     }

     public void initData(){
         main = (RelativeLayout) getActivity().findViewById(R.id.fragment_main);
         // 创建一个空白的图片
         alterBitmap = Bitmap.createBitmap(320, 320, Bitmap.Config.ARGB_8888);
         canvas = new Canvas(alterBitmap);
         paint = new Paint();
         // 设置画笔的颜色
         paint.setColor(Color.BLACK);
         // 设置画笔的颜色
         paint.setColor(Color.BLACK);

     };
    public void initEVent(){
        main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                point++;
                if (point == 1) {
                    main.setBackgroundColor(Color.RED);
                } else if (point == 2) {
                    main.setBackgroundColor(Color.YELLOW);
                } else if (point == 3) {
                    main.setBackgroundColor(Color.BLUE);
                } else if (point == 4) {
                    main.setBackgroundColor(Color.BLACK);
                } else {
                    main.setBackgroundColor(Color.WHITE);
                    point = 0;
                }
            }
        });
    }
}
