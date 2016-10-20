package com.brick.robotctrl;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ant.liao.GifView;
import com.kjn.askquestion.AccountInfoTts;
import com.sinovoice.hcicloudsdk.android.tts.player.TTSPlayer;
import com.sinovoice.hcicloudsdk.api.HciCloudSys;
import com.sinovoice.hcicloudsdk.common.AuthExpireTime;
import com.sinovoice.hcicloudsdk.common.HciErrorCode;
import com.sinovoice.hcicloudsdk.common.InitParam;
import com.sinovoice.hcicloudsdk.common.asr.AsrInitParam;
import com.sinovoice.hcicloudsdk.common.hwr.HwrInitParam;
import com.sinovoice.hcicloudsdk.common.tts.TtsConfig;
import com.sinovoice.hcicloudsdk.common.tts.TtsInitParam;
import com.sinovoice.hcicloudsdk.player.TTSCommonPlayer;
import com.sinovoice.hcicloudsdk.player.TTSPlayerListener;
import com.zhangyt.log.LogUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class ShowSureQueryActivity extends BaseActivity {
    public static final String TAG = "ShowSureQueryActivity";

    private GifView showGf;
    /**
     * 加载用户信息工具类
     */
    private AccountInfoTts mAccountInfo;

    private TextView text = null;
    private String showText = null;
    private TtsConfig ttsConfig = null;
    private TTSPlayer mTtsPlayer = null;
    public String mp3Url = Environment.getExternalStorageDirectory().getPath() + "/Movies/record3.m4a";
    private MediaPlayer mp;
//    private boolean flag = true;
//    ADVideo adVideo = null;
    private Button goButton;
    private Button personservice_bt;
    private boolean flag = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showsurequery );

        showGf =(GifView)findViewById(R.id.gif4);
        showGf.setGifImage(R.drawable.deyi);
        showGf.setGifImageType(GifView.GifImageType.COVER);

        text = (TextView) findViewById(R.id.singleshow);

        final Intent intent = getIntent();
        showText = intent.getStringExtra("extra_showResult");
        text.setText(showText);

