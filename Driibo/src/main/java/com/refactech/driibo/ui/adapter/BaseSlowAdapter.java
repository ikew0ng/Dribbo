
package com.refactech.driibo.ui.adapter;

import android.widget.BaseAdapter;

public abstract class BaseSlowAdapter extends BaseAdapter implements SlowAdapter {
    protected boolean mListBusy;

    @Override
    public void setListBusy(boolean value) {
        mListBusy = value;
    }
}
