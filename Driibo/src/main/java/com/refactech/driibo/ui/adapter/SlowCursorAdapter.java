
package com.refactech.driibo.ui.adapter;

import android.content.Context;
import android.support.v4.widget.CursorAdapter;

public abstract class SlowCursorAdapter extends CursorAdapter implements SlowAdapter {
    protected boolean mListBusy;

    public SlowCursorAdapter(Context context) {
        super(context, null, false);
    }

    @Override
    public void setListBusy(boolean value) {
        mListBusy = value;
    }

}
