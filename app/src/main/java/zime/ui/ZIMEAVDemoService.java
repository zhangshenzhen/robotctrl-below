package zime.ui;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.opengl.GLSurfaceView;
import android.os.IBinder;
import android.os.PowerManager;
import android.view.SurfaceView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import zime.media.VideoDeviceCallBack;
import zime.media.ZIMEClientJni;
import zime.media.ZIMEVideoClientJNI;
import zime.media.ZMCEVideoGLRender;

/**
 * Created by li on 2016/10/10.
 */
public class ZIMEAVDemoService extends Service {
    private final static String ZIMETAG = "ZIMEAVDemoActivity";

    private Context mContext = null;
    private Button mButtonStart;
    private Button mButtonStop;
    private Button mButtonConf;
    private Button mButtonExit;
    private Button mButtonStartSend;
    private Button mButtonStartRecv;
    private Button mButtonSendDTMF;
    private Button mButtonSendOneIFrame;
    private Button mButtonAstart;
    private Button mButtonSwtich;
    private TextView mTextViewDownQoS;
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
    //Handler mHandler = null;
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

        ZIMEVideoClientJNI.ZIMELoadLibrary();
        mVideoClientJNI = new ZIMEVideoClientJNI();
        mAudioClientJNI = new ZIMEClientJni();
        mZIMEConfig = new ZIMEConfig();
        mVideoGLRender = new ZMCEVideoGLRender();

        int eRet = ZIMEVideoClientJNI.ConnectDevice(mZIMEConfig.mChannelId, m_iDeviceType);
        String logString = "surfaceCreated---ConnectDevice Device:" + m_iDeviceType + "----ret: " + eRet;
        Toast.makeText(mContext, logString, Toast.LENGTH_LONG).show();
        m_iDeviceType = DEVTYPE_DEFAULT_VALUE;

        mZIMEJniThread = new ZIMEJniThread(mVideoClientJNI, mAudioClientJNI);
        mZIMEJniThread.SetActivity(ZIMEAVDemoService.this);

        boolean mbHaveStart = false;
        if(mbHaveStart == false)
        {
            mZIMEJniThread.start();
            mbHaveStart = true;
        }
        am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        /*mDiaglogBuilder 	= new ZIMEDialogSetting.Builder(mContext);
        mDiaglogBuilder.SetZIMESDKClient(mVideoClientJNI, mAudioClientJNI, mZIMEConfig);
        //mDialogSetting = mDiaglogBuilder.create(mViewHandler);
        am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        mDialogSetting.setAudioManager(am);*/

        mDialogDTMFDialerBuilder = new ZIMEDialogDTMFDialer.DTMFDialerBuilder(mContext);
        mDialogDTMFDialerBuilder.SetZIMESDKClient(mVideoClientJNI, mAudioClientJNI, mZIMEConfig);
        mDialogDTMFDialer  = mDialogDTMFDialerBuilder.create();


        //VideoDeviceCallBack.SetCurActivity(this);
        mZIMEJniThread.setAudioMan(am);

        mZIMEJniThread.Input(ZIMEConfig.SET_PARAM, mZIMEConfig);
        mZIMEJniThread.Input(ZIMEConfig.START, null);
        //mVResWidth_S = mZIMEConfig.mWidth;

        VideoDeviceCallBack.SetCodecType(ZIMEConfig.mCodecType);

        if(ZIMEConfig.mCodecType == ZIMEConfig.enumZIME_AMLOGICHARDWEAR)
        {
            mVideoGLRender.useMediaCodecInfo(false, 0);

            mVideoGLRender.setAmlogicEnable(true);
        }
        else if(ZIMEConfig.mCodecType == ZIMEConfig.enumZIME_MediaCodec)
        {
            mVideoGLRender.useMediaCodecInfo(true, mYUVType);

            mVideoGLRender.setAmlogicEnable(false);
        }
        else
        {
            mVideoGLRender.useMediaCodecInfo(false, 0);
            mVideoGLRender.setAmlogicEnable(false);
        }

        /*mZIMEJniThread.Input(ZIMEConfig.SET_AUDIOPARAM_BEFORSTART, mZIMEConfig);
        mZIMEJniThread.Input(ZIMEConfig.ASTART_BY_AVCLIENTINTERFACE, null);*/

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
