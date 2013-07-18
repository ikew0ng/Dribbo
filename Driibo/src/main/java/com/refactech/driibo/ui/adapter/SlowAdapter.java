
package com.refactech.driibo.ui.adapter;

import android.view.View;
import android.widget.Adapter;

public interface SlowAdapter extends Adapter {
    public void setListBusy(boolean value);

    public void doBindView(View child, Object data);
}
