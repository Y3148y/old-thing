package com.example.hc;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

/**
 * 全局异常处理器
 * 捕获未处理的异常，记录日志并显示调试信息
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler, Application.ActivityLifecycleCallbacks {
    private static final String TAG = "CrashHandler";
    private static CrashHandler instance;
    private Context context;
    private Thread.UncaughtExceptionHandler defaultHandler;
    private DebugLogger debugLogger;
    private Activity currentActivity;

    private CrashHandler() {}

    public static CrashHandler getInstance() {
        if (instance == null) {
            synchronized (CrashHandler.class) {
                if (instance == null) {
                    instance = new CrashHandler();
                }
            }
        }
        return instance;
    }

    /**
     * 初始化异常处理器
     * @param context 应用上下文
     */
    public void init(Context context) {
        this.context = context.getApplicationContext();
        this.debugLogger = DebugLogger.getInstance(context);

        // 获取默认的异常处理器
        defaultHandler = Thread.getDefaultUncaughtExceptionHandler();

        // 设置自定义异常处理器
        Thread.setDefaultUncaughtExceptionHandler(this);

        // 注册Activity生命周期回调
        if (context instanceof Application) {
            ((Application) context).registerActivityLifecycleCallbacks(this);
        }

        debugLogger.writeLog("CRASH_HANDLER", "异常处理器初始化完成", "INFO");
    }

//    @Override
     
    /**
     * 手动触发调试日志显示（用于测试或手动查看日志）
     */
    public void showDebugLog() {
        if (debugLogger != null) {
            debugLogger.showDebugLogDialog();
        }
    }
    
    // Activity生命周期回调方法
    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        currentActivity = activity;
        debugLogger.writeLog("ACTIVITY_LIFECYCLE", 
            "Activity创建: " + activity.getClass().getSimpleName(), "INFO");
    }
    
    @Override
    public void onActivityStarted(Activity activity) {
        currentActivity = activity;
    }
    
    @Override
    public void onActivityResumed(Activity activity) {
        currentActivity = activity;
        debugLogger.writeLog("ACTIVITY_LIFECYCLE", 
            "Activity恢复: " + activity.getClass().getSimpleName(), "INFO");
    }
    
    @Override
    public void onActivityPaused(Activity activity) {
        debugLogger.writeLog("ACTIVITY_LIFECYCLE", 
            "Activity暂停: " + activity.getClass().getSimpleName(), "INFO");
    }
    
    @Override
    public void onActivityStopped(Activity activity) {
        debugLogger.writeLog("ACTIVITY_LIFECYCLE", 
            "Activity停止: " + activity.getClass().getSimpleName(), "INFO");
    }
    
    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}
    
    @Override
    public void onActivityDestroyed(Activity activity) {
        debugLogger.writeLog("ACTIVITY_LIFECYCLE", 
            "Activity销毁: " + activity.getClass().getSimpleName(), "INFO");
        if (currentActivity == activity) {
            currentActivity = null;
        }
    }
    
    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        // 记录异常信息
        debugLogger.writeLog("UNCAUGHT_EXCEPTION",
            "线程: " + thread.getName() + ", 异常: " + throwable.getClass().getSimpleName(), "ERROR");
        debugLogger.logException("CRASH", "应用崩溃", throwable);

        // 记录内存使用情况
        debugLogger.logMemoryUsage();

        // 记录堆栈信息
        StringBuilder stackTrace = new StringBuilder();
        for (StackTraceElement element : throwable.getStackTrace()) {
            stackTrace.append(element.toString()).append("\n");
        }
        try {
            debugLogger.writeLog("STACK_TRACE", stackTrace.toString(), "ERROR");
        } catch (Exception e) {
            Log.e(TAG, "写入崩溃日志失败", e);
        }

        // 调用默认处理器
        if (defaultHandler != null) {
            defaultHandler.uncaughtException(thread, throwable);
        } else {
            System.exit(1);
        }
    }
    
    /**
     * 记录蓝牙连接状态变化
     */
    public void logBluetoothStateChange(String state, String details) {
        if (debugLogger != null) {
            debugLogger.logBluetoothConnection(state, details);
        }
    }
    
    /**
     * 记录数据发送情况
     */
    public void logDataTransmission(String command, boolean success, String errorMsg) {
        if (debugLogger != null) {
            String details = success ? "成功" : "失败: " + errorMsg;
            debugLogger.logDataSend(command, success);
        }
    }
    
    /**
     * 记录摇杆操作
     */
    public void logRockerOperation(double angle, String command) {
        if (debugLogger != null) {
            debugLogger.logRockerAction(angle, command);
        }
    }
    
    /**
     * 记录内存警告
     */
    public void logMemoryWarning() {
        if (debugLogger != null) {
            debugLogger.logMemoryUsage();
            debugLogger.writeLog("MEMORY_WARNING", "收到内存警告", "WARN");
        }
    }
}