
package com.refactech.driibo.ui.fragment;

import com.refactech.driibo.AppData;
import com.refactech.driibo.R;
import com.refactech.driibo.dao.ShotsDataHelper;
import com.refactech.driibo.type.dribble.Category;
import com.refactech.driibo.type.dribble.Shot;
import com.refactech.driibo.ui.adapter.ShotsAdapter;
import com.refactech.driibo.util.PreferenceUtils;
import com.refactech.driibo.vendor.DribbbleApi;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import java.util.ArrayList;

/**
 * Created by Issac on 7/18/13.
 */
public class LikeFragment extends BasePageListFragment<Shot.ShotsRequestData> implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private ShotsDataHelper mDataHelper;

    public static LikeFragment newInstance() {
        LikeFragment fragment = new LikeFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = super.onCreateView(inflater, container, savedInstanceState);
        mDataHelper = new ShotsDataHelper(AppData.getContext(), Category.likes);

        getLoaderManager().initLoader(0, null, this);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Shot shot = (Shot) getAdapter().getItem(position - mListView.getHeaderViewsCount());
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(shot.getUrl()));
                startActivity(intent);
            }
        });

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Shot shot = (Shot) getAdapter().getItem(position - mListView.getHeaderViewsCount());
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(shot.getImage_url()));
                startActivity(intent);
                return true;
            }
        });

        return contentView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return mDataHelper.getCursorLoader();
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        getAdapter().changeCursor(data);
        if (data != null && data.getCount() == 0) {
            loadFirstPage();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        getAdapter().changeCursor(null);
    }

    @Override
    protected int getContentViewResId() {
        return R.layout.fragment_shot;
    }

    @Override
    protected CursorAdapter getAdapter() {
        return (CursorAdapter) super.getAdapter();
    }

    @Override
    protected BaseAdapter newAdapter() {
        return new ShotsAdapter(getActivity(), mListView);
    }

    @Override
    protected void processData(Shot.ShotsRequestData response) {
        mPage = response.getPage();
        if (mPage == 1) {
            mDataHelper.deleteAll();
        }
        ArrayList<Shot> shots = response.getShots();
        mDataHelper.bulkInsert(shots);
    }

    @Override
    protected String getUrl(int page) {
        return String.format(
                DribbbleApi.LIKES,
                PreferenceUtils.getPrefString(getString(R.string.pref_key_login), null).replaceAll(
                        " ", ""), page);
    }

    @Override
    protected Class getResponseDataClass() {
        return Shot.ShotsRequestData.class;
    }
}
