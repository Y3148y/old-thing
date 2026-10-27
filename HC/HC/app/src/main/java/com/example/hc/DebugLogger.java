package com.example.hc;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 调试日志记录器
 * 用于记录蓝牙连接、数据发送等关键操作的日志
 * 在APP闪退时可以弹出日志窗口供用户保存分析
 */
public class DebugLogger {
    private static final String TAG = "DebugLogger";
    private static final String LOG_FILE_NAME = "bluetooth_debug.log";
    private static DebugLogger instance;
    private Context context;
    private FileWriter logWriter;
    private File logFile;
    private StringBuilder logBuffer;
    private int logCount = 0;
    private static final int MAX_LOG_BUFFER_SIZE = 1000; // 最大缓存日志条数
    
    private DebugLogger(Context context) {
        this.context = context.getApplicationContext();
        this.logBuffer = new StringBuilder();
        initLogFile();
    }
    
    public static synchronized DebugLogger getInstance(Context context) {
        if (instance == null) {
            instance = new DebugLogger(context);
        }
        return instance;
    }
    
    /**
     * 初始化日志文件
     */
    private void initLogFile() {
        try {
            File logDir = new File(context.getExternalFilesDir(null), "logs");
            if (!logDir.exists()) {
                logDir.mkdirs();
            }
            logFile = new File(logDir, LOG_FILE_NAME);
            logWriter = new FileWriter(logFile, true);
            
            // 记录启动日志
            writeLog("APP_START", "应用启动", "INFO");
        } catch (IOException e) {
            Log.e(TAG, "初始化日志文件失败", e);
        }
    }
    
    /**
     * 写入日志
     * @param tag 标签
     * @param message 消息
     * @param level 级别 (INFO, WARN, ERROR)
     */
    public synchronized void writeLog(String tag, String message, String level) {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault()).format(new Date());
        String logEntry = String.format("[%s] [%s] [%s] %s\n", timestamp, level, tag, message);
        
        // 添加到缓存
        logBuffer.append(logEntry);
        logCount++;
        
        // 如果缓存过大，清理一部分
        if (logCount > MAX_LOG_BUFFER_SIZE) {
            String bufferStr = logBuffer.toString();
            int halfPoint = bufferStr.length() / 2;
            int newlineIndex = bufferStr.indexOf('\n', halfPoint);
            if (newlineIndex != -1) {
                logBuffer = new StringBuilder(bufferStr.substring(newlineIndex + 1));
                logCount = logCount / 2;
            }
        }
        
        // 写入文件
        try {
            if (logWriter != null) {
                logWriter.write(logEntry);
                logWriter.flush();
            }
        } catch (IOException e) {
            Log.e(TAG, "写入日志文件失败", e);
        }
        
        // 同时输出到Logcat
        switch (level) {
            case "ERROR":
                Log.e(tag, message);
                break;
            case "WARN":
                Log.w(tag, message);
                break;
            default:
                Log.i(tag, message);
                break;
        }
    }
    
    /**
     * 记录蓝牙连接相关日志
     */
    public void logBluetoothConnection(String action, String details) {
        writeLog("BLUETOOTH_CONN", action + ": " + details, "INFO");
    }
    
    /**
     * 记录数据发送日志
     */
    public void logDataSend(String command, boolean success) {
        String level = success ? "INFO" : "WARN";
        writeLog("DATA_SEND", "发送命令: " + command + ", 结果: " + (success ? "成功" : "失败"), level);
    }
    
    /**
     * 记录摇杆操作日志
     */
    public void logRockerAction(double angle, String command) {
        writeLog("ROCKER_ACTION", String.format("角度: %.1f°, 命令: %s", angle, command), "INFO");
    }
    
    /**
     * 记录摇杆操作日志（重载方法）
     */
    public void logRockerAction(String action, String details) {
        writeLog("ROCKER_ACTION", action + ": " + details, "INFO");
    }
    
    /**
     * 记录数据接收日志
     */
    public void logDataReceive(String data, int bytes) {
        writeLog("DATA_RECEIVE", String.format("接收数据: %s, 字节数: %d", data, bytes), "INFO");
    }
    
    /**
     * 获取日志内容
     */
    public String getLogContent() {
        return logBuffer.toString();
    }
    
    /**
     * 记录异常日志
     */
    public void logException(String tag, String message, Throwable throwable) {
        String exceptionMsg = message + ": " + throwable.getMessage();
        writeLog(tag, exceptionMsg, "ERROR");
    }
    
    /**
     * 记录内存使用情况
     */
    public void logMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = runtime.totalMemory() - runtime.freeMemory();
        long maxMemory = runtime.maxMemory();
        double memoryUsagePercent = (double) usedMemory / maxMemory * 100;
        
        writeLog("MEMORY", String.format("内存使用: %.1f%% (%d/%d MB)", 
            memoryUsagePercent, usedMemory / 1024 / 1024, maxMemory / 1024 / 1024), "INFO");
    }
    
    /**
     * 显示调试日志对话框
     */
    public void showDebugLogDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("调试日志")
               .setMessage("检测到可能的问题，是否查看调试日志？")
               .setPositiveButton("查看日志", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       showLogContent();
                   }
               })
               .setNegativeButton("保存日志", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       saveAndShareLog();
                   }
               })
               .setNeutralButton("取消", null)
               .show();
    }
    
    /**
     * 显示日志内容
     */
    private void showLogContent() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("调试日志内容")
               .setMessage(logBuffer.toString())
               .setPositiveButton("保存", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       saveAndShareLog();
                   }
               })
               .setNegativeButton("关闭", null)
               .show();
    }
    
    /**
     * 保存并分享日志文件
     */
    private void saveAndShareLog() {
        try {
            if (logWriter != null) {
                logWriter.flush();
            }
            
            // 创建分享Intent
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            
            Uri fileUri = FileProvider.getUriForFile(context, 
                context.getPackageName() + ".fileprovider", logFile);
            shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "蓝牙遥控APP调试日志");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "请查看附件中的调试日志文件");
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            
            Intent chooser = Intent.createChooser(shareIntent, "分享调试日志");
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(chooser);
            
            Toast.makeText(context, "日志已保存到: " + logFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.e(TAG, "保存日志失败", e);
            Toast.makeText(context, "保存日志失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * 清理日志
     */
    public void clearLogs() {
        logBuffer.setLength(0);
        logCount = 0;
        try {
            if (logWriter != null) {
                logWriter.close();
            }
            if (logFile.exists()) {
                logFile.delete();
            }
            initLogFile();
        } catch (IOException e) {
            Log.e(TAG, "清理日志失败", e);
        }
    }
    
    /**
     * 关闭日志记录器
     */
    public void close() {
        try {
            if (logWriter != null) {
                writeLog("APP_CLOSE", "应用关闭", "INFO");
                logWriter.close();
                logWriter = null;
            }
        } catch (IOException e) {
            Log.e(TAG, "关闭日志记录器失败", e);
        }
    }
}