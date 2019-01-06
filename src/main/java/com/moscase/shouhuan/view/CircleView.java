package com.moscase.shouhuan.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DrawFilter;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.moscase.shouhuan.R;

/**
 * Created by 陈航 on 2017/7/25.
 *
 * 我挥舞着键盘和本子，发誓要把世界写个明明白白
 */

public class CircleView extends View {



    /**
     * 圆环半径 根据view的宽度计算
     * */
    private int mRadius = 200;

    /**
     * 圆环的中心点 -- 画圆环和旋转画布时需要使用
     * */
    private int x, y;

    /**
     * 圆环使用
     * */
    private Paint mRingPaint;
    private Paint mCirclePaint;

    /**
     * 圆环动画使用 -- 与mRingPaint唯一不同得方在于颜色
     * */
    private Paint mRingAnimPaint;
    private Paint mCircleAnimPaint;


    private Context mContext;

    /**
     * 圆环 宽度
     * */
    private int mHeartPaintWidth = 50;

    /**
     * 圆环动画开始时 画弧的偏移量
     * */
    private int mAnimAngle = -1;



    /**
     * canvas抗锯齿开启需要
     * */
    private DrawFilter mDrawFilter;


    private Paint mLinePaint;

    private int mNeedToShow = 300;

    private void init() {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        mRingPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setColor(mContext.getResources().getColor(R.color.black));
        mLinePaint.setStyle(Paint.Style.FILL);
        if (!isInEditMode()) {
            // 造成错误的代码段
            mRingPaint.setColor(mContext.getResources().getColor(R.color.heart_default));
            mCirclePaint.setColor(Color.BLACK);
        }
        mRingPaint.setStrokeWidth(mHeartPaintWidth);
        mRingPaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setStyle(Paint.Style.FILL);
        mRingAnimPaint = new Paint(mRingPaint);
        mCircleAnimPaint = new Paint(mCirclePaint);
        mCircleAnimPaint.setColor(mContext.getResources().getColor(R.color.heart_default));
        mRingAnimPaint.setColor(mContext.getResources().getColor(R.color.heart_default));

    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mHeartPaintWidth = getMeasuredHeight() / 10;
        x = getMeasuredWidth() / 2;
        y = (int) (getMeasuredHeight() / 2.25f);
        mRadius = w / 3 - mHeartPaintWidth / 2; //因为制定了Paint的宽度，因此计算半径需要减去这个
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawLine(x,y,mRadius,y,mLinePaint);
        canvas.drawLine(x,y,mRadius + getMeasuredWidth()/2.5f,y,mLinePaint);
        canvas.drawLine(x,y,x,getMeasuredHeight()/1.5f,mLinePaint);
        canvas.setDrawFilter(mDrawFilter);//在canvas上抗锯齿
//        canvas.rotate((float) 44.5,x,y);
        canvas.rotate(0.3f,x,y);
        for (int i = 0; i < 360;i+=3){
            canvas.drawCircle(getMeasuredWidth()/2, (int) (getMeasuredHeight() / 2.25f)-mRadius-mHeartPaintWidth/1.7f, getMeasuredWidth()/240, mCirclePaint);
            canvas.rotate(3f,x,y);
        }
        if (mAnimAngle != -1) {// 如果开启了动画
            for (int i = 0; i < mAnimAngle; i += 3) {
                canvas.drawCircle(getMeasuredWidth()/2, getMeasuredHeight()/6.5f, getMeasuredWidth()/240, mCircleAnimPaint);
                canvas.rotate(3f,x,y);
            }
        }
    }

    public void setAngel(double baifenbi){
        mNeedToShow = (int) (360 * baifenbi);
        mAnimAngle = -1;
        postInvalidate();
        startAnim();

    }


	/*---------------------------------动画-----------------------------------------*/
    /**
     * 开启圆环动画
     * */
    private void startRingAnim() {
        mAnimAngle = 0;
        new Thread(new Runnable() {

            @Override
            public void run() {
                while (mAnimAngle < mNeedToShow) {
                    mAnimAngle++;
//                    try {
//                        Thread.sleep(10);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
                    postInvalidate();
                }
            }
        }).start();
    }

    public void startAnim(){
        startRingAnim();
    }

	/*---------------------------------动画  end------------------------------------*/

    public CircleView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public CircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public CircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

}
