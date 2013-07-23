
package com.refactech.driibo.ui.fragment;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.refactech.driibo.AppData;
import com.refactech.driibo.R;
import com.refactech.driibo.dao.ShotsDataHelper;
import com.refactech.driibo.data.GsonRequest;
import com.refactech.driibo.type.dribble.Category;
import com.refactech.driibo.type.dribble.Shot;
import com.refactech.driibo.ui.MainActivity;
import com.refactech.driibo.ui.adapter.ShotsAdapter;
import com.refactech.driibo.util.CommonUtils;
import com.refactech.driibo.vendor.DribbbleApi;
import com.refactech.driibo.view.LoadingFooter;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.*;
import android.widget.ShareActionProvider;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Issac on 7/23/13.
 */
public abstract class BaseShotsFragment extends BaseFragment implements
        LoaderManager.LoaderCallbacks<Cursor> {
    public static final String EXTRA_CATEGORY = "EXTRA_CATEGORY";

    protected Category mCategory;

    protected ShotsDataHelper mDataHelper;

    protected ShotsAdapter mAdapter;

    protected int mPage = 1;

    protected MainActivity mActivity;

    protected ActionMode mActionMode;

    protected ShareActionProvider mShareActionProvider;

    protected LoadingFooter mLoadingFooter;

    protected ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.context_menu, menu);
            mShareActionProvider = (ShareActionProvider) menu.findItem(R.id.action_share)
                    .getActionProvider();
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
        }
    };

    protected void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

    public static ShotsFragment newInstance(Category category) {
        ShotsFragment fragment = new ShotsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_CATEGORY, category.name());
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(getContentViewLayoutRedId(), null);
        parseArgument();
        mDataHelper = new ShotsDataHelper(AppData.getContext(), mCategory);
        mAdapter = new ShotsAdapter(getActivity());
        View header = new View(getActivity());
        getLoaderManager().initLoader(0, null, this);
        mActivity = (MainActivity) getActivity();
        mLoadingFooter = new LoadingFooter(getActivity());
        return contentView;
    }

    protected abstract int getContentViewLayoutRedId();

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
        if (data != null && data.getCount() == 0) {
            loadFirstPage();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.changeCursor(null);
    }

    private void loadData(final int page) {
        final boolean isRefreshFromTop = page == 1;
        executeRequest(new GsonRequest<Shot.ShotsRequestData>(String.format(DribbbleApi.SHOTS_LIST,
                mCategory.name(), page), Shot.ShotsRequestData.class, null,
                new Response.Listener<Shot.ShotsRequestData>() {
                    @Override
                    public void onResponse(final Shot.ShotsRequestData requestData) {
                        CommonUtils.executeAsyncTask(new AsyncTask<Object, Object, Object>() {
                            @Override
                            protected Object doInBackground(Object... params) {
                                mPage = requestData.getPage();
                                if (mPage == 1) {
                                    mDataHelper.deleteAll();
                                }
                                ArrayList<Shot> shots = requestData.getShots();
                                mDataHelper.bulkInsert(shots);
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Object o) {
                                super.onPostExecute(o);
                                if (isRefreshFromTop) {
                                    // mPullToRefreshAttacher.setRefreshComplete();
                                } else {
                                    mLoadingFooter.setState(LoadingFooter.State.Idle, 3000);
                                }
                            }
                        });
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(getActivity(), R.string.refresh_list_failed,
                                Toast.LENGTH_SHORT).show();
                        if (isRefreshFromTop) {
                            // mPullToRefreshAttacher.setRefreshComplete();
                        } else {
                            mLoadingFooter.setState(LoadingFooter.State.Idle, 3000);
                        }
                    }
                }));
    }

    private void loadNextPage() {
        mLoadingFooter.setState(LoadingFooter.State.Loading);
        loadData(mPage + 1);
    }

    private void loadFirstPage() {
        loadData(1);
    }

    public void loadFirstPageAndScrollToTop() {
        loadFirstPage();
    }

    public void finishActionMode() {
        if (mActionMode != null) {
            mActionMode.finish();
        }
    }
}
