
package com.refactech.driibo.data;

import com.android.volley.toolbox.ImageLoader;
import com.refactech.driibo.util.ImageUtils;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

/**
 * Created by Issac on 7/19/13.
 */
public class BitmapLruCache extends LruCache<String, Bitmap> implements ImageLoader.ImageCache {

    public BitmapLruCache(int maxSize) {
        super(maxSize);
    }

    @Override
    protected int sizeOf(String key, Bitmap bitmap) {
        return ImageUtils.getBitmapSize(bitmap);
    }

    @Override
    public Bitmap getBitmap(String url) {
        return get(url);
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        put(url, bitmap);
    }
}
