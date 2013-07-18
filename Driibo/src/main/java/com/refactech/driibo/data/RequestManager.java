
package com.refactech.driibo.data;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.refactech.driibo.AppData;

/**
 * Created by Issac on 7/18/13.
 */
public class RequestManager {
    private static RequestQueue mRequestQueue = Volley.newRequestQueue(AppData.getContext());

    // private static ImageLoader mImageLoader = new ImageLoader(mRequestQueue,
    // new ImageCache(
    // new ImageCache.ImageCacheParams("Driibo")));
    private static ImageLoader mImageLoader = new ImageLoader(mRequestQueue,
            new ImageCache.BitmapLruCache(1024 * 1024 * 5));

    public static void addRequest(Request request, Object tag) {
        if (tag != null) {
            request.setTag(tag);
        }
        mRequestQueue.add(request);
    }

    public static void cancelAll(Object tag) {
        mRequestQueue.cancelAll(tag);
    }

    public static ImageLoader.ImageContainer loadImage(String requestUrl,
            ImageLoader.ImageListener imageListener) {
        return loadImage(requestUrl, imageListener, 0, 0);
    }

    public static ImageLoader.ImageContainer loadImage(String requestUrl,
            ImageLoader.ImageListener imageListener, int maxWidth, int maxHeight) {
        return mImageLoader.get(requestUrl, imageListener, maxWidth, maxHeight);
    }

}
