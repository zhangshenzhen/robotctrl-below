package com.service;

import android.os.Environment;
import android.util.Log;

/**
 * Created by lx on 2018-06-14.
 */

public class MediaPlayerDecide {

    private static final String TAG = "MediaPlayerDecide";
    public  MPlayerService.SetPlayerBinder setPlayerBinder;

    public  static String Dirpath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Movies/";
    public static void startPlayer(String[]mode,MPlayerService.SetPlayerBinder setPlayerBinder){

        Log.d(TAG,"receive command 1 "+mode[0]);
        Log.d(TAG,"receive command 1  "+setPlayerBinder);
        if (mode.length>1){
         Log.d(TAG,"receive command 2"+mode[1]);
        }
        switch (mode[0]){
            case "Play":
               break;
            case "ContinuePlay":
                setPlayerBinder.setContinue();
                break;
            case "Pause":
                setPlayerBinder.setPause();
                break;
            case "Stop":
                setPlayerBinder.setstop();
                break;
            case "Single":
                String MP3path= Dirpath+mode[1];
                setPlayerBinder.setstart(MP3path,"Single");
                break;
            case "Cycle":
                String MP3path2= Dirpath+mode[1];
                setPlayerBinder.setstart(MP3path2,"Cycle");
                break;
            case "SingleCycle":
                String MP3path3= Dirpath+mode[1];
                setPlayerBinder.setstart(MP3path3,"SingleCycle");
                break;
        }

    }

    public static void MediaChanger(String fileName,MPlayerService.SetPlayerBinder setPlayerBinder){
        String MP3path= Dirpath+fileName;
        setPlayerBinder.setstart(MP3path,"Cycle");
    }
    /*停止音乐*/
    public static void MediaStop(MPlayerService.SetPlayerBinder setPlayerBinder){
        setPlayerBinder.setstop();
    }
}
