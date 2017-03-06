package cn.ittiger.gallery;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
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
    private int mImageHeight;//图片高度
    private int mScrollUpHeight;//上滑高度
    private int mScrollDownHeight;//下滑高度
    private OverScroller mOverScroller;
    private ScrollListener mScrollListener;

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

        View itemView = findViewWithTag(getCurrentItem());
        View imageView = ((ViewGroup)itemView).getChildAt(0);
        int itemHeight = itemView.getMeasuredHeight();
        mImageHeight = imageView.getMeasuredHeight();
        mScrollUpHeight = imageView.getBottom();
        mScrollDownHeight = itemHeight - imageView.getTop();

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
                if (yDiff > mTouchSlop && yDiff * 0.5f > xDiff) {//垂直方向滑动时拦截滑动事件
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
                    int distance = y >= mInitialMotionY ? mScrollDownHeight : mScrollUpHeight;
                    if(mScrollListener != null) {
                        float percent = yDiff / distance > 0.99 ? 1 : yDiff / distance;
                        mScrollListener.onScrolling(percent);
                    }
                }
                mLastMotionX = event.getX();
                mLastMotionY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                x = event.getX();
                xDiff = Math.abs(x - mInitialMotionX);
                y = event.getY();
                yDiff = Math.abs(y - mInitialMotionY);
                if (yDiff > mTouchSlop && yDiff * 0.5f > xDiff) {//垂直方向
                    FlingRunnable runnable = new FlingRunnable(yDiff, y - mInitialMotionY < 0);
                    runnable.start();
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    public void setScrollListener(ScrollListener scrollListener) {

        mScrollListener = scrollListener;
    }

    public interface ScrollListener {

        void onScrolling(float percent);
    }

    class FlingRunnable implements Runnable {
        private float mDistance;//向上或向下滑动的总距离
        private float mFlingDistance;//松开手指后需要滑动的总距离
        private float mCurDistance;//当前已滑动的距离
        private boolean mScrollUp;//是否向上滑动
        private int mLastY = 0;
        private boolean mRestore;//是否为恢复到滑动前的原位置

        public FlingRunnable(float movedDis, boolean scrollUp) {

            mScrollUp = scrollUp;
            mDistance  = scrollUp ? mScrollUpHeight : mScrollDownHeight;//总共要滑动的距离
            if(movedDis < mImageHeight / 3.0) {//已滑动距离小于图片的1/3高度，恢复到源位置
                mCurDistance = 0;
                mFlingDistance = movedDis;
                mScrollUp = !scrollUp;
                mRestore = true;
            } else {
                mLastY = (int) (movedDis + 0.5f);
                mCurDistance = movedDis;
                mFlingDistance = mDistance;
                mRestore = false;
            }
        }

        @Override
        public void run() {

            if (mOverScroller != null) {
                if (mOverScroller.computeScrollOffset()) {
                    int currY = mOverScroller.getCurrY();
                    int y;
                    if(mScrollUp) {
                        y = currY - mLastY;
                    } else {
                        y = -(currY - mLastY);
                    }
                    GalleryViewPager.this.scrollBy(0, y);
                    ViewCompat.postOnAnimation(GalleryViewPager.this, this);
                    if(mScrollListener != null) {
                        float percent = (mRestore ? mFlingDistance - currY : currY) / mDistance;
                        mScrollListener.onScrolling(percent);
                    }
                    mLastY = currY;
                } else {
                    if(mScrollListener != null) {
                        mScrollListener.onScrolling(mRestore ? 0 : 1);
                    }
                }
            }
        }

        public void start() {
            mOverScroller.startScroll(0, (int) (mCurDistance + 0.5f), 0, (int)(mFlingDistance + 0.5f), 1000);
            ViewCompat.postOnAnimation(GalleryViewPager.this, this);
        }
    }
}
