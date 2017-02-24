package cn.ittiger.gallery;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.OverScroller;

/**
 * Created by ylhu on 17-2-24.
 */
public class GalleryViewPager extends ViewPager {
    private int mTouchSlop;

    private float mInitialMotionX;
    private float mInitialMotionY;
    private float mLastMotionX;
    private float mLastMotionY;
    private OverScroller mOverScroller;
    private float mHalfScreenHeight;
    private MovingListener mMovingListener;

    public GalleryViewPager(Context context) {

        super(context, null);
    }

    public GalleryViewPager(Context context, AttributeSet attrs) {

        super(context, attrs);
        initGalleryViewPager(context);
    }

    private void initGalleryViewPager(Context context) {

        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mOverScroller = new OverScroller(context);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {

        if(mHalfScreenHeight == 0) {
            mHalfScreenHeight = getBottom() / 2;
            Log.d("Gallery", getBottom() + "");
            Log.d("Gallery", "mHalfScreenHeight:" + mHalfScreenHeight);
        }
        boolean flag = super.onInterceptTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastMotionX = event.getX();
                mLastMotionY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                final float x = event.getX();
                final float xDiff = Math.abs(x - mLastMotionX);
                final float y = event.getY();
                final float yDiff = Math.abs(y - mLastMotionY);
                if (yDiff > mTouchSlop && yDiff * 0.5f > xDiff) {//垂直方向
                    flag = true;
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return flag;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction() & MotionEventCompat.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mLastMotionX = mInitialMotionX = event.getX();
                mLastMotionY = mInitialMotionY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float x = event.getX();
                float xDiff = Math.abs(x - mInitialMotionX);
                float y = event.getY();
                float yDiff = Math.abs(y - mInitialMotionY);
                if (yDiff > mTouchSlop && yDiff * 0.5f > xDiff) {//垂直方向
                    scrollBy(0, -(int) (y - mLastMotionY + 0.5f));
//                    this.setTranslationY(y - mInitialMotionY);
                    if(mMovingListener != null) {
                        float percent = yDiff / mHalfScreenHeight > 0.99 ? 1 : yDiff / mHalfScreenHeight;
                        mMovingListener.onMoving(percent);
                    }
                }
                mLastMotionX = event.getX();
                mLastMotionY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                y = event.getY();
                yDiff = Math.abs(y - mInitialMotionY);
                FlingRunnable runnable = new FlingRunnable(mHalfScreenHeight, yDiff, y - mInitialMotionY < 0);
                runnable.start();
                break;
        }
        return super.onTouchEvent(event);
    }

    public void setMovingListener(MovingListener movingListener) {

        mMovingListener = movingListener;
    }

    public interface MovingListener {

        void onMoving(float percent);
    }

    class FlingRunnable implements Runnable {
        private float mDistance;
        private float mMovedDis;
        private boolean mIsUp;
        private float mLastY;

        public FlingRunnable(float distance, float movedDis, boolean isUp) {

            mDistance = distance;
            mMovedDis = mLastY = movedDis;
            mIsUp = isUp;
        }

        @Override
        public void run() {

            if (mOverScroller != null) {
                if (mOverScroller.computeScrollOffset()) {
                    int currY = mOverScroller.getCurrY();
                    int y;
                    if(mIsUp) {
                        y = (int) (currY - mLastY);
                    } else {
                        y = -(int) (currY - mLastY);
                    }
                    GalleryViewPager.this.scrollBy(0, y);
                    ViewCompat.postOnAnimation(GalleryViewPager.this, this);
                    if(mMovingListener != null) {
                        mMovingListener.onMoving(currY/mDistance);
                    }
                    mLastY = currY;
                } else {
                    if(mMovingListener != null) {
                        mMovingListener.onMoving(1);
                    }
                }
            }
        }

        public void start() {
            mOverScroller.startScroll(0, (int) mMovedDis, 0, (int)mDistance, 700);
            ViewCompat.postOnAnimation(GalleryViewPager.this, this);
        }
    }
}
