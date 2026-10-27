package com.example.hc;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 崩溃日志处理类
 * 用于捕获应用崩溃异常并显示详细信息
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {
    
    private static final String TAG = "CrashHandler";
    private static final String CRASH_DIR = "HC_Crash_Logs";
    
    private static CrashHandler instance;
    private Context mContext;
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    
    private CrashHandler() {}
    
    public static CrashHandler getInstance() {
        if (instance == null) {
            instance = new CrashHandler();
        }
        return instance;
    }
    
    /**
     * 初始化崩溃处理器
     * @param context 应用上下文
     */
    public void init(Context context) {
        mContext = context;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }
    
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null) {
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Log.e(TAG, "error : ", e);
            }
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }
    
    /**
     * 处理异常
     * @param ex 异常信息
     * @return true表示已处理，false表示未处理
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }
        
        // 收集崩溃信息
        String crashInfo = collectCrashInfo(ex);
        
        // 保存崩溃日志到文件
        saveCrashInfoToFile(crashInfo);
        
        // 在UI线程显示崩溃对话框
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                showCrashDialog(crashInfo);
                Looper.loop();
            }
        }.start();
        
        return true;
    }
    
    /**
     * 收集崩溃信息
     * @param ex 异常对象
     * @return 崩溃信息字符串
     */
    private String collectCrashInfo(Throwable ex) {
        StringBuilder sb = new StringBuilder();
        
        // 添加时间戳
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        sb.append("崩溃时间: ").append(sdf.format(new Date())).append("\n\n");
        
        // 添加设备信息
        sb.append("设备信息:\n");
        sb.append("设备型号: ").append(Build.MODEL).append("\n");
        sb.append("Android版本: ").append(Build.VERSION.RELEASE).append("\n");
        sb.append("API级别: ").append(Build.VERSION.SDK_INT).append("\n");
        sb.append("制造商: ").append(Build.MANUFACTURER).append("\n\n");
        
        // 添加应用信息
        try {
            PackageManager pm = mContext.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES);
            sb.append("应用信息:\n");
            sb.append("应用版本: ").append(pi.versionName).append("\n");
            sb.append("版本代码: ").append(pi.versionCode).append("\n\n");
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "获取应用信息失败", e);
        }
        
        // 添加异常堆栈信息
        sb.append("异常信息:\n");
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        sb.append(sw.toString());
        
        return sb.toString();
    }
    
    /**
     * 保存崩溃信息到文件
     * @param crashInfo 崩溃信息
     */
    private void saveCrashInfoToFile(String crashInfo) {
        try {
            // 创建崩溃日志目录
            File crashDir = new File(mContext.getExternalFilesDir(null), CRASH_DIR);
            if (!crashDir.exists()) {
                crashDir.mkdirs();
            }
            
            // 创建日志文件
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault());
            String fileName = "crash_" + sdf.format(new Date()) + ".log";
            File crashFile = new File(crashDir, fileName);
            
            // 写入崩溃信息
            FileOutputStream fos = new FileOutputStream(crashFile);
            fos.write(crashInfo.getBytes());
            fos.close();
            
            Log.i(TAG, "崩溃日志已保存到: " + crashFile.getAbsolutePath());
        } catch (IOException e) {
            Log.e(TAG, "保存崩溃日志失败", e);
        }
    }
    
    /**
     * 显示崩溃对话框
     * @param crashInfo 崩溃信息
     */
    private void showCrashDialog(String crashInfo) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("应用崩溃");
            builder.setMessage("很抱歉，应用发生了崩溃。\n\n崩溃详情:\n" + 
                             crashInfo.substring(0, Math.min(crashInfo.length(), 500)) + 
                             (crashInfo.length() > 500 ? "..." : ""));
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(1);
                }
            });
            builder.setNegativeButton("复制日志", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // 复制崩溃信息到剪贴板
                    android.content.ClipboardManager clipboard = 
                        (android.content.ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                    android.content.ClipData clip = android.content.ClipData.newPlainText("崩溃日志", crashInfo);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(mContext, "崩溃日志已复制到剪贴板", Toast.LENGTH_SHORT).show();
                    
                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(1);
                }
            });
            builder.setCancelable(false);
            
            AlertDialog dialog = builder.create();
            
            // 根据Android版本设置窗口类型
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Android 8.0+ 使用 TYPE_APPLICATION_OVERLAY
                dialog.getWindow().setType(android.view.WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Android 6.0+ 检查权限
                if (android.provider.Settings.canDrawOverlays(mContext)) {
                    dialog.getWindow().setType(android.view.WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                } else {
                    // 没有权限时使用普通对话框类型，但可能无法在崩溃时显示
                    Log.w(TAG, "没有悬浮窗权限，崩溃对话框可能无法显示");
                    dialog.getWindow().setType(android.view.WindowManager.LayoutParams.TYPE_APPLICATION_ATTACHED_DIALOG);
                }
            } else {
                // Android 6.0以下直接使用
                dialog.getWindow().setType(android.view.WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            }
            
            dialog.show();
            Log.i(TAG, "崩溃对话框已显示");
        } catch (Exception e) {
            Log.e(TAG, "显示崩溃对话框失败: " + e.getMessage(), e);
            // 如果对话框显示失败，至少在日志中记录崩溃信息
            Log.e(TAG, "崩溃信息: " + crashInfo);
        }
    }
    
    /**
     * 记录特定操作的异常
     * @param operation 操作名称
     * @param ex 异常对象
     */
    public static void logException(String operation, Throwable ex) {
        Log.e(TAG, operation + " 发生异常", ex);
        
        // 可以在这里添加更详细的日志记录逻辑
        StringBuilder sb = new StringBuilder();
        sb.append("操作: ").append(operation).append("\n");
        sb.append("时间: ").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date())).append("\n");
        sb.append("异常: ").append(ex.getMessage()).append("\n");
        
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        sb.append("堆栈: ").append(sw.toString());
        
        Log.d(TAG, sb.toString());
    }
}