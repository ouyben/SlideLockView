package com.ouyben.slidelockview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;

/**
 * TODO :
 * Created by owen
 * on 2016-10-10.
 */

public class SlideLockView extends TextView {

    private static final String TAG = "SlideLockView";

    private Bitmap mLockBitmap;
    private int mLockDrawableId;
    private Paint mPaint;
    private int mLockRadius;

    private int height, with;
    private float mLocationX;
    private boolean mIsDragable = false;
    private OnLockListener mLockListener;

    public SlideLockView(Context context) {
        this(context, null);
    }

    public SlideLockView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideLockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray tp = context.obtainStyledAttributes(attrs, R.styleable.SlideLockView, defStyleAttr, 0);
        mLockDrawableId = tp.getResourceId(R.styleable.SlideLockView_lock_drawable, -1);
        mLockRadius = tp.getDimensionPixelOffset(R.styleable.SlideLockView_lock_radius, 1);

        tp.recycle();

        if (mLockDrawableId == -1) {
            throw new RuntimeException("未设置滑动解锁图片");
        }

        init(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        height = getMeasuredHeight();
        with = getMeasuredWidth();
    }

    private void init(Context context) {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        mLockBitmap = BitmapFactory.decodeResource(context.getResources(), mLockDrawableId);
        int oldSize = mLockBitmap.getHeight();
        int newSize = mLockRadius * 2;
        float scale = newSize * 1.0f / oldSize;
        Matrix matrix = new Matrix();
        matrix.setScale(scale, scale);
        mLockBitmap = Bitmap.createBitmap(mLockBitmap, 0, 0, oldSize, oldSize, matrix, true);
    }

    /**
     * TODO: 重绘控件
     *
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int rightMax = getWidth() - mLockRadius * 2;
        // 保证滑动图片绘制居中 (height / 2 - mLockRadius)
        if (mLocationX < 0) {
            canvas.drawBitmap(mLockBitmap, 0, height / 2 - mLockRadius, mPaint);
        } else if (mLocationX > rightMax) {
            canvas.drawBitmap(mLockBitmap, rightMax, height / 2 - mLockRadius, mPaint);
        } else {
            canvas.drawBitmap(mLockBitmap, mLocationX, height / 2 - mLockRadius, mPaint);
        }

    }

    /**
     * TODO: 滑动事件
     * 1、当触摸屏幕是触发ACTION_DOWN事件，计算时候触摸到锁，只有当触到锁的时候才能滑动；
     * 2、手指移动时，获得新的位置后计算新的位置，然后重新绘制，若移动到另一端表示解锁成功，执行回调方法解锁成功；
     * 3、手指离开屏幕后重新reset View,动画回到初始位置
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN://开始触摸
                float xPos = event.getX();
                float yPos = event.getY();
                if (isTouchLock(xPos, yPos)) {
                    Log.d(TAG, "触摸目标");
                    mLocationX = xPos - mLockRadius;
                    mIsDragable = true;
                    invalidate();
                } else {
                    mIsDragable = false;
                }
                return true;
//                break;
            case MotionEvent.ACTION_CANCEL://手势被取消了
                Log.e(TAG, "抬起手指");
                if (!mIsDragable)
                    return true;
                resetLock();
                break;
            case MotionEvent.ACTION_MOVE://移动
                // 如果不在焦点
                if (!mIsDragable)
                    return true;

                int rightMax = getWidth() - mLockRadius * 2;
                resetLocationX(event.getX(), rightMax);
                invalidate();

                if (mLocationX >= rightMax) {
                    mIsDragable = false;
                    mLocationX = 0;
                    invalidate();
                    if (mLockListener != null) {
                        mLockListener.onOpenLockSuccess();
                    }
                    Log.e(TAG, "解锁成功");
                }
                return true;
//                break;
            case MotionEvent.ACTION_UP://抬起了手指
                Log.e(TAG, "抬起手指");
                if (!mIsDragable)
                    return true;
                resetLock();
                break;
            case MotionEvent.ACTION_OUTSIDE://超出了正常的UI边界

                break;
            default:
                break;
        }

        return super.onTouchEvent(event);
    }

    /**
     * TODO: 回到初始位置
     */
    private void resetLock() {
        ValueAnimator anim = ValueAnimator.ofFloat(mLocationX, 0);
        anim.setDuration(300);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mLocationX = (Float) valueAnimator.getAnimatedValue();
                invalidate();
            }
        });
        anim.start();
    }

    private void resetLocationX(float eventXPos, float rightMax) {

        float xPos = eventXPos;
        mLocationX = xPos - mLockRadius;
        if (mLocationX < 0) {
            mLocationX = 0;
        } else if (mLocationX >= rightMax) {
            mLocationX = rightMax;
        }
    }

    /**
     * TODO: 判断是不是在目标点上
     *
     * @param xPos
     * @param yPox
     * @return
     */
    private boolean isTouchLock(float xPos, float yPox) {
        float centerX = mLocationX + mLockRadius;
        float diffX = xPos - centerX;
        float diffY = yPox - mLockRadius;

        return diffX * diffX + diffY * diffY < mLockRadius * mLockRadius;
    }


    public void setLockListener(OnLockListener lockListener) {
        this.mLockListener = lockListener;
    }

    public interface OnLockListener {
        void onOpenLockSuccess();
    }

}
