package io.amosbake.animationsummary.wiget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

/**
 * Author: mopel
 * Date : 2016/12/27
 */
public class JumpPointView extends View {
    private Paint mPaint;
    private int color = Color.parseColor("#0000FF");
    private int radius = 10;
    private float density;
    private RectF mRectF;

    private int startY,startX,endY,currentY;

    public JumpPointView(Context context) {
        this(context,null);
    }

    public JumpPointView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public JumpPointView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        density = getResources().getDisplayMetrics().density;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(color);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        startX = getMeasuredWidth()/2;
        endY = getMeasuredHeight()/2;
        startY =endY * 5 /6;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (currentY == 0) {
            playAnimator();
        } else {
            drawCircle(canvas);
            drawShader(canvas);
        }
    }

    private void playAnimator() {
        // 我们只需要取Y轴方向上的变化即可
        ValueAnimator valueAnimator = ValueAnimator.ofInt(startY, endY);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentY = (Integer) animation.getAnimatedValue();
                invalidate();
            }
        });
        valueAnimator.setInterpolator(new AccelerateInterpolator(1.2f));
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.setRepeatMode(ValueAnimator.REVERSE);
        valueAnimator.setDuration(500);
        valueAnimator.start();
    }

    /**
     * 绘制圆形
     *
     * @param canvas
     */
    private void drawCircle(Canvas canvas) {
        // 当接触到底部时候，我们为了要描绘一种压扁的效果，实际上就是绘制椭圆
        if (endY - currentY > 10) {
            canvas.drawCircle(startX, currentY, radius * density, mPaint);
        } else {
            mRectF = new RectF(startX - radius * density - 2, currentY - radius * density + 5,
                    startX + radius * density + 2, currentY + radius * density);
            canvas.drawOval(mRectF, mPaint);
        }
    }



    /**
     * 绘制阴影部分，由椭圆来支持，根据高度比来底部阴影的大小
     */
    private void drawShader(Canvas canvas) {
        // 计算差值高度
        int dx = endY - startY;
        // 计算当前点的高度差值
        int dx1 = currentY - startY;
        float ratio = (float) (dx1 * 1.0 / dx);
        if (ratio <= 0.3) {// 当高度比例小于0.3，所在比较高的时候就不进行绘制影子
            return;
        }
        int ovalRadius = (int) (radius * ratio * density);
        // 设置倒影的颜色
        mPaint.setColor(Color.parseColor("#3F3B2D"));
        // 绘制椭圆
        mRectF = new RectF(startX - ovalRadius, endY + 10, startX + ovalRadius, endY + 15);
        canvas.drawOval(mRectF, mPaint);
    }

    /**
     * 设置颜色
     *
     * @param color
     */
    public void setColor(int color) {
        this.color = color;
    }
}
