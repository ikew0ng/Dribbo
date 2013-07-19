
package com.refactech.driibo.ui.fragment;

import com.android.volley.Response;
import com.refactech.driibo.AppData;
import com.refactech.driibo.R;
import com.refactech.driibo.dao.ShotsDataHelper;
import com.refactech.driibo.data.GsonRequest;
import com.refactech.driibo.type.dribble.Category;
import com.refactech.driibo.type.dribble.Shot;
import com.refactech.driibo.ui.adapter.ShotsAdapter;
import com.refactech.driibo.vendor.DribbbleApi;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by Issac on 7/18/13.
 */
public class ShotsFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String EXTRA_CATEGORY = "EXTRA_CATEGORY";

    private Category mCategory;

    private ShotsDataHelper mDataHelper;

    private ShotsAdapter mAdapter;

    private ListView mLisetView;

    public static ShotsFragment newInstance(Category category) {
        ShotsFragment fragment = new ShotsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_CATEGORY, category.name());
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_content, null);
        mLisetView = (ListView) contentView.findViewById(R.id.listView);
        parseArgument();
        mDataHelper = new ShotsDataHelper(AppData.getContext(), mCategory);
        mAdapter = new ShotsAdapter(getActivity());
        View header = new View(getActivity());
        View footer = new View(getActivity());
        mLisetView.addHeaderView(header);
        mLisetView.addFooterView(footer);
        mLisetView.setAdapter(mAdapter);

        mDataHelper.deleteAll();
        executeRequest(new GsonRequest<Shot.ShotsRequestData>(String.format(DribbbleApi.SHOTS_LIST,
                mCategory.name()), Shot.ShotsRequestData.class, null,
                new Response.Listener<Shot.ShotsRequestData>() {
                    @Override
                    public void onResponse(Shot.ShotsRequestData requestData) {
                        ArrayList<Shot> shots = requestData.getShots();
                        mDataHelper.bulkInsert(shots);
                    }
                }, null));

        getLoaderManager().initLoader(0, null, this);
        return contentView;
    }

    private void parseArgument() {
        Bundle bundle = getArguments();
        mCategory = Category.valueOf(bundle.getString(EXTRA_CATEGORY));
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return mDataHelper.getCursorLoader();
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.changeCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.changeCursor(null);
    }
}
