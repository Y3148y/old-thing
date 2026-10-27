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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
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
    private static final int REQUEST_OVERLAY_PERMISSION = 1001;
    
    // 蓝牙快连接相关常量
    private static final String BLUETOOTH_PREFS = "BluetoothPrefs";
    private static final String LAST_CONNECTED_DEVICE_ADDRESS = "last_connected_device_address";
    private static final String LAST_CONNECTED_DEVICE_NAME = "last_connected_device_name";
    private static final String AUTO_CONNECT_ENABLED = "auto_connect_enabled";
    
    private SharedPreferences mBluetoothPrefs;
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

    int delay_ms = 5;

    public static boolean flag = true;
    public static boolean CONNECT_STATUS = false;

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

    Button btnReset;



    private void initif() {
        try {
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
            btnReset = findViewById(R.id.resetLocal);
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
            
            Log.d(TAG, "initif初始化成功");
        } catch (Exception e) {
            Log.e(TAG, "initif初始化异常: " + e.getMessage());
            CrashHandler.logException("initif初始化异常", e);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // 初始化崩溃处理器
        CrashHandler.getInstance().init(this);
        
        // 检查悬浮窗权限 (Android 6.0+)
        checkOverlayPermission();
        
        // 初始化蓝牙快连接SharedPreferences
        mBluetoothPrefs = getSharedPreferences(BLUETOOTH_PREFS, MODE_PRIVATE);
        
        initReceiver();
        initif();
        initView();
        
        // 延迟执行自动连接，确保所有初始化完成
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                attemptAutoConnect();
            }
        }, 2000); // 延迟2秒执行自动连接
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


                    for (int i = 0; i < 8; i++) {
                        SendCommand("RC0015SD", delay_ms);
                    }

                    toastShorter("RC0015SD~", 700);


                } else if (slider.getValue() == 1.0) {

                    for (int i = 0; i < 8; i++) {
                        SendCommand("RC0010SD", delay_ms);
                    }
                    Log.e("slider", "onStopTrackingTouch: VE0001R1");
                    toastShorter("RC0010SD~", 700);

                }else if (slider.getValue() == 0.0) {

                    for (int i = 0; i < 8; i++) {
                        SendCommand("RC0005SD", delay_ms);
                    }
                    Log.e("slider", "onStopTrackingTouch: VE0001R1");
                    toastShorter("RC0005SD~", 700);

                } else if (slider.getValue() == 3.0) {
                    for (int i = 0; i < 8; i++) {
                        SendCommand("RC0020SD", delay_ms);
                    }
                    Log.e("slider", "onStopTrackingTouch: VE0001R1");
                    toastShorter("RC0020SD~", 700);

                } else if (slider.getValue() == 4.0) {
                    for (int i = 0; i < 8; i++) {
                        SendCommand("RC0025SD", delay_ms);
                    }
                    Log.e("slider", "onStopTrackingTouch: VE0001R1");
                    toastShorter("RC0025SD~", 700);

                }
            }
        });
        myRockerView3.setOnAngleChangeListener(new MyRockerView.OnAngleChangeListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void angle(double angle) {
                try {
                    int angle_int =  (int)angle;
                    if (angle_int >= 0 && angle_int <= 9){
                        //-------------------------------线程阻塞-------------------------------------------------------------------------
                        for (int i = 0; i < 4; i++) {
                        SendCommand("RC000"+angle_int+"YG", delay_ms);
                        }

//                    tv_angle_2.setText("00"+angle_int);
                    }
                    else if (angle_int >= 10 && angle_int <= 99){
                        for (int i = 0; i < 4; i++) {
                        SendCommand("RC00"+angle_int+"YG", delay_ms);
                        }

//                    tv_angle_2.setText("0"+angle_int);
                    }
                    else {
                        for (int i = 0; i < 4; i++) {
                            SendCommand("RC0"+angle_int+"YG", delay_ms);}

//                    tv_angle_2.setText(""+angle_int);
                    }
                } catch (Exception e) {
                    CrashHandler.logException("摇杆角度控制", e);
                    Log.e(TAG, "摇杆角度控制异常: " + e.getMessage(), e);
                    mToast.showToast("摇杆控制异常: " + e.getMessage());
                }
            }

            @Override
            public void onFinish() {
                try {
                    for (int i = 0; i < 4; i++) {
                        SendCommand("RC0999YG", delay_ms);
                    }
                } catch (Exception e) {
                    CrashHandler.logException("摇杆结束控制", e);
                    Log.e(TAG, "摇杆结束控制异常: " + e.getMessage(), e);
                    mToast.showToast("摇杆结束控制异常: " + e.getMessage());
                }
            }
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
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override// RCXXREST
            public void onClick(View v) {
                toastShorter("RC05REST~", 300);
                SendCommand("RC05REST", delay_ms);
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










    /*
        注册广播接收者
         */
    private void initReceiver() {
        try {
            //创建用于接收蓝牙状态改变广播的广播接收者
            mBLuetoothStateReceiver = new BroadcastReceiver(){
                @Override
                public void onReceive(Context context, Intent intent) {
                    try {
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
                    } catch (Exception e) {
                        Log.e(TAG, "蓝牙状态广播接收异常: " + e.getMessage());
                        CrashHandler.logException("蓝牙状态广播接收异常", e);
                    }
                }
            };
            //创建设备扫描广播接收者
            mBluetoothReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    try {
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
                    } catch (Exception e) {
                        Log.e(TAG, "设备扫描广播接收异常: " + e.getMessage());
                        CrashHandler.logException("设备扫描广播接收异常", e);
                    }
                }
            };
            //注册广播接收者
            IntentFilter filter1 = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            IntentFilter filter2 = new IntentFilter(BluetoothDevice.ACTION_FOUND);

            registerReceiver(mBLuetoothStateReceiver,filter1);
            registerReceiver(mBluetoothReceiver,filter2);
            Log.d(TAG, "initReceiver初始化成功");
        } catch (Exception e) {
            Log.e(TAG, "initReceiver初始化异常: " + e.getMessage());
            CrashHandler.logException("initReceiver初始化异常", e);
        }
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
        try {
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
            btnReset = findViewById(R.id.resetLocal);
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
                    try {
                        Intent intent = new Intent(MainActivity.this, AdjustActivity.class);
                        startActivity(intent);
                    } catch (Exception e) {
                        Log.e(TAG, "跳转到AdjustActivity异常: " + e.getMessage());
                        CrashHandler.logException("跳转到AdjustActivity异常", e);
                    }
                }
            });
            
            Log.d(TAG, "initView初始化成功");
        } catch (Exception e) {
            Log.e(TAG, "initView初始化异常: " + e.getMessage());
            CrashHandler.logException("initView初始化异常", e);
        }
    }
    //初始化监听
    private void initListener() {
        try {
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
                        try {
                            showBluetoothMenu();
                        } catch (Exception e) {
                            Log.e(TAG, "蓝牙菜单显示异常: " + e.getMessage());
                            CrashHandler.logException("蓝牙菜单显示异常", e);
                        }
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
                        try {
                            hideDeviceList();
                        } catch (Exception e) {
                            Log.e(TAG, "隐藏设备列表异常: " + e.getMessage());
                            CrashHandler.logException("隐藏设备列表异常", e);
                        }
                    }
                });
            }
            //设备列表item的点击事件
            if (mDeviceList != null) {
                mDeviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                        try {
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
                        } catch (Exception e) {
                            Log.e(TAG, "设备连接异常: " + e.getMessage());
                            CrashHandler.logException("设备连接异常", e);
                        }
                    }
                });
            }
            
            Log.d(TAG, "initListener初始化成功");
        } catch (Exception e) {
            Log.e(TAG, "initListener初始化异常: " + e.getMessage());
            CrashHandler.logException("initListener初始化异常", e);
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
        try {
            if (!checkBluetoothPermissions()) {
                String errorMsg = "蓝牙权限未授予";
                Log.e(TAG, "openBluetooth: " + errorMsg);
                CrashHandler.logException("openBluetooth权限检查", new Exception(errorMsg));
                mToast.showToast("请先授予蓝牙权限");
                return;
            }
            
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter != null) {
                //判断蓝牙是否打开并可见
                if (!mBluetoothAdapter.isEnabled()) {
                    try {
                        //请求打开并可见
                        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(intent,1);
                        Log.d(TAG, "openBluetooth请求打开蓝牙");
                        mToast.showToast("蓝牙已打开");
                    } catch (Exception e) {
                        CrashHandler.logException("openBluetooth启动蓝牙异常", e);
                        Log.e(TAG, "openBluetooth启动蓝牙异常: " + e.getMessage(), e);
                        mToast.showToast("打开蓝牙时发生异常: " + e.getMessage());
                    }
                }
                else{
                    Log.d(TAG, "openBluetooth蓝牙已经打开");
                    mToast.showToast("蓝牙已经打开");
                }
            }else{
                String errorMsg = "设备不支持蓝牙功能";
                Log.e(TAG, "openBluetooth: " + errorMsg);
                CrashHandler.logException("openBluetooth适配器检查", new Exception(errorMsg));
                mToast.showToast("设备不支持蓝牙功能");
            }
        } catch (Exception e) {
            CrashHandler.logException("openBluetooth未知异常", e);
            Log.e(TAG, "openBluetooth未知异常: " + e.getMessage(), e);
            mToast.showToast("打开蓝牙时发生异常: " + e.getMessage());
        }
    }

    /**
     * 关闭蓝牙
     */
    private void closeBluetooth() {
        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter != null) {
                //判断蓝牙是否打开并可见
                if (mBluetoothAdapter.isEnabled()) {
                    try {
                        //请求关闭
                        mBluetoothAdapter.disable();
                        Log.d(TAG, "closeBluetooth请求关闭蓝牙");
                        mToast.showToast("蓝牙已关闭");
                    } catch (Exception e) {
                        CrashHandler.logException("closeBluetooth关闭蓝牙异常", e);
                        Log.e(TAG, "closeBluetooth关闭蓝牙异常: " + e.getMessage(), e);
                        mToast.showToast("关闭蓝牙时发生异常: " + e.getMessage());
                    }
                }
                else{
                    Log.d(TAG, "closeBluetooth蓝牙已经关闭");
                    mToast.showToast("蓝牙已经关闭");
                }
            }else{
                String errorMsg = "设备不支持蓝牙功能";
                Log.e(TAG, "closeBluetooth: " + errorMsg);
                CrashHandler.logException("closeBluetooth适配器检查", new Exception(errorMsg));
                mToast.showToast("设备不支持蓝牙功能");
            }
        } catch (Exception e) {
            CrashHandler.logException("closeBluetooth未知异常", e);
            Log.e(TAG, "closeBluetooth未知异常: " + e.getMessage(), e);
            mToast.showToast("关闭蓝牙时发生异常: " + e.getMessage());
        }
    }

    /**
     * 搜索蓝牙设备
     */
    private void discoverBluetooth(){
        try {
            if (!checkBluetoothPermissions()) {
                String errorMsg = "蓝牙权限未授予";
                Log.e(TAG, "discoverBluetooth: " + errorMsg);
                CrashHandler.logException("discoverBluetooth权限检查", new Exception(errorMsg));
                mToast.showToast("请先授予蓝牙权限");
                return;
            }
            
            if (mBluetoothAdapter == null) {
                String errorMsg = "蓝牙适配器为空";
                Log.e(TAG, "discoverBluetooth: " + errorMsg);
                CrashHandler.logException("discoverBluetooth适配器检查", new Exception(errorMsg));
                mToast.showToast("设备不支持蓝牙功能");
                return;
            }
            
            Log.d(TAG, "discoverBluetooth开始搜索蓝牙设备");
            
            if (mBluetoothAdapter.isDiscovering()) {
                try {
                    mBluetoothAdapter.cancelDiscovery();
                    Log.d(TAG, "discoverBluetooth取消之前的搜索");
                } catch (Exception e) {
                    CrashHandler.logException("discoverBluetooth取消搜索异常", e);
                    Log.e(TAG, "discoverBluetooth取消搜索异常: " + e.getMessage(), e);
                }
            }
            
            //清空之前的搜索结果
            mDatas.clear();
            
            //获取已配对的设备
            try {
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
                    Log.d(TAG, "discoverBluetooth已添加" + bondedDevices.size() + "个已配对设备");
                    mToast.showToast("已显示" + bondedDevices.size() + "个已配对设备");
                } else {
                    Log.d(TAG, "discoverBluetooth没有已配对的设备");
                    mToast.showToast("没有已配对的设备");
                }
            } catch (Exception e) {
                CrashHandler.logException("discoverBluetooth获取已配对设备异常", e);
                Log.e(TAG, "discoverBluetooth获取已配对设备异常: " + e.getMessage(), e);
            }
            
            try {
                mAdapter.notifyDataSetChanged();
                Log.d(TAG, "discoverBluetooth适配器数据已更新");
            } catch (Exception e) {
                CrashHandler.logException("discoverBluetooth更新适配器异常", e);
                Log.e(TAG, "discoverBluetooth更新适配器异常: " + e.getMessage(), e);
            }
        } catch (Exception e) {
            CrashHandler.logException("discoverBluetooth未知异常", e);
            Log.e(TAG, "discoverBluetooth未知异常: " + e.getMessage(), e);
            mToast.showToast("搜索设备时发生异常: " + e.getMessage());
        }
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
        try {
            if (!checkBluetoothPermissions()) {
                String errorMsg = "蓝牙权限未授予";
                Log.e(TAG, "connectDevice: " + errorMsg);
                CrashHandler.logException("connectDevice权限检查", new Exception(errorMsg));
                mToast.showToast("请先授予蓝牙权限");
                return;
            }
            
            if (mBluetoothAdapter == null || mAddress == null) {
                String errorMsg = "蓝牙适配器为空或设备地址为空";
                Log.e(TAG, "connectDevice: " + errorMsg);
                CrashHandler.logException("connectDevice适配器检查", new Exception(errorMsg));
                mToast.showToast("蓝牙适配器或设备地址无效");
                return;
            }
            
            Log.d(TAG, "connectDevice开始连接设备: " + mAddress);
            //通过地址拿到该蓝牙设备device
            mDevice = mBluetoothAdapter.getRemoteDevice(mAddress);
            try {
                //建立socket通信
                mBluetoothSocket = mDevice.createRfcommSocketToServiceRecord(mUUID);
                mBluetoothSocket.connect();
                if (mBluetoothSocket.isConnected()) {
                    Log.d(TAG, "connectDevice连接成功");
                    mToast.showToast("连接成功");
                    CONNECT_STATUS = true;
                    
                    // 保存成功连接的设备信息
                    saveLastConnectedDevice(mAddress, mDevice.getName());
                    
                    //获取输出流用于发送数据
                    outputStream = mBluetoothSocket.getOutputStream();
                    //开启接收数据的线程
                    receiveThread = new ReceiveDataThread();
                    receiveThread.start();
                }else{
                    String errorMsg = "Socket连接失败";
                    Log.e(TAG, "connectDevice: " + errorMsg);
                    CrashHandler.logException("connectDevice连接状态检查", new Exception(errorMsg));
                    mToast.showToast("连接失败，请重试");
                    CONNECT_STATUS = false;
                }
            } catch (IOException e) {
                CrashHandler.logException("connectDevice IO异常", e);
                Log.e(TAG, "connectDevice IO异常: " + e.getMessage(), e);
                mToast.showToast("连接出错: " + e.getMessage());
                CONNECT_STATUS = false;
                try {
                    if (mBluetoothSocket != null) {
                        mBluetoothSocket.close();
                    }
                } catch (IOException ioException) {
                    CrashHandler.logException("connectDevice关闭Socket异常", ioException);
                    Log.e(TAG, "connectDevice关闭Socket异常: " + ioException.getMessage(), ioException);
                }
            }
        } catch (Exception e) {
            CrashHandler.logException("connectDevice未知异常", e);
            Log.e(TAG, "connectDevice未知异常: " + e.getMessage(), e);
            mToast.showToast("连接时发生异常: " + e.getMessage());
            CONNECT_STATUS = false;
        }
    }
    
    /**
     * 断开设备连接
     */
    private void disconnectDevice() {
        try {
            Log.d(TAG, "disconnectDevice开始断开连接");
            
            if (receiveThread != null) {
                try {
                    receiveThread.interrupt();
                    receiveThread = null;
                    Log.d(TAG, "disconnectDevice接收线程已中断");
                } catch (Exception e) {
                    CrashHandler.logException("disconnectDevice中断接收线程异常", e);
                    Log.e(TAG, "disconnectDevice中断接收线程异常: " + e.getMessage(), e);
                }
            }
            
            if (outputStream != null) {
                try {
                    outputStream.close();
                    outputStream = null;
                    Log.d(TAG, "disconnectDevice输出流已关闭");
                } catch (IOException e) {
                    CrashHandler.logException("disconnectDevice关闭输出流异常", e);
                    Log.e(TAG, "disconnectDevice关闭输出流异常: " + e.getMessage(), e);
                }
            }
            
            if (mBluetoothSocket != null) {
                try {
                    if (mBluetoothSocket.isConnected()) {
                        //关闭socket
                        mBluetoothSocket.close();
                        Log.d(TAG, "disconnectDevice Socket已关闭");
                        mToast.showToast("连接已断开");
                    } else {
                        Log.d(TAG, "disconnectDevice Socket已经断开");
                    }
                } catch (IOException e) {
                    CrashHandler.logException("disconnectDevice关闭Socket异常", e);
                    Log.e(TAG, "disconnectDevice关闭Socket异常: " + e.getMessage(), e);
                }
            }
            
            CONNECT_STATUS = false;
            Log.d(TAG, "disconnectDevice断开连接完成");
        } catch (Exception e) {
            CrashHandler.logException("disconnectDevice未知异常", e);
            Log.e(TAG, "disconnectDevice未知异常: " + e.getMessage(), e);
            CONNECT_STATUS = false;
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
        try {
            this.runOnUiThread(new Runnable() {
                public void run() {
                    try {
                        final Toast toast = Toast.makeText(MainActivity.this, word, Toast.LENGTH_LONG);
                        toast.show();
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                try {
                                    toast.cancel();
                                } catch (Exception e) {
                                    Log.e(TAG, "Error canceling toast: " + e.getMessage());
                                    CrashHandler.logException("取消Toast异常", e);
                                }
                            }
                        }, delay_ms);
                    } catch (Exception e) {
                        Log.e(TAG, "Error showing toast: " + e.getMessage());
                        CrashHandler.logException("显示Toast异常", e);
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error in toastShorter: " + e.getMessage());
            CrashHandler.logException("toastShorter方法异常", e);
        }
    }
    
    /**
     * 静态写入方法
     * @param str 要写入的字符串
     */
    public static void write(String str) throws IOException {
        try {
            if (outputStream != null) {
                outputStream.write(str.getBytes());
                outputStream.flush();
                Log.d("MainActivity", "write方法成功发送: " + str);
            } else {
                String errorMsg = "输出流未初始化";
                Log.e("MainActivity", "write方法错误: " + errorMsg);
                CrashHandler.logException("write方法输出流检查", new Exception(errorMsg));
                throw new IOException(errorMsg);
            }
        } catch (IOException e) {
            CrashHandler.logException("write方法IO异常", e);
            Log.e("MainActivity", "write方法IO异常: " + e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            CrashHandler.logException("write方法未知异常", e);
            Log.e("MainActivity", "write方法未知异常: " + e.getMessage(), e);
            throw new IOException("write方法发生未知异常: " + e.getMessage(), e);
        }
    }
    
    /**
     * 发送命令数据
     * @param command 要发送的命令字符串
     * @param delay_ms 发送后的延迟时间（毫秒）
     */
    public void SendCommand(String command, int delay_ms) {
        try {
            // 检查蓝牙连接状态
            if (mBluetoothSocket == null || !mBluetoothSocket.isConnected()) {
                String errorMsg = "蓝牙未连接或Socket为空";
                Log.e(TAG, "SendCommand: " + errorMsg);
                CrashHandler.logException("SendCommand蓝牙连接检查", new Exception(errorMsg));
                mToast.showToast("请先连接设备");
                return;
            }
            
            // 检查输出流是否可用
            if (outputStream == null) {
                String errorMsg = "输出流未初始化";
                Log.e(TAG, "SendCommand: " + errorMsg);
                CrashHandler.logException("SendCommand输出流检查", new Exception(errorMsg));
                mToast.showToast("发送组件未初始化");
                return;
            }
            
            // 检查命令是否为空
            if (command == null || command.trim().isEmpty()) {
                String errorMsg = "命令为空或仅包含空白字符";
                Log.w(TAG, "SendCommand: " + errorMsg);
                CrashHandler.logException("SendCommand命令检查", new Exception(errorMsg));
                mToast.showToast("命令不能为空");
                return;
            }
            
            // 显示发送的命令Toast
            toastShorter(command, 100);
            
            try {
                // 发送命令数据
                write(command);
                Log.d(TAG, "SendCommand成功发送: " + command);
                
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
                        CrashHandler.logException("SendCommand延迟中断", e);
                        Log.e(TAG, "SendCommand延迟中断: " + e.getMessage(), e);
                        Thread.currentThread().interrupt();
                    }
                }
                
//            mToast.showToast("命令发送成功");
            } catch (IOException e) {
                CrashHandler.logException("SendCommand写入异常", e);
                Log.e(TAG, "SendCommand写入异常: " + e.getMessage(), e);
                mToast.showToast("命令发送失败: " + e.getMessage());
            }
        } catch (Exception e) {
            CrashHandler.logException("SendCommand未知异常", e);
            Log.e(TAG, "SendCommand未知异常: " + e.getMessage(), e);
            mToast.showToast("发送命令时发生异常: " + e.getMessage());
        }
    }
    
    /**
     * 发送方向命令
     * @param direction 方向参数
     */
    private void sendDirectionCommand(String direction) {
        try {
            // 检查蓝牙连接状态
            if (!MainActivity.CONNECT_STATUS) {
                String errorMsg = "蓝牙未连接";
                Log.e(TAG, "sendDirectionCommand: " + errorMsg);
                CrashHandler.logException("sendDirectionCommand连接检查", new Exception(errorMsg));
                Toast.makeText(this, "蓝牙未连接，请先连接设备", Toast.LENGTH_SHORT).show();
                return;
            }

            String command = "RC0" + direction + "YT";
            try {
                MainActivity.write(command);
                Log.d(TAG, "sendDirectionCommand成功发送: " + command);
                toastShorter(direction, delay_ms);
            } catch (IOException e) {
                CrashHandler.logException("sendDirectionCommand写入异常", e);
                Log.e(TAG, "sendDirectionCommand写入异常: " + e.getMessage(), e);
                Toast.makeText(this, "发送方向命令失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }

//            Toast.makeText(this, command, Toast.LENGTH_SHORT).show();

            // 发送后再次检查连接状态，如果断开则提示用户
            if (!MainActivity.CONNECT_STATUS) {
                String errorMsg = "命令发送后连接已断开";
                Log.e(TAG, "sendDirectionCommand: " + errorMsg);
                CrashHandler.logException("sendDirectionCommand发送后连接检查", new Exception(errorMsg));
                Toast.makeText(this, "命令发送失败，连接已断开", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            CrashHandler.logException("sendDirectionCommand未知异常", e);
            Log.e(TAG, "sendDirectionCommand未知异常: " + e.getMessage(), e);
            Toast.makeText(this, "发送方向命令时发生异常: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    @Override
    protected void onDestroy() {
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
    
    /**
     * 负责接收数据的线程 (从CommunicationActivity合并)
     */
    public class ReceiveDataThread extends Thread{

        private InputStream inputStream;//输入流

        public ReceiveDataThread() {
            super();
            try {
                //获取连接socket的输入流
                inputStream = mBluetoothSocket.getInputStream();//获取输入流
                Log.d(TAG, "ReceiveDataThread初始化成功");
            } catch (IOException e) {
                CrashHandler.logException("ReceiveDataThread初始化异常", e);
                Log.e(TAG, "ReceiveDataThread初始化异常: " + e.getMessage(), e);
            } catch (Exception e) {
                CrashHandler.logException("ReceiveDataThread初始化未知异常", e);
                Log.e(TAG, "ReceiveDataThread初始化未知异常: " + e.getMessage(), e);
            }
        }
        
        @Override
        public void run() {
            super.run();
            byte[] buffer = new byte[1024];
            Log.d(TAG, "ReceiveDataThread开始运行");
            
            while (!isInterrupted() && mBluetoothSocket != null && mBluetoothSocket.isConnected()){
                try {
                    //每次运行需新生成一个
                    //将buffer清空
                    Arrays.fill(buffer, (byte) 0);
                    
                    if (inputStream == null) {
                        String errorMsg = "输入流为空，退出接收线程";
                        Log.e(TAG, "ReceiveDataThread: " + errorMsg);
                        CrashHandler.logException("ReceiveDataThread输入流检查", new Exception(errorMsg));
                        break;
                    }
                    
                    inputStream.read(buffer);
                    String a = new String(buffer,0,buffer.length);
                    
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                //将收到的数据显示在TextView上，只保存最近四次命令
                                if (mReceiveContent != null) {
                                    final int MAX_COMMANDS = 4; // 硬编码：只保存最近4次命令
                                    
                                    // 获取当前文本内容
                                    String currentText = mReceiveContent.getText().toString();
                                    
                                    // 按行分割当前内容
                                    String[] lines = currentText.split("\n");
                                    
                                    // 创建新的命令列表
                                    java.util.List<String> commandList = new java.util.ArrayList<>();
                                    
                                    // 如果当前有内容，先添加现有的命令（排除"等待接收数据..."）
                                    for (String line : lines) {
                                        if (!line.trim().isEmpty() && !line.equals("等待接收数据...")) {
                                            commandList.add(line.trim());
                                        }
                                    }
                                    
                                    // 添加新接收的命令
                                    String newCommand = a.trim();
                                    if (!newCommand.isEmpty()) {
                                        commandList.add(newCommand);
                                    }
                                    
                                    // 如果命令数量超过最大值，移除最早的命令
                                    while (commandList.size() > MAX_COMMANDS) {
                                        commandList.remove(0); // 移除第一个（最早的）命令
                                    }
                                    
                                    // 重新构建显示文本
                                    StringBuilder displayText = new StringBuilder();
                                    if (commandList.isEmpty()) {
                                        displayText.append("等待接收数据...");
                                    } else {
                                        for (int i = 0; i < commandList.size(); i++) {
                                            displayText.append(commandList.get(i));
                                            if (i < commandList.size() - 1) {
                                                displayText.append("\n");
                                            }
                                        }
                                    }
                                    
                                    // 更新TextView内容
                                    mReceiveContent.setText(displayText.toString());
                                    
                                    /**
                                     * 注释：修改后的逻辑说明
                                     * 1. 只保存最近4次接收到的命令数据
                                     * 2. 自动移除超出数量限制的旧命令
                                     * 3. 保持界面简洁，避免数据过多导致的显示问题
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
                                } else {
                                    Log.w(TAG, "ReceiveDataThread: mReceiveContent为空");
                                }
                            } catch (Exception e) {
                                CrashHandler.logException("ReceiveDataThread UI更新异常", e);
                                Log.e(TAG, "ReceiveDataThread UI更新异常: " + e.getMessage(), e);
                            }
                        }
                    });
                } catch (IOException e) {
                    CrashHandler.logException("ReceiveDataThread IO异常", e);
                    Log.e(TAG, "ReceiveDataThread IO异常: " + e.getMessage(), e);
                    break;
                } catch (Exception e) {
                    CrashHandler.logException("ReceiveDataThread未知异常", e);
                    Log.e(TAG, "ReceiveDataThread未知异常: " + e.getMessage(), e);
                    break;
                }
            }
            Log.d(TAG, "ReceiveDataThread结束运行");
        }
    }
    
    /**
     * 检查悬浮窗权限
     */
    private void checkOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                // 请求悬浮窗权限
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, REQUEST_OVERLAY_PERMISSION);
            }
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_OVERLAY_PERMISSION) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this)) {
                    Toast.makeText(this, "悬浮窗权限已获取，崩溃对话框将正常显示", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "悬浮窗权限被拒绝，崩溃对话框可能无法显示", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
    
    /**
     * 尝试自动连接上次连接的蓝牙设备
     */
    private void attemptAutoConnect() {
        try {
            // 检查是否启用自动连接
            boolean autoConnectEnabled = mBluetoothPrefs.getBoolean(AUTO_CONNECT_ENABLED, true);
            if (!autoConnectEnabled) {
                Log.d(TAG, "自动连接已禁用");
                return;
            }
            
            // 检查蓝牙是否开启
            if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
                Log.d(TAG, "蓝牙未开启，无法自动连接");
                return;
            }
            
            // 检查是否已经连接
            if (CONNECT_STATUS) {
                Log.d(TAG, "已有蓝牙连接，跳过自动连接");
                return;
            }
            
            // 获取上次连接的设备地址
            String lastDeviceAddress = getLastConnectedDeviceAddress();
            if (lastDeviceAddress == null || lastDeviceAddress.isEmpty()) {
                Log.d(TAG, "没有保存的设备地址，无法自动连接");
                return;
            }
            
            String lastDeviceName = getLastConnectedDeviceName();
            Log.d(TAG, "尝试自动连接设备: " + lastDeviceName + " (" + lastDeviceAddress + ")");
            
            // 设置设备地址并尝试连接
            mAddress = lastDeviceAddress;
            
            // 获取设备对象
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                Log.w(TAG, "缺少蓝牙连接权限，无法自动连接");
                return;
            }
            
            mDevice = mBluetoothAdapter.getRemoteDevice(lastDeviceAddress);
            
            // 显示自动连接提示
            mToast.showToast("正在自动连接: " + (lastDeviceName != null ? lastDeviceName : "未知设备"));
            
            // 执行连接
            connectDevice();
            
        } catch (Exception e) {
            Log.e(TAG, "自动连接失败: " + e.getMessage());
            CrashHandler.logException("自动连接", e);
        }
    }
    
    /**
     * 保存成功连接的设备信息
     */
    private void saveLastConnectedDevice(String deviceAddress, String deviceName) {
        try {
            SharedPreferences.Editor editor = mBluetoothPrefs.edit();
            editor.putString(LAST_CONNECTED_DEVICE_ADDRESS, deviceAddress);
            editor.putString(LAST_CONNECTED_DEVICE_NAME, deviceName != null ? deviceName : "未知设备");
            editor.putBoolean(AUTO_CONNECT_ENABLED, true);
            editor.apply();
            
            Log.d(TAG, "已保存设备信息: " + deviceName + " (" + deviceAddress + ")");
        } catch (Exception e) {
            Log.e(TAG, "保存设备信息失败: " + e.getMessage());
            CrashHandler.logException("保存设备信息", e);
        }
    }
    
    /**
     * 获取上次连接的设备地址
     */
    private String getLastConnectedDeviceAddress() {
        return mBluetoothPrefs.getString(LAST_CONNECTED_DEVICE_ADDRESS, null);
    }
    
    /**
     * 获取上次连接的设备名称
     */
    private String getLastConnectedDeviceName() {
        return mBluetoothPrefs.getString(LAST_CONNECTED_DEVICE_NAME, "未知设备");
    }
    
    /**
     * 清除保存的设备信息
     */
    private void clearLastConnectedDevice() {
        try {
            SharedPreferences.Editor editor = mBluetoothPrefs.edit();
            editor.remove(LAST_CONNECTED_DEVICE_ADDRESS);
            editor.remove(LAST_CONNECTED_DEVICE_NAME);
            editor.apply();
            
            Log.d(TAG, "已清除保存的设备信息");
        } catch (Exception e) {
            Log.e(TAG, "清除设备信息失败: " + e.getMessage());
            CrashHandler.logException("清除设备信息", e);
        }
    }
    
    /**
     * 设置自动连接开关
     */
    private void setAutoConnectEnabled(boolean enabled) {
        try {
            SharedPreferences.Editor editor = mBluetoothPrefs.edit();
            editor.putBoolean(AUTO_CONNECT_ENABLED, enabled);
            editor.apply();
            
            Log.d(TAG, "自动连接设置: " + (enabled ? "启用" : "禁用"));
        } catch (Exception e) {
            Log.e(TAG, "设置自动连接失败: " + e.getMessage());
            CrashHandler.logException("设置自动连接", e);
        }
    }
    
    /**
     * 检查是否启用自动连接
     */
    private boolean isAutoConnectEnabled() {
        return mBluetoothPrefs.getBoolean(AUTO_CONNECT_ENABLED, true);
    }
}