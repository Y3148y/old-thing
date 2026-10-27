package com.example.hc;

import static androidx.core.content.ContextCompat.startActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.slider.Slider;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int MY_PERMISSION_REQUEST_CONSTANT = 1;
    private Button mOpenBluetoothBtn;
    private Button mCloseBluetoothBtn;
    private Button mFoundDeviceBtn;
    private ListView mDeviceList;
    private MyArrayAdapter mAdapter;
    private List<DeviceInformation> mDatas = new ArrayList<>();
    
    // 二级蓝牙菜单相关变量
    private Button mBluetoothModeBtn;
    private android.app.AlertDialog mBluetoothMenuDialog;
    private LinearLayout mBluetoothDeviceContainer;
    private Button mCloseDeviceListBtn;
    private BluetoothAdapter mBluetoothAdapter;
    private ToastUtil mToast;
    private BroadcastReceiver mBluetoothReceiver;//用于接收蓝牙状态改变广播的广播接收者
    private String TAG = "MainActivity";
    private BroadcastReceiver mBLuetoothStateReceiver;

    // CommunicationActivity合并的变量
    private String mAddress;
    private BluetoothDevice mDevice;
    private BluetoothSocket mBluetoothSocket;
    private final UUID mUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");//蓝牙串口服务的UUID
    private TextView mReceiveContent;
    // private TextView mSendContent;  // 注释：发送数据相关控件
    // private Button mCancelConn;     // 注释：发送数据相关控件
    // private EditText mSendDataInput; // 注释：发送数据相关控件
    // private Button mSendDataBtn;    // 注释：发送数据相关控件
    private ReceiveDataThread receiveThread;
    private static OutputStream outputStream;


    private MyRockerView myRockerView3;

    int delay_ms = 10;

    public static boolean flag = true;
    public static boolean CONNECT_STATUS = false;

    // 在类成员变量区域添加
    private Thread joystickSenderThread;
    private final Object joystickLock = new Object();
    private volatile String pendingJoystickCommand = null;
    private volatile boolean keepJoystickRunning = true;


    SharedPreferences sharedPreferencesFromChooser;//获取数据
    Button change;
    View map1;
    View map2;
    Button touqiu;
    Button chuanqiu;
    Button yunqiu;
    Button jieqiu;
    Button btn1;
    Button btn2;
    Button btn3;
    Button btn4;
    Button btn5;
    Button btn6;
    Button btn7;
    Button btn0;
    Button begin;
    Button stop;
    Button reset;
    Button reset2;
    Button btnGotoAdjust;
    // 方向按键
    Button mainBtnDirectionUp;
    Button mainBtnDirectionDown;
    Button mainBtnDirectionLeft;
    Button mainBtnDirectionRight;
    Slider sl_progress1;

    private void initif() {

        change = findViewById(R.id.change);
        touqiu = findViewById(R.id.touqiu);
        chuanqiu = findViewById(R.id.chuanqiu);
        yunqiu = findViewById(R.id.yunqiu);
        jieqiu = findViewById(R.id.jieqiu);
        begin = findViewById(R.id.begin);
        stop = findViewById(R.id.stop);
        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);
        btn3 = findViewById(R.id.btn3);
        btn4 = findViewById(R.id.btn4);
        btn5 = findViewById(R.id.btn5);
        btn6 = findViewById(R.id.btn6);
        btn7 = findViewById(R.id.btn7);
        btn0 = findViewById(R.id.btn0);
        reset = findViewById(R.id.reset);
        reset2 = findViewById(R.id.reset2);
//        stretch = findViewById(R.id.stretch);
//        reposition = findViewById(R.id.reposition);
//        put = findViewById(R.id.put);
//        drop = findViewById(R.id.drop);
//        directly_put = findViewById(R.id.directly_put);
        map1 = findViewById(R.id.map1);
        map2 = findViewById(R.id.map2);
        myRockerView3 = findViewById(R.id.myRockerView3);
//        arrive = findViewById(R.id.arrive);
//        choice = findViewById(R.id.choice);

