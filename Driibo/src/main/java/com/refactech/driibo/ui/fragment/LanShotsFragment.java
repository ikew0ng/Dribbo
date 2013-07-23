
package com.refactech.driibo.ui.fragment;

import com.refactech.driibo.AppData;
import com.refactech.driibo.R;
import com.refactech.driibo.dao.ShotsDataHelper;
import com.refactech.driibo.type.dribble.Category;
import com.refactech.driibo.ui.adapter.ShotsAdapter;
import com.refactech.driibo.view.StaggeredGridView;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Issac on 7/23/13.
 */
public class LanShotsFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String EXTRA_CATEGORY = "EXTRA_CATEGORY";

    protected ShotsDataHelper mDataHelper;

    protected ShotsAdapter mAdapter;

    private StaggeredGridView mGridView;

    private Category mCategory;

    public static LanShotsFragment newInstance(Category category) {
        LanShotsFragment fragment = new LanShotsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_CATEGORY, category.name());
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        parseArgument();
        mDataHelper = new ShotsDataHelper(AppData.getContext(), mCategory);
        View contentView = inflater.inflate(R.layout.fragment_shot_lan, null);
        mGridView = (StaggeredGridView) contentView.findViewById(R.id.staggeredGridView);
        mAdapter = new ShotsAdapter(getActivity());
        mGridView.setAdapter(mAdapter);
        getLoaderManager().initLoader(0, null, this);
        return contentView;
    }

    private void parseArgument() {
        Bundle bundle = getArguments();
        mCategory = Category.valueOf(bundle.getString(EXTRA_CATEGORY));
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
