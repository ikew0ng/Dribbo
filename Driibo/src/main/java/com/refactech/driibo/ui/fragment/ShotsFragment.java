
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
import com.refactech.driibo.ui.adapter.ListViewUtils;
import com.refactech.driibo.ui.adapter.ShotsAdapter;
import com.refactech.driibo.util.CommonUtils;
import com.refactech.driibo.vendor.DribbbleApi;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.*;
import android.widget.*;

import java.util.ArrayList;

/**
 * Created by Issac on 7/18/13.
 */
public class ShotsFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String EXTRA_CATEGORY = "EXTRA_CATEGORY";

    private Category mCategory;

    private ShotsDataHelper mDataHelper;

    private ShotsAdapter mAdapter;

    private ListView mListView;

    private int mPage = 1;

    private MainActivity mActivity;

    private ActionMode mActionMode;

    ShareActionProvider mShareActionProvider;

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

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
            mListView.setItemChecked(-1, true);
            mActionMode = null;
        }
    };

    private void setShareIntent(Intent shareIntent) {
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
        View contentView = inflater.inflate(R.layout.fragment_content, null);
        mListView = (ListView) contentView.findViewById(R.id.listView);
        parseArgument();
        mDataHelper = new ShotsDataHelper(AppData.getContext(), mCategory);
        mAdapter = new ShotsAdapter(getActivity(), mListView);
        View header = new View(getActivity());
        View footer = new View(getActivity());
        mListView.addHeaderView(header);
        mListView.addFooterView(footer);
        mListView.setAdapter(mAdapter);
        getLoaderManager().initLoader(0, null, this);
        mActivity = (MainActivity) getActivity();

        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                    int totalItemCount) {
                if (firstVisibleItem + visibleItemCount >= totalItemCount
                        && totalItemCount != 0
                        && totalItemCount != mListView.getHeaderViewsCount()
                                + mListView.getFooterViewsCount() && mAdapter.getCount() > 0) {
                    loadNextPage();
                }
            }
        });
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (mActionMode != null) {
                    return false;
                }
                mActionMode = getActivity().startActionMode(mActionModeCallback);
                mListView.setItemChecked(position, true);
                Shot shot = mAdapter.getItem(position - mListView.getHeaderViewsCount());
                mActionMode.setTitle(getString(R.string.action_share));
                mActionMode.setSubtitle(shot.getTitle());
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, shot.getTitle());
                sendIntent.setType("text/plain");
                setShareIntent(sendIntent);
                return true;
            }
        });
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
        if (data != null && data.getCount() == 0) {
            loadFirstPage();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.changeCursor(null);
    }

    private void loadDate(final int page) {
        if (mActivity.isRefreshing()) {
            return;
        }
        mActivity.setRefreshing(true);
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
                                mActivity.setRefreshing(false);
                            }
                        });
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(getActivity(), R.string.refresh_list_failed,
                                Toast.LENGTH_SHORT).show();
                        mActivity.setRefreshing(false);
                    }
                }));
    }

    private void loadNextPage() {
        loadDate(mPage + 1);
    }

    public void loadFirstPage() {
        ListViewUtils.smoothScrollListViewToTop(mListView);
        loadDate(1);
    }

    public void finishActionMode() {
        if (mActionMode != null) {
            mActionMode.finish();
        }
    }
}
