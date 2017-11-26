package com.moscase.shouhuan.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DrawFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.moscase.shouhuan.R;

/**
 * Created by 陈航 on 2017/7/30.
 * <p>
 * 我挥舞着键盘和本子，发誓要把世界写个明明白白
 */
public class RingView extends View {

    /**
     * 心跳线的总宽度 -- 圆环的宽度
     */
    private int mHeartBeatWidth;

    /**
     * 圆环半径 根据view的宽度计算
     */
    private int mRadius = 200;

    /**
     * 圆环的中心点 -- 画圆环和旋转画布时需要使用
     */
    private int x, y;

    /**
     * 圆环使用
     */
    private Paint mRingPaint;
    private Paint mCirclePaint;

    /**
     * 圆环动画使用 -- 与mRingPaint唯一不同得方在于颜色
     */
    private Paint mRingAnimPaint;
    private Paint mCircleAnimPaint;

    /**
     * 圆环大小 矩形
     */
    private RectF mRectf;

    private Context mContext;

    /**
     * 圆环 宽度
     */
    private int mHeartPaintWidth;

    /**
     * 圆环动画开始时 画弧的偏移量
     */
    private int mAnimAngle = -1;


    /**
     * 期初的偏移量
     */
    private final int OFFSET_Y = 0;

    /**
     * canvas抗锯齿开启需要
     */
    private DrawFilter mDrawFilter;

    private Paint mLinePaint;

    Path path = new Path();

    private boolean isAnimRunning;

    private int mNeedToShow = 300;

    private void init() {

        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        mRingPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        if (!isInEditMode()) {
            mRingPaint.setColor(mContext.getResources().getColor(R.color.black));
            mCirclePaint.setColor(mContext.getResources().getColor(R.color.heart_default));
            mLinePaint.setColor(mContext.getResources().getColor(R.color.white));
        }

        mRingPaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setStyle(Paint.Style.FILL);
        mLinePaint.setStyle(Paint.Style.FILL);
        mRingAnimPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRingAnimPaint.setColor(mContext.getResources().getColor(R.color.heart_default));
        mRingAnimPaint.setStyle(Paint.Style.STROKE);
        mCircleAnimPaint = new Paint(mCirclePaint);
        mCircleAnimPaint.setColor(Color.WHITE);


    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mHeartBeatWidth = w - mHeartPaintWidth * 2 - 40; //内圆宽度
        mHeartPaintWidth = getMeasuredHeight() / 10;
        mRingPaint.setStrokeWidth(mHeartPaintWidth);
        mRingAnimPaint.setStrokeWidth(mHeartPaintWidth);
        x = getMeasuredWidth() / 2;
        y = (int) (getMeasuredHeight() / 2.25f);
        mRadius = w / 3 - mHeartPaintWidth / 2; //因为制定了Paint的宽度，因此计算半径需要减去这个
        mRectf = new RectF(x - mRadius, y - mRadius, x + mRadius, y + mRadius);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.setDrawFilter(mDrawFilter);//在canvas上抗锯齿

        //由于drawArc默认从x轴开始画，因此需要将画布旋转或者绘制角度旋转，2种方案
        //int level = canvas.save();
        //canvas.rotate(-90, x, y);// 旋转的时候一定要指明中心
        for (int i = -90; i < 270; i += 3) {
            canvas.drawArc(mRectf, i, 1, false, mRingPaint);
        }

        if (mAnimAngle != -1) {// 如果开启了动画
            for (int i = -90; i < mAnimAngle - 90; i += 3) {
                canvas.drawArc(mRectf, i, 1, false, mRingAnimPaint);
            }
        }
    }


	/*---------------------------------动画-----------------------------------------*/

    /**
     * 开启圆环动画
     */
    private void startRingAnim() {
        mAnimAngle = 0;
        new Thread(new Runnable() {

            @Override
            public void run() {
                isAnimRunning = true;
                while (mAnimAngle < mNeedToShow) {
                    mAnimAngle++;
//                    try {
//                        Thread.sleep(10);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
                    postInvalidate();
                }
                isAnimRunning = false;
//				mAnimAngle = -1;// 停止动画
            }
        }).start();
    }

    public boolean getIsAnimRunning() {
        return isAnimRunning;
    }

    public void startAnim() {
        startRingAnim();
    }

    public void setAngel(double baifenbi) {
        mNeedToShow = (int) (360 * baifenbi);
        Log.d("koma---百分比是", mNeedToShow + "");

        mAnimAngle = -1;
        postInvalidate();
        startAnim();

    }

	/*---------------------------------动画  end------------------------------------*/

    /*---------------------------------构造函数-----------------------------------*/
    public RingView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public RingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public RingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

}



