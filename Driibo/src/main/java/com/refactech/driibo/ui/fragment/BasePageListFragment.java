
package com.refactech.driibo.ui.fragment;

import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.haarman.listviewanimations.swinginadapters.AnimationAdapter;
import com.refactech.driibo.R;
import com.refactech.driibo.data.GsonRequest;
import com.refactech.driibo.ui.adapter.CardsAnimationAdapter;
import com.refactech.driibo.util.CommonUtils;
import com.refactech.driibo.util.ListViewUtils;
import com.refactech.driibo.view.LoadingFooter;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.Options;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

/**
 * Created by Issac on 7/31/13.
 */
public abstract class BasePageListFragment<T> extends BaseFragment implements
        OnRefreshListener {
    private BaseAdapter mAdapter;

    protected ListView mListView;

    protected int mPage = 1;

    private PullToRefreshLayout mPullToRefreshLayout;

    private LoadingFooter mLoadingFooter;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewGroup viewGroup = (ViewGroup) view;
        mPullToRefreshLayout = new PullToRefreshLayout(viewGroup.getContext());
        ActionBarPullToRefresh.from(getActivity())
                .insertLayoutInto(viewGroup)
                .theseChildrenArePullable(R.id.listView)
                .listener(this)
                .options(Options.create()
                        .scrollDistance(0.3f)
                        .build())
                .setup(mPullToRefreshLayout);
        mPullToRefreshLayout.setRefreshing(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(getContentViewResId(), null);
        mListView = (ListView) contentView.findViewById(R.id.listView);
        View header = new View(getActivity());
//        mPullToRefreshLayout = ((MainActivity) getActivity()).getPullToRefreshLayout();
//        ActionBarPullToRefresh.from(getActivity())
//                .allChildrenArePullable()
//                .listener(this)
//                .options(Options.create()
//                        .scrollDistance(0.3f)
//                        .headerLayout(R.layout.pulldown_header)
//                        .build())
//                .setup(mPullToRefreshLayout);
        mLoadingFooter = new LoadingFooter(getActivity());

        mListView.addHeaderView(header);
        mListView.addFooterView(mLoadingFooter.getView());
        mAdapter = newAdapter();
        AnimationAdapter animationAdapter = new CardsAnimationAdapter(mAdapter);
        animationAdapter.setListView(mListView);
        mListView.setAdapter(animationAdapter);

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
                                + mListView.getFooterViewsCount() && mListView.getCount() > 0) {
                    loadNextPage();
                }
            }
        });
        return contentView;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mPullToRefreshLayout.getHeaderTransformer().onConfigurationChanged(getActivity(), newConfig);
    }

    protected void loadData(final int page) {
        final boolean isRefreshFromTop = page == 1;
        if (!mPullToRefreshLayout.isRefreshing() && isRefreshFromTop) {
            mPullToRefreshLayout.setRefreshing(true);
        }
        executeRequest(new GsonRequest<T>(getUrl(page), getResponseDataClass(), null,
                new Response.Listener<T>() {
                    @Override
                    public void onResponse(final T response) {
                        CommonUtils.executeAsyncTask(new AsyncTask<Object, Object, Object>() {
                            @Override
                            protected Object doInBackground(Object... params) {
                                processData(response);
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Object o) {
                                super.onPostExecute(o);
                                if (isRefreshFromTop) {
                                    mPullToRefreshLayout.setRefreshComplete();
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
                            mPullToRefreshLayout.setRefreshComplete();
                        } else {
                            mLoadingFooter.setState(LoadingFooter.State.Idle, 3000);
                        }
                    }
                }));
    }

    protected void loadNextPage() {
        mLoadingFooter.setState(LoadingFooter.State.Loading);
        loadData(mPage + 1);
    }

    protected void loadFirstPage() {
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

    protected BaseAdapter getAdapter() {
        return mAdapter;
    }

    protected abstract int getContentViewResId();

    protected abstract BaseAdapter newAdapter();

    protected abstract void processData(T response);

    protected abstract String getUrl(int page);

    protected abstract Class getResponseDataClass();

}