//        videoView.setVideoPath(mp3Url);             //获得第一个video的路径
//        videoView.start();                                   //开始播放
//        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {  //监听视频播放块结束时，做next操作
//            @Override
//            public void onCompletion(MediaPlayer mp) {
//                next();
//            }
//        });
        goButton = (Button) findViewById(R.id.returnq);
        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goButton.setClickable(false);
                clearTimerCount();
                if (flag) {
                    mTtsPlayer.release();
                }
                HciCloudSys.hciRelease();
                mp = new MediaPlayer();
                mp.reset();
                try {
                    mp.setDataSource(mp3Url);
                    mp.prepare();
                    mp.start();
                    mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            Intent intent = new Intent(ShowSureQueryActivity.this, QuestTestActivity.class);
                            startActivity(intent);
                        }
                    });
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
        personservice_bt = (Button) findViewById(R.id.humanButton1);
        personservice_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearTimerCount();
                ExpressionActivity.startAction(ShowSureQueryActivity.this, 0);
            }
        });

        mAccountInfo = AccountInfoTts.getInstance();
        boolean loadResult = mAccountInfo.loadAccountInfo(this);
        if (loadResult) {
            // 加载信息成功进入主界面
            Toast.makeText(getApplicationContext(), "加载灵云账号成功",
                    Toast.LENGTH_SHORT).show();
        } else {
            // 加载信息失败，显示失败界面
            Toast.makeText(getApplicationContext(), "加载灵云账号失败！请在assets/AccountInfo.txt文件中填写正确的灵云账户信息，账户需要从www.hcicloud.com开发者社区上注册申请。",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // 加载信息,返回InitParam, 获得配置参数的字符串
        InitParam initParam = getInitParam();
        String strConfig = initParam.getStringConfig();
        LogUtil.i(TAG, "\nhciInit config:" + strConfig);

        // 初始化
        int errCode = HciCloudSys.hciInit(strConfig, this);
        if (errCode != HciErrorCode.HCI_ERR_NONE && errCode != HciErrorCode.HCI_ERR_SYS_ALREADY_INIT) {
            Toast.makeText(getApplicationContext(), "hciInit error: " + HciCloudSys.hciGetErrorInfo(errCode),Toast.LENGTH_SHORT).show();
            return;
        }

        // 获取授权/更新授权文件 :
        errCode = checkAuthAndUpdateAuth();
        if (errCode != HciErrorCode.HCI_ERR_NONE) {
            // 由于系统已经初始化成功,在结束前需要调用方法hciRelease()进行系统的反初始化
            Toast.makeText(getApplicationContext(), "CheckAuthAndUpdateAuth error: " + HciCloudSys.hciGetErrorInfo(errCode),Toast.LENGTH_SHORT).show();
            HciCloudSys.hciRelease();
            return;
        }

        //传入了capKey初始化TTS播发器
        boolean isPlayerInitSuccess = initPlayer();
        flag = isPlayerInitSuccess;
        if (!isPlayerInitSuccess) {
            Toast.makeText(this, "播放器初始化失败", Toast.LENGTH_LONG).show();
            LogUtil.d(TAG, "播放器初始化失败");
            return;
        }

        LogUtil.d(TAG,"播放声音");
        if (showText != null){
            LogUtil.d(TAG,"yao播放声音");
            if (mTtsPlayer != null) {
                try {
                    LogUtil.d(TAG,"播放声音ing");
                    synth(showText);
                } catch (IllegalStateException ex) {
                    Toast.makeText(getBaseContext(), "状态错误", Toast.LENGTH_SHORT)
                            .show();
                }

            }
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    // 测试按钮 ,播放,停止TTS语音播放
//    public void onClick(View v) {
//            try {
//                switch (v.getId()) {
//                    case R.id.returnq:
////                        PlayerService.startPlayerService(ShowSureQueryActivity.this, mp3Url);
//                        mp.setDataSource(mp3Url);
//                        Intent intent = new Intent(ShowSureQueryActivity.this, MainActivity.class);
//                        startActivity(intent);
//
//                        break;
//
//                    /*case R.id.btnPause:
//                        if (mTtsPlayer.getPlayerState() == TTSCommonPlayer.PLAYER_STATE_PLAYING) {
//                            mTtsPlayer.pause();
//                        }
//                        break;
//
//                    case R.id.btnResume:
//                        if (mTtsPlayer.getPlayerState() == TTSCommonPlayer.PLAYER_STATE_PAUSE) {
//                            mTtsPlayer.resume();
//                        }
//                        break;*/
//
//                    default:
//                        break;
//                }
//            } catch (IllegalStateException ex) {
//                Toast.makeText(getBaseContext(), "状态错误", Toast.LENGTH_SHORT)
//                        .show();
//            }
//
//    }

    /**
     * 初始化播放器
     */
    private boolean initPlayer() {
        // 读取用户的调用的能力
        String capKey = mAccountInfo.getCapKey();

        // 构造Tts初始化的帮助类的实例
        TtsInitParam ttsInitParam = new TtsInitParam();
        // 获取App应用中的lib的路径
        String dataPath = getBaseContext().getFilesDir().getAbsolutePath().replace("files", "lib");
        ttsInitParam.addParam(TtsInitParam.PARAM_KEY_DATA_PATH, dataPath);
        // 此处演示初始化的能力为tts.cloud.xiaokun, 用户可以根据自己可用的能力进行设置, 另外,此处可以传入多个能力值,并用;隔开
        ttsInitParam.addParam(AsrInitParam.PARAM_KEY_INIT_CAP_KEYS, capKey);
        // 使用lib下的资源文件,需要添加android_so的标记
        ttsInitParam.addParam(HwrInitParam.PARAM_KEY_FILE_FLAG, "android_so");

        mTtsPlayer = new TTSPlayer();

        // 配置TTS初始化参数
        ttsConfig = new TtsConfig();
        mTtsPlayer.init(ttsInitParam.getStringConfig(), new TTSEventProcess());

        if (mTtsPlayer.getPlayerState() == TTSPlayer.PLAYER_STATE_IDLE) {
            return true;
        } else {
            return false;
        }
    }

    // 云端合成,不启用编码传输(默认encode=none)
    private void synth(String text) {
        // 读取用户的调用的能力
        String capKey = mAccountInfo.getCapKey();

        // 配置播放器的属性。包括：音频格式，音库文件，语音风格，语速等等。详情见文档。
        ttsConfig = new TtsConfig();
        // 音频格式
        ttsConfig.addParam(TtsConfig.BasicConfig.PARAM_KEY_AUDIO_FORMAT, "pcm16k16bit");
        // 指定语音合成的能力(云端合成,发言人是XiaoKun)
        ttsConfig.addParam(TtsConfig.SessionConfig.PARAM_KEY_CAP_KEY, capKey);
        // 设置合成语速
        ttsConfig.addParam(TtsConfig.BasicConfig.PARAM_KEY_SPEED, "5");
        // property为私有云能力必选参数，公有云传此参数无效
        ttsConfig.addParam("property", "cn_wangjing_common");

        if (mTtsPlayer.getPlayerState() == TTSCommonPlayer.PLAYER_STATE_PLAYING
                || mTtsPlayer.getPlayerState() == TTSCommonPlayer.PLAYER_STATE_PAUSE) {
            mTtsPlayer.stop();
        }

        if (mTtsPlayer.getPlayerState() == TTSCommonPlayer.PLAYER_STATE_IDLE) {
            mTtsPlayer.play(text,
                    ttsConfig.getStringConfig());
        } else {
            Toast.makeText(ShowSureQueryActivity.this, "播放器内部状态错误",
                    Toast.LENGTH_SHORT).show();
        }
    }

    // 播放器回调
    private class TTSEventProcess implements TTSPlayerListener {

        @Override
        public void onPlayerEventPlayerError(TTSCommonPlayer.PlayerEvent playerEvent,
                                             int errorCode) {
            LogUtil.i(TAG, "onError " + playerEvent.name() + " code: " + errorCode);
        }

        @Override
        public void onPlayerEventProgressChange(TTSCommonPlayer.PlayerEvent playerEvent,
                                                int start, int end) {
            LogUtil.i(TAG, "onProcessChange " + playerEvent.name() + " from "
                    + start + " to " + end);
        }

        @Override
        public void onPlayerEventStateChange(TTSCommonPlayer.PlayerEvent playerEvent) {
            LogUtil.i(TAG, "onStateChange " + playerEvent.name());
        }

    }

    /**
     * 获取授权
     *
     * @return true 成功
     */
    private int checkAuthAndUpdateAuth() {

        // 获取系统授权到期时间
        int initResult;
        AuthExpireTime objExpireTime = new AuthExpireTime();
        initResult = HciCloudSys.hciGetAuthExpireTime(objExpireTime);
        if (initResult == HciErrorCode.HCI_ERR_NONE) {
            // 显示授权日期,如用户不需要关注该值,此处代码可忽略
            Date date = new Date(objExpireTime.getExpireTime() * 1000);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd",
                    Locale.CHINA);
            LogUtil.i(TAG, "expire time: " + sdf.format(date));

            if (objExpireTime.getExpireTime() * 1000 > System
                    .currentTimeMillis()) {
                // 已经成功获取了授权,并且距离授权到期有充足的时间(>7天)
                LogUtil.i(TAG, "checkAuth success");
                return initResult;
            }

        }

        // 获取过期时间失败或者已经过期
        initResult = HciCloudSys.hciCheckAuth();
        if (initResult == HciErrorCode.HCI_ERR_NONE) {
            LogUtil.i(TAG, "checkAuth success");
            return initResult;
        } else {
            LogUtil.e(TAG, "checkAuth failed: " + initResult);
            return initResult;
        }
    }

    private InitParam getInitParam() {
        String authDirPath = this.getFilesDir().getAbsolutePath();

        // 前置条件：无
        InitParam initParam = new InitParam();

        // 授权文件所在路径，此项必填
        initParam.addParam(InitParam.AuthParam.PARAM_KEY_AUTH_PATH, authDirPath);

        // 是否自动访问云授权,详见 获取授权/更新授权文件处注释
        initParam.addParam(InitParam.AuthParam.PARAM_KEY_AUTO_CLOUD_AUTH, "no");

        // 灵云云服务的接口地址，此项必填
        initParam.addParam(InitParam.AuthParam.PARAM_KEY_CLOUD_URL, AccountInfoTts
                .getInstance().getCloudUrl());

        // 开发者Key，此项必填，由捷通华声提供
        initParam.addParam(InitParam.AuthParam.PARAM_KEY_DEVELOPER_KEY, AccountInfoTts
                .getInstance().getDeveloperKey());

        // 应用Key，此项必填，由捷通华声提供
        initParam.addParam(InitParam.AuthParam.PARAM_KEY_APP_KEY, AccountInfoTts
                .getInstance().getAppKey());

        // 配置日志参数
        String sdcardState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(sdcardState)) {
            String sdPath = Environment.getExternalStorageDirectory()
                    .getAbsolutePath();
            String packageName = this.getPackageName();

            String logPath = sdPath + File.separator + "sinovoice"
                    + File.separator + packageName + File.separator + "log"
                    + File.separator;

            // 日志文件地址
            File fileDir = new File(logPath);
            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }

            // 日志的路径，可选，如果不传或者为空则不生成日志
            initParam.addParam(InitParam.LogParam.PARAM_KEY_LOG_FILE_PATH, logPath);

            // 日志数目，默认保留多少个日志文件，超过则覆盖最旧的日志
            initParam.addParam(InitParam.LogParam.PARAM_KEY_LOG_FILE_COUNT, "5");

            // 日志大小，默认一个日志文件写多大，单位为K
            initParam.addParam(InitParam.LogParam.PARAM_KEY_LOG_FILE_SIZE, "1024");

            // 日志等级，0=无，1=错误，2=警告，3=信息，4=细节，5=调试，SDK将输出小于等于logLevel的日志信息
            initParam.addParam(InitParam.LogParam.PARAM_KEY_LOG_LEVEL, "5");
        }

        return initParam;
    }

}
