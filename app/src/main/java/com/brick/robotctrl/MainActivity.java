package com.brick.robotctrl;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.VideoView;

import com.bean.serialport.ComBean;
import com.bean.serialport.SerialHelper;
import com.cedric.serialport.SerialPortFinder;
import com.kjn.videoview.myvideoview;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener, CompoundButton.OnCheckedChangeListener {

    private static final String TAG = "MainActivity";

    SharedPreferences.OnSharedPreferenceChangeListener presChangeListener = null;

    TextView gravityTextView = null;
    TextView notifyTextView = null;
    ImageView pointView = null;
    CheckBox dirCtrlSwitch = null;
    SSDBTask ssdbTask = null;



    private boolean serverChanged = false;
    private boolean serialChanged = false;

    // relative serial
    Button spinButton;
    ToggleButton toggleButtonCOMA;
    Spinner SpinnerCOMA;
    SerialControl ComA;
    SerialPortFinder mSerialPortFinder;//串口设备搜索

    ////////////videoview相关
    private VideoView videoView;
    private Thread newThread;
    myvideoview myvideoview = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pointView = (ImageView) findViewById(R.id.pointView);
        pointView.setOnTouchListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // remove text in toolbar
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        ssdbTask = new SSDBTask(MainActivity.this, handler);

        notifyTextView = (TextView) findViewById(R.id.notifyTextView);
        pointView = (ImageView) findViewById(R.id.pointView);
        pointView.setOnTouchListener(this);
        dirCtrlSwitch = (CheckBox) findViewById(R.id.dirCtrlCheckBox);
        dirCtrlSwitch.setOnCheckedChangeListener(this);


        //////////////////////////////////////
        videoView = (VideoView) findViewById(R.id.videoView);
        new Thread() {
            @Override
            public void run() {
                myvideoview = new myvideoview(videoView);//这里写入子线程需要做的工作
            }
        }.start();
        



        ///////////////////////////////////////////

        //NOTE OnSharedPreferenceChangeListener: listen settings changed
        presChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            private final String robotName = getString(R.string.robotName);
            private final String serverIp = getString(R.string.serverIp);
            private final String serverPort = getString(R.string.serverPort);
            private final String controlType = getString(R.string.controlType);

            private final String serialBaud = getString(R.string.serialBaud);
            private final String serialCom = getString(R.string.serialCOM);

            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (key.equals(controlType)) {
                    boolean val = sharedPreferences.getBoolean(key, false);
                    changeCtrlType(val);
                    Log.i(TAG, "onSharedPreferenceChanged: " + key + " " + val);
                } else {
                    String val = null;
                    try {
                        val = sharedPreferences.getString(key, "");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (key.equals(robotName)) {
                        ssdbTask.setRobotName(val);
                    } else if (key.equals(serverIp)) {
                        ssdbTask.setServerIP(val);
                        serverChanged = true;
                    } else if (key.equals(serverPort)) {
                        int serverPort = Integer.parseInt(val);
                        ssdbTask.setServerPort(serverPort);
                        serverChanged = true;
                    } else if(key.equals(serialCom)) {
                        // do some thing
                        serialChanged = true;
                    } else if(key.equals(serialBaud)) {
                        // do some thing
                        serialChanged = true;
                    }
                    Log.i(TAG, "onSharedPreferenceChanged: " + key + " " + val);
                }
            }
        };
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(presChangeListener);


        // relative serial
        ComA = new SerialControl();
        setControls();
    }

    // receive ssdb server info
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SSDBTask.ENABLECTRL:
                    dirCtrlSwitch.setChecked(true);
                    break;
                case SSDBTask.ACTION_HGET:
                    String rlt = (String) msg.obj;
                    gravityTextView.setText(rlt);
                    break;
                case SSDBTask.DIRCTRLWARNING:
                    notifyTextView.setText("open switch please");
                    break;
                default:
                    break;
            }
        }
    };

    // relative menu
    Menu menu = null;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(TAG, "onCreateOptionsMenu: set menu UI");
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu = menu;
        return true;
    }

    boolean gravityCtrlEnable = false;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, "onCreateOptionsMenu: "+item);
        switch (item.getItemId()) {
            // menu context
            case R.id.actionSettings:
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivityForResult(intent, 0);
                // do some thing else
                break;
            case R.id.actionSwitchCtrl:
                changeCtrlType(!gravityCtrlEnable);
                // do some thing else
                break;
            default:
                break;
        }
        return true;
    }

    private void changeCtrlType(boolean enable) {
        gravityCtrlEnable = enable;
        if (menu != null) {
            menu.findItem(R.id.actionSwitchCtrl).setIcon(enable ?
                    R.drawable.ic_action_changectrl :
                    R.drawable.ic_action_changectrl_disable);
        }
    }


    PointF lastPoint = new PointF(), initPoint = new PointF(-1f, -1f);
    long lastTime = 0, curTime = 0;
    public static float MAX_RADIUS = 0f;
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v.getId() == R.id.pointView) {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    Log.i(TAG, "onTouch: ACTION_DOWN X:"+event.getRawX()+" Y:"+event.getRawY());
                    lastPoint.set(event.getRawX(), event.getRawY());
                    break;
                case MotionEvent.ACTION_MOVE:
                    Log.i(TAG, "onTouch: ACTION_MOVE X:"+event.getRawX()+" Y:"+event.getRawY());
                    PointF distance = new PointF();
                    distance.set(lastPoint);
                    distance.offset(-event.getRawX(), -event.getRawY());

                    float nextX = pointView.getX() - distance.x;
                    float nextY = pointView.getY() - distance.y;

                    //50ms check once，do not WR ssdb too quick
                    curTime = System.currentTimeMillis();
                    if (curTime - lastTime > 50) {
                        lastTime = curTime;
                        int dir = getMoveDirection(nextX, nextY);
                        ssdbTask.robotMove(dir);
                        notifyTextView.setText(SSDBTask.DirCtrlVals[dir]);
                    }

                    Log.i(TAG, "onTouch: ACTION_MOVE ===========pointView.getX():"+pointView.getX()+"-distance.x:"+distance.x);
                    // limit the pointView in the circle
                    float lengthX = nextX - initPoint.x, lengthY = nextY - initPoint.y;
                    float sqLength = (float) Math.hypot(lengthX, lengthY);
                    if (sqLength > MAX_RADIUS) {
                        nextX = initPoint.x + MAX_RADIUS / sqLength * lengthX;
                        nextY = initPoint.y + MAX_RADIUS / sqLength * lengthY;
                        lastPoint.set(nextX + pointView.getWidth() / 2, nextY + pointView.getHeight() / 2);
                    } else {
                        lastPoint.set(event.getRawX(), event.getRawY());
                    }
                    animationMoveTo(nextX, nextY, 0);
                    break;
                case MotionEvent.ACTION_UP: {
                    Log.i(TAG, "onTouch: ACTION_UP X:"+event.getRawX()+" Y:"+event.getRawY());
                    // back to the center
                    animationMoveTo(initPoint, 150);
                    ssdbTask.robotMove(SSDBTask.DIR_STOP);
                    notifyTextView.setText(SSDBTask.DirCtrlVals[SSDBTask.DIR_STOP]);
                }
                break;
                default:
                    break;
            }
        }
        return true;
    }

    private void animationMoveTo(PointF point, long duration) {
        animationMoveTo(point.x, point.y, duration);
    }
    private void animationMoveTo(float nextX, float nextY, long duration) {
        // move the animator
        ObjectAnimator x = ObjectAnimator.ofFloat(pointView, "x", pointView.getX(), nextX);
        ObjectAnimator y = ObjectAnimator.ofFloat(pointView, "y", pointView.getY(), nextY);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(x, y);
        animatorSet.setDuration(duration);
        animatorSet.start();
    }

    // limit the speed of pointView
    int screenHeight, screenWidth;
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        // 这里来获取容器的宽和高
        if (hasFocus) {
            Point screen = new Point();
            getWindowManager().getDefaultDisplay().getSize(screen);
            screenHeight = screen.y;
            screenWidth = screen.x;
            MAX_RADIUS = screenHeight * 0.35f;
            if (initPoint.x < 0f && initPoint.y < 0f) {
                initPoint.set(pointView.getX(), pointView.getY());
                presChangeListener.onSharedPreferenceChanged(PreferenceManager.getDefaultSharedPreferences(this), getString(R.string.controlType));
            }
            Log.i(TAG, "onWindowFocusChanged: " + initPoint.toString());
        }
    }

    private int getMoveDirection(float x, float y) {
        float deltaX = x - initPoint.x;
        float deltaY = y - initPoint.y;
        float deltaAbsX = Math.abs(deltaX);
        float deltaAbsY = Math.abs(deltaY);
        if (deltaAbsX < MAX_RADIUS / 2 && deltaAbsY < MAX_RADIUS / 2) {
            return SSDBTask.DIR_STOP;
        } else {
            if (deltaAbsY > deltaAbsX) {
                if (deltaY < 0f) {
                    return SSDBTask.DIR_UP;
                } else {
                    return SSDBTask.DIR_DOWN;
                }
            } else {
                if (deltaX < 0f) {
                    return SSDBTask.DIR_LEFT;
                } else {
                    return SSDBTask.DIR_RIGHT;
                }
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.dirCtrlCheckBox) {
            ssdbTask.setDirCtrlEnable(isChecked);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                Log.i(TAG, "onActivityResult: " + data.getBooleanExtra("data", false));
                if (serverChanged) {
                    serverChanged = false;
                    ssdbTask.connect();
                }
                if ( serialChanged ) {
                    serialChanged = false;
                    // do some thing
                }
            }
        }
    }

    @Override
    protected void onStop() {
        Log.i(TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onRestart() {
        Log.i(TAG, "onRestart");
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy");
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(presChangeListener);
        ssdbTask.disConnect();
        CloseComPort(ComA);
        super.onDestroy();
    }






    ///////////////////////// relative serial

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        CloseComPort(ComA);
        setControls();
    }


    private void setControls()
    {
        String appName = getString(R.string.app_name);
        try {
            PackageInfo pinfo = getPackageManager().getPackageInfo("com.bjw.ComAssistant", PackageManager.GET_CONFIGURATIONS);
            String versionName = pinfo.versionName;
            setTitle(appName+" V"+versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        spinButton=(Button)findViewById(R.id.spinButton);
        toggleButtonCOMA=(ToggleButton)findViewById(R.id.toggleButtonCOMA);
        SpinnerCOMA=(Spinner)findViewById(R.id.SpinnerCOMA);
        spinButton.setOnClickListener(new ButtonClickEvent());

        toggleButtonCOMA.setOnCheckedChangeListener(new ToggleButtonCheckedChangeEvent());
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.baudrates_value,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSerialPortFinder= new SerialPortFinder();
        String[] entryValues = mSerialPortFinder.getAllDevicesPath();
        List<String> allDevices = new ArrayList<String>();
        for (int i = 0; i < entryValues.length; i++) {
            allDevices.add(entryValues[i]);
        }
        ArrayAdapter<String> aspnDevices = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, allDevices);
        aspnDevices.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SpinnerCOMA.setAdapter(aspnDevices);
        if (allDevices.size()>0)
        {
            SpinnerCOMA.setSelection(0);
        }
        SpinnerCOMA.setOnItemSelectedListener(new ItemSelectedEvent());
    }
    // close or open serial if serial com and baud changed
    class ItemSelectedEvent implements Spinner.OnItemSelectedListener{
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3)
        {
            if (arg0 == SpinnerCOMA)
            {
                CloseComPort(ComA);
                toggleButtonCOMA.setChecked(false);
            }
        }
        public void onNothingSelected(AdapterView<?> arg0)
        {}

    }

    class ButtonClickEvent implements View.OnClickListener {
        public void onClick(View v)
        {
            if(v==spinButton){
                sendPortData(ComA, "FF10FF10");
            }

        }
    }
    // open or close serial
    class ToggleButtonCheckedChangeEvent implements ToggleButton.OnCheckedChangeListener{
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
        {
            if (buttonView == toggleButtonCOMA){
                if (isChecked){
//						ComA=new SerialControl("/dev/s3c2410_serial0", "9600");
                    ComA.setPort(SpinnerCOMA.getSelectedItem().toString());
                    ComA.setBaudRate(9600);
                    OpenComPort(ComA);
                }else {
                    CloseComPort(ComA);
                }
            }
        }
    }
    // serial control class
    private class SerialControl extends SerialHelper {
        public SerialControl(){
        }

        @Override
        protected void onDataReceived(final ComBean ComRecData)
        {
            // receive data
        }
    }
    // send
    private void sendPortData(SerialHelper ComPort,String sOut){
        if (ComPort!=null && ComPort.isOpen())
        {
            ComPort.sendHex(sOut);
        }
    }
    // close serial
    private void CloseComPort(SerialHelper ComPort){
        if (ComPort!=null){
            ComPort.stopSend();
            ComPort.close();
        }
    }
    // open serial
    private void OpenComPort(SerialHelper ComPort){
        try
        {
            ComPort.open();
        } catch (SecurityException e) {
            ShowMessage("open serial failure: permission denied!");
        } catch (IOException e) {
            ShowMessage("open serial failure: unknow why!");
        } catch (InvalidParameterException e) {
            ShowMessage("open serial failure: parameeter error!");
        }
    }

    private void ShowMessage(String sMsg)
    {
        Toast.makeText(this, sMsg, Toast.LENGTH_SHORT).show();
    }

}
