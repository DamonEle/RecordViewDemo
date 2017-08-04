package com.damon43.recordviewdemo.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.damon43.recordviewdemo.ImageTools;
import com.damon43.recordviewdemo.R;

/**
 * 录音自定义view
 */

public class RecordView extends View {

    private static final float DEFAULT_WIDTH = 380;
    private static final float DEFAULT_HEIGHT = 380;
    private static final String TAG = "Recordviewdemo";
    /*动画更新时间*/
    private static final long SPREAD_SIZE_UPDATE_TIME = 50;
    /*每次扩散更新的大小*/
    private static final float SPREAD_UPDATE_SIZE = 2;
    private int mWidth;
    private int mHeight;
    private Handler mHandler;
    /*背景画笔*/
    private Paint mRecordViewBgPaint;
    private Paint mRecordViewSpreadPaint;
    /*前景画笔*/
    private Paint mRecordViewFgPaint;

    private Context mContext;
    /*当前扩散值*/
    private float spreadSize;
    private float mRadius;
    private Drawable bg;
    private Bitmap bitmapBg;
    private int bgMinimumWidht;
    private int bgMinimumHeight;
    /*扩散圆与圆之间的间隔*/
    private int spreadInterval;
    /*定时刷新任务*/
    private Runnable refresh;
    /*扩散的最大值*/
    private int spreadMax;
    private int centerX;
    private int centerY;
    /*扩散到最大值需要的次数*/
    private int spreadMaxCount;

    public RecordView(Context context) {
        this(context, null);
    }

    public RecordView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecordView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initView();
    }

    private void initView() {
        mHandler = new Handler();
        refresh = new Runnable() {
            @Override
            public void run() {
                spreadSize = spreadSize >= mRadius + (spreadMaxCount*SPREAD_UPDATE_SIZE*2 ) ?
                        spreadSize - (spreadMaxCount * SPREAD_UPDATE_SIZE) + SPREAD_UPDATE_SIZE: spreadSize + SPREAD_UPDATE_SIZE;
                invalidate();
                mHandler.postDelayed(refresh, SPREAD_SIZE_UPDATE_TIME);
            }
        };
        bg = mContext.getResources().getDrawable(R.mipmap.ic_microphone_style_1);
        bitmapBg = ImageTools.drawableToBitmap(bg);
        mRecordViewBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRecordViewBgPaint.setColor(mContext.getResources().getColor(R.color.colorPrimary));
        mRecordViewBgPaint.setAlpha(100);
        mRecordViewSpreadPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRecordViewSpreadPaint.setStyle(Paint.Style.FILL);
        mRecordViewSpreadPaint.setColor(mContext.getResources().getColor(R.color.colorPrimary));
        mRecordViewBgPaint.setAlpha(100);
        mRecordViewFgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heigetSize = MeasureSpec.getSize(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heigthMode = MeasureSpec.getMode(heightMeasureSpec);

        if (widthMode == MeasureSpec.AT_MOST) {
            mWidth = (int) (widthSize > DEFAULT_WIDTH ? DEFAULT_WIDTH : widthSize);
        } else if (widthMode == MeasureSpec.EXACTLY) {
            mWidth = widthSize;
        }
        if (heigthMode == MeasureSpec.AT_MOST) {
            mHeight = (int) (heigetSize > DEFAULT_HEIGHT ? DEFAULT_HEIGHT : heigetSize);
        } else if (heigthMode == MeasureSpec.EXACTLY) {
            mHeight = heigetSize;
        }
        //初始化关键数值
        mRadius = mWidth / 3;
        spreadSize = mRadius;
        spreadMax = mWidth / 6;
        spreadInterval = mWidth / 12;
        centerX = mWidth / 2;
        centerY = mHeight / 2;
        spreadMaxCount = (int) (spreadMax / SPREAD_UPDATE_SIZE) + 1;
        bgMinimumWidht = centerX - bg.getMinimumWidth() / 2;
        bgMinimumHeight = centerY - bg.getMinimumHeight() / 2;

        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float currentChange = spreadSize - mRadius;
        //计算当前扩散值对应的透明度
        int theAlpha = (int) (255 * (1 - (currentChange / (spreadMax))));
        if (theAlpha < 0) {
            theAlpha = 0;
        }
        mRecordViewSpreadPaint.setAlpha(theAlpha);
        canvas.drawCircle(centerX, centerY, spreadSize, mRecordViewSpreadPaint);
        //循环绘制当前扩散值内的圆
        while (currentChange > spreadInterval) {
            float otherC = currentChange - spreadInterval;
            //计算该圆扩散值对应的透明度
            int alpha = (int) (255 * (1 - (otherC / (spreadMax))));
            if (alpha < 0) {
                alpha = 0;
            }
            mRecordViewSpreadPaint.setAlpha(alpha);
            canvas.drawCircle(centerX, centerY, mRadius + otherC, mRecordViewSpreadPaint);
            currentChange -= spreadInterval;
        }
        canvas.drawCircle(centerX, centerY, mRadius, mRecordViewBgPaint);
        canvas.drawBitmap(bitmapBg, bgMinimumWidht, bgMinimumHeight, mRecordViewFgPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int type = event.getAction();
        float ly = event.getY();
        float lx = event.getX();
        switch (type) {
            case MotionEvent.ACTION_DOWN:
                readyToRecord();
                return true;
            case MotionEvent.ACTION_UP:
//                endRecording(ly);
                break;
            case MotionEvent.ACTION_CANCEL:
                cancelRecording();
                break;
            case MotionEvent.ACTION_MOVE:
                moveToCancleRecord(lx, ly);
                break;
            default:
                break;
        }
        return false;
    }

    private void moveToCancleRecord(float lx, float ly) {
        Log.d(TAG, "moveToCancleRecord");
    }

    private void cancelRecording() {
        Log.d(TAG, "cancelRecording");
    }

    private void endRecording(float ly) {
        Log.d(TAG, "endRecording");
        mHandler.removeCallbacks(refresh);
        spreadSize = mRadius;
        invalidate();
    }

    /**
     * 开始扩散
     */
    private void readyToRecord() {
        Log.d(TAG, "readyToRecord");
        spreadSize = mRadius;
        mHandler.postDelayed(refresh, SPREAD_SIZE_UPDATE_TIME);
    }

}
