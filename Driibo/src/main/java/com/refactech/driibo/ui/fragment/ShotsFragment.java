
package com.refactech.driibo.ui.fragment;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.haarman.listviewanimations.swinginadapters.AnimationAdapter;
import com.refactech.driibo.AppData;
import com.refactech.driibo.R;
import com.refactech.driibo.dao.ShotsDataHelper;
import com.refactech.driibo.data.GsonRequest;
import com.refactech.driibo.type.dribble.Category;
import com.refactech.driibo.type.dribble.Shot;
import com.refactech.driibo.ui.MainActivity;
import com.refactech.driibo.ui.adapter.CardsAnimationAdapter;
import com.refactech.driibo.ui.adapter.ListViewUtils;
import com.refactech.driibo.ui.adapter.ShotsAdapter;
import com.refactech.driibo.util.CommonUtils;
import com.refactech.driibo.vendor.DribbbleApi;
import com.refactech.driibo.view.LoadingFooter;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Issac on 7/18/13.
 */
public class ShotsFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor>,
        PullToRefreshAttacher.OnRefreshListener {
    public static final String EXTRA_CATEGORY = "EXTRA_CATEGORY";

    private Category mCategory;

    private ShotsDataHelper mDataHelper;

    private ShotsAdapter mAdapter;

    private ListView mListView;

    private int mPage = 1;

    private MainActivity mActivity;

    private PullToRefreshAttacher mPullToRefreshAttacher;

    private LoadingFooter mLoadingFooter;

    public static ShotsFragment newInstance(Category category) {
        ShotsFragment fragment = new ShotsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_CATEGORY, category.name());
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_shot, null);
        mListView = (ListView) contentView.findViewById(R.id.listView);
        parseArgument();
        mDataHelper = new ShotsDataHelper(AppData.getContext(), mCategory);
        mAdapter = new ShotsAdapter(getActivity(), mListView);
        View header = new View(getActivity());
        mPullToRefreshAttacher = ((MainActivity) getActivity()).getPullToRefreshAttacher();
        mPullToRefreshAttacher.setRefreshableView(mListView, this);
        mLoadingFooter = new LoadingFooter(getActivity());

        mListView.addHeaderView(header);
        mListView.addFooterView(mLoadingFooter.getView());
        AnimationAdapter animationAdapter = new CardsAnimationAdapter(mAdapter);
        animationAdapter.setListView(mListView);
        mListView.setAdapter(animationAdapter);
        getLoaderManager().initLoader(0, null, this);
        mActivity = (MainActivity) getActivity();

        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                    int totalItemCount) {
                if (mLoadingFooter.getState() == LoadingFooter.State.Loading
                        || mLoadingFooter.getState() == LoadingFooter.State.TheEnd) {
                    return;
                }
                if (firstVisibleItem + visibleItemCount >= totalItemCount
                        && totalItemCount != 0
                        && totalItemCount != mListView.getHeaderViewsCount()
                                + mListView.getFooterViewsCount() && mAdapter.getCount() > 0) {
                    loadNextPage();
                }
            }
        });
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Shot shot = mAdapter.getItem(position - mListView.getHeaderViewsCount());
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(shot.getUrl()));
                startActivity(intent);
            }
        });

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Shot shot = mAdapter.getItem(position - mListView.getHeaderViewsCount());
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(shot.getImage_url()));
                startActivity(intent);
                return true;
            }
        });

        return contentView;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mPullToRefreshAttacher.getHeaderTransformer().onConfigurationChanged(getActivity());
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

    private void loadData(final int page) {
        final boolean isRefreshFromTop = page == 1;
        if (!mPullToRefreshAttacher.isRefreshing() && isRefreshFromTop) {
            mPullToRefreshAttacher.setRefreshing(true);
        }
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
                                    mPullToRefreshAttacher.setRefreshComplete();
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
                            mPullToRefreshAttacher.setRefreshComplete();
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
        ListViewUtils.smoothScrollListViewToTop(mListView);
        loadFirstPage();
    }

    @Override
    public void onRefreshStarted(View view) {
        loadFirstPage();
    }
}