//       myRockerView1 = findViewById(R.id.myRockerView3);
//        open = findViewById(R.id.open);
//        close = findViewById(R.id.close);
//        closedoor = findViewById(R.id.closedoor);
        sl_progress1 = findViewById(R.id.seekbar02);


        if (map1.getVisibility() != View.GONE ) {//8 //预选界面:运球点位
            map1.setVisibility(View.VISIBLE);//0


            map2.setVisibility(View.GONE);//image正式比赛界面
            begin.setVisibility(View.VISIBLE);
            stop.setVisibility(View.VISIBLE);
            reset.setVisibility(View.VISIBLE);

            //Button正式比赛界面
            touqiu.setVisibility(View.GONE);
            yunqiu.setVisibility(View.GONE);
            chuanqiu.setVisibility(View.GONE);
            jieqiu.setVisibility(View.GONE);
            reset2.setVisibility(View.GONE);
            sl_progress1.setVisibility(View.VISIBLE);
            myRockerView3.setVisibility(View.VISIBLE);
        }
    }

    // 调试日志相关
    private DebugLogger debugLogger;
    private CrashHandler crashHandler;
    private long lastRockerActionTime = 0;
    private static final long ROCKER_ACTION_INTERVAL = 10; // 摇杆操作最小间隔(ms)
    private int rockerActionCount = 0; // 摇杆操作计数器

    /**
     * 初始化调试系统
     */
    private void initDebugSystem() {
        try {
            debugLogger = DebugLogger.getInstance(this);
            crashHandler = CrashHandler.getInstance();
            crashHandler.init(getApplication());

            debugLogger.writeLog("INIT", "MainActivity初始化开始", "INFO");
            debugLogger.logMemoryUsage();
        } catch (Exception e) {
            Log.e(TAG, "初始化调试系统失败", e);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化调试日志系统
        initDebugSystem();

        // 初始化摇杆发送线程
        initJoystickSenderThread();

        initReceiver();
        initif();
        initView();
        Objects.requireNonNull(getSupportActionBar()).hide();
        initListener();
        openBluetooth();//开始运行自动开启蓝牙
        discoverBluetooth();//开始运行自动进行搜索

        Slider.OnSliderTouchListener mSliderListener1= new Slider.OnSliderTouchListener() {


            // 在开始滑动进度时触发
            @SuppressLint("RestrictedApi")
            @Override
            public void onStartTrackingTouch(Slider slider) {

            }

            // 在停止滑动进度时触发
            @SuppressLint("RestrictedApi")
            @Override
            public void onStopTrackingTouch(Slider slider) {

                if(slider.getValue()==0.0){

                    slider.setValue(0);
                } else if(slider.getValue()==1.0) {
                    slider.setValue(0);
                }else if(slider.getValue()==2.0){
                    slider.setValue(0);
                } else if (slider.getValue()==3.0) {
                    slider.setValue(0);
                } else if (slider.getValue()==4.0) {
                    slider.setValue(0);
                }
                Log.d(TAG, "onStopTrackingTouch: -----------------------setValue---");

            }





        };
        sl_progress1.addOnSliderTouchListener(mSliderListener1); // 设置滑动条的触摸监听器

        sl_progress1.addOnChangeListener(new Slider.OnChangeListener() {

            @SuppressLint("RestrictedApi")
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                if (slider.getValue() == 2.0) {


                    for (int i = 0; i < 2; i++) {
                        SendCommand("RC0015SD", delay_ms);
                    }

                    toastShorter("RC0015SD~", 700);


                } else if (slider.getValue() == 1.0) {

                    for (int i = 0; i < 2; i++) {
                        SendCommand("RC0010SD", delay_ms);
                    }
//                    Log.e("slider", "onStopTrackingTouch: VE0001R1");
                    toastShorter("RC0010SD~", 700);

                }else if (slider.getValue() == 0.0) {

                    for (int i = 0; i < 2; i++) {
                        SendCommand("RC0005SD", delay_ms);
                    }
                    Log.e("slider", "onStopTrackingTouch: VE0001R1");
                    toastShorter("RC0005SD~", 700);

                } else if (slider.getValue() == 3.0) {
                    for (int i = 0; i < 2; i++) {
                        SendCommand("RC0020SD", delay_ms);
                    }
                    Log.e("slider", "onStopTrackingTouch: VE0001R1");
                    toastShorter("RC0020SD~", 700);

                } else if (slider.getValue() == 4.0) {
                    for (int i = 0; i < 2; i++) {
                        SendCommand("RC0025SD", delay_ms);
                    }
                    Log.e("slider", "onStopTrackingTouch: VE0001R1");
                    toastShorter("RC0025SD~", 700);

                }
            }
        });


        myRockerView3.setOnAngleChangeListener(new MyRockerView.OnAngleChangeListener() {
            private final RateLimiter rateLimiter = new RateLimiter(delay_ms); // 50ms间隔
            @Override
            public void onStart() {

            }

            @Override
            public void angle(double angle) {
//                int angle_int =  (int)angle;
//                if (angle_int >= 0 && angle_int <= 9){
//                    //-------------------------------线程阻塞-------------------------------------------------------------------------
//                    for (int i = 0; i < 4; i++) {
//                    SendCommand("RC000"+angle_int+"YG", delay_ms);
//
//                    }
//
////                    tv_angle_2.setText("00"+angle_int);
//                }
//                else if (angle_int >= 10 && angle_int <= 99){
//                    for (int i = 0; i < 4; i++) {
//                    SendCommand("RC00"+angle_int+"YG", delay_ms);
//                    }
//
////                    tv_angle_2.setText("0"+angle_int);
//                }
//                else {
//                    for (int i = 0; i < 4; i++) {
//                        SendCommand("RC0"+angle_int+"YG", delay_ms);}
//
////                    tv_angle_2.setText(""+angle_int);
//                }

                try {

                if (!rateLimiter.tryAcquire()) return;

                    rockerActionCount++;
                int angleInt = (int)angle;
                String command;
// 记录摇杆操作日志
                if (debugLogger != null && rockerActionCount % 20 == 0) { // 每20次记录一次详细日志
                    debugLogger.logRockerAction("ROCKER_ANGLE", "摇杆角度: " + angleInt + ", 操作次数: " + rockerActionCount);
                    debugLogger.logMemoryUsage();
                }
                // 格式化命令
                if (angleInt >= 0 && angleInt <= 9) {
                    command = "RC000" + angleInt + "YG";
                } else if (angleInt >= 10 && angleInt <= 99) {
                    command = "RC00" + angleInt + "YG";
                } else {
                    command = "RC0" + angleInt + "YG";
                }

                // 提交到摇杆发送线程
                synchronized (joystickLock) {
                    pendingJoystickCommand = command;
                    joystickLock.notifyAll();
                }
                }catch (Exception e){
                    if (debugLogger != null) {
                        debugLogger.logException("ROCKER_ANGLE_ERROR", "摇杆角度处理异常", e);
                    }
                }

            }

            @Override
            public void onFinish() {
                try{
                synchronized (joystickLock) {
                    pendingJoystickCommand = "RC0999YG"; // 停止命令
                    joystickLock.notifyAll();
                }
//                for (int i = 0; i < 4; i++) {
//                    SendCommand("RC0999YG", delay_ms);
//                }

                }   catch (Exception e){
                    if (debugLogger != null) {
                        debugLogger.writeLog("ERROR", "摇杆停止操作异常: " + e.getMessage(), "ERROR");
                    }

                }            }
        });


        begin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toastShorter("RC0001R1~", 300);
                SendCommand("RC0001R1", delay_ms);
                //MainActivity.write( "ReStart");
            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                toastShorter("RC0099R1~", 300);
                SendCommand("RC0099R1", delay_ms);
            }
        });
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toastShorter("RC0001DW~", 300);
                SendCommand("RC0001DW", delay_ms);
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toastShorter("RC0002DW~", 300);
                SendCommand("RC0002DW", delay_ms);
            }
        });
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toastShorter("RC0003DW~", 300);
                SendCommand("RC0003DW", delay_ms);
            }
        });
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toastShorter("RC0004DW~", 300);
                SendCommand("RC0004DW", delay_ms);
            }
        });
        btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toastShorter("RC0005DW~", 300);
                SendCommand("RC0005DW", delay_ms);
            }
        });
        btn6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toastShorter("RC0006DW~", 300);
                SendCommand("RC0006DW", delay_ms);
            }
        });
        btn7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toastShorter("RC0007DW~", 300);
                SendCommand("RC0007DW", delay_ms);
            }
        });
        btn0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toastShorter("RC0000DW~", 300);
                SendCommand("RC0000DW", delay_ms);
            }
        });
        yunqiu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toastShorter("RC0101R1~", 300);
                SendCommand("RC0101R1", delay_ms);
            }
        });
        touqiu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toastShorter("RC0102R1~", 300);
                SendCommand("RC0102R1", delay_ms);
            }
        });

        chuanqiu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toastShorter("RC0103R1~", 300);
                SendCommand("RC0103R1",  delay_ms);
            }
        });
        jieqiu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toastShorter("RC0100R1~", 300);
                SendCommand("RC0100R1", delay_ms);
            }
        });
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toastShorter("RC0011R1~", 300);
                SendCommand("RC0011R1", delay_ms);
            }
        });
        reset2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toastShorter("RC0111R1~", 300);
                SendCommand("RC0111R1", delay_ms);
            }
        });

        // 设置方向按键监听器 - RC0xxxYT 000\090\180\270
        mainBtnDirectionUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendDirectionCommand("000");
            }
        });

        mainBtnDirectionRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendDirectionCommand("090");
            }
        });

        mainBtnDirectionDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendDirectionCommand("180");
            }
        });

        mainBtnDirectionLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendDirectionCommand("270");
            }
        });



    }







    private void initJoystickSenderThread() {
        joystickSenderThread = new Thread(() -> {
            while (keepJoystickRunning) {
                try {
                    String commandToSend;
                    synchronized (joystickLock) {
                        while (pendingJoystickCommand == null) {
                            joystickLock.wait();
                            if (!keepJoystickRunning) return;
                        }
                        commandToSend = pendingJoystickCommand;
                        pendingJoystickCommand = null;
                    }

                    if (commandToSend != null && CONNECT_STATUS) {
                        // 同步发送4次（保持原有逻辑）
                            // UI反馈
                            runOnUiThread(() -> toastShorter(commandToSend, 200));
                        for (int i = 0; i < 4; i++) {
                            long startTime = System.currentTimeMillis();

                            outputStream.write(commandToSend.getBytes());
                            outputStream.flush();


                            // 确保每次发送间隔至少20ms
                            long elapsed = System.currentTimeMillis() - startTime;
                            if (elapsed < delay_ms) {
                                Thread.sleep(delay_ms - elapsed);
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "摇杆发送线程异常", e);
                    if (e instanceof IOException) {
                        runOnUiThread(() -> mToast.showToast("蓝牙连接中断"));
                        CONNECT_STATUS = false;
                    }
                }
            }
        }, "JoystickSenderThread");
        joystickSenderThread.start();
    }



    /*
        注册广播接收者
         */
    private void initReceiver() {
        //创建用于接收蓝牙状态改变广播的广播接收者
        mBLuetoothStateReceiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
                switch (state){
                    case BluetoothAdapter.STATE_ON:
                        mToast.showToast("蓝牙已打开");
                        break;
                    case BluetoothAdapter.STATE_OFF:
                        mToast.showToast("蓝牙已关闭");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        mToast.showToast("蓝牙正在打开");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        mToast.showToast("蓝牙正在关闭");
                        break;
                }
            }
        };
        //创建设备扫描广播接收者
        mBluetoothReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG,"onReceive");

                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    boolean isAdded = false;//标记扫描到的设备是否已经在数据列表里了
                    //获取扫描到的设备
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    //保存设备的信息
                    DeviceInformation deviceInformation = new DeviceInformation(device.getName(),device.getAddress());
                    for (DeviceInformation data : mDatas) {
                        //判断已保存的设备信息里是否有一样的
                        if (data.getDeviceAddress().equals(deviceInformation.getDeviceAddress())) {
                            isAdded = true;
                            break;
                        }
                    }
                    if (!isAdded) {
                        //通知UI更新
                        mDatas.add(deviceInformation);
                        mAdapter.notifyDataSetChanged();
                    }
                }
            }
        };
        //注册广播接收者
        IntentFilter filter1 = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        IntentFilter filter2 = new IntentFilter(BluetoothDevice.ACTION_FOUND);

        registerReceiver(mBLuetoothStateReceiver,filter1);
        registerReceiver(mBluetoothReceiver,filter2);
    }
    //权限是否授予，给出提示
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_CONSTANT: {
                int grantedCount = 0;
                int deniedCount = 0;
                StringBuilder deniedPermissions = new StringBuilder();
                
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        grantedCount++;
                    } else {
                        deniedCount++;
                        if (deniedPermissions.length() > 0) {
                            deniedPermissions.append(", ");
                        }
                        // 简化权限名称显示
                        String permissionName = permissions[i];
                        if (permissionName.contains("BLUETOOTH_SCAN")) {
                            deniedPermissions.append("蓝牙扫描");
                        } else if (permissionName.contains("BLUETOOTH_CONNECT")) {
                            deniedPermissions.append("蓝牙连接");
                        } else if (permissionName.contains("NEARBY_WIFI_DEVICES")) {
                            deniedPermissions.append("附近设备");
                        } else if (permissionName.contains("ACCESS_FINE_LOCATION")) {
                            deniedPermissions.append("精确位置");
                        } else if (permissionName.contains("ACCESS_COARSE_LOCATION")) {
                            deniedPermissions.append("大致位置");
                        }
                    }
                }
                
                if (deniedCount == 0) {
                    mToast.showToast("所有权限授权成功，可以正常使用蓝牙功能");
                } else {
                    mToast.showToast("缺少权限: " + deniedPermissions.toString() + "，部分功能可能无法使用");
                }
                return;
            }
        }
    }

    private void initView() {
        //动态申请蓝牙相关权限
        requestBluetoothPermissions();
        // 注释掉原来的按钮，因为它们在布局中被注释了
        // mOpenBluetoothBtn = findViewById(R.id.open_bluetooth_btn);
        // mCloseBluetoothBtn = findViewById(R.id.close_bluetooth_btn);
        // mFoundDeviceBtn = findViewById(R.id.fount_device_btn);
        
        // 初始化新的设备列表控件
        mDeviceList = findViewById(R.id.bluetooth_device_list);
        mBluetoothDeviceContainer = findViewById(R.id.bluetooth_device_container);
        mCloseDeviceListBtn = findViewById(R.id.close_device_list_btn);
        
        mToast = new ToastUtil(this);
        mAdapter = new MyArrayAdapter(mDatas,this);
        if (mDeviceList != null) {
            mDeviceList.setAdapter(mAdapter);
        }
        
        // 初始化蓝牙模式按钮
        mBluetoothModeBtn = findViewById(R.id.Btn_Connection);
        
        // 初始化CommunicationActivity的UI组件
        mReceiveContent = findViewById(R.id.receive_content);
        // mSendContent = findViewById(R.id.send_content);      // 注释：发送数据相关控件初始化
        // mCancelConn = findViewById(R.id.cancel_conn);        // 注释：发送数据相关控件初始化
        // mSendDataInput = findViewById(R.id.send_data_input); // 注释：发送数据相关控件初始化
        // mSendDataBtn = findViewById(R.id.send_data_btn);     // 注释：发送数据相关控件初始化



        change = findViewById(R.id.change);
        touqiu = findViewById(R.id.touqiu);
        chuanqiu = findViewById(R.id.chuanqiu);
        yunqiu = findViewById(R.id.yunqiu);
        jieqiu = findViewById(R.id.jieqiu);
        begin = findViewById(R.id.begin);
        stop = findViewById(R.id.stop);
        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);
        btn3 = findViewById(R.id.btn3);
        btn4 = findViewById(R.id.btn4);
        btn5 = findViewById(R.id.btn5);
        btn6 = findViewById(R.id.btn6);
        btn7 = findViewById(R.id.btn7);
        btn0 = findViewById(R.id.btn0);
        reset = findViewById(R.id.reset);
        reset2 = findViewById(R.id.reset2);
        btnGotoAdjust = findViewById(R.id.btn_goto_adjust);

        // 初始化方向按键
        mainBtnDirectionUp = findViewById(R.id.main_btn_direction_up);
        mainBtnDirectionDown = findViewById(R.id.main_btn_direction_down);
        mainBtnDirectionLeft = findViewById(R.id.main_btn_direction_left);
        mainBtnDirectionRight = findViewById(R.id.main_btn_direction_right);


        map1 = findViewById(R.id.map1);
        map2 = findViewById(R.id.map2);
        myRockerView3 = findViewById(R.id.myRockerView3);

        sl_progress1 = findViewById(R.id.seekbar02);
        sharedPreferencesFromChooser = getSharedPreferences("setDataToMainActivity", MODE_PRIVATE);//创建一个SharedPreferences对象
        String role = sharedPreferencesFromChooser.getString("checkRole", "notExist");// 读取键值，如果没找到，返回第二个参数指定的值。
        Log.e("role", "role: "+role);

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "---------------onClick:change ----------------"+flag);
                 if(flag){
                    Log.d(TAG, "---------------onClick:map1.getVisibility() ----------------"+flag+map1.getVisibility());
                    map1.setVisibility(View.VISIBLE);//0


                    map2.setVisibility(View.GONE);//image正式比赛界面
                    begin.setVisibility(View.VISIBLE);
                    Log.d(TAG, "--------onClick:begin.setVisibility(View.VISIBLE);");
                    stop.setVisibility(View.VISIBLE);
                    reset.setVisibility(View.VISIBLE);

                    //Button正式比赛界面
                    touqiu.setVisibility(View.GONE);
                    yunqiu.setVisibility(View.GONE);
                    chuanqiu.setVisibility(View.GONE);
                    jieqiu.setVisibility(View.GONE);
                    reset2.setVisibility(View.GONE);
                    sl_progress1.setVisibility(View.VISIBLE);
                    myRockerView3.setVisibility(View.VISIBLE);
                    Log.d(TAG, "--------onClick: 预选界面finish");
                    flag=false;
                }else {//map2正式比赛界面
                    Log.d(TAG, "---------------onClick:map2.getVisibility() ----------------"+flag+map2.getVisibility());
                    map1.setVisibility(View.GONE);
                    reset.setVisibility(View.GONE);

//                    map2.setVisibility(View.GONE);
                    map2.setVisibility(View.VISIBLE);

                    begin.setVisibility(View.GONE);
                    stop.setVisibility(View.GONE);


                    reset2.setVisibility(View.VISIBLE);
                    touqiu.setVisibility(View.VISIBLE);
                    yunqiu.setVisibility(View.VISIBLE);
                    chuanqiu.setVisibility(View.VISIBLE);
                    jieqiu.setVisibility(View.VISIBLE);
                    myRockerView3.setVisibility(View.VISIBLE);
                    sl_progress1.setVisibility(View.VISIBLE);
                    Log.d(TAG, "--------onClick: 正式界面");
                    flag=true;
                }
            }
        });

        // 设置跳转到AdjustActivity的按钮点击事件
        btnGotoAdjust.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AdjustActivity.class);
                startActivity(intent);
            }
        });





    }
    //初始化监听
    private void initListener() {
        if (mOpenBluetoothBtn != null) {
            mOpenBluetoothBtn.setOnClickListener(this);
        }
        if (mCloseBluetoothBtn != null) {
            mCloseBluetoothBtn.setOnClickListener(this);
        }
        if (mFoundDeviceBtn != null) {
            mFoundDeviceBtn.setOnClickListener(this);
        }
        
        // 添加蓝牙模式按钮的点击监听器
        if (mBluetoothModeBtn != null) {
            mBluetoothModeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showBluetoothMenu();
                }
            });
        }
        
        // 注释：发送数据相关控件的监听器设置
        // 添加取消连接按钮的监听器
        // if (mCancelConn != null) {
        //     mCancelConn.setOnClickListener(new View.OnClickListener() {
        //         @Override
        //         public void onClick(View view) {
        //             disconnectDevice();
        //         }
        //     });
        // }
        // 
        // // 添加发送数据按钮的监听器
        // if (mSendDataBtn != null) {
        //     mSendDataBtn.setOnClickListener(new View.OnClickListener() {
        //         @Override
        //         public void onClick(View view) {
        //             sendData();
        //         }
        //     });
        // }
        
        // 添加关闭设备列表按钮的监听器
        if (mCloseDeviceListBtn != null) {
            mCloseDeviceListBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    hideDeviceList();
                }
            });
        }
        //设备列表item的点击事件
        if (mDeviceList != null) {
            mDeviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    if (mBluetoothAdapter.isDiscovering()) {
                        //停止搜索设备
                        mBluetoothAdapter.cancelDiscovery();
                    }
                    //获取点击的item的设备信息
                    DeviceInformation deviceInformation = mDatas.get(position);
                    //直接在MainActivity中建立连接，不再跳转
                    mAddress = deviceInformation.getDeviceAddress();
                    connectDevice();
                    
                    // 隐藏设备列表控件
                    hideDeviceList();
                }
            });
        }
    }

    /**
     * 请求蓝牙相关权限
     */
    private void requestBluetoothPermissions() {
        List<String> permissionsToRequest = new ArrayList<>();
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ 需要附近设备权限
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.BLUETOOTH_SCAN);
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.BLUETOOTH_CONNECT);
            }
            if (ActivityCompat.checkSelfPermission(this, "android.permission.NEARBY_WIFI_DEVICES") != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add("android.permission.NEARBY_WIFI_DEVICES");
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Android 12+ 需要新的蓝牙权限
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.BLUETOOTH_SCAN);
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.BLUETOOTH_CONNECT);
            }
            // 如果没有使用neverForLocation标志，仍需要位置权限
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android 6.0+ 需要位置权限
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
        }
        
        if (!permissionsToRequest.isEmpty()) {
            ActivityCompat.requestPermissions(this, 
                permissionsToRequest.toArray(new String[0]), 
                MY_PERMISSION_REQUEST_CONSTANT);
        }
    }

    /**
     * 检查蓝牙权限
     */
    private boolean checkBluetoothPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            boolean hasBluetoothConnect = ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED;
            boolean hasBluetoothScan = ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED;
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // Android 13+ 检查附近设备权限
                boolean hasNearbyDevices = ActivityCompat.checkSelfPermission(this, "android.permission.NEARBY_WIFI_DEVICES") == PackageManager.PERMISSION_GRANTED;
                return hasBluetoothConnect && hasBluetoothScan && hasNearbyDevices;
            } else {
                return hasBluetoothConnect && hasBluetoothScan;
            }
        } else {
            return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        }
    }

    /**
     * 检测和开启蓝牙
     */
    private void openBluetooth() {
        if (!checkBluetoothPermissions()) {
            mToast.showToast("请先授予蓝牙权限");
            return;
        }
        
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter != null) {
            //判断蓝牙是否打开并可见
            if (!mBluetoothAdapter.isEnabled()) {
                //请求打开并可见
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent,1);
                mToast.showToast("蓝牙已打开");
            }
            else{
                mToast.showToast("蓝牙已经打开");
            }
        }else{
            mToast.showToast("设备不支持蓝牙功能");
        }
    }

    /**
     * 关闭蓝牙
     */
    private void closeBluetooth() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter != null) {
            //判断蓝牙是否打开并可见
            if (mBluetoothAdapter.isEnabled()) {
                //请求关闭
                mBluetoothAdapter.disable();
                mToast.showToast("蓝牙已关闭");
            }
            else{
                mToast.showToast("蓝牙已经关闭");
            }
        }else{
            mToast.showToast("设备不支持蓝牙功能");
        }
    }

    /**
     * 搜索蓝牙设备
     */
    private void discoverBluetooth(){
        if (!checkBluetoothPermissions()) {
            mToast.showToast("请先授予蓝牙权限");
            return;
        }
        
        if (mBluetoothAdapter == null) {
            mToast.showToast("设备不支持蓝牙功能");
            return;
        }
        
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        //清空之前的搜索结果
        mDatas.clear();
        
        //获取已配对的设备
        Set<BluetoothDevice> bondedDevices = mBluetoothAdapter.getBondedDevices();
        if (bondedDevices.size() > 0) {
            for (BluetoothDevice device : bondedDevices) {
                String deviceName = device.getName();
                String deviceAddress = device.getAddress();
                if (deviceName == null) {
                    deviceName = "未知设备";
                }
                DeviceInformation deviceInfo = new DeviceInformation(deviceName, deviceAddress);
                mDatas.add(deviceInfo);
            }
            mToast.showToast("已显示" + bondedDevices.size() + "个已配对设备");
        } else {
            mToast.showToast("没有已配对的设备");
        }
        
        mAdapter.notifyDataSetChanged();
    }
    
    /**
     * 显示蓝牙菜单对话框
     */
    private void showBluetoothMenu() {
        if (mBluetoothMenuDialog != null && mBluetoothMenuDialog.isShowing()) {
            return;
        }
        
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("蓝牙功能菜单");
        
        // 创建自定义布局
        android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(50, 30, 50, 30);
        
        // 创建三个按钮
        android.widget.Button openBluetoothBtn = new android.widget.Button(this);
        openBluetoothBtn.setText("打开蓝牙");
        openBluetoothBtn.setTextSize(16);
        openBluetoothBtn.setPadding(20, 20, 20, 20);
        
        android.widget.Button searchBluetoothBtn = new android.widget.Button(this);
        searchBluetoothBtn.setText("搜索蓝牙");
        searchBluetoothBtn.setTextSize(16);
        searchBluetoothBtn.setPadding(20, 20, 20, 20);
        
        android.widget.Button disconnectBtn = new android.widget.Button(this);
        disconnectBtn.setText("断开连接");
        disconnectBtn.setTextSize(16);
        disconnectBtn.setPadding(20, 20, 20, 20);
        
        // 设置按钮点击事件
        openBluetoothBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBluetooth();
                if (mBluetoothMenuDialog != null) {
                    mBluetoothMenuDialog.dismiss();
                }
            }
        });
        
        searchBluetoothBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                discoverBluetooth();
                if (mBluetoothMenuDialog != null) {
                    mBluetoothMenuDialog.dismiss();
                }
                // 显示设备列表控件
                showDeviceList();
            }
        });
        
        disconnectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disconnectDevice();
                if (mBluetoothMenuDialog != null) {
                    mBluetoothMenuDialog.dismiss();
                }
            }
        });
        
        // 添加按钮到布局
        layout.addView(openBluetoothBtn);
        layout.addView(searchBluetoothBtn);
        layout.addView(disconnectBtn);
        
        builder.setView(layout);
        builder.setNegativeButton("取消", null);
        
        mBluetoothMenuDialog = builder.create();
        mBluetoothMenuDialog.show();
    }
    
    /**
     * 显示设备列表控件
     */
    private void showDeviceList() {
        if (mBluetoothDeviceContainer != null) {
            mBluetoothDeviceContainer.setVisibility(View.VISIBLE);
        }
    }
    
    /**
     * 隐藏设备列表控件
     */
    private void hideDeviceList() {
        if (mBluetoothDeviceContainer != null) {
            mBluetoothDeviceContainer.setVisibility(View.GONE);
        }
        // 停止搜索
        if (mBluetoothAdapter != null && mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
    }

    /**
     点击事件
     */
    @Override
    public void onClick(View view) {
//        if(view.getId() == R.id.open_bluetooth_btn){
//            openBluetooth();
//        } else if (view.getId() == R.id.close_bluetooth_btn) {
//            closeBluetooth();
//        }else if(view.getId() == R.id.fount_device_btn){
//            discoverBluetooth();
//        }
//        switch (view.getId()){
//            case R.id.open_bluetooth_btn:
//                //开启蓝牙
//                openBluetooth();
//                break;
//            case R.id.close_bluetooth_btn:
//                //关闭蓝牙
//                closeBluetooth();
//                break;
//            case R.id.fount_device_btn:
//                //搜索设备
//                discoverBluetooth();
//                break;
//            default:
//                break;
//        }
    }
    
    /**
     * 与目标设备建立连接 (从CommunicationActivity合并)
     */
    private void connectDevice() {
        if (!checkBluetoothPermissions()) {
            mToast.showToast("请先授予蓝牙权限");
            return;
        }
        
        if (mBluetoothAdapter == null || mAddress == null) {
            mToast.showToast("蓝牙适配器或设备地址无效");
            return;
        }
        
        //通过地址拿到该蓝牙设备device
        mDevice = mBluetoothAdapter.getRemoteDevice(mAddress);
        try {
            //建立socket通信
            mBluetoothSocket = mDevice.createRfcommSocketToServiceRecord(mUUID);
            mBluetoothSocket.connect();
            if (mBluetoothSocket.isConnected()) {
                mToast.showToast("连接成功");
                CONNECT_STATUS = true;
                //获取输出流用于发送数据
                outputStream = mBluetoothSocket.getOutputStream();
                //开启接收数据的线程
                receiveThread = new ReceiveDataThread();
                receiveThread.start();
            }else{
                mToast.showToast("连接失败，请重试");
                CONNECT_STATUS = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            mToast.showToast("连接出错！ ");
            CONNECT_STATUS = false;
            try {
                if (mBluetoothSocket != null) {
                    mBluetoothSocket.close();
                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
    
    /**
     * 断开设备连接
     */
    private void disconnectDevice() {
        try {
            if (receiveThread != null) {
                receiveThread.interrupt();
                receiveThread = null;
            }
            if (outputStream != null) {
                outputStream.close();
                outputStream = null;
            }
            if (mBluetoothSocket != null && mBluetoothSocket.isConnected()) {
                //关闭socket
                mBluetoothSocket.close();
                mToast.showToast("连接已断开");
            }
            CONNECT_STATUS = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // 注释：原发送数据方法
    // /**
    //  * 发送数据
    //  */
    // private void sendData() {
    //     if (mBluetoothSocket == null || !mBluetoothSocket.isConnected()) {
    //         mToast.showToast("请先连接设备");
    //         return;
    //     }
    //     
    //     if (mSendDataInput == null || outputStream == null) {
    //         mToast.showToast("发送组件未初始化");
    //         return;
    //     }
    //     
    //     String dataToSend = mSendDataInput.getText().toString().trim();
    //     if (dataToSend.isEmpty()) {
    //         mToast.showToast("请输入要发送的数据");
    //         return;
    //     }
    //     
    //     try {
    //         // 发送数据
    //         outputStream.write(dataToSend.getBytes());
    //         outputStream.flush();
    //         
    //         // 在UI线程中显示发送的数据
    //          runOnUiThread(new Runnable() {
    //              @Override
    //              public void run() {
    //                  if (mSendContent != null) {
    //                      mSendContent.append(dataToSend + "\n");
    //                      // 自动滚动发送区域到底部
    //                      final android.widget.ScrollView sendScrollView = (android.widget.ScrollView) mSendContent.getParent();
    //                      if (sendScrollView != null) {
    //                          sendScrollView.post(new Runnable() {
    //                              @Override
    //                              public void run() {
    //                                  sendScrollView.fullScroll(android.widget.ScrollView.FOCUS_DOWN);
    //                              }
    //                          });
    //                      }
    //                  }
    //                  // 清空输入框
    //                  mSendDataInput.setText("");
    //              }
    //          });
    //         
    //         mToast.showToast("数据发送成功");
    //     } catch (IOException e) {
    //         e.printStackTrace();
    //         mToast.showToast("数据发送失败: " + e.getMessage());
    //     }
    // }
    
    /**
     * 显示短时间Toast提示
     * @param word 要显示的文字
     * @param delay_ms Toast显示的持续时间（毫秒）
     */
    public void toastShorter(final String word, int delay_ms) {
        this.runOnUiThread(new Runnable() {
            public void run() {
                final Toast toast = Toast.makeText(MainActivity.this, word, Toast.LENGTH_LONG);
                toast.show();
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        toast.cancel();
                    }
                }, delay_ms);
            }
        });
    }
    
    /**
     * 静态写入方法
     * @param str 要写入的字符串
     */
    public static void write(String str) throws IOException {
        if (outputStream != null) {
            outputStream.write(str.getBytes());
            outputStream.flush();
            // 记录数据发送成功日志
            DebugLogger debugLogger = DebugLogger.getInstance(null);
            if (debugLogger != null) {
                debugLogger.logDataSend(str, true);
            }
        } else {
            DebugLogger debugLogger = DebugLogger.getInstance(null);
            if (debugLogger != null) {
                debugLogger.logBluetoothConnection("WRITE_FAILED", "mmSocket为null，连接已断开");
            }
            throw new IOException("输出流未初始化");
        }
    }
    
    /**
     * 发送命令数据
     * @param command 要发送的命令字符串
     * @param delay_ms 发送后的延迟时间（毫秒）
     */
    public void SendCommand(String command, int delay_ms) {
        // 检查蓝牙连接状态
        if (mBluetoothSocket == null || !mBluetoothSocket.isConnected()) {
            mToast.showToast("请先连接设备");
            return;
        }
        
        // 检查输出流是否可用
        if (outputStream == null) {
            mToast.showToast("发送组件未初始化");
            return;
        }
        
        // 检查命令是否为空
        if (command == null || command.trim().isEmpty()) {
            mToast.showToast("命令不能为空");
            return;
        }
        
        // 显示发送的命令Toast
        toastShorter(command, 100);
        // 记录命令发送日志
        if (debugLogger != null) {
            debugLogger.logDataSend(command, CONNECT_STATUS);
        }
        try {
            // 发送命令数据
            write(command);
            
            // 注释：在接收区域显示发送的命令（已注释，UI控件只显示接收数据）
            // runOnUiThread(new Runnable() {
            //     @Override
            //     public void run() {
            //         if (mReceiveContent != null) {
            //             mReceiveContent.append("发送命令: " + command + "\n");
            //             // 自动滚动到底部
            //             final android.widget.ScrollView scrollView = (android.widget.ScrollView) mReceiveContent.getParent();
            //             if (scrollView != null) {
            //                 scrollView.post(new Runnable() {
            //                     @Override
            //                     public void run() {
            //                         scrollView.fullScroll(android.widget.ScrollView.FOCUS_DOWN);
            //                     }
            //                 });
            //             }
            //         }
            //     }
            // });
            
            // 如果设置了延迟时间，则等待指定时间
            if (delay_ms > 0) {
                try {
                    Thread.sleep(delay_ms);
                } catch (InterruptedException e) {
                    if (debugLogger != null) {
                        debugLogger.logException("SEND_COMMAND", "发送命令线程中断", e);
                    }
                    Thread.currentThread().interrupt();
                }
            }
            
//            mToast.showToast("命令发送成功");
        } catch (IOException e) {
            if (debugLogger != null) {
                debugLogger.logException("SEND_COMMAND", "发送命令异常: " + command, e);
            }
            e.printStackTrace();
            mToast.showToast("命令发送失败: " + e.getMessage());
        }
    }

    private void sendDCommand(String command) {
     new Thread(new Runnable() {
        @Override
        public void run() {
            for (int i = 0; i < 4; i++) {
                try {
                    if (!MainActivity.CONNECT_STATUS) {
                        runOnUiThread(() -> Toast.makeText(MainActivity.this, "蓝牙未连接，请先连接设备", Toast.LENGTH_SHORT).show());
                        return;
                    }
                    MainActivity.write(command);
                    Log.d(TAG, "异步发送方向命令: " + command);
                    runOnUiThread(() -> toastShorter(command, delay_ms));
                    Thread.sleep(30); // 每次发送间隔30ms，可根据实际情况调整
                } catch (Exception e) {
                    Log.e(TAG, "异步发送方向命令失败: " + e.getMessage());
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "发送命令异常: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                }
            }
        }
    }).start();
}

    /**
     * 发送方向命令
     * @param direction 方向参数
     */
    private void sendDirectionCommand(String direction) {
        try {
            // 检查蓝牙连接状态
            if (!MainActivity.CONNECT_STATUS) {
                Toast.makeText(this, "蓝牙未连接，请先连接设备", Toast.LENGTH_SHORT).show();
                return;
            }

            String command = "RC0" + direction + "YT";
            MainActivity.write(command);
            Log.d(TAG, "发送方向命令: " + command);
            toastShorter(direction, delay_ms);

//            Toast.makeText(this, command, Toast.LENGTH_SHORT).show();

            // 发送后再次检查连接状态，如果断开则提示用户
            if (!MainActivity.CONNECT_STATUS) {
                Toast.makeText(this, "命令发送失败，连接已断开", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "发送方向命令失败: " + e.getMessage());
            Toast.makeText(this, "发送命令异常: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    @Override
    protected void onDestroy() {
        // 停止摇杆线程
        keepJoystickRunning = false;
        synchronized (joystickLock) {
            joystickLock.notifyAll();
        }
        if (joystickSenderThread != null) {
            try {
                joystickSenderThread.join(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        // 停止接收线程
        if (receiveThread != null) {
            receiveThread.running = false;
            receiveThread.interrupt();
            try {
                receiveThread.join(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            receiveThread = null;
        }
        
        super.onDestroy();
        // 断开蓝牙连接
        disconnectDevice();
        // 关闭对话框
        if (mBluetoothMenuDialog != null && mBluetoothMenuDialog.isShowing()) {
            mBluetoothMenuDialog.dismiss();
        }
        // 隐藏设备列表控件
        hideDeviceList();
        // 注销广播接收者
        if (mBLuetoothStateReceiver != null) {
            unregisterReceiver(mBLuetoothStateReceiver);
        }
        if (mBluetoothReceiver != null) {
            unregisterReceiver(mBluetoothReceiver);
        }
    }

    // 速率限制器实现
    public class RateLimiter {
        private final long interval;
        private long lastTime;

        public RateLimiter(long intervalMs) {
            this.interval = intervalMs;
        }

        public synchronized boolean tryAcquire() {
            long now = System.currentTimeMillis();
            if (now - lastTime >= interval) {
                lastTime = now;
                return true;
            }
            return false;
        }
    }

    /**
     * 负责接收数据的线程 (从CommunicationActivity合并)
     */
    public class ReceiveDataThread extends Thread{

        private InputStream inputStream;//输入流
        private volatile boolean running = true;

        public ReceiveDataThread() {
            super();
            try {
                //获取连接socket的输入流
                inputStream = mBluetoothSocket.getInputStream();//获取输入流
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        @Override
        public void run() {
            super.run();
            byte[] buffer = new byte[1024];
            while (running && !isInterrupted() && mBluetoothSocket != null && mBluetoothSocket.isConnected()){
                try {
                    //每次运行需新生成一个
                    //将buffer清空
                    Arrays.fill(buffer, (byte) 0);
                    int bytesRead = inputStream.read(buffer);
                    if (bytesRead == -1) {
                        // 流结束，断开连接
                        break;
                    }
                    String a = new String(buffer,0,bytesRead);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //将收到的数据显示在TextView上
                            if (mReceiveContent != null) {
                                mReceiveContent.append(a.trim() + "\n");
                                //System.out.println(a);
                                /**
                                 * 注:inputStream控制固定字节读入时并不稳定，读入数据容易出现漏读情况，因此采用其它方法来更新数据
                                 * 在每次UI线程中，获取收到数据的最后一行，即最新数据，截取出来进行显示
                                 * 由于append会自动换行，因此line_num-2
                                 */
                                // 自动滚动到底部，显示最新数据
                                final android.widget.ScrollView scrollView = (android.widget.ScrollView) mReceiveContent.getParent();
                                if (scrollView != null) {
                                    scrollView.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            scrollView.fullScroll(android.widget.ScrollView.FOCUS_DOWN);
                                        }
                                    });
                                }
                            }
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
    }
}