package com.example.hc;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

//RCXXREST
//上层复位 01 纯系统复位 解决傻逼伺泰威
//底盘复位 02 转发给底盘
//机械复位 03 3508和2006回到初始位置
//M2006复位 04 接球 2006数据清除

//调试按键
//T SJ0xxxTO 100-350
//P SJ0xxxPO 140-200
//a SJ0xxxOP 000-070

//RC0xxxYT 000\090\180\270


public class AdjustActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    private String TAG = "AdjustActivity";
    
    // SharedPreferences相关常量
    private static final String PREFS_NAME = "AdjustActivityPrefs";
    private static final String KEY_SEEKBAR1_VALUE = "seekbar1_value";
    private static final String KEY_SEEKBAR2_VALUE = "seekbar2_value";
    private static final String KEY_SEEKBAR3_VALUE = "seekbar3_value";
    
    private SharedPreferences sharedPreferences;
    SeekBar seekBar1;
    TextView tvSeekBar1;
    SeekBar seekBar2;
    TextView tvSeekBar2;
    SeekBar seekBar3;
    TextView tvSeekBar3;

    // 三角形按钮
    ImageButton btnSeekbar1Minus, btnSeekbar1Plus;
    ImageButton btnSeekbar2Minus, btnSeekbar2Plus;
    ImageButton btnSeekbar3Minus, btnSeekbar3Plus;

    // 复位按键
    Button btnResetUpper;
    Button btnResetChassis;
    Button btnResetMechanical;
    Button btnResetM2006;

    Button btnBackToMain;
    Button btnchuanqiu;

    int delay_ms = 10;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adjust);

        // 初始化崩溃处理器
        CrashHandler.getInstance().init(this);

        // 初始化视图组件
        seekBar1 = findViewById(R.id.seekBar1);
        tvSeekBar1 = findViewById(R.id.tv_seekbar1);
        seekBar2 = findViewById(R.id.seekBar2);
        tvSeekBar2 = findViewById(R.id.tv_seekbar2);
        seekBar3 = findViewById(R.id.seekBar3);
        tvSeekBar3 = findViewById(R.id.tv_seekbar3);

        // 初始化三角形按钮
        btnSeekbar1Minus = findViewById(R.id.btn_seekbar1_minus);
        btnSeekbar1Plus = findViewById(R.id.btn_seekbar1_plus);
        btnSeekbar2Minus = findViewById(R.id.btn_seekbar2_minus);
        btnSeekbar2Plus = findViewById(R.id.btn_seekbar2_plus);
        btnSeekbar3Minus = findViewById(R.id.btn_seekbar3_minus);
        btnSeekbar3Plus = findViewById(R.id.btn_seekbar3_plus);

        // 初始化复位按键
        btnResetUpper = findViewById(R.id.btn_reset_upper);
        btnResetChassis = findViewById(R.id.btn_reset_chassis);
        btnResetMechanical = findViewById(R.id.btn_reset_mechanical);
        btnResetM2006 = findViewById(R.id.btn_reset_m2006);
        btnBackToMain = findViewById(R.id.btn_back_to_main);

        btnchuanqiu = findViewById(R.id.chuanqiu1);

        //隐藏标题栏，节省空间
        Objects.requireNonNull(getSupportActionBar()).hide();

        // 初始化SharedPreferences
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        
        // 恢复保存的滑杆数值
        restoreSeekBarValues();

        // 设置初始进度值显示
        tvSeekBar1.setText(String.valueOf(seekBar1.getProgress()));
        tvSeekBar2.setText(String.valueOf(seekBar2.getProgress()));
        tvSeekBar3.setText(String.valueOf(seekBar3.getProgress()));

        // 设置监听器
        seekBar1.setOnSeekBarChangeListener(this);
        seekBar2.setOnSeekBarChangeListener(this);
        seekBar3.setOnSeekBarChangeListener(this);



        // 设置复位按键监听器
        // RCXXREST
        //上层复位 01 纯系统复位 解决傻逼伺泰威
        //底盘复位 02 转发给底盘
        //机械复位 03 3508和2006回到初始位置
        //M2006复位 04 接球 2006数据清除
        btnResetUpper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendResetCommand("01");
            }
        });

        btnResetChassis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendResetCommand("02");
                //转发给底盘

            }
        });

        btnResetMechanical.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendResetCommand("03");
                //3508和2006回到初始位置
//                sendResetCommand("3508");
//                sendResetCommand("2006");
            }
        });

        btnResetM2006.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendResetCommand("04");
                //接球 2006数据清除
//                sendResetCommand("2006");
            }
        });
        
        // 设置返回按钮点击事件
        btnBackToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 返回到MainActivity
                finish();
            }
        });

        btnchuanqiu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCommand("RC0103R1");
            }
        });


        // 设置三角形按钮点击事件
        setupTriangleButtonListeners();
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        // 保存当前滑杆数值
        saveSeekBarValues();
    }
    
    /**
     * 保存滑杆当前数值到SharedPreferences
     */
    private void saveSeekBarValues() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_SEEKBAR1_VALUE, seekBar1.getProgress());
        editor.putInt(KEY_SEEKBAR2_VALUE, seekBar2.getProgress());
        editor.putInt(KEY_SEEKBAR3_VALUE, seekBar3.getProgress());
        editor.apply();
        Log.d(TAG, "保存滑杆数值: SeekBar1=" + seekBar1.getProgress() + 
                   ", SeekBar2=" + seekBar2.getProgress() + 
                   ", SeekBar3=" + seekBar3.getProgress());
    }
    
    /**
     * 从SharedPreferences恢复滑杆数值
     */
    private void restoreSeekBarValues() {
        // 获取保存的数值，如果没有保存过则使用默认值
        int seekBar1Value = sharedPreferences.getInt(KEY_SEEKBAR1_VALUE, seekBar1.getProgress());
        int seekBar2Value = sharedPreferences.getInt(KEY_SEEKBAR2_VALUE, seekBar2.getProgress());
        int seekBar3Value = sharedPreferences.getInt(KEY_SEEKBAR3_VALUE, seekBar3.getProgress());
        
        // 设置滑杆数值
        seekBar1.setProgress(seekBar1Value);
        seekBar2.setProgress(seekBar2Value);
        seekBar3.setProgress(seekBar3Value);
        
        Log.d(TAG, "恢复滑杆数值: SeekBar1=" + seekBar1Value + 
                   ", SeekBar2=" + seekBar2Value + 
                   ", SeekBar3=" + seekBar3Value);
    }
    //滑动条
    //改变时触发
    //调试按键
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            // 只更新显示值，不发送命令
            if (seekBar == seekBar1) {
                tvSeekBar1.setText(String.valueOf(progress));
            } else if (seekBar == seekBar2) {
                tvSeekBar2.setText(String.valueOf(progress));
            } else if (seekBar == seekBar3) {
                tvSeekBar3.setText(String.valueOf(progress));
            }
        }
    }

    //按下触发
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        Log.d(TAG, "onStartTrackingTouch: --------------");
    }

    //松开触发
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        try {
            int progress = seekBar.getProgress();
            String command = "";
            String seekBarName = "";

//T SJ0xxxTO 100-350
//P SJ0xxxPO 140-200
//a SJ0xxxOP 000-070
            if (seekBar == seekBar1) {
                command = String.format("SJ0%03dTO", progress);
                seekBarName = "SeekBar1";
                Log.d(TAG, "发送命令: " + command);
            } else if (seekBar == seekBar2) {
                command = String.format("SJ0%03dPO", progress);
                seekBarName = "SeekBar2";
            } else if (seekBar == seekBar3) {
                command = String.format("SJ00%02dOP", progress);
                seekBarName = "SeekBar3";
            }
            
            if (!MainActivity.CONNECT_STATUS) {
                String errorMsg = "蓝牙未连接";
                Log.e(TAG, "onStopTrackingTouch: " + errorMsg);
                CrashHandler.logException("onStopTrackingTouch连接检查", new Exception(errorMsg));
                Toast.makeText(this, "蓝牙未连接，请先连接设备", Toast.LENGTH_SHORT).show();
                return;
            }
            
            try {
                MainActivity.write(command);
                Log.d(TAG, "onStopTrackingTouch成功发送: " + command);
                Toast.makeText(this, seekBarName+command, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                CrashHandler.logException("onStopTrackingTouch写入异常", e);
                Log.e(TAG, "onStopTrackingTouch写入异常: " + e.getMessage(), e);
                Toast.makeText(this, "发送命令异常: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            
            // 保存当前滑杆数值
            saveSeekBarValues();
        } catch (Exception e) {
            CrashHandler.logException("onStopTrackingTouch未知异常", e);
            Log.e(TAG, "onStopTrackingTouch未知异常: " + e.getMessage(), e);
            Toast.makeText(this, "滑杆操作时发生异常: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }



    /**
     * 设置三角形按钮的点击事件监听器
     */
    private void setupTriangleButtonListeners() {
        // SeekBar1 的按钮
        btnSeekbar1Minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adjustSeekBarValue(seekBar1, -1);
            }
        });
        
        btnSeekbar1Plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adjustSeekBarValue(seekBar1, 1);
            }
        });
        
        // SeekBar2 的按钮
        btnSeekbar2Minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adjustSeekBarValue(seekBar2, -1);
            }
        });
        
        btnSeekbar2Plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adjustSeekBarValue(seekBar2, 1);
            }
        });
        
        // SeekBar3 的按钮
        btnSeekbar3Minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adjustSeekBarValue(seekBar3, -1);
            }
        });
        
        btnSeekbar3Plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adjustSeekBarValue(seekBar3, 1);
            }
        });
    }
    
    /**
     * 调整滑杆数值
     * @param seekBar 要调整的滑杆
     * @param delta 调整量 (+1 或 -1)
     */
    @SuppressLint("NewApi")
    private void adjustSeekBarValue(SeekBar seekBar, int delta) {
        int currentProgress = seekBar.getProgress();
        int newProgress = currentProgress + delta;
        
        // 确保新值在滑杆的范围内
        if (newProgress >= seekBar.getMin() && newProgress <= seekBar.getMax()) {
            seekBar.setProgress(newProgress);
            
            // 更新显示文本
            if (seekBar == seekBar1) {
                tvSeekBar1.setText(String.valueOf(newProgress));
            } else if (seekBar == seekBar2) {
                tvSeekBar2.setText(String.valueOf(newProgress));
            } else if (seekBar == seekBar3) {
                tvSeekBar3.setText(String.valueOf(newProgress));
            }
            
            // 发送命令
            sendSeekBarCommand(seekBar, newProgress);
            
            // 立即保存数值
            saveSeekBarValues();
        }
    }
    
    /**
     * 发送滑杆命令
     * @param seekBar 滑杆对象
     * @param progress 进度值
     */
    private void sendSeekBarCommand(SeekBar seekBar, int progress) {
        try {
            String command = "";
            String seekBarName = "";
            
            if (seekBar == seekBar1) {
                command = String.format("SJ0%03dTO", progress);
                seekBarName = "SeekBar1";
            } else if (seekBar == seekBar2) {
                command = String.format("SJ0%03dPO", progress);
                seekBarName = "SeekBar2";
            } else if (seekBar == seekBar3) {
                command = String.format("SJ00%02dOP", progress);
                seekBarName = "SeekBar3";
            }
            
            if (!MainActivity.CONNECT_STATUS) {
                String errorMsg = "蓝牙未连接";
                Log.e(TAG, "sendSeekBarCommand: " + errorMsg);
                CrashHandler.logException("sendSeekBarCommand连接检查", new Exception(errorMsg));
                Toast.makeText(this, "蓝牙未连接，请先连接设备", Toast.LENGTH_SHORT).show();
                return;
            }
            
            try {
                MainActivity.write(command);
                Log.d(TAG, "sendSeekBarCommand成功发送: " + command);
                Toast.makeText(this, seekBarName + " " + command, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                CrashHandler.logException("sendSeekBarCommand写入异常", e);
                Log.e(TAG, "sendSeekBarCommand写入异常: " + e.getMessage(), e);
                Toast.makeText(this, "发送命令异常: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            CrashHandler.logException("sendSeekBarCommand未知异常", e);
            Log.e(TAG, "sendSeekBarCommand未知异常: " + e.getMessage(), e);
            Toast.makeText(this, "滑杆命令发送时发生异常: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }




    /**
     * 发送复位控制命令
     * @param resetType 复位类型 (01=上层复位, 02=底盘复位, 03=机械复位, 04=M2006复位)
     */
    private void sendResetCommand(String resetType) {
        try {
            // 检查蓝牙连接状态
            if (!MainActivity.CONNECT_STATUS) {
                String errorMsg = "蓝牙未连接";
                Log.e(TAG, "sendResetCommand: " + errorMsg);
                CrashHandler.logException("sendResetCommand连接检查", new Exception(errorMsg));
                Toast.makeText(this, "蓝牙未连接，请先连接设备", Toast.LENGTH_SHORT).show();
                return;
            }

            String command = "RC" + resetType + "REST";
            try {
                MainActivity.write(command);
                Log.d(TAG, "sendResetCommand成功发送: " + command);
            } catch (Exception e) {
                CrashHandler.logException("sendResetCommand写入异常", e);
                Log.e(TAG, "sendResetCommand写入异常: " + e.getMessage(), e);
                Toast.makeText(this, "发送复位命令失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }

            // 发送后再次检查连接状态，如果断开则提示用户
            if (!MainActivity.CONNECT_STATUS) {
                String errorMsg = "复位命令发送后连接已断开";
                Log.e(TAG, "sendResetCommand: " + errorMsg);
                CrashHandler.logException("sendResetCommand发送后连接检查", new Exception(errorMsg));
                Toast.makeText(this, "命令发送失败，连接已断开", Toast.LENGTH_SHORT).show();
            } else {
                String resetName = "";
                switch (resetType) {
                    case "01":
                        resetName = "上层复位";
                        break;
                    case "02":
                        resetName = "底盘复位";
                        break;
                    case "03":
                        resetName = "机械复位";
                        break;
                    case "04":
                        resetName = "M2006复位";
                        break;
                }
                Toast.makeText(this, command, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            CrashHandler.logException("sendResetCommand未知异常", e);
            Log.e(TAG, "sendResetCommand未知异常: " + e.getMessage(), e);
            Toast.makeText(this, "发送复位命令时发生异常: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void sendCommand(String command) {
        try {
            // 检查蓝牙连接状态
            if (!MainActivity.CONNECT_STATUS) {
                String errorMsg = "蓝牙未连接";
                Log.e(TAG, "sendResetCommand: " + errorMsg);
                CrashHandler.logException("sendResetCommand连接检查", new Exception(errorMsg));
                Toast.makeText(this, "蓝牙未连接，请先连接设备", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                MainActivity.write(command);
                Log.d(TAG, "sendResetCommand成功发送: " + command);
            } catch (Exception e) {
                CrashHandler.logException("sendResetCommand写入异常", e);
                Log.e(TAG, "sendResetCommand写入异常: " + e.getMessage(), e);
                Toast.makeText(this, "发送复位命令失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }

            // 发送后再次检查连接状态，如果断开则提示用户
            if (!MainActivity.CONNECT_STATUS) {
                String errorMsg = "复位命令发送后连接已断开";
                Log.e(TAG, "sendResetCommand: " + errorMsg);
                CrashHandler.logException("sendResetCommand发送后连接检查", new Exception(errorMsg));
                Toast.makeText(this, "命令发送失败，连接已断开", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, command, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            CrashHandler.logException("sendResetCommand未知异常", e);
            Log.e(TAG, "sendResetCommand未知异常: " + e.getMessage(), e);
            Toast.makeText(this, "发送复位命令时发生异常: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}