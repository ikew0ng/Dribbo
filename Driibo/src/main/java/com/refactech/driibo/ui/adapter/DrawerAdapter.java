
package com.refactech.driibo.ui.adapter;

import com.refactech.driibo.AppData;
import com.refactech.driibo.R;
import com.refactech.driibo.type.dribble.Category;
import com.refactech.driibo.util.PreferenceUtils;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by Issac on 7/18/13.
 */
public class DrawerAdapter extends BaseAdapter {
    private ListView mListView;

    public DrawerAdapter(ListView listView) {
        mListView = listView;
    }

    @Override
    public int getCount() {
        int count = Category.values().length;
        if (TextUtils.isEmpty(PreferenceUtils.getPrefString(
                AppData.getContext().getString(R.string.pref_key_login), null))) {
            return count - 2;
        }
        return count;
    }

    @Override
    public Category getItem(int position) {
        return Category.values()[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(AppData.getContext()).inflate(
                    R.layout.listitem_drawer, null);
        }
        TextView textView = (TextView) convertView.findViewById(R.id.textView);
        textView.setText(getItem(position).getDisplayName());
        textView.setSelected(mListView.isItemChecked(position));
        return convertView;
    };
}
