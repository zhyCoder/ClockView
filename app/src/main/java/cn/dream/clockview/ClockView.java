package cn.dream.clockview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import java.util.Calendar;

public class ClockView extends View {

    private final String TAG = "ClockView";

    private Paint mPaintCircle;
    private Paint mPaintDegree;

    private Paint mPaintSecond;
    private Paint mPaintMinute;
    private Paint mPaintHour;

    private int mWidth;
    private int mHeight;
    private float mCircleStrokeWidth;
    private float mRadius;

    // 时分秒针的转角
    private float mSecondAngle;
    private float mMinuteAngle;
    private float mHourAngle;

    // 时分秒针的路径
    private Path mSecondPath;
    private Path mMinutePath;
    private Path mHourPath;

    public ClockView(Context context) {
        super(context);
        init();
    }

    public ClockView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        // 圆画笔初始化
        mPaintCircle = new Paint();
        mPaintCircle.setStyle(Paint.Style.STROKE);
        mPaintCircle.setAntiAlias(true);
        mCircleStrokeWidth = 5.f;
        mPaintCircle.setStrokeWidth(mCircleStrokeWidth);

        // 角度线画笔初始化
        mPaintDegree = new Paint();
        mPaintDegree.setStyle(Paint.Style.STROKE);
        mPaintDegree.setAntiAlias(true);

        // 时分秒针画笔初始化
        mPaintSecond = new Paint();
        mPaintSecond.setStyle(Paint.Style.STROKE);
        mPaintSecond.setAntiAlias(true);
        mPaintSecond.setStrokeWidth(3);
        mPaintSecond.setColor(Color.RED);
        mPaintSecond.setDither(true);
        mPaintSecond.setStrokeJoin(Paint.Join.ROUND);
        mPaintSecond.setStrokeCap(Paint.Cap.ROUND);

        mPaintMinute = new Paint();
        mPaintMinute.setStyle(Paint.Style.STROKE);
        mPaintMinute.setAntiAlias(true);
        mPaintMinute.setStrokeWidth(3);
        mPaintMinute.setColor(Color.BLACK);
        mPaintMinute.setDither(true);
        mPaintMinute.setStrokeJoin(Paint.Join.ROUND);
        mPaintMinute.setStrokeCap(Paint.Cap.ROUND);

        mPaintHour = new Paint();
        mPaintHour.setStyle(Paint.Style.STROKE);
        mPaintHour.setAntiAlias(true);
        mPaintHour.setStrokeWidth(5);
        mPaintHour.setColor(Color.BLACK);
        mPaintHour.setDither(true);
        mPaintHour.setStrokeJoin(Paint.Join.ROUND);
        mPaintHour.setStrokeCap(Paint.Cap.ROUND);

        // 时分秒针路径初始化
        mSecondPath = new Path();
        mMinutePath = new Path();
        mHourPath = new Path();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 获取测量规格对应的测量大小
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        // 默认宽度
        int defaultWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300, getResources().getDisplayMetrics());
        int defaultHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300, getResources().getDisplayMetrics());

        if (getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT && getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(defaultWidth, defaultHeight);
        } else if (getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(defaultWidth, heightSize);
        } else if (getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(widthSize, defaultHeight);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            mWidth = getWidth();
            mHeight = getHeight();
            mRadius = mWidth / 2.f - mCircleStrokeWidth;
            Log.d(TAG, "width=" + mWidth + ",height=" + mHeight + ",radius=" + mRadius);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float sx = mWidth / 2.f;
        float sy = mHeight / 2.f - mRadius;
        float ex = mWidth / 2.f;
        float ey;
        canvas.drawCircle(sx, mHeight / 2.f, mRadius, mPaintCircle);
        for (int i = 0; i < 60; i++) {
            // 有标数字1 - 12的情况;
            if (i % 5 == 0) {
                mPaintDegree.setStrokeWidth(5);
                mPaintDegree.setTextSize(30);
                ey = sy + 60;
                canvas.drawLine(sx, sy, ex, ey, mPaintDegree);
                // 相应的刻度值
                String num = String.valueOf(i / 5);
                if (num.equals("0")) {
                    num = "12";
                }
                float degrees = i * 6;
                canvas.rotate(-degrees, sx, mHeight / 2.f);
                double angle = (degrees * Math.PI) / 180;
                float cosv = (float) Math.cos(angle);
                float sinv = (float) Math.sin(angle);
                float x = (mRadius - 90) * sinv + sx - mPaintDegree.measureText(num) / 2;
                // +10这个偏移量。
                float y = (90 - mRadius) * cosv + mHeight / 2 + 10;
                // canvas.drawText(num, sx - mPaintDegree.measureText(num) / 2, sy + 90, mPaintDegree);
                canvas.drawText(num, x, y, mPaintDegree);
                canvas.rotate(degrees, sx, mHeight / 2.f);
            } else {
                mPaintDegree.setStrokeWidth(3);
                mPaintDegree.setTextSize(15);
                ey = sy + 30;
                canvas.drawLine(sx, sy, ex, ey, mPaintDegree);
            }
            canvas.rotate(6, sx, mHeight / 2.f);
        }
        // 初始化当前时间
        initCurrentTime();
        // 画秒针
        drawSecondHand(canvas);
        // 画分针
        drawMinuteHand(canvas);
        // 画时针
        drawHourHand(canvas);
        invalidate();
    }

    private void initCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        float milliSecond = calendar.get(Calendar.MILLISECOND);
        // 精确到小数点后 保证圆滑
        float second = calendar.get(Calendar.SECOND) + milliSecond / 1000;
        float minute = calendar.get(Calendar.MINUTE) + second / 60;
        float hour = calendar.get(Calendar.HOUR) + minute / 60;

        mSecondAngle = second / 60 * 360;
        mMinuteAngle = minute / 60 * 360;
        mHourAngle = hour / 12 * 360;
    }

    private void drawSecondHand(Canvas canvas) {
        canvas.save();
        canvas.rotate(mSecondAngle, mWidth / 2.f, mHeight / 2.f);
        mSecondPath.reset();
        mSecondPath.moveTo(mWidth / 2.f, mHeight / 2.f);
        mSecondPath.lineTo(mWidth / 2.f, mHeight / 2.f - mRadius + mCircleStrokeWidth);
        canvas.drawPath(mSecondPath, mPaintSecond);
        canvas.restore();
    }

    private void drawMinuteHand(Canvas canvas) {
        canvas.save();
        canvas.rotate(mMinuteAngle, mWidth / 2.f, mHeight / 2.f);
        mMinutePath.reset();
        mMinutePath.moveTo(mWidth / 2.f, mHeight / 2.f);
        mMinutePath.lineTo(mWidth / 2.f, mHeight / 2.f - mRadius + 60);
        canvas.drawPath(mMinutePath, mPaintMinute);
        canvas.restore();
    }

    private void drawHourHand(Canvas canvas) {
        canvas.save();
        canvas.rotate(mHourAngle, mWidth / 2.f, mHeight / 2.f);
        mHourPath.reset();
        mHourPath.moveTo(mWidth / 2.f, mHeight / 2.f);
        mHourPath.lineTo(mWidth / 2.f, mHeight / 2.f - mRadius + 90);
        canvas.drawPath(mHourPath, mPaintHour);
        canvas.restore();
    }
}