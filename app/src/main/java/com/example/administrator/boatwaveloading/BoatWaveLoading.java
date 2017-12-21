package com.example.administrator.boatwaveloading;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * 小船飘动
 * Created by lishaojie on 2017/12/19.
 */

public class BoatWaveLoading extends View {
    private Paint mPaint;
    private Bitmap mBoat;
    private int mWidth;
    private int mHeight;
    private Path mPath;
    private int mLineWidth = 8;
    //初相角
    private float mAngle;
    /**
     * 波浪的高度
     */
    private int mWaveHeight = 25;
    /**
     * 波浪的真实高度
     */
    private int mWaveRealHeight;

    /**
     * 小船画笔
     */
    Paint mPaintBoat = new Paint();
    /**
     * 小船开始的位置
     */
    private int mBoatStartX;
    private Matrix mMatrix;

    /**
     * 中间控制小船上下俯仰的宽度
     */
    private int mThresholdWidth = 5;

    /**
     * 上下的角度
     */

    private float mUpDownAngle = 1f;

    private boolean mIsRun = true;
    /**
     * 控制移动的速度,越大越快
     */
    private float mSpeed = (float) (Math.PI / 24);
    /**
     * 控制波浪的密集度，控制移动的速度,越小越密集
     */
    private float mDense = 60;

    /**
     *波浪的颜色
     */
    private @ColorInt int mWaveColor = 0xff61ABFF;

    public BoatWaveLoading(Context context) {
        this(context, null);
    }

    public BoatWaveLoading(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BoatWaveLoading(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(Context context, @Nullable AttributeSet attrs) {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mLineWidth);
        mPaint.setColor(mWaveColor);

        mPaintBoat = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintBoat.setStyle(Paint.Style.FILL);
        mPaintBoat.setDither(true);//设定是否使用图像抖动处理，会使绘制出来的图片颜色更加平滑和饱满，图像更加清晰
        mPaintBoat.setFilterBitmap(true);//加快显示速度，本设置项依赖于dither和xfermode的设置

        mBoat = BitmapFactory.decodeResource(getResources(), R.mipmap.boat);

        mPath = new Path();

        mMatrix = new Matrix();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 获得它的父容器为它设置的测量模式和大小
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);
        if (modeWidth == MeasureSpec.AT_MOST) {
            sizeWidth = 300;
        }
        if (modeHeight == MeasureSpec.AT_MOST) {
            sizeHeight = mWaveHeight + mBoat.getHeight();
        }
        mWidth = sizeWidth;
        mHeight = sizeHeight;
        mBoatStartX = (mWidth - mBoat.getWidth()) / 2;
        mWaveRealHeight = mWaveHeight / 2 - mLineWidth / 2;
        setMeasuredDimension(sizeWidth, sizeHeight);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!mIsRun) {
            return;
        }
        /**
         * 小船画在中间的位置
         */
        for (int i = 0; i < mWidth; i++) {
            if (i == 0) {
                mPath.moveTo(0, mWaveRealHeight * (float) Math.sin(mAngle));
            } else {
                float y = mWaveRealHeight * (float) Math.sin(i * (2 * Math.PI / mDense) + mAngle);
                /**
                 * 取中间一段取的y值，控制小船俯仰以及上下浮动
                 */
                if (i == mWidth / 2) {
                    //中间点相邻点用于判断小船是网上还是往下还是保持平衡
                    float deltaY5 = mWaveRealHeight * (float) Math.sin((i + mThresholdWidth) * (2 * Math.PI / mDense) + mAngle);
                    mMatrix.postTranslate(mBoatStartX, -y/2 + mWaveRealHeight);
                    if (deltaY5 > y) {
                        mMatrix.postRotate(mUpDownAngle, mWidth / 2, mBoat.getHeight());
                    } else {
                        mMatrix.postRotate(-mUpDownAngle, mWidth / 2, mBoat.getHeight());
                    }
                    canvas.drawBitmap(mBoat, mMatrix, mPaintBoat);
                    mMatrix.reset();

                }

                mPath.lineTo(i, y);
            }
        }
        canvas.translate(0, mBoat.getHeight() + mWaveRealHeight);
        canvas.drawPath(mPath, mPaint);
        mAngle += mSpeed;
        mPath.reset();
        invalidate();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mIsRun = false;
    }
}
