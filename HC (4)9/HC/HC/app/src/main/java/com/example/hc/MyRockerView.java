package com.example.hc;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;


/*
* https://www.jianshu.com/p/1a1491f059fc?u_atoken=07477492-6918-4bb1-8343-cfa02ba2cee7&u_asession=01VuMAJF-Xn-8ijMNp0x08H2H242z4Ic6FyQbpxdJTvbGqmOcOuXtqoxwhsST1LGOXX0KNBwm7Lovlpxjd_P_q4JsKWYrT3W_NKPr8w6oU7K9NCv9A3vrAiqH3KHlH704HS_nntv0fnCqKMMYW1x5qJGBkFo3NEHBv0PZUm
* 6pbxQU&u_asig=05AuiogwoKKcxKt5qAOOwfnnCy3o1a5xmCrb608KPwUMqBqFWZOcQH2fmwJoCsMrkCtrehtEsODCPtlyb1siSpQxl-JKOd01ntndA67S5vr_ibRnpuAYYQ9oYbRol0e5pYE82h7AbKordskDKzVcayaPpMTi5tsRRP3_TFgXcJzn79JS7q8ZD7Xtz2Ly-b0kmuyAKRFSVJkkdwVUnyHAIJzZ3gd4QENxI62LHw17m8siZRnKY0GMLzLTnacGVOXhc04Nx7nJyT20ni5onZOBbfUe3h9VXwMyh6PgyDIVSG1W_8AP3zSIyNiyRbuwPUSJW2753T8BzzOAZPMzsyYES8L4EWGYvTwUl8xN2nzQV8dAuzPTEaBi7ty92DZ_1Jf2h9mWspDxyAEEo4kbsryBKb9Q&u_aref=dBfjfv6F%2BEhQ557ulShg%2FYxfEos%3D
* */

public class MyRockerView extends View {
    private static final String TAG = "RockerView";

    private static final int DEFAULT_SIZE = 400;
    private static final float DEFAULT_ROCKER_SCALE = 0.5f;//默认半径为背景的1/2
    private static final int flag_r_l = 1;
    private final int flag_r_l_2 = 8;
    public int flag_round_or_line = 0;

    private Paint mAreaBackgroundPaint;
    private Paint mRockerPaint;

    private Point mRockerPosition;
    private Point mCenterPoint;

    private int mAreaRadius;
    private float mRockerScale;

    private int mRockerRadius;

    private CallBackMode mCallBackMode = CallBackMode.CALL_BACK_MODE_MOVE;
    private OnAngleChangeListener mOnAngleChangeListener;
    private OnShakeListener mOnShakeListener;
    private OnDistanceLevelListener mOnDistanceLevelListener;

    private DirectionMode mDirectionMode;
    private Direction tempDirection = Direction.DIRECTION_CENTER;

    private float lastDistance = 0;
    private boolean hasCall = false;
    private float baseDistance = 0;
    private int mDistanceLevel = 10;//分成10分
    
    // UI刷新相关变量
    private Handler mUIHandler; // UI更新Handler
    private Runnable mRefreshRunnable; // 刷新任务
    private static final int REFRESH_INTERVAL_MS = 30; // 刷新间隔30ms
    private boolean mIsRefreshing = false; // 是否正在刷新


    // 角度
    private static final double ANGLE_0 = 0;
    private static final double ANGLE_360 = 360;
    // 360°水平方向平分2份的边缘角度
    private static final double ANGLE_HORIZONTAL_2D_OF_0P = 90;
    private static final double ANGLE_HORIZONTAL_2D_OF_1P = 270;
    // 360°垂直方向平分2份的边缘角度
    private static final double ANGLE_VERTICAL_2D_OF_0P = 0;
    private static final double ANGLE_VERTICAL_2D_OF_1P = 180;
    // 360°平分4份的边缘角度
    private static final double ANGLE_4D_OF_0P = 0;
    private static final double ANGLE_4D_OF_1P = 90;
    private static final double ANGLE_4D_OF_2P = 180;
    private static final double ANGLE_4D_OF_3P = 270;
    // 360°平分4份的边缘角度(旋转45度)
    private static final double ANGLE_ROTATE45_4D_OF_0P = 45;
    private static final double ANGLE_ROTATE45_4D_OF_1P = 135;
    private static final double ANGLE_ROTATE45_4D_OF_2P = 225;
    private static final double ANGLE_ROTATE45_4D_OF_3P = 315;

    // 360°平分8份的边缘角度
    private static final double ANGLE_8D_OF_0P = 22.5;
    private static final double ANGLE_8D_OF_1P = 67.5;
    private static final double ANGLE_8D_OF_2P = 112.5;
    private static final double ANGLE_8D_OF_3P = 157.5;
    private static final double ANGLE_8D_OF_4P = 202.5;
    private static final double ANGLE_8D_OF_5P = 247.5;
    private static final double ANGLE_8D_OF_6P = 292.5;
    private static final double ANGLE_8D_OF_7P = 337.5;

    // 摇杆可移动区域背景
    private static final int AREA_BACKGROUND_MODE_PIC = 0;
    private static final int AREA_BACKGROUND_MODE_COLOR = 1;
    private static final int AREA_BACKGROUND_MODE_XML = 2;
    private static final int AREA_BACKGROUND_MODE_DEFAULT = 3;
    private int mAreaBackgroundMode = AREA_BACKGROUND_MODE_DEFAULT;
    private Bitmap mAreaBitmap;
    private int mAreaColor;
    // 摇杆背景
    private static final int ROCKER_BACKGROUND_MODE_PIC = 4;
    private static final int ROCKER_BACKGROUND_MODE_COLOR = 5;
    private static final int ROCKER_BACKGROUND_MODE_XML = 6;
    private static final int ROCKER_BACKGROUND_MODE_DEFAULT = 7;
    private int mRockerBackgroundMode = ROCKER_BACKGROUND_MODE_DEFAULT;
    private Bitmap mRockerBitmap;
    private int mRockerColor;


    public MyRockerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // 获取自定义属性
        initAttribute(context, attrs);

        if (isInEditMode()) {
        }

        // 移动区域画笔
        mAreaBackgroundPaint = new Paint();
        mAreaBackgroundPaint.setAntiAlias(true);

        // 摇杆画笔
        mRockerPaint = new Paint();
        mRockerPaint.setAntiAlias(true);

        // 中心点
        mCenterPoint = new Point();
        // 摇杆位置
        mRockerPosition = new Point();
        
        // 初始化UI刷新Handler和任务
        initUIRefresh();
    }

    /**
     * 获取属性
     *
     * @param context context
     * @param attrs   attrs
     */
    private void initAttribute(@NonNull Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RockerView);

        flag_round_or_line = typedArray.getInt(R.styleable.RockerView_shh, this.flag_r_l_2);

        // 可移动区域背景
        Drawable areaBackground = typedArray.getDrawable(R.styleable.RockerView_areaBackground);
        if (null != areaBackground) {
            // 设置了背景
            if (areaBackground instanceof BitmapDrawable) {
                // 设置了一张图片
                mAreaBitmap = ((BitmapDrawable) areaBackground).getBitmap();
                mAreaBackgroundMode = AREA_BACKGROUND_MODE_PIC;
            } else if (areaBackground instanceof GradientDrawable) {
                // XML
                mAreaBitmap = drawable2Bitmap(areaBackground);
                mAreaBackgroundMode = AREA_BACKGROUND_MODE_XML;
            } else if (areaBackground instanceof ColorDrawable) {
                // 色值
                mAreaColor = ((ColorDrawable) areaBackground).getColor();
                mAreaBackgroundMode = AREA_BACKGROUND_MODE_COLOR;
            } else {
                // 其他形式
                mAreaBackgroundMode = AREA_BACKGROUND_MODE_DEFAULT;
            }
        } else {
            // 没有设置背景
            mAreaBackgroundMode = AREA_BACKGROUND_MODE_DEFAULT;
        }
        // 摇杆背景
        Drawable rockerBackground = typedArray.getDrawable(R.styleable.RockerView_rockerBackground);
        if (null != rockerBackground) {
            // 设置了摇杆背景
            if (rockerBackground instanceof BitmapDrawable) {
                // 图片
                mRockerBitmap = ((BitmapDrawable) rockerBackground).getBitmap();
                mRockerBackgroundMode = ROCKER_BACKGROUND_MODE_PIC;
            } else if (rockerBackground instanceof GradientDrawable) {
                // XML
                mRockerBitmap = drawable2Bitmap(rockerBackground);
                mRockerBackgroundMode = ROCKER_BACKGROUND_MODE_XML;
            } else if (rockerBackground instanceof ColorDrawable) {
                // 色值
                mRockerColor = ((ColorDrawable) rockerBackground).getColor();
                mRockerBackgroundMode = ROCKER_BACKGROUND_MODE_COLOR;
            } else {
                // 其他形式
                mRockerBackgroundMode = ROCKER_BACKGROUND_MODE_DEFAULT;
            }
        } else {
            // 没有设置摇杆背景
            mRockerBackgroundMode = ROCKER_BACKGROUND_MODE_DEFAULT;
        }

        // 摇杆半径
        mRockerScale = typedArray.getFloat(R.styleable.RockerView_rockerScale, DEFAULT_ROCKER_SCALE);
        //距离级别
        mDistanceLevel = typedArray.getInt(R.styleable.RockerView_rockerSpeedLevel, 10);
        //回调模式
        mCallBackMode = getCallBackMode(typedArray.getInt(R.styleable.RockerView_rockerCallBackMode, 0));
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measureWidth, measureHeight;
//        https://blog.csdn.net/iteye_3520/article/details/82579900

        /*
        * http://www.blogjava.net/liuyanbo/archive/2012/03/15/371969.html#:~:text=onMeasure%E4%BC%A0%E5%85%A5%E7%9A%84widthMeasureSpec%E5%92%8CheightMeasureSpec%E4%B8%8D%E6%98%AF%E4%B8%80%E8%88%AC%E7%9A%84%E5%B0%BA%E5%AF%B8%E6%95%B0%E5%80%BC%EF%BC%8C%E8%80%8C%E6%98%AF%E5%B0%86%E6%A8%A1%E5%BC%8F%E5%92%8C%E5%B0%BA%E5%AF%B8%E7%BB%84%E5%90%88%E5%9C%A8%E4%B8%80%E8%B5%B7%E7%9A%84%E6%95%B0%E5%80%BC%E3%80%82%20%E6%88%91%E4%BB%AC%E9%9C%80%E8%A6%81%E9%80%9A%E8%BF%87int%20mode%20%3D%20MeasureSpec.getMode,%28widthMeasureSpec%29%E5%BE%97%E5%88%B0%E6%A8%A1%E5%BC%8F%EF%BC%8C%E7%94%A8int%20size%20%3D%20MeasureSpec.
        * getSize%20%28widthMeasureSpec%29%E5%BE%97%E5%88%B0%E5%B0%BA%E5%AF%B8%E3%80%82
        * */

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode == MeasureSpec.EXACTLY) {
            // 具体的值和match_parent
            measureWidth = widthSize;
        } else {
            // wrap_content
            measureWidth = DEFAULT_SIZE;
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            measureHeight = heightSize;
        } else {
            measureHeight = DEFAULT_SIZE;
        }
        setMeasuredDimension(measureWidth, measureHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();

        int cx = measuredWidth / 2;
        int cy = measuredHeight / 2;

        // 中心点
        mCenterPoint.set(cx, cy);
        // 可移动区域的半径
        mAreaRadius = (measuredWidth <= measuredHeight) ? (int) (cx / (mRockerScale + 1)) : (int) (cy / (mRockerScale + 1));
        mRockerRadius = (int) (mAreaRadius * mRockerScale);
        // 摇杆位置
        if (0 == mRockerPosition.x || 0 == mRockerPosition.y) {
            mRockerPosition.set(mCenterPoint.x, mCenterPoint.y);
        }
/*
* https://blog.csdn.net/tianjian4592/article/details/45031663
* */
        // 画可移动区域
        if (AREA_BACKGROUND_MODE_PIC == mAreaBackgroundMode || AREA_BACKGROUND_MODE_XML == mAreaBackgroundMode) {
            // 图片
            Rect src = new Rect(0, 0, mAreaBitmap.getWidth(), mAreaBitmap.getHeight());
            Rect dst = new Rect(mCenterPoint.x - mAreaRadius, mCenterPoint.y - mAreaRadius, mCenterPoint.x + mAreaRadius, mCenterPoint.y + mAreaRadius);
            canvas.drawBitmap(mAreaBitmap, src, dst, mAreaBackgroundPaint);
        } else if (AREA_BACKGROUND_MODE_COLOR == mAreaBackgroundMode) {
            // 色值
            mAreaBackgroundPaint.setColor(mAreaColor);
            canvas.drawCircle(mCenterPoint.x, mCenterPoint.y, mAreaRadius, mAreaBackgroundPaint);
        } else {
            // 其他或者未设置
            mAreaBackgroundPaint.setColor(Color.GRAY);
            canvas.drawCircle(mCenterPoint.x, mCenterPoint.y, mAreaRadius, mAreaBackgroundPaint);
        }

        // 画摇杆
        if (ROCKER_BACKGROUND_MODE_PIC == mRockerBackgroundMode || ROCKER_BACKGROUND_MODE_XML == mRockerBackgroundMode) {
            // 图片
            Rect src = new Rect(0, 0, mRockerBitmap.getWidth(), mRockerBitmap.getHeight());
            Rect dst = new Rect(mRockerPosition.x - mRockerRadius, mRockerPosition.y - mRockerRadius, mRockerPosition.x + mRockerRadius, mRockerPosition.y + mRockerRadius);
            canvas.drawBitmap(mRockerBitmap, src, dst, mRockerPaint);
        } else if (ROCKER_BACKGROUND_MODE_COLOR == mRockerBackgroundMode) {
            // 色值
            mRockerPaint.setColor(mRockerColor);
            canvas.drawCircle(mRockerPosition.x, mRockerPosition.y, mRockerRadius, mRockerPaint);
        } else {
            // 其他或者未设置
            mRockerPaint.setColor(Color.RED);
            canvas.drawCircle(mRockerPosition.x, mRockerPosition.y, mRockerRadius, mRockerPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:// 按下
                // 回调 开始
                callBackStart();
            case MotionEvent.ACTION_MOVE:// 移动
                float moveX = event.getX();
                float moveY = event.getY();
                baseDistance = mAreaRadius + 2;
//                Log.e("baseDistance", baseDistance + "");
                mRockerPosition = getRockerPositionPoint(mCenterPoint, new Point((int) moveX, (int) moveY), mAreaRadius + mRockerRadius, mRockerRadius);

                if (flag_round_or_line == 100){
                    moveRocker(mRockerPosition.x, mCenterPoint.y);
                }
                else {
                    moveRocker(mRockerPosition.x, mRockerPosition.y);
                }
//                moveRocker(mRockerPosition.x, mRockerPosition.y);

                break;
            case MotionEvent.ACTION_UP:// 抬起
            case MotionEvent.ACTION_CANCEL:// 移出区域
                // 回调 结束
                callBackFinish();
                if (mOnShakeListener != null) {
                    mOnShakeListener.direction(Direction.DIRECTION_CENTER);
                }
                float upX = event.getX();
                float upY = event.getY();
                // 摇杆回到中心位置，使用最后一次刷新
                mRockerPosition.set(mCenterPoint.x, mCenterPoint.y);
                invalidate();
                break;
        }
        return true;
    }

    /**
     * 获取摇杆实际要显示的位置（点）
     *
     * @param centerPoint  中心点
     * @param touchPoint   触摸点
     * @param regionRadius 摇杆可活动区域半径
     * @param rockerRadius 摇杆半径
     * @return 摇杆实际显示的位置（点）
     */
    private Point getRockerPositionPoint(Point centerPoint, Point touchPoint, float regionRadius, float rockerRadius) {
        // 两点在X轴的距离
        float lenX = (float) (touchPoint.x - centerPoint.x);
        // 两点在Y轴距离
        float lenY = (float) (touchPoint.y - centerPoint.y);
        // 两点距离
        float lenXY = (float) Math.sqrt((double) (lenX * lenX + lenY * lenY));
        // 计算弧度
        double radian = Math.acos(lenX / lenXY) * (touchPoint.y < centerPoint.y ? -1 : 1);
        // 计算角度
        double angle = radian2Angle(radian);

        if (lenXY + rockerRadius <= regionRadius) { // 触摸位置在可活动范围内
            // 回调 返回参数
            callBack(angle, (int) lenXY);
            return touchPoint;
        } else { // 触摸位置在可活动范围以外
            // 计算要显示的位置
            int showPointX = (int) (centerPoint.x + (regionRadius - rockerRadius) * Math.cos(radian));
            int showPointY = (int) (centerPoint.y + (regionRadius - rockerRadius) * Math.sin(radian));

            callBack(angle, (int) Math.sqrt((showPointX - centerPoint.x) * (showPointX - centerPoint.x) + (showPointY - centerPoint.y) * (showPointY - centerPoint.y)));
            return new Point(showPointX, showPointY);
        }
    }

    /**
     * 移动摇杆到指定位置
     *
     * @param x x坐标
     * @param y y坐标
     */
    private void moveRocker(float x, float y) {
        mRockerPosition.set((int) x, (int) y);
        // 使用定时刷新机制，30ms间隔刷新UI
        startUIRefresh();
    }

    /**
     * 弧度转角度
     *
     * @param radian 弧度
     * @return 角度[0, 360)
     */
    private double radian2Angle(double radian) {
        double tmp = Math.round(radian / Math.PI * 180);
        return tmp >= 0 ? tmp : 360 + tmp;
    }

    /**
     * Drawable 转 Bitmap
     *
     * @param drawable Drawable
     * @return Bitmap
     */
    private Bitmap drawable2Bitmap(Drawable drawable) {
        // 取 drawable 的长宽
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        // 取 drawable 的颜色格式
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
        // 建立对应 bitmap
        Bitmap bitmap = Bitmap.createBitmap(width, height, config);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * 回调
     * 开始
     */
    private void callBackStart() {
        tempDirection = Direction.DIRECTION_CENTER;
        if (null != mOnAngleChangeListener) {
            mOnAngleChangeListener.onStart();
        }
//        if (null != mOnShakeListener) {
//            mOnShakeListener.onStart();
//        }
    }

    /**
     * 回调
     * 返回参数
     *
     * @param angle 摇动角度
     */
    private void callBack(double angle, float distance) {
//        Log.e("distance", distance + "");
        if (Math.abs(distance - lastDistance) >= (baseDistance / mDistanceLevel)) {
            lastDistance = distance;
            if (null != mOnDistanceLevelListener) {
                int level = (int) (distance / (baseDistance / mDistanceLevel));
                mOnDistanceLevelListener.onDistanceLevel(level);
            }
        }
        if (null != mOnAngleChangeListener) {
            mOnAngleChangeListener.angle(angle);
        }
        if (null != mOnShakeListener) {
            if (CallBackMode.CALL_BACK_MODE_MOVE == mCallBackMode) {
                switch (mDirectionMode) {
                    case DIRECTION_2_HORIZONTAL:// 左右方向
                        if (ANGLE_0 <= angle && ANGLE_HORIZONTAL_2D_OF_0P > angle || ANGLE_HORIZONTAL_2D_OF_1P <= angle && ANGLE_360 > angle) {
                            // 右
                            mOnShakeListener.direction(Direction.DIRECTION_RIGHT);
                        } else if (ANGLE_HORIZONTAL_2D_OF_0P <= angle && ANGLE_HORIZONTAL_2D_OF_1P > angle) {
                            // 左
                            mOnShakeListener.direction(Direction.DIRECTION_LEFT);
                        }
                        break;
                    case DIRECTION_2_VERTICAL:// 上下方向
                        if (ANGLE_VERTICAL_2D_OF_0P <= angle && ANGLE_VERTICAL_2D_OF_1P > angle) {
                            // 下
                            mOnShakeListener.direction(Direction.DIRECTION_DOWN);
                        } else if (ANGLE_VERTICAL_2D_OF_1P <= angle && ANGLE_360 > angle) {
                            // 上
                            mOnShakeListener.direction(Direction.DIRECTION_UP);
                        }
                        break;
                    case DIRECTION_4_ROTATE_0:// 四个方向
                        if (ANGLE_4D_OF_0P <= angle && ANGLE_4D_OF_1P > angle) {
                            // 右下
                            mOnShakeListener.direction(Direction.DIRECTION_DOWN_RIGHT);
                        } else if (ANGLE_4D_OF_1P <= angle && ANGLE_4D_OF_2P > angle) {
                            // 左下
                            mOnShakeListener.direction(Direction.DIRECTION_DOWN_LEFT);
                        } else if (ANGLE_4D_OF_2P <= angle && ANGLE_4D_OF_3P > angle) {
                            // 左上
                            mOnShakeListener.direction(Direction.DIRECTION_UP_LEFT);
                        } else if (ANGLE_4D_OF_3P <= angle && ANGLE_360 > angle) {
                            // 右上
                            mOnShakeListener.direction(Direction.DIRECTION_UP_RIGHT);
                        }
                        break;
                    case DIRECTION_4_ROTATE_45:// 四个方向 旋转45度
                        if (ANGLE_0 <= angle && ANGLE_ROTATE45_4D_OF_0P > angle || ANGLE_ROTATE45_4D_OF_3P <= angle && ANGLE_360 > angle) {
                            // 右
                            mOnShakeListener.direction(Direction.DIRECTION_RIGHT);
                        } else if (ANGLE_ROTATE45_4D_OF_0P <= angle && ANGLE_ROTATE45_4D_OF_1P > angle) {
                            // 下
                            mOnShakeListener.direction(Direction.DIRECTION_DOWN);
                        } else if (ANGLE_ROTATE45_4D_OF_1P <= angle && ANGLE_ROTATE45_4D_OF_2P > angle) {
                            // 左
                            mOnShakeListener.direction(Direction.DIRECTION_LEFT);
                        } else if (ANGLE_ROTATE45_4D_OF_2P <= angle && ANGLE_ROTATE45_4D_OF_3P > angle) {
                            // 上
                            mOnShakeListener.direction(Direction.DIRECTION_UP);
                        }
                        break;
                    case DIRECTION_8:// 八个方向
                        if (ANGLE_0 <= angle && ANGLE_8D_OF_0P > angle || ANGLE_8D_OF_7P <= angle && ANGLE_360 > angle) {
                            // 右
                            mOnShakeListener.direction(Direction.DIRECTION_RIGHT);
                        } else if (ANGLE_8D_OF_0P <= angle && ANGLE_8D_OF_1P > angle) {
                            // 右下
                            mOnShakeListener.direction(Direction.DIRECTION_DOWN_RIGHT);
                        } else if (ANGLE_8D_OF_1P <= angle && ANGLE_8D_OF_2P > angle) {
                            // 下
                            mOnShakeListener.direction(Direction.DIRECTION_DOWN);
                        } else if (ANGLE_8D_OF_2P <= angle && ANGLE_8D_OF_3P > angle) {
                            // 左下
                            mOnShakeListener.direction(Direction.DIRECTION_DOWN_LEFT);
                        } else if (ANGLE_8D_OF_3P <= angle && ANGLE_8D_OF_4P > angle) {
                            // 左
                            mOnShakeListener.direction(Direction.DIRECTION_LEFT);
                        } else if (ANGLE_8D_OF_4P <= angle && ANGLE_8D_OF_5P > angle) {
                            // 左上
                            mOnShakeListener.direction(Direction.DIRECTION_UP_LEFT);
                        } else if (ANGLE_8D_OF_5P <= angle && ANGLE_8D_OF_6P > angle) {
                            // 上
                            mOnShakeListener.direction(Direction.DIRECTION_UP);
                        } else if (ANGLE_8D_OF_6P <= angle && ANGLE_8D_OF_7P > angle) {
                            // 右上
                            mOnShakeListener.direction(Direction.DIRECTION_UP_RIGHT);
                        }
                        break;
                    default:
                        break;
                }
            } else if (CallBackMode.CALL_BACK_MODE_STATE_CHANGE == mCallBackMode) {
                switch (mDirectionMode) {
                    case DIRECTION_2_HORIZONTAL:// 左右方向
                        if ((ANGLE_0 <= angle && ANGLE_HORIZONTAL_2D_OF_0P > angle || ANGLE_HORIZONTAL_2D_OF_1P <= angle && ANGLE_360 > angle) && tempDirection != Direction.DIRECTION_RIGHT) {
                            // 右
                            tempDirection = Direction.DIRECTION_RIGHT;
                            mOnShakeListener.direction(Direction.DIRECTION_RIGHT);
                        } else if (ANGLE_HORIZONTAL_2D_OF_0P <= angle && ANGLE_HORIZONTAL_2D_OF_1P > angle && tempDirection != Direction.DIRECTION_LEFT) {
                            // 左
                            tempDirection = Direction.DIRECTION_LEFT;
                            mOnShakeListener.direction(Direction.DIRECTION_LEFT);
                        }
                        break;
                    case DIRECTION_2_VERTICAL:// 上下方向
                        if (ANGLE_VERTICAL_2D_OF_0P <= angle && ANGLE_VERTICAL_2D_OF_1P > angle && tempDirection != Direction.DIRECTION_DOWN) {
                            // 下
                            tempDirection = Direction.DIRECTION_DOWN;
                            mOnShakeListener.direction(Direction.DIRECTION_DOWN);
                        } else if (ANGLE_VERTICAL_2D_OF_1P <= angle && ANGLE_360 > angle && tempDirection != Direction.DIRECTION_UP) {
                            // 上
                            tempDirection = Direction.DIRECTION_UP;
                            mOnShakeListener.direction(Direction.DIRECTION_UP);
                        }
                        break;
                    case DIRECTION_4_ROTATE_0:// 四个方向
                        if (ANGLE_4D_OF_0P <= angle && ANGLE_4D_OF_1P > angle && tempDirection != Direction.DIRECTION_DOWN_RIGHT) {
                            // 右下
                            tempDirection = Direction.DIRECTION_DOWN_RIGHT;
                            mOnShakeListener.direction(Direction.DIRECTION_DOWN_RIGHT);
                        } else if (ANGLE_4D_OF_1P <= angle && ANGLE_4D_OF_2P > angle && tempDirection != Direction.DIRECTION_DOWN_LEFT) {
                            // 左下
                            tempDirection = Direction.DIRECTION_DOWN_LEFT;
                            mOnShakeListener.direction(Direction.DIRECTION_DOWN_LEFT);
                        } else if (ANGLE_4D_OF_2P <= angle && ANGLE_4D_OF_3P > angle && tempDirection != Direction.DIRECTION_UP_LEFT) {
                            // 左上
                            tempDirection = Direction.DIRECTION_UP_LEFT;
                            mOnShakeListener.direction(Direction.DIRECTION_UP_LEFT);
                        } else if (ANGLE_4D_OF_3P <= angle && ANGLE_360 > angle && tempDirection != Direction.DIRECTION_UP_RIGHT) {
                            // 右上
                            tempDirection = Direction.DIRECTION_UP_RIGHT;
                            mOnShakeListener.direction(Direction.DIRECTION_UP_RIGHT);
                        }
                        break;
                    case DIRECTION_4_ROTATE_45:// 四个方向 旋转45度
                        if ((ANGLE_0 <= angle && ANGLE_ROTATE45_4D_OF_0P > angle || ANGLE_ROTATE45_4D_OF_3P <= angle && ANGLE_360 > angle) && tempDirection != Direction.DIRECTION_RIGHT) {
                            // 右
                            tempDirection = Direction.DIRECTION_RIGHT;
                            mOnShakeListener.direction(Direction.DIRECTION_RIGHT);
                        } else if (ANGLE_ROTATE45_4D_OF_0P <= angle && ANGLE_ROTATE45_4D_OF_1P > angle && tempDirection != Direction.DIRECTION_DOWN) {
                            // 下
                            tempDirection = Direction.DIRECTION_DOWN;
                            mOnShakeListener.direction(Direction.DIRECTION_DOWN);
                        } else if (ANGLE_ROTATE45_4D_OF_1P <= angle && ANGLE_ROTATE45_4D_OF_2P > angle && tempDirection != Direction.DIRECTION_LEFT) {
                            // 左
                            tempDirection = Direction.DIRECTION_LEFT;
                            mOnShakeListener.direction(Direction.DIRECTION_LEFT);
                        } else if (ANGLE_ROTATE45_4D_OF_2P <= angle && ANGLE_ROTATE45_4D_OF_3P > angle && tempDirection != Direction.DIRECTION_UP) {
                            // 上
                            tempDirection = Direction.DIRECTION_UP;
                            mOnShakeListener.direction(Direction.DIRECTION_UP);
                        }
                        break;
                    case DIRECTION_8:// 八个方向
                        if ((ANGLE_0 <= angle && ANGLE_8D_OF_0P > angle || ANGLE_8D_OF_7P <= angle && ANGLE_360 > angle) && tempDirection != Direction.DIRECTION_RIGHT) {
                            // 右
                            tempDirection = Direction.DIRECTION_RIGHT;
                            mOnShakeListener.direction(Direction.DIRECTION_RIGHT);
                        } else if (ANGLE_8D_OF_0P <= angle && ANGLE_8D_OF_1P > angle && tempDirection != Direction.DIRECTION_DOWN_RIGHT) {
                            // 右下
                            tempDirection = Direction.DIRECTION_DOWN_RIGHT;
                            mOnShakeListener.direction(Direction.DIRECTION_DOWN_RIGHT);
                        } else if (ANGLE_8D_OF_1P <= angle && ANGLE_8D_OF_2P > angle && tempDirection != Direction.DIRECTION_DOWN) {
                            // 下
                            tempDirection = Direction.DIRECTION_DOWN;
                            mOnShakeListener.direction(Direction.DIRECTION_DOWN);
                        } else if (ANGLE_8D_OF_2P <= angle && ANGLE_8D_OF_3P > angle && tempDirection != Direction.DIRECTION_DOWN_LEFT) {
                            // 左下
                            tempDirection = Direction.DIRECTION_DOWN_LEFT;
                            mOnShakeListener.direction(Direction.DIRECTION_DOWN_LEFT);
                        } else if (ANGLE_8D_OF_3P <= angle && ANGLE_8D_OF_4P > angle && tempDirection != Direction.DIRECTION_LEFT) {
                            // 左
                            tempDirection = Direction.DIRECTION_LEFT;
                            mOnShakeListener.direction(Direction.DIRECTION_LEFT);
                        } else if (ANGLE_8D_OF_4P <= angle && ANGLE_8D_OF_5P > angle && tempDirection != Direction.DIRECTION_UP_LEFT) {
                            // 左上
                            tempDirection = Direction.DIRECTION_UP_LEFT;
                            mOnShakeListener.direction(Direction.DIRECTION_UP_LEFT);
                        } else if (ANGLE_8D_OF_5P <= angle && ANGLE_8D_OF_6P > angle && tempDirection != Direction.DIRECTION_UP) {
                            // 上
                            tempDirection = Direction.DIRECTION_UP;
                            mOnShakeListener.direction(Direction.DIRECTION_UP);
                        } else if (ANGLE_8D_OF_6P <= angle && ANGLE_8D_OF_7P > angle && tempDirection != Direction.DIRECTION_UP_RIGHT) {
                            // 右上
                            tempDirection = Direction.DIRECTION_UP_RIGHT;
                            mOnShakeListener.direction(Direction.DIRECTION_UP_RIGHT);
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    }

    /**
     * 回调
     * 结束
     */
    private void callBackFinish() {
        tempDirection = Direction.DIRECTION_CENTER;
        
        // 停止UI定时刷新，节省系统资源
        stopUIRefresh();
        
        if (null != mOnAngleChangeListener) {
            mOnAngleChangeListener.onFinish();
        }
        if (null != mOnShakeListener) {
            mOnShakeListener.onFinish();
        }
    }

    /**
     * 回调模式
     */
    public enum CallBackMode {
        // 有移动就立刻回调
        CALL_BACK_MODE_MOVE,
        // 只有状态变化的时候才回调
        CALL_BACK_MODE_STATE_CHANGE,
        //只有状态变化或者距离变化的时候才回调
        CALL_BACK_MODE_STATE_DISTANCE_CHANGE
    }
    public void setCallBackMode(CallBackMode mode) {
        mCallBackMode = mode;
    }
    public enum DirectionMode {
        DIRECTION_2_HORIZONTAL,// 横向 左右两个方向
        DIRECTION_2_VERTICAL, // 纵向 上下两个方向
        DIRECTION_4_ROTATE_0, // 四个方向
        DIRECTION_4_ROTATE_45, // 四个方向 旋转45度
        DIRECTION_8 // 八个方向
    }
    public enum Direction {
        DIRECTION_LEFT, // 左
        DIRECTION_RIGHT, // 右
        DIRECTION_UP, // 上
        DIRECTION_DOWN, // 下
        DIRECTION_UP_LEFT, // 左上
        DIRECTION_UP_RIGHT, // 右上
        DIRECTION_DOWN_LEFT, // 左下
        DIRECTION_DOWN_RIGHT, // 右下
        DIRECTION_CENTER // 中间
    }

    /**
     * 添加摇杆摇动角度的监听
     *
     * @param listener 回调接口
     */
    public void setOnAngleChangeListener(OnAngleChangeListener listener) {
        mOnAngleChangeListener = listener;
    }
    public void setOnShakeListener(DirectionMode directionMode, OnShakeListener listener) {
        mDirectionMode = directionMode;
        mOnShakeListener = listener;
    }
    public void setOnDistanceLevelListener(OnDistanceLevelListener listener) {
        mOnDistanceLevelListener = listener;
    }
    public interface OnShakeListener {
        // 开始
        void onStart();

        /**
         * 摇动方向
         *
         * @param direction 方向
         */
        void direction(Direction direction);

        // 结束
        void onFinish();
    }
    public interface OnAngleChangeListener {
        // 开始
        void onStart();

        /**
         * 摇杆角度变化
         *
         * @param angle 角度[0,360)
         */
        void angle(double angle);

        // 结束
        void onFinish();
    }
    public interface OnDistanceLevelListener {

        void onDistanceLevel(int level);
    }

    private CallBackMode getCallBackMode(int mode) {
        switch (mode) {
            case 0:
                return CallBackMode.CALL_BACK_MODE_MOVE;
            case 1:
                return CallBackMode.CALL_BACK_MODE_STATE_CHANGE;
        }
        return mCallBackMode;
    }

    public void f1_test(int test_value){
        flag_round_or_line = test_value;
    }
    
    /**
     * 初始化UI刷新机制
     * 硬编码刷新间隔为30ms，提供流畅的摇杆UI更新体验
     */
    private void initUIRefresh() {
        // 创建主线程Handler用于UI更新
        mUIHandler = new Handler(Looper.getMainLooper());
        
        // 创建刷新任务Runnable
        mRefreshRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    // 执行UI刷新
                    invalidate();
                    
                    // 如果仍在刷新状态，继续下一次刷新
                    if (mIsRefreshing) {
                        mUIHandler.postDelayed(this, REFRESH_INTERVAL_MS);
                    }
                } catch (Exception e) {
                    // 异常处理，停止刷新
                    mIsRefreshing = false;
                    android.util.Log.e("MyRockerView", "UI刷新异常: " + e.getMessage(), e);
                }
            }
        };
    }
    
    /**
     * 开始UI定时刷新
     * 使用30ms间隔进行UI更新，确保摇杆移动的流畅性
     */
    private void startUIRefresh() {
        try {
            // 如果已经在刷新，先停止当前刷新
            if (mIsRefreshing) {
                stopUIRefresh();
            }
            
            // 设置刷新状态
            mIsRefreshing = true;
            
            // 立即执行一次刷新
            invalidate();
            
            // 启动定时刷新任务
            if (mUIHandler != null && mRefreshRunnable != null) {
                mUIHandler.postDelayed(mRefreshRunnable, REFRESH_INTERVAL_MS);
            }
        } catch (Exception e) {
            android.util.Log.e("MyRockerView", "启动UI刷新异常: " + e.getMessage(), e);
            mIsRefreshing = false;
        }
    }
    
    /**
     * 停止UI定时刷新
     * 在摇杆操作结束时调用，节省系统资源
     */
    private void stopUIRefresh() {
        try {
            mIsRefreshing = false;
            
            // 移除待执行的刷新任务
            if (mUIHandler != null && mRefreshRunnable != null) {
                mUIHandler.removeCallbacks(mRefreshRunnable);
            }
            
            // 执行最后一次刷新确保UI状态正确
            invalidate();
        } catch (Exception e) {
             android.util.Log.e("MyRockerView", "停止UI刷新异常: " + e.getMessage(), e);
         }
     }
     
     @Override
     protected void onDetachedFromWindow() {
         super.onDetachedFromWindow();
         // View被移除时清理资源，停止UI刷新
         try {
             stopUIRefresh();
             if (mUIHandler != null) {
                 mUIHandler.removeCallbacksAndMessages(null);
                 mUIHandler = null;
             }
             mRefreshRunnable = null;
         } catch (Exception e) {
             android.util.Log.e("MyRockerView", "清理资源异常: " + e.getMessage(), e);
         }
     }

}