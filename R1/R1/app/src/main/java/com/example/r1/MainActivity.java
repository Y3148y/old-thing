package com.example.r1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.slider.Slider;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;
import java.util.UUID;

public class MainActivity extends AppCompatActivity{
    //定时器
    private Handler handler;
    private Runnable taskRunnable;
    private static final long INTERVAL = 200;


    public static boolean flag = true;
    public static int STOP = 0;

    private static final String TAG = "MainActivity";
    public static String RADIO_DATA_KEY = "getRadioDataFromActivityChooser";
    String role;
    SharedPreferences sharedPreferencesFromChooser;//获取数据
    Button change;
//    Button go;
//    Button get;
//    Button pull_right_left;
//    Button revolve901;
//    Button opendoor;
//    Button stretch;
//    Button reposition;
//    Button put;
//    Button drop;
//    Button directly_put;
    View map1;
    View map2;


//    Button choice;
//    Button open;
//    Button close;
//    Button closedoor;
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
    Button begin;
    Button stop;
    Button reset;
    Button reset2;
    MyRockerView myRockerView3;
    Slider sl_progress1;
//    Slider sl_progress1 = findViewById(R.id.seekbar02);
    private void initView(){
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
        sharedPreferencesFromChooser = getSharedPreferences("setDataToMainActivity", MODE_PRIVATE);//创建一个SharedPreferences对象
        String role = sharedPreferencesFromChooser.getString("checkRole", "notExist");// 读取键值，如果没找到，返回第二个参数指定的值。
        Log.e("role", "role: "+role);
//        if (role.equals(ActivityChooser.CHECKED_Red)){
//            map2.setVisibility(View.GONE);
//            reposition.setVisibility(View.GONE);
//            drop.setVisibility(View.GONE);
//            blue.setVisibility(View.GONE);
//            arrive.setVisibility(View.GONE);
//            open.setVisibility(View.GONE);
//            close.setVisibility(View.GONE);
//            directly_put.setVisibility(View.GONE);
////            myRockerView1.setVisibility(View.INVISIBLE);
//        }else if (role.equals(ActivityChooser.CHECKED_Blue)){
//            map2.setVisibility(View.GONE);
//            reposition.setVisibility(View.GONE);
//            drop.setVisibility(View.GONE);
//            red.setVisibility(View.GONE);
//            arrive.setVisibility(View.GONE);
//            open.setVisibility(View.GONE);
//            close.setVisibility(View.GONE);
//            directly_put.setVisibility(View.GONE);
////            myRockerView1.setVisibility(View.INVISIBLE);
//        }

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "---------------onClick:change ----------------"+flag);
//                if (map1 == null || map2 == null) {
//                    Log.e(TAG, "--------------map1 or map2 is null"+map1+map2);
//                    return;
//                }

//                if (map1.getVisibility() != View.GONE && flag) {//8 //预选界面:运球点位
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
    }











    ////////////////////////////////////////////////////////////////////////////////////////////////////
    FragmentManager fm = getSupportFragmentManager();//获取FragmentManager对象
    frag1_2_bluetooth f2;//Fragment对象
    RelativeLayout linearLayout_BluetoothList;

    private TextView others1;
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    public static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public static BluetoothSocket mmSocket;
    public static OutputStream mmOutStream;
    public static InputStream mmInStream;
    private BluetoothAdapter mAdapter;
    public static Handler mHandler;
    public static final int REQUEST_CODE = 0;
    public static final int REQUEST_CODE_1 = 1;
    //    private BlueToothController mController = new BlueToothController();
    private BluetoothController mController = new BluetoothController();
    public static boolean CONNECT_STATUS = false;



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

    int delay_ms = 10;
    //发送命令
    public void SendCommand(String command, int delay_ms){
        //发完400次前显示命令
        toastShorter(command, 100);
        for (int i = 0;i < 1;i++){
            write(command);
            //每10ms发一次
            try {
                Thread.sleep(delay_ms);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //发完400次后显示命令
//        toastShorter(command, 350);
    }

    void f1(Button ...btn){
        for (int i = 0;i <  btn.length;i++){
//            btn[i].setBackgroundColor(getResources().getColor(R.color.purple_500));
//            btn[i].setTextColor(getResources().getColor(R.color.white));
        }
    }

    void f2(Button btn, TextView tv){
        String s_temp = new String();

        scrollView1_text = s_temp+"<font color = grey>"+scrollView1_text;
        tv.setText(Html.fromHtml(scrollView1_text));
        scrollView1.post(new Runnable() {
            @Override
            public void run() {
                scrollView1.fullScroll(HorizontalScrollView.FOCUS_LEFT);
            }
        });
    }

    void f2(String s, TextView tv){
        //将传入的字符串 s 添加到 scrollView1_text 变量的前面，并为新增的内容加上 <font color=grey> 标签（使其以灰色字体显示）
        scrollView1_text = s+"<font color = grey>"+scrollView1_text;
        //使用 Html.fromHtml() 方法解析带有 HTML 标签的字符串 scrollView1_text，并将其设置为指定 TextView 的文本内容。
        tv.setText(Html.fromHtml(scrollView1_text));
        scrollView1.post(new Runnable() {
            @Override
            public void run() {
                //会将水平滚动条移动到左边，使最新的消息始终显示在最左端。
                scrollView1.fullScroll(HorizontalScrollView.FOCUS_LEFT);
            }
        });
    }

    //滑动条
    HorizontalScrollView scrollView1;
    //滑动调信息-文本框
    TextView tv_info;
    String scrollView1_text = new String();//滚动信息

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        initPermission();

        // 初始化Handler，绑定主线程Looper
        handler = new Handler(Looper.getMainLooper());

        // 定义Runnable任务
        taskRunnable = new Runnable() {
            @Override
            public void run() {
                // 任务执行内容，例如日志输出、UI更新、数据轮询等
                write("RCOKOKR1");
                Log.d(TAG, "任务执行于：" + System.currentTimeMillis());
                // 这里也可加入业务逻辑，如网络请求、数据刷新
                // 重新调度任务，达到周期性执行的目的
                handler.postDelayed(this, INTERVAL);
            }
        };

        // 启动任务
        handler.post(taskRunnable);


        initif();
        initView();

//        String data = getIntent().getExtras().getString(ActivityChooser.RADIO_DATA_KEY);
//        if (data.equals(ActivityChooser.CHECKED_Red)){
//            role = "红方";
//        } else if (data.equals(ActivityChooser.CHECKED_Blue)) {
//            role = "蓝方";
//        }
//        //首次启动 Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT 为 0，再次点击图标启动时就不为零了
//        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
//            finish();
//            return;
//        }
//————————————————
//        版权声明：本文为CSDN博主「安歌_claire」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
//        原文链接：https://blog.csdn.net/lxy1740/article/details/112651751



        /*
         * 蓝牙碎片
         * */

        f2 = new frag1_2_bluetooth();
        linearLayout_BluetoothList = findViewById(R.id.linearLayout_BluetoothList);


        mAdapter = BluetoothAdapter.getDefaultAdapter();

        //隐藏标题栏，节省空间
        Objects.requireNonNull(getSupportActionBar()).hide();
        SeekBar seekBar1 = findViewById(R.id.seekBar1);
        TextView tv_seekBar = findViewById(R.id.tv_seekBar);
        scrollView1 = findViewById(R.id.scrollView1);
        tv_info = findViewById(R.id.tv_info);





//        Button btnshenchu = findViewById(R.id.btnShenchu);
//        Button btnquyang = findViewById(R.id.btnQuYang);
//        Button btntouzhi = findViewById(R.id.Touzhi);
//        Button up = findViewById(R.id.up90);
//        Button down = findViewById(R.id.down90);
//        Button left = findViewById(R.id.left90);
//        Button right = findViewById(R.id.right90);

//        MyRockerView myRockerView1 = findViewById(R.id.myRockerView1);
//        MyRockerView myRockerView2 = findViewById(R.id.myRockerView2);
//        MyRockerView myRockerView3 = findViewById(R.id.myRockerView3);

//        myRockerView2.setVisibility(View.INVISIBLE);
//
//        myRockerView3.setVisibility(View.INVISIBLE);



//        activity_claw.setRotation(-50);

        LinearLayout linearLayout = findViewById(R.id.linearLayout);
        RelativeLayout activity_tower = findViewById(R.id.activity_tower);
        RelativeLayout activity_speed_set = findViewById(R.id.activity_speed_set);

        TextView tv_angle_1 = findViewById(R.id.tv_angle_1);
        TextView tv_angle_2 = findViewById(R.id.tv_angle_2);

        //菜单按钮，返回红蓝选择界面
//        back = findViewById(R.id.back);
//        back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // 点击按钮后的操作
//                Intent intent = new Intent(MainActivity.this, ActivityChooser.class);
//                startActivity(intent);
//            }
//        });
//        opendoor.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Vibrator mVibrator= (Vibrator) getSystemService(VIBRATOR_SERVICE);
//
//                Toast.makeText(getApplicationContext(), "0023~", Toast.LENGTH_SHORT).show();
//                for (int i = 0; i < 8; i++) {
//                    SendCommand("SC0023R1", delay_ms);}
//
//                //MainActivity.write( "ReStart");
//                mVibrator.vibrate(200);//振动0.2秒
//            }
//        });
//        open.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Vibrator mVibrator= (Vibrator) getSystemService(VIBRATOR_SERVICE);
//
//                    Toast.makeText(getApplicationContext(), "OPEN~", Toast.LENGTH_SHORT).show();
//                    for (int i = 0; i < 8; i++) {
//                        SendCommand("SCOPENR1", delay_ms);}
//
//                //MainActivity.write( "ReStart");
//                mVibrator.vibrate(200);//振动0.2秒
//            }
//        });
//        close.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Vibrator mVibrator= (Vibrator) getSystemService(VIBRATOR_SERVICE);
//
//                Toast.makeText(getApplicationContext(), "SHUT~", Toast.LENGTH_SHORT).show();
//                for (int i = 0; i < 8; i++) {
//                    SendCommand("SCSHUTR1", delay_ms);}
//
//                //MainActivity.write( "ReStart");
//                mVibrator.vibrate(200);//振动0.2秒
//            }
//        });
//
//
//
//
//
////
//
//        directly_put.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Vibrator mVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
//
//
//                Toast.makeText(getApplicationContext(), "dput~", Toast.LENGTH_SHORT).show();
//                for (int i = 0; i < 8; i++) {
//                    SendCommand("SCREPUR1", delay_ms);
//                }
//
//
//                mVibrator.vibrate(200);//振动0.2秒
//            }
//        });
//
//        go.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Vibrator mVibrator= (Vibrator) getSystemService(VIBRATOR_SERVICE);
//
//                    Toast.makeText(getApplicationContext(), "6666~", Toast.LENGTH_SHORT).show();
//                    for (int i = 0; i < 8; i++) {
//                    SendCommand("SC6666R1", delay_ms);}
//
//                //MainActivity.write( "ReStart");
//                mVibrator.vibrate(200);//振动0.2秒
//            }
//        });
//
//        get.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Vibrator mVibrator= (Vibrator) getSystemService(VIBRATOR_SERVICE);
//
//                    Toast.makeText(getApplicationContext(), "TAKE~", Toast.LENGTH_SHORT).show();
//                for (int i = 0; i < 8; i++) {
//                    SendCommand("SCTAKER1", delay_ms);}
//                mVibrator.vibrate(200);//振动0.2秒
//            }
//        });




//      up.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Vibrator mVibrator= (Vibrator) getSystemService(VIBRATOR_SERVICE);
//                Toast.makeText(getApplicationContext(), "upup~", Toast.LENGTH_SHORT).show();
//
//                SendCommand("DWUPUPR1", delay_ms);
//
//
//                mVibrator.vibrate(200);//振动0.2秒
//            }
//        });
//        down.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Vibrator mVibrator= (Vibrator) getSystemService(VIBRATOR_SERVICE);
//                Toast.makeText(getApplicationContext(), "DOWN~", Toast.LENGTH_SHORT).show();
//
//                SendCommand("DWDOWNR1", delay_ms);
//
//
//                mVibrator.vibrate(200);//振动0.2秒
//            }
//        });
//        left.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Vibrator mVibrator= (Vibrator) getSystemService(VIBRATOR_SERVICE);
//                Toast.makeText(getApplicationContext(), "LEFT~", Toast.LENGTH_SHORT).show();
//
//                SendCommand("DWLEFTR1", delay_ms);
//                mVibrator.vibrate(200);//振动0.2秒
//            }
//        });
//        right.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Vibrator mVibrator= (Vibrator) getSystemService(VIBRATOR_SERVICE);
//                Toast.makeText(getApplicationContext(), "RIGH~", Toast.LENGTH_SHORT).show();
//
//                SendCommand("DWRIGHR1", delay_ms);
//
//
//                mVibrator.vibrate(200);//振动0.2秒
//            }
//        });
//        myRockerView1.setOnAngleChangeListener(new MyRockerView.OnAngleChangeListener() {
//            @Override
//            public void onStart() {
//
//            }
//
//            @Override
//            public void angle(double angle) {
//                int angle_int = 360- (int)angle;
//                if ((angle_int >45 && angle_int <= 135) ){
//                    for (int i = 0; i < 8; i++) {
//
//                    SendCommand("YG0270R1", delay_ms);}
//
//                }else if((angle_int > 135 && angle_int <= 225)){
//                    for (int i = 0; i < 8; i++) {
//
//                    SendCommand("YG0180R1", delay_ms);}
//
//                }else if((angle_int > 225 && angle_int <= 315)){
//                    for (int i = 0; i < 8; i++) {
//
//                    SendCommand("YG0090R1", delay_ms);}
//
//                }else if((angle_int > 315 && angle_int <=360||angle_int >= 0 && angle_int <=45)){
//                    for (int i = 0; i < 8; i++) {
//
//
//                    SendCommand("YG0000R1", delay_ms);}
//
//                }
//            }
//
//            @Override
//            public void onFinish() {
//                for (int i = 0; i < 9; i++) {
//
//
//                        SendCommand("YG0999R1", delay_ms);
//                }
//            }
//        });

//        Slider slider1;
//        slider1 = findViewById(R.id.seekbar02);
        //又一个测试速度滑动条



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

                    STOP=2;
                    Log.d(TAG, "onValueChange: --------------------STOP-----"+STOP);

                } else if (slider.getValue() == 1.0) {

                    for (int i = 0; i < 8; i++) {
                        SendCommand("RC0010SD", delay_ms);
                    }
                    Log.e("slider", "onStopTrackingTouch: VE0001R1");
                    toastShorter("RC0010SD~", 700);
                    STOP=1;
                    Log.d(TAG, "onValueChange: --------------------STOP-----"+STOP);

                }else if (slider.getValue() == 0.0) {

                    for (int i = 0; i < 8; i++) {
                        SendCommand("RC0005SD", delay_ms);
                    }
                    Log.e("slider", "onStopTrackingTouch: VE0001R1");
                    toastShorter("RC0005SD~", 700);
                    STOP=0;
                    Log.d(TAG, "onValueChange: --------------------STOP-----"+STOP+"---slider---"+slider.getValue());

                } else if (slider.getValue() == 3.0) {
                    for (int i = 0; i < 8; i++) {
                        SendCommand("RC0020SD", delay_ms);
                    }
                Log.e("slider", "onStopTrackingTouch: VE0001R1");
                toastShorter("RC0020SD~", 700);
                STOP=3;
                    Log.d(TAG, "onValueChange: --------------------STOP-----"+STOP);
                
                } else if (slider.getValue() == 4.0) {
                    for (int i = 0; i < 8; i++) {
                        SendCommand("RC0025SD", delay_ms);
                    }
                    Log.e("slider", "onStopTrackingTouch: VE0001R1");
                    toastShorter("RC0025SD~", 700);
                    STOP=4;
                    Log.d(TAG, "onValueChange: --------------------STOP-----"+STOP);

                }
            }
        });
//        myRockerView1.setOnAngleChangeListener(new MyRockerView.OnAngleChangeListener() {
//            @Override
//            public void onStart() {
//
//            }
//
//            @Override
//            public void angle(double angle) {
//                int angle_int = 360 - (int) angle;
//                if ((angle_int > 45 && angle_int <= 135)) {
//                    for (int i = 0; i < 8; i++) {
//
//                    SendCommand("YG0270R1", delay_ms);}
//                } else if ((angle_int > 135 && angle_int <= 225)) {
//                    for (int i = 0; i < 8; i++) {
//                    SendCommand("YG0180R1", delay_ms);}
//                } else if ((angle_int > 225 && angle_int <= 315)) {
//                    for (int i = 0; i < 8; i++) {
//                    SendCommand("YG0090R1", delay_ms);}
//                } else if ((angle_int > 315 && angle_int <= 360 || angle_int >= 0 && angle_int <= 45)) {
//
//                    for (int i = 0; i < 8; i++) {
//                    SendCommand("YG0000R1", delay_ms);}
//                }
//
//
//            }
//
//            @Override
//            public void onFinish() {
//                for (int i = 0; i < 8; i++) {
//
//
//                    SendCommand("YG0999R1", delay_ms);
//                }
//            }
//        });
//        myRockerView2.setOnAngleChangeListener(new MyRockerView.OnAngleChangeListener() {
//            @Override
//            public void onStart() {
//
//            }
//
//            @Override
//            public void angle(double angle) {
//                int angle_int = 360- (int)angle;
//                if ((angle_int >= 0 && angle_int <= 90) || (angle_int >= 270 && angle_int <= 360)){
//                    for (int i = 0; i < 8; i++) {
//
//                        SendCommand("PHCWSER1", delay_ms);}
//
//                }else {
//                    for (int i = 0; i < 8; i++) {
//
//                        SendCommand("PHATCWR1", delay_ms);}
//
//                }
//            }
//
//            @Override
//            public void onFinish() {
//                for (int i = 0; i < 8; i++) {
//                    SendCommand("PH0999R1", delay_ms);
//                }
//            }
//        });

        myRockerView3.setOnAngleChangeListener(new MyRockerView.OnAngleChangeListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void angle(double angle) {
                int angle_int =  (int)angle;
                if (angle_int >= 0 && angle_int <= 9){
                    //-------------------------------线程阻塞-------------------------------------------------------------------------
                        SendCommand("RC000"+angle_int+"YG", delay_ms);
//                    for (int i = 0; i < 8; i++) {
//                    }

//                    tv_angle_2.setText("00"+angle_int);
                }
                else if (angle_int >= 10 && angle_int <= 99){
                        SendCommand("RC00"+angle_int+"YG", delay_ms);
//                    for (int i = 0; i < 8; i++) {
//                    }

//                    tv_angle_2.setText("0"+angle_int);
                }
                else {
                    SendCommand("RC0"+angle_int+"YG", delay_ms);}
//                    for (int i = 0; i < 8; i++) {
//
////                    tv_angle_2.setText(""+angle_int);
//                }
            }

            @Override
            public void onFinish() {
                //判断速度，先逐档降速再停止
                if(STOP==1){
                    for(int i = 0; i < 8; i++){
                        SendCommand("RC0005SD", delay_ms);
                    }
                    toastShorter("RC0005SD~", 700);
                    STOP=0;
                } else if (STOP==2) {
                    for(int i = 0; i < 8; i++){
                        SendCommand("RC0010SD", delay_ms);
                    }
                    toastShorter("RC0010SD~", 700);
                    STOP=1;
                    for(int i = 0; i < 8; i++){
                        SendCommand("RC0005SD", delay_ms);
                    }
                    toastShorter("RC0005SD~", 700);
                    STOP=0;
                } else if (STOP==3) {
                    for (int i = 0; i < 8; i++){
                        SendCommand("RC0015SD", delay_ms);
                    }
                    toastShorter("RC0015SD~", 700);
                    STOP=2;
                    for (int i = 0; i < 8; i++){
                        SendCommand("RC0010SD", delay_ms);
                    }
                    toastShorter("RC0010SD~", 700);
                    STOP=1;
                    for (int i = 0; i < 8; i++){
                        SendCommand("RC0005SD", delay_ms);
                    }
                    toastShorter("RC0005SD~", 700);
                    STOP=0;
                } else if (STOP==4) {
                    for (int i = 0; i < 8; i++){
                        SendCommand("RC0020SD", delay_ms);
                    }
                    toastShorter("RC0020SD~", 700);
                    STOP=3;
                    Log.d(TAG, "onFinish: ------------------STOP4-----"+STOP);
                    for (int i = 0; i < 8; i++){
                        SendCommand("RC0015SD", delay_ms);
                    }
                    toastShorter("RC0015SD~", 700);
                    STOP=2;
                    Log.d(TAG, "onFinish: ------------------STOP4-----"+STOP);
                    for (int i = 0; i < 8; i++){
                        SendCommand("RC0010SD", delay_ms);
                    }
                    toastShorter("RC0010SD~", 700);
                    STOP=1;
                    Log.d(TAG, "onFinish: ------------------STOP4-----"+STOP);
                    for (int i = 0; i < 8; i++){
                        SendCommand("RC0005SD", delay_ms);
                    }
                    toastShorter("RC0005SD~", 700);
                    STOP=0;
                    Log.d(TAG, "onFinish: ------------------STOP4-----"+STOP);
                }
                SendCommand("RC0999YG", delay_ms);
//                for (int i = 0; i < 8; i++) {
//                }
            }
        });





        /*
        * 按钮夹具开合、夹具翻转
        * */


//        btn_claw.setPivotX(btn_claw.getWidth()/2);
//        btn_claw.setRotation(-40);




        seekBar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                tv_seekBar.setText(""+i);
                if (i >= 0 && i <= 9){
                    SendCommand("XZ"+"+"+"00"+i+"BF", delay_ms);
                }else if (i >= 10 && i <= 99){
                    SendCommand("XZ"+"+"+"0"+i+"BF", delay_ms);
                }else if (i >= 100){
                    SendCommand("XZ"+"+"+""+i+"BF", delay_ms);
                }else if (i >= -9 && i <= -1){
                    SendCommand("XZ"+"-"+"00"+Math.abs(i)+"BF", delay_ms);
                }else if (i >= -99 && i <= -10){
                    SendCommand("XZ"+"-"+"0"+Math.abs(i)+"BF", delay_ms);
                }else {
                    SendCommand("XZ"+"-"+""+Math.abs(i)+"BF", delay_ms);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });



        Button Btn_Connection = findViewById(R.id.Btn_Connection);
        Btn_Connection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dimBackground(1.f, 0.5f);
                PopupMenu popupMenu = new PopupMenu(MainActivity.this, Btn_Connection);
                popupMenu.getMenuInflater().inflate(R.menu.menu_two, popupMenu.getMenu());
                popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
                    @Override
                    public void onDismiss(PopupMenu popupMenu) {
                        dimBackground(0.5f, 1.f);
                    }
                });
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        dimBackground(1.f, 0.5f);
                        int id = menuItem.getItemId();
                        if (mAdapter.isEnabled()){
                            connect_state = 1;
                        }
                        else {
                            connect_state = 0;
                        }
                        if (id == R.id.item_one) {
                            dimBackground(0.5f, 1.f);
                            if (connect_state == 0) {
//                                Toast.makeText(MainActivity.this, "正在打开蓝牙", Toast.LENGTH_SHORT).show();
                                toastShorter("正在打开蓝牙", 300);
                                mController.turnOnBlueTooth(MainActivity.this, REQUEST_CODE);
                                connect_state = 1;
                            } else {
                                toastShorter("蓝牙已打开", 300);
                            }
                        }
                        if (id == R.id.item_two) {
                            dimBackground(0.5f, 1.f);

                            /*
                            * 蓝牙窗口2，通过一个Fragment实现
                            * */
                            if (!f2.isAdded()){
                                fm.beginTransaction().add(R.id.linearLayout_BluetoothList, f2).commit();
                            }
                            else {
                                fm.beginTransaction().show(f2).commit();
                            }

                            /*
                            * 蓝牙窗口1，通过一个Activity实现
                            * */
//                            Intent serverIntent = new Intent(MainActivity.this, ListActivity.class);
//                            startActivityForResult(serverIntent, REQUEST_CODE_1);
//                            image1.setImageDrawable(ResourcesCompat.getDrawable(getResources(), bluetooth_status2, getTheme()));
                        }
                        if (id == R.id.item_three) {
                            dimBackground(0.5f, 1.f);
                            if (connect_state == 1) {
//                mController.turnoffBlueTooth();
                                CancelConnect();
//                Toast.makeText(MainActivity.this, "已断开连接", Toast.LENGTH_SHORT).show();
                                connect_state = 0;
                            } else {
                                toastShorter("未打开蓝牙", 300);
                            }
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });



        begin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibrator mVibrator= (Vibrator) getSystemService(VIBRATOR_SERVICE);
                toastShorter("RC0001R1~", 300);
                SendCommand("RC0001R1", delay_ms);
                //MainActivity.write( "ReStart");
                mVibrator.vibrate(200);//振动0.2秒
            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Vibrator mVibrator= (Vibrator) getSystemService(VIBRATOR_SERVICE);
                toastShorter("RC0099R1~", 300);
                SendCommand("RC0099R1", delay_ms);
                mVibrator.vibrate(200);//振动0.2秒
            }
        });
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibrator mVibrator= (Vibrator) getSystemService(VIBRATOR_SERVICE);
                toastShorter("RC0001DW~", 300);
                SendCommand("RC0001DW", delay_ms);
                mVibrator.vibrate(200);//振动0.2秒
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibrator mVibrator= (Vibrator) getSystemService(VIBRATOR_SERVICE);
                toastShorter("RC0002DW~", 300);
                SendCommand("RC0002DW", delay_ms);
                mVibrator.vibrate(200);//振动0.2秒
            }
        });
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibrator mVibrator= (Vibrator) getSystemService(VIBRATOR_SERVICE);
                toastShorter("RC0003DW~", 300);
                SendCommand("RC0003DW", delay_ms);
                mVibrator.vibrate(200);//振动0.2秒
            }
        });
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibrator mVibrator= (Vibrator) getSystemService(VIBRATOR_SERVICE);
                toastShorter("RC0004DW~", 300);
                SendCommand("RC0004DW", delay_ms);
                mVibrator.vibrate(200);//振动0.2秒
            }
        });
        btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibrator mVibrator= (Vibrator) getSystemService(VIBRATOR_SERVICE);
                toastShorter("RC0005DW~", 300);
                SendCommand("RC0005DW", delay_ms);
                mVibrator.vibrate(200);//振动0.2秒
            }
        });
        btn6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibrator mVibrator= (Vibrator) getSystemService(VIBRATOR_SERVICE);
                toastShorter("RC0006DW~", 300);
                SendCommand("RC0006DW", delay_ms);
                mVibrator.vibrate(200);//振动0.2秒
            }
        });
        btn7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibrator mVibrator= (Vibrator) getSystemService(VIBRATOR_SERVICE);
                toastShorter("RC0007DW~", 300);
                SendCommand("RC0007DW", delay_ms);
                mVibrator.vibrate(200);//振动0.2秒
            }
        });
        yunqiu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibrator mVibrator= (Vibrator) getSystemService(VIBRATOR_SERVICE);
                toastShorter("RC0101R1~", 300);
                SendCommand("RC0101R1", delay_ms);
                mVibrator.vibrate(200);//振动0.2秒
            }
        });
        touqiu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibrator mVibrator= (Vibrator) getSystemService(VIBRATOR_SERVICE);
                toastShorter("RC0102R1~", 300);
                SendCommand("RC0102R1", delay_ms);
                mVibrator.vibrate(200);
            }
        });

        chuanqiu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibrator mVibrator= (Vibrator) getSystemService(VIBRATOR_SERVICE);
                toastShorter("RC0103R1~", 300);
                SendCommand("RC0103R1",  delay_ms);
                mVibrator.vibrate(200);
            }
        });
        jieqiu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibrator mVibrator= (Vibrator) getSystemService(VIBRATOR_SERVICE);
                toastShorter("RC0100R1~", 300);
                SendCommand("RC0100R1", delay_ms);
                mVibrator.vibrate(200);
            }
        });
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibrator mVibrator= (Vibrator) getSystemService(VIBRATOR_SERVICE);
                toastShorter("RC0011R1~", 300);
                SendCommand("RC0011R1", delay_ms);
                mVibrator.vibrate(200);
            }
        });
        reset2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibrator mVibrator= (Vibrator) getSystemService(VIBRATOR_SERVICE);
                toastShorter("RC0111R1~", 300);
                SendCommand("RC0111R1", delay_ms);
                mVibrator.vibrate(200);
            }
        });




    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 移除所有回调，防止内存泄漏
        handler.removeCallbacks(taskRunnable);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_menu, menu);
        return true;
    }

    int connect_state;
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (mAdapter.isEnabled()) {
            connect_state = 1;
        } else {
            connect_state = 0;
        }

        if (id == R.id.item_1) {
            if (connect_state == 0) {
                Toast.makeText(MainActivity.this, "正在打开蓝牙", Toast.LENGTH_SHORT).show();
                mController.turnOnBlueTooth(this, REQUEST_CODE);
                connect_state = 1;
            } else {
                Toast.makeText(MainActivity.this, "蓝牙已打开", Toast.LENGTH_SHORT).show();
            }
        }

        if (id == R.id.item_2) {
            Intent serverIntent = new Intent(MainActivity.this, ListActivity.class);
            startActivityForResult(serverIntent, REQUEST_CODE_1);
        }

        if (id == R.id.item_3) {
            if (connect_state == 1) {
//                mController.turnoffBlueTooth();
                CancelConnect();
                Toast.makeText(MainActivity.this, "已断开连接", Toast.LENGTH_SHORT).show();
                connect_state = 0;
            } else {
                Toast.makeText(MainActivity.this, "未连接", Toast.LENGTH_SHORT).show();
            }
        }

        return super.onOptionsItemSelected(item);
    }
    public void CancelConnect() {
        try {
//            mmSocket.close();
            if (CONNECT_STATUS){
                mmSocket.close();
                toastShorter("已断开连接", 500);
//                tv_bluetooth.setText("F");
//                tv_bluetooth.setTextColor(getResources().getColor(R.color.red));
//                image1.setImageDrawable(ResourcesCompat.getDrawable(getResources(), bluetooth_status1, getTheme()));
                CONNECT_STATUS = false;
            }
            else {

//                mmSocket.close();
                toastShorter("已打开蓝牙，但是未连接", 500);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CONNECT_DEVICE) {
            // 当DeviceListActivity返回与设备连接的消息
            if (resultCode == Activity.RESULT_OK) {
                // 得到链接设备的MAC
                String address = data.getExtras().getString(ListActivity.EXTRA_DEVICE_ADDRESS);
                // 得到BluetoothDevice对象
                if (!TextUtils.isEmpty(address)) {
                    BluetoothDevice device = mAdapter.getRemoteDevice(address);
                    ConnectThread(device);
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    public void ConnectThread(BluetoothDevice device) {
        //用服务号得到socket
        mAdapter.cancelDiscovery();
        try {
            mmSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
            Toast.makeText(this, "连接失败1", Toast.LENGTH_SHORT).show();
        }
        //连接socket
        try {
            mmSocket.connect();
            Toast.makeText(this, "连接成功", Toast.LENGTH_SHORT).show();
            CONNECT_STATUS = true;
            ReceiveData receive_thread = new ReceiveData();// 连接成功后开启接收数据服务
            receive_thread.start();
        } catch (IOException e) {
            try {
                Toast.makeText(this, "连接失败2", Toast.LENGTH_SHORT).show();
                mmSocket.close();
                mmSocket = null;
            } catch (IOException ee) {
                Toast.makeText(this, "连接失败3", Toast.LENGTH_SHORT).show();
            }

            return;
        }

    }


    public void cancel_connect() {
        try {
            mmSocket.close();
            CONNECT_STATUS = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private class ReceiveData extends Thread {

        private ReceiveData() {
            try {
                mmInStream = mmSocket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {

            int bytes = 0;
            byte[] buffer = new byte[1024];
            int ch = 0;
            while (true) {

                try {
                    final String readStr = new String(buffer, 0, bytes);
                    if (mmInStream.available() > 0 == false) {
                        continue;
                    } else {
                        Thread.sleep(200);
                    }
                    bytes = mmInStream.read(buffer);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if ("TAKIOK".equals(readStr)){
                                f2("TAKIOK", tv_info);
                            }

//                            if (readStr.contains("x")) {
//                                others1.setText(readStr);
//                            }
//                            Toast.makeText(MainActivity.this, readStr, Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void write(String str) {
        if (CONNECT_STATUS) {
            byte[] buffer = str.getBytes();
            try {
                mmOutStream = mmSocket.getOutputStream();
                mmOutStream.write(buffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static void write_num(int v) {
        if (CONNECT_STATUS) {
//            byte[] buffer = str.getBytes();
            try {
                mmOutStream = mmSocket.getOutputStream();
                mmOutStream.write(v);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        fm.beginTransaction().hide(f2).commit();
        return super.onTouchEvent(event);
    }
    /*
     * https://www.it610.com/article/1296014932332060672.htm
     * */

    @SuppressLint("NewApi")
    private void dimBackground(final float from, final float to) {
        final Window window = getWindow();
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(from, to);
        valueAnimator.setDuration(500);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                WindowManager.LayoutParams params = window.getAttributes();
                params.alpha = (Float) animation.getAnimatedValue();
                window.setAttributes(params);
            }
        });
        valueAnimator.start();
    }

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
            Log.d(TAG, "--------onClick: 预选界面finish");
            flag  = false;
        }
    }


}