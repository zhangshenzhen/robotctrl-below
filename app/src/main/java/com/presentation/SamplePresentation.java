/*
 * Copyright 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.presentation;

import android.app.Presentation;
import android.content.Context;
import android.graphics.Color;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.brick.robotctrl.R;

/**
 * <p>
 * A {@link Presentation} used to demonstrate interaction between primary and
 * secondary screens.
 * </p>
 * <p>
 * It displays the name of the display in which it has been embedded (see
 * {@link Presentation#getDisplay()}) and exposes a facility to change its
 * background color and display its text.
 * </p>
 */
//扩展Presentation类并实现onCreate()方法;
public class SamplePresentation extends BasePresentation {

    public SamplePresentation(Context outerContext, Display display) {
        super(outerContext, display);
        mContext = outerContext;
    }

    public SamplePresentation(Context outerContext, Display display, int theme) {
        super(outerContext, display, theme);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        // Set the content view to the custom layout
        // Inflate a layout.
        //调用setContentView()来指定您要在辅助显示屏上显示的UI
        setContentView(R.layout.okienko_vice);
        RelativeLayout Rl = (RelativeLayout) findViewById(R.id.rl);
        //动态添加布局
        TextView tv = new TextView(mContext);
        tv.setText("欢迎使用智能型机器人，很高兴为您服务！！！");
        tv.setTextSize(36);
        tv.setTextColor(Color.BLACK);
        tv.setGravity(Gravity.CENTER);
        Rl.addView(tv);
     }
}

