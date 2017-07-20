package com.moscase.shouhuan.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DrawFilter;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.moscase.shouhuan.R;

/**
 * Created by Administrator on 2017/7/20.
 */

public class CircleView extends View {

    /**
     * 整个View的宽高
     * */
    private int mTotalHeight, mTotalWidth;

    /**
     * 心跳线的总宽度 -- 圆环的宽度
     * */
    private int mHeartBeatWidth;

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

    /**
     * 圆环大小 矩形
     * */
    private RectF mRectf;

    private Context mContext;

    /**
     * 圆环 宽度
     * */
    private final int mHeartPaintWidth = 50;

    /**
     * 圆环动画开始时 画弧的偏移量
     * */
    private int mAnimAngle = -1;


    /**
     * 期初的偏移量
     * */
    private final int OFFSET_Y = 0;

    /**
     * canvas抗锯齿开启需要
     * */
    private DrawFilter mDrawFilter;

    Path path = new Path();

    private int width;
    private int height;

    private void init() {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        mRingPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        if (!isInEditMode()) {
            // 造成错误的代码段
            mRingPaint.setColor(mContext.getResources().getColor(R.color.heart_default));
            mCirclePaint.setColor(mContext.getResources().getColor(R.color.heart_default));
        }
        mRingPaint.setStrokeWidth(mHeartPaintWidth);
        mRingPaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setStyle(Paint.Style.FILL);
        mRingAnimPaint = new Paint(mRingPaint);
        mCircleAnimPaint = new Paint(mCirclePaint);
        mCircleAnimPaint.setColor(Color.WHITE);
        mRingAnimPaint.setColor(Color.WHITE);


        //初始化心跳曲线
        mDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mTotalHeight = h;
        mTotalWidth = w;
        mHeartBeatWidth = w - mHeartPaintWidth*2-40; //内圆宽度
        x = getMeasuredWidth() / 2;
        y = (int) (getMeasuredHeight() / 2.25f);
        width =w;
        height =h;
        mRadius = w / 3 - mHeartPaintWidth / 2; //因为制定了Paint的宽度，因此计算半径需要减去这个
        mRectf = new RectF(x - mRadius, y - mRadius, x + mRadius, y + mRadius);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.setDrawFilter(mDrawFilter);//在canvas上抗锯齿
//        canvas.rotate((float) 44.5,x,y);
        //由于drawArc默认从x轴开始画，因此需要将画布旋转或者绘制角度旋转，2种方案
        canvas.rotate(0.3f,x,y);
        for (int i = 0; i < 360;i+=3){
            canvas.drawCircle(getMeasuredWidth()/2, getMeasuredHeight()/6.8f, getMeasuredWidth()/240, mCirclePaint);
            canvas.rotate(3f,x,y);
        }
        if (mAnimAngle != -1) {// 如果开启了动画
            for (int i = 0; i < mAnimAngle; i += 3) {
                canvas.drawCircle(getMeasuredWidth()/2, getMeasuredHeight()/6.8f, getMeasuredWidth()/240, mCircleAnimPaint);
                canvas.rotate(3f,x,y);
            }
        }
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
                while (mAnimAngle < 300) {
                    mAnimAngle++;
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    postInvalidate();
                }
//				mAnimAngle = -1;// 停止动画
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
