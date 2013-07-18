
package com.refactech.driibo.data;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.refactech.driibo.DriiboApp;

/**
 * Created by Issac on 7/18/13.
 */
public class RequestManager {
    private static RequestQueue mRequestQueue = Volley.newRequestQueue(DriiboApp.getContext());;

    public static void addRequest(Request request, Object tag) {
        if (tag != null) {
            request.setTag(tag);
        }
        mRequestQueue.add(request);
    }

    public static void cancelAll(Object tag) {
        mRequestQueue.cancelAll(tag);
    }

}
