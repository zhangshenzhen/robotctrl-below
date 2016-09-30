package com.rg2.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.rg2.listener.MyOnClickListener;


/**
 * Created by Administrator on 2016/9/21.
 */
public class DialogUtils
{

    public static void showListDialog(String title, final String[] arr, Context mContext, final MyOnClickListener listener)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        builder.setTitle(title)
                .setItems(arr, new DialogInterface.OnClickListener()
                {

                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        listener.onClicked(arr[which]);
                    }
                }).show();
    }
}
