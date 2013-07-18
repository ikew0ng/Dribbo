/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.refactech.driibo.data;

import com.android.volley.toolbox.ImageLoader;
import com.refactech.driibo.AppData;
import com.refactech.driibo.util.ImageUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.support.v4.util.LruCache;

import java.io.File;

/**
 * This class holds our bitmap caches (memory and disk).
 */
public class ImageCache implements ImageLoader.ImageCache {

    // Default memory cache size
    private static final int DEFAULT_MEM_CACHE_SIZE = 1024 * 1024 * 5; // 5MB

    // Default disk cache size
    private static final int DEFAULT_DISK_CACHE_SIZE = 1024 * 1024 * 10; // 10MB

    private static final int DEFAULT_DISK_CACHE_ITEM_SIZE = -1;

    // Compression settings when writing images to disk cache
    private static final CompressFormat DEFAULT_COMPRESS_FORMAT = CompressFormat.JPEG;

    private static final int DEFAULT_COMPRESS_QUALITY = 100;

    // Constants to easily toggle various caches
    private static final boolean DEFAULT_MEM_CACHE_ENABLED = true;

    private static final boolean DEFAULT_DISK_CACHE_ENABLED = true;

    private static final boolean DEFAULT_CLEAR_DISK_CACHE_ON_START = false;

    private SimpleDiskLruCache mDiskCache;

    private BitmapLruCache mMemoryCache;

    /**
     * Creating a new ImageCache object using the specified parameters.
     * 
     * @param context The context to use
     * @param cacheParams The cache parameters to use to initialize the cache
     */
    public ImageCache(ImageCacheParams cacheParams) {
        init(cacheParams);
    }

    /**
     * Creating a new ImageCache object using the default parameters.
     * 
     * @param context The context to use
     * @param uniqueName A unique name that will be appended to the cache
     *            directory
     */
    public ImageCache(String uniqueName) {
        init(new ImageCacheParams(uniqueName));
    }

    public SimpleDiskLruCache getDiskCache() {
        return mDiskCache;
    }

    /**
     * Initialize the cache, providing all parameters.
     * 
     * @param context The context to use
     * @param cacheParams The cache parameters to initialize the cache
     */
    private void init(ImageCacheParams cacheParams) {
        Context context = AppData.getContext();
        final File diskCacheDir = SimpleDiskLruCache.getDiskCacheDir(context,
                cacheParams.uniqueName);

        // Set up disk cache
        if (cacheParams.diskCacheEnabled && diskCacheDir != null) {
            mDiskCache = SimpleDiskLruCache.openCache(context, diskCacheDir,
                    cacheParams.diskCacheItemSize, cacheParams.diskCacheSize);
            if (mDiskCache != null) {
                mDiskCache.setCompressParams(cacheParams.compressFormat,
                        cacheParams.compressQuality);
            }
            if (cacheParams.clearDiskCacheOnStart) {
                mDiskCache.clearCache();
            }
        }

        // Set up memory cache
        if (cacheParams.memoryCacheEnabled) {
            mMemoryCache = new BitmapLruCache(cacheParams.memCacheSize);
        }
    }

    public void addBitmapToDiskCache(String key, Bitmap bitmap) {
        if (bitmap == null) {
            return;
        }
        // Add to disk cache
        if (mDiskCache != null) {
            mDiskCache.put(key, bitmap);
        }
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (bitmap == null) {
            return;
        }
        // Add to memory cache
        if (mMemoryCache != null && mMemoryCache.get(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    /**
     * Get from memory cache.
     * 
     * @param data Unique identifier for which item to get
     * @return The bitmap if found in cache, null otherwise
     */
    public Bitmap getBitmapFromMemCache(String key) {
        if (mMemoryCache != null && key != null) {
            final Bitmap memBitmap = mMemoryCache.get(key);
            if (memBitmap != null) {
                return memBitmap;
            }
        }
        return null;
    }

    /**
     * Get from disk cache.
     * 
     * @param data Unique identifier for which item to get
     * @return The bitmap if found in cache, null otherwise
     */
    public Bitmap getBitmapFromDiskCache(String key, int width, int height) {
        if (mDiskCache != null) {
            return ImageUtils.decodeSampledBitmapFromFile(mDiskCache.get(key), width, height);
        }

        return null;
    }

    public void clearMemoryCache() {
        mMemoryCache.evictAll();
    }

    public void clearCaches() {
        mDiskCache.clearCache();
        mMemoryCache.evictAll();
    }

    @Override
    public Bitmap getBitmap(String key) {
        Bitmap bitmap = getBitmapFromMemCache(key);
        if (bitmap != null) {
            return bitmap;
        }
        bitmap = getBitmapFromDiskCache(key, 0, 0);
        if (bitmap != null) {
            addBitmapToMemoryCache(key, bitmap);
        }
        return bitmap;
    }

    @Override
    public void putBitmap(String key, Bitmap bitmap) {
        if (key == null || bitmap == null) {
            return;
        }

        addBitmapToMemoryCache(key, bitmap);

        addBitmapToDiskCache(key, bitmap);
    }

    public static class BitmapLruCache extends LruCache<String, Bitmap> implements
            ImageLoader.ImageCache {

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

    /**
     * A holder class that contains cache parameters.
     */
    public static class ImageCacheParams {
        public String uniqueName;

        public int memCacheSize = DEFAULT_MEM_CACHE_SIZE;

        public int diskCacheSize = DEFAULT_DISK_CACHE_SIZE;

        public int diskCacheItemSize = DEFAULT_DISK_CACHE_ITEM_SIZE;

        public CompressFormat compressFormat = DEFAULT_COMPRESS_FORMAT;

        public int compressQuality = DEFAULT_COMPRESS_QUALITY;

        public boolean memoryCacheEnabled = DEFAULT_MEM_CACHE_ENABLED;

        public boolean diskCacheEnabled = DEFAULT_DISK_CACHE_ENABLED;

        public boolean clearDiskCacheOnStart = DEFAULT_CLEAR_DISK_CACHE_ON_START;

        public ImageCacheParams(String uniqueName) {
            this.uniqueName = uniqueName;
        }
    }
}
