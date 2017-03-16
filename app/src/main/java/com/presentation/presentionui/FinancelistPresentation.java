package com.presentation.presentionui;

import android.content.Context;
import android.os.Bundle;
import android.view.Display;

import com.brick.robotctrl.R;
import com.presentation.BasePresentation;

/**
 * Created by lx on 2017/3/16.
 */
public class FinancelistPresentation  extends BasePresentation{
    public FinancelistPresentation(Context outerContext, Display display) {
        super(outerContext, display);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.persentation_finence_list);
    }
}
