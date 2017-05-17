package zime.ui;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.opengl.GLES11Ext;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import zime.media.VideoDeviceCallBack;
import zime.media.ZIMEClientJni;
import zime.media.ZIMEVideoClientJNI;
import zime.media.ZMCEVideoGLRender;

/**
 * Created by li on 2016/10/10.
 */
public class ZIMEAVDemoService extends Service {
    private final static String ZIMETAG = "ZIMEAVDemoService";

    public static SurfaceTexture surfaceTexture;
    private Context mContext = null;
    private ZIMEDialogSetting.Builder mDiaglogBuilder = null;
    private ZIMEDialogSetting mDialogSetting = null;
    private ZIMEDialogDTMFDialer.DTMFDialerBuilder mDialogDTMFDialerBuilder= null;
    private ZIMEDialogDTMFDialer mDialogDTMFDialer = null;

    private ZIMEClientJni mAudioClientJNI = null;
    private ZIMEVideoClientJNI mVideoClientJNI = null;
    private ZIMEConfig mZIMEConfig = null;
    public ZIMEJniThread mZIMEJniThread = null;
    private static boolean mStarted = false;
    private static boolean mStopped = true;

    private static int DEVTYPE_DEFAULT_VALUE = 2;
    private static int m_iDeviceType = DEVTYPE_DEFAULT_VALUE;  // 默认home行为

    // opengl
    private ZMCEVideoGLRender mVideoGLRender = null;
    private AudioManager am = null;
    public  int  mYUVType = 19;

    public static int proId;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d(ZIMETAG,"onCreate");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(ZIMETAG,"onStartCommand");
        new Thread(new Runnable() {
            @Override
            public void run() {
                String[] str = new String[]{"edge -a 192.168.10.8 -c test -k 123456 -l 118.178.122.224:8080 &",
                        "ip route delete 192.168.10.0/24","ip route add 192.168.10.0/24 via 192.168.10.8 dev edge0 table local"};
                proId = CommandExecution.execCommand(str,true).getId();
            }
        }).start();


        mContext = this;
        am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        surfaceTexture = new SurfaceTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES);

        ZIMEVideoClientJNI.ZIMELoadLibrary();
        mVideoClientJNI = new ZIMEVideoClientJNI();
        mVideoClientJNI = new ZIMEVideoClientJNI();
        mAudioClientJNI = new ZIMEClientJni();
        mZIMEConfig = new ZIMEConfig();

        mDiaglogBuilder = new ZIMEDialogSetting.Builder(mContext);
        mDiaglogBuilder.SetZIMESDKClient(mVideoClientJNI, mAudioClientJNI, mZIMEConfig);
        mDialogSetting = mDiaglogBuilder.create(/*mViewHandler*/);
        mDialogSetting.setAudioManager(am);

        mDialogDTMFDialerBuilder = new ZIMEDialogDTMFDialer.DTMFDialerBuilder(mContext);
        mDialogDTMFDialerBuilder.SetZIMESDKClient(mVideoClientJNI, mAudioClientJNI, mZIMEConfig);
        mDialogDTMFDialer  = mDialogDTMFDialerBuilder.create();

        mZIMEJniThread = new ZIMEJniThread(mVideoClientJNI, mAudioClientJNI);

        // opengl
        mVideoGLRender = new ZMCEVideoGLRender();
        int eRet = ZIMEVideoClientJNI.ConnectDevice(mZIMEConfig.mChannelId, m_iDeviceType);
        String logString = "surfaceCreated---ConnectDevice Device:" + m_iDeviceType + "----ret: " + eRet;
        Toast.makeText(mContext, logString, Toast.LENGTH_LONG).show();
        m_iDeviceType = DEVTYPE_DEFAULT_VALUE;

        mZIMEJniThread.setAudioMan(am);
        mZIMEJniThread.start();

        Log.i(ZIMETAG, "-------------Start AV Button--------------");
        mZIMEJniThread.Input(ZIMEConfig.SET_PARAM, mZIMEConfig);
        mZIMEJniThread.Input(ZIMEConfig.START, null);

        VideoDeviceCallBack.SetCodecType(ZIMEConfig.mCodecType);

        if(ZIMEConfig.mCodecType == ZIMEConfig.enumZIME_AMLOGICHARDWEAR)
        {
            mVideoGLRender.useMediaCodecInfo(false, 0);
        }
        else if(ZIMEConfig.mCodecType == ZIMEConfig.enumZIME_MediaCodec)
        {
            mVideoGLRender.useMediaCodecInfo(true, mYUVType);
        }
        else
        {
            mVideoGLRender.useMediaCodecInfo(false, 0);
        }

        mStarted = true;
        mStopped = false;
        mDialogSetting.setStatus(mStarted, mStopped);


        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {

        Log.i(ZIMETAG, "-------------Exit Button--------------1");

        mZIMEJniThread.Input(ZIMEConfig.EXIT, null);

        ToastUtil.cancelToast();

        mStarted = false;
        mDialogSetting.setStatus(mStarted, mStopped);

        Log.i(ZIMETAG, "-------------Exit Button--------------3");
        Log.d(ZIMETAG,"service destroy");
        super.onDestroy();
    }


    private void execShellCmd(String cmd) {
        try {
            Log.d(ZIMETAG, "execShellCmd1: " + cmd);
            // 申请获取root权限，这一步很重要，不然会没有作用
            Process process = Runtime.getRuntime().exec("su");
            // 获取输出流
            OutputStream outputStream = process.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(
                    outputStream);
            dataOutputStream.writeBytes(cmd);
            dataOutputStream.flush();
            dataOutputStream.close();
            outputStream.close();
            /*InputStream inputStream = process.getInputStream();
            InputStreamReader buInputStreamReader = new InputStreamReader(inputStream);//装饰器模式
            BufferedReader bufferedReader = new BufferedReader(buInputStreamReader);//直接读字符串
            String str = null;
            StringBuilder sb = new StringBuilder();
            while((str = bufferedReader.readLine())!=null){
                sb.append(str);//每读一行拼接到sb里面去
                sb.append("\n");//每一行一个换行符
            }
            Log.d(ZIMETAG, sb.toString());*/
            Log.d(ZIMETAG, "execShellCmd2: " + cmd);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
    public static String do_exec(String cmd) {
        Log.d(ZIMETAG, "do_exec1: " + cmd);
        String resultMsg = "";
        try {
            Process p = Runtime.getRuntime().exec(cmd);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(p.getInputStream()));
            String line = null;
            while ((line = in.readLine()) != null) {
                resultMsg += line + "|";
            }
            Log.d(ZIMETAG, "do_exec2: " + cmd);
            Log.e(ZIMETAG,resultMsg);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return resultMsg;
    }

}
