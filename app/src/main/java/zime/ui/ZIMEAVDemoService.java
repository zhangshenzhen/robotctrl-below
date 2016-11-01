package zime.ui;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.Toast;

import zime.media.VideoDeviceCallBack;
import zime.media.ZIMEClientJni;
import zime.media.ZIMEVideoClientJNI;
import zime.media.ZMCEVideoGLRender;

/**
 * Created by li on 2016/10/10.
 */
public class ZIMEAVDemoService extends Service {
    private final static String ZIMETAG = "ZIMEAVDemoService";

    private Context mContext = null;
    private SurfaceView mSurfaceLocalView = null;
    private GLSurfaceView mSurfaceRemoteView = null;
    private ZIMEDialogSetting.Builder mDiaglogBuilder = null;
    private boolean mSurfaceExist = false;
    private ZIMEDialogSetting mDialogSetting = null;
    private ZIMEDialogDTMFDialer.DTMFDialerBuilder mDialogDTMFDialerBuilder= null;
    private ZIMEDialogDTMFDialer mDialogDTMFDialer = null;

    private ZIMEClientJni mAudioClientJNI = null;
    private ZIMEVideoClientJNI mVideoClientJNI = null;
    private ZIMEConfig mZIMEConfig = null;
    public ZIMEJniThread mZIMEJniThread = null;
    private static PowerManager.WakeLock mWakelock = null;
    private static boolean mStarted = false;
    private static boolean mStopped = true;
    private static boolean m_bSetParam = false;

    private static int DEVTYPE_DEFAULT_VALUE = 2;
    private static int m_iDeviceType = DEVTYPE_DEFAULT_VALUE;  // 默认home行为

    // opengl
    private ZMCEVideoGLRender mVideoGLRender = null;
    private AudioManager am = null;
    public String mStringDecodeTypeName = "";
    public  int  mYUVType = 19;
    public  static boolean  mSupportMediaCodec = false;
    Handler mHandler = null;
    String mStrResSwitchInfo = "";
    public int mVResWidth_S = 0;
    public int mVResWidth_R = 0;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mContext = this;
        am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);




        ZIMEVideoClientJNI.ZIMELoadLibrary();
        Log.e(ZIMETAG,"1");
        mVideoClientJNI = new ZIMEVideoClientJNI();
        mAudioClientJNI = new ZIMEClientJni();
        Log.e(ZIMETAG,"2");
        mZIMEConfig = new ZIMEConfig();
        Log.e(ZIMETAG,"3");

        mDiaglogBuilder = new ZIMEDialogSetting.Builder(mContext);
        mDiaglogBuilder.SetZIMESDKClient(mVideoClientJNI, mAudioClientJNI, mZIMEConfig);
        mDialogSetting = mDiaglogBuilder.create(/*mViewHandler*/);
        mDialogSetting.setAudioManager(am);
        Log.e(ZIMETAG,"4");

        mDialogDTMFDialerBuilder = new ZIMEDialogDTMFDialer.DTMFDialerBuilder(mContext);
        mDialogDTMFDialerBuilder.SetZIMESDKClient(mVideoClientJNI, mAudioClientJNI, mZIMEConfig);
        mDialogDTMFDialer  = mDialogDTMFDialerBuilder.create();
        Log.e(ZIMETAG,"5");

        mZIMEJniThread = new ZIMEJniThread(mVideoClientJNI, mAudioClientJNI);
        Log.e(ZIMETAG,"6");
        /*mZIMEJniThread.SetActivity(ZIMEAVDemoActivity.this);
        VideoDeviceCallBack.SetCurActivity(this);*/
        Log.e(ZIMETAG,"7");
        mZIMEJniThread.setAudioMan(am);

        // opengl
        mVideoGLRender = new ZMCEVideoGLRender();
        //mVideoGLRender.SetGLSurface(mSurfaceRemoteView);
        Log.e(ZIMETAG,"8");



        Log.e(ZIMETAG,"x");
        int eRet = ZIMEVideoClientJNI.ConnectDevice(mZIMEConfig.mChannelId, m_iDeviceType);
        String logString = "surfaceCreated---ConnectDevice Device:" + m_iDeviceType + "----ret: " + eRet;
        Toast.makeText(mContext, logString, Toast.LENGTH_LONG).show();
        m_iDeviceType = DEVTYPE_DEFAULT_VALUE;

        boolean mbHaveStart = false;
        if(mbHaveStart == false)
        {
            Log.e(ZIMETAG,"y");
            mZIMEJniThread.start();
            mbHaveStart = true;
        }

        for(int x = 0;x<10000;x++) {
            for(int y = 0;y<1100;y++)
            {}
        }

        Log.i(ZIMETAG, "-------------Start AV Button--------------");
        mZIMEJniThread.Input(ZIMEConfig.SET_PARAM, mZIMEConfig);
        Log.e(ZIMETAG,"9");
        mZIMEJniThread.Input(ZIMEConfig.START, null);
        Log.e(ZIMETAG,"10");

        VideoDeviceCallBack.SetCodecType(ZIMEConfig.mCodecType);

        if(ZIMEConfig.mCodecType == ZIMEConfig.enumZIME_AMLOGICHARDWEAR)
        {
            Log.e(ZIMETAG,"11");
            mVideoGLRender.useMediaCodecInfo(false, 0);

            //mVideoGLRender.setAmlogicEnable(true);
        }
        else if(ZIMEConfig.mCodecType == ZIMEConfig.enumZIME_MediaCodec)
        {
            mVideoGLRender.useMediaCodecInfo(true, mYUVType);

            //mVideoGLRender.setAmlogicEnable(false);
        }
        else
        {
            mVideoGLRender.useMediaCodecInfo(false, 0);
            //mVideoGLRender.setAmlogicEnable(false);
        }

        if(ZIMEConfig.mIsOnlyAudio)
        {
            //mButtonSwtich.setEnabled(false);
            //mButtonSendOneIFrame.setEnabled(false);
        }
        else
        {
            //mButtonSwtich.setEnabled(true);
            //mButtonSwtich.setText("SwitchA");
            //ButtonSendOneIFrame.setEnabled(true);
        }

        mStarted = true;
        mStopped = false;
        mDialogSetting.setStatus(mStarted, mStopped);


        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
