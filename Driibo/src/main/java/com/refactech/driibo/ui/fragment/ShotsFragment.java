
package com.refactech.driibo.ui.fragment;

import com.android.volley.Response;
import com.refactech.driibo.data.GsonRequest;
import com.refactech.driibo.type.dribble.Category;
import com.refactech.driibo.type.dribble.Shot;
import com.refactech.driibo.vendor.DribbbleApi;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by Issac on 7/18/13.
 */
public class ShotsFragment extends BaseFragment {
    private String mShotList;

    public static final String EXTRA_LIST_ID = "EXTRA_LIST_ID";

    public static ShotsFragment newInstance(Category list) {
        ShotsFragment fragment = new ShotsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_LIST_ID, list.name());
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        parseArgument();
        executeRequest(new GsonRequest<Shot.ShotsRequestData>(String.format(DribbbleApi.SHOTS_LIST,
                mShotList), Shot.ShotsRequestData.class, null,
                new Response.Listener<Shot.ShotsRequestData>() {
                    @Override
                    public void onResponse(Shot.ShotsRequestData requestData) {
                        ArrayList<Shot> shots = requestData.getShots();
                        for (Shot shot : shots) {
                            Log.e("shot", shot.getImage_url());
                        }
                    }
                }, null));
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void parseArgument() {
        Bundle bundle = getArguments();
        mShotList = bundle.getString(EXTRA_LIST_ID);
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
