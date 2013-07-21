
package com.refactech.driibo.view;

import android.content.Context;
import android.graphics.Rect;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

public class StaggeredGridViewExpand extends StaggeredGridView {

    public interface OnItemClickListener {
        void onItemClick(ViewGroup parent, View view, int position, long id);

        void onItemClickUp(ViewGroup parent, View view, int position, long id);
    }

    public interface OnItemMotionListener {
        boolean onItemDown(ViewGroup parent, View view, int position, long id);

        boolean onItemUp(ViewGroup parent, View view, int position, long id);
    }

    public interface OnItemLongClickListener {
        boolean onItemLongClick(ViewGroup parent, View view, int position, long id);
    }

    public interface OnItemSelectedListener {

    }

    private static final int ACTION_MASK = 0xFF;

    private static final long PERFORM_PRESSED_STATUS_DURATION = 300;

    GestureDetectorCompat mDetector;

    OnItemClickListener mOnItemClickListener;

    OnItemLongClickListener mOnItemLongClickListener;

    OnItemSelectedListener mOnItemSelectedListener;

    OnItemMotionListener mOnItemMotionListener;

    private boolean mEnableEditMode = false;

    public StaggeredGridViewExpand(Context context) {
        super(context);
        init();
    }

    public StaggeredGridViewExpand(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public StaggeredGridViewExpand(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        // Instantiate the gesture detector with the
        // application context and an implementation of
        // GestureDetector.OnGestureListener
        mDetector = new GestureDetectorCompat(getContext(), new ClickGestureDectorListener());

    }

    // //////////////Gesture///////////////////////
    private boolean isLongPressed = false;

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if ((ev.getAction() & ACTION_MASK) == MotionEvent.ACTION_UP) {
            isLongPressed = false;
        }
        if (mDetector != null) {
            boolean consumed = mDetector.onTouchEvent(ev);
            if (consumed)
                return true;

        }

        if (isLongPressed) {
            // isLongPressed = false;
            return true;
        }
        if ((ev.getAction() & ACTION_MASK) == MotionEvent.ACTION_UP) {
            if (mLastPressedChild != null) {
                mLastPressedChild.setPressed(false);
            }
        }
        return super.onTouchEvent(ev);
    }

    public class ClickGestureDectorListener extends GestureDetector.SimpleOnGestureListener {

        public static final int TOUCH_ACTION_DOWN = 0;

        public static final int TOUCH_ACTION_LONG_PRESS = 1;

        public static final int TOUCH_ACTION_SINGLE_TAP = 2;

        public static final int TOUCH_ACTION_SINGLE_TAP_UP = 3;

        public static final int TOUCH_ACTION_MOVE = 4;

        @Override
        public void onLongPress(MotionEvent e) {
            isLongPressed = true;
            dispatchItemClick(e, TOUCH_ACTION_LONG_PRESS);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {

            return dispatchItemClick(e, TOUCH_ACTION_SINGLE_TAP);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {

            return dispatchItemClick(e, TOUCH_ACTION_SINGLE_TAP_UP);
        }

        @Override
        public boolean onDown(MotionEvent e) {
            if (!mScroller.isFinished())
                return true;
            if (dispatchItemClick(e, TOUCH_ACTION_DOWN)) {
                return true;
            }
            return super.onDown(e);
        }

        @Override
        public void onShowPress(MotionEvent e) {

            super.onShowPress(e);
        }

    }

    public View mLastPressedChild = null;

    protected boolean dispatchItemClick(MotionEvent ev, int touchAction) {
        // if (longClick == false && mOnItemClickListener == null)
        // return;
        // if (longClick && mOnItemLongClickListener == null)
        // return;
        boolean consumed = false;
        float pointX = ev.getX();
        float pointY = ev.getY();
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            Rect rect = new Rect(child.getLeft(), child.getTop(), child.getRight(),
                    child.getBottom());
            if (rect.contains((int) pointX, (int) pointY)) {

                // Log.e("Rect" + ((LayoutParams)
                // child.getLayoutParams()).position,
                // rect.toShortString());
                child.requestFocus();

                if (touchAction == ClickGestureDectorListener.TOUCH_ACTION_LONG_PRESS) {
                    if (mEnableEditMode == false && mOnItemLongClickListener != null) {
                        consumed = mOnItemLongClickListener.onItemLongClick(this, child,
                                ((LayoutParams) child.getLayoutParams()).position,
                                ((LayoutParams) child.getLayoutParams()).id);
                    }
                } else if (touchAction == ClickGestureDectorListener.TOUCH_ACTION_SINGLE_TAP) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(this, child,
                                ((LayoutParams) child.getLayoutParams()).position,
                                ((LayoutParams) child.getLayoutParams()).id);
                    }
                } else if (touchAction == ClickGestureDectorListener.TOUCH_ACTION_SINGLE_TAP_UP) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClickUp(this, child,
                                ((LayoutParams) child.getLayoutParams()).position,
                                ((LayoutParams) child.getLayoutParams()).id);
                    }

                    if (mEnableEditMode == false && mOnItemMotionListener != null) {
                        consumed = mOnItemMotionListener.onItemUp(this, child,
                                ((LayoutParams) child.getLayoutParams()).position,
                                ((LayoutParams) child.getLayoutParams()).id);
                    }

                } else if (touchAction == ClickGestureDectorListener.TOUCH_ACTION_DOWN) {
                    if (mEnableEditMode == false && mOnItemMotionListener != null) {
                        consumed = mOnItemMotionListener.onItemDown(this, child,
                                ((LayoutParams) child.getLayoutParams()).position,
                                ((LayoutParams) child.getLayoutParams()).id);
                        child.setPressed(true);
                    }

                    mLastPressedChild = child;

                } else {
                    if (mLastPressedChild != null)
                        mLastPressedChild.setPressed(false);
                    child.setPressed(false);

                    if (mOnItemMotionListener != null) {
                        consumed = mOnItemMotionListener.onItemUp(this, child,
                                ((LayoutParams) child.getLayoutParams()).position,
                                ((LayoutParams) child.getLayoutParams()).id);
                    }
                }

                break;
            }
        }
        return consumed;
    }

    // //////////////Listener Method///////////////////////

    public OnItemClickListener getOnItemClickListener() {
        return mOnItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public OnItemLongClickListener getOnItemLongClickListener() {
        return mOnItemLongClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener mOnItemLongClickListener) {
        this.mOnItemLongClickListener = mOnItemLongClickListener;
    }

    public OnItemSelectedListener getOnItemSelectedListener() {
        return mOnItemSelectedListener;
    }

    public void setOnItemSelectedListener(OnItemSelectedListener mOnItemSelectedListener) {
        this.mOnItemSelectedListener = mOnItemSelectedListener;
    }

    public int getFirstScrollTop() {
        return mItemTops[0];
    }

    public int getHeaderViewHeight() {
        return mHeaderViewHeight;
    }

    public OnItemMotionListener getOnItemMotionListener() {
        return mOnItemMotionListener;
    }

    public void setOnItemMotionListener(OnItemMotionListener mOnItemMotionListener) {
        this.mOnItemMotionListener = mOnItemMotionListener;
    }

    public boolean isEnableEditMode() {
        return mEnableEditMode;
    }

    public void setEnableEditMode(boolean mEnableEditMode) {
        this.mEnableEditMode = mEnableEditMode;
    }

}
