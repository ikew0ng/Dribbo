
package com.refactech.driibo.ui.adapter;

import com.refactech.driibo.R;

import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

public abstract class SlowAdapterOnScrollListener implements OnScrollListener {
    public static int BIND_DATA_TAG = R.string.app_name;

    private SlowAdapter mAdapter;

    private boolean mStrictMode = false;

    private int mScrollState;

    /**
     * strictmode 设置之后 touch scroll不加载图片
     * 
     * @param adapter
     * @param strictMode
     */
    public SlowAdapterOnScrollListener(SlowAdapter adapter, boolean strictMode) {
        this(adapter);
        mStrictMode = strictMode;
    }

    public SlowAdapterOnScrollListener(SlowAdapter adapter) {
        mAdapter = adapter;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        mScrollState = scrollState;
        switch (scrollState) {
            case OnScrollListener.SCROLL_STATE_IDLE:
                view.postDelayed(new BindRunnable(view), 500);
                break;
            case OnScrollListener.SCROLL_STATE_FLING:
                mAdapter.setListBusy(true);
                break;
            default:
                if (mStrictMode) {
                    mAdapter.setListBusy(true);
                }
                break;
        }

    }

    private class BindRunnable implements Runnable {
        private AbsListView mListView;

        public BindRunnable(AbsListView listView) {
            mListView = listView;
        }

        @Override
        public void run() {
            if (mScrollState != OnScrollListener.SCROLL_STATE_IDLE) {
                return;
            }
            mAdapter.setListBusy(false);
            int count = mListView.getChildCount();
            for (int i = 0; i < count; i++) {
                View child = mListView.getChildAt(i);
                Object data = child.getTag(BIND_DATA_TAG);
                if (data != null) {
                    mAdapter.doBindView(child, data);
                }
            }
        }
    }
}
