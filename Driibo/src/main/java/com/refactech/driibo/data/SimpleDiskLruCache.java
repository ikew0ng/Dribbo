
package com.refactech.driibo.data;

import com.refactech.driibo.BuildConfig;
import com.refactech.driibo.util.CommonUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.*;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class SimpleDiskLruCache {
    private static final String TAG = "DiskLruCache";

    private static final String CACHE_FILENAME_PREFIX = "cache_";

    private static final int INITIAL_CAPACITY = 32;

    private static final float LOAD_FACTOR = 0.75f;

    private final File mCacheDir;

    private int cacheSize = 0;

    private int cacheByteSize = 0;

    private int maxCacheItemSize = -1; // 64 item default

    private long maxCacheByteSize = 1024 * 1024 * 5; // 5MB default

    private CompressFormat mCompressFormat = CompressFormat.JPEG;

    private int mCompressQuality = 100;

    private final Map<String, String> mLinkedHashMap = Collections
            .synchronizedMap(new LinkedHashMap<String, String>(INITIAL_CAPACITY, LOAD_FACTOR, true));

    /**
     * A filename filter to use to identify the cache filenames which have
     * CACHE_FILENAME_PREFIX prepended.
     */
    private static final FilenameFilter cacheFileFilter = new FilenameFilter() {
        @Override
        public boolean accept(File dir, String filename) {
            return filename.startsWith(CACHE_FILENAME_PREFIX);
        }
    };

    /**
     * Used to fetch an instance of DiskLruCache.
     * 
     * @param context
     * @param cacheDir
     * @param maxByteSize
     * @return
     */
    public static SimpleDiskLruCache openCache(Context context, String cacheDir, int maxItemSize,
            long maxByteSize) {
        return openCache(context, getDiskCacheDir(context, cacheDir), maxItemSize, maxByteSize);
    }

    /**
     * Used to fetch an instance of DiskLruCache.
     * 
     * @param context
     * @param cacheDir
     * @param maxByteSize
     * @return
     */
    public static SimpleDiskLruCache openCache(Context context, File cacheDir, int maxItemSize,
            long maxByteSize) {
        if (!cacheDir.exists()) {
            cacheDir.mkdir();
        }

        if (cacheDir.isDirectory() && cacheDir.canWrite()
                && CacheUtils.getUsableSpace(cacheDir) > maxByteSize) {
            return new SimpleDiskLruCache(cacheDir, maxItemSize, maxByteSize);
        }

        return null;
    }

    /**
     * Constructor that should not be called directly, instead use
     * {@link DiskLruCache#openCache(android.content.Context, java.io.File, long)}
     * which runs some extra checks before creating a DiskLruCache instance.
     * 
     * @param cacheDir
     * @param maxByteSize
     */
    private SimpleDiskLruCache(File cacheDir, int maxItemSize, long maxByteSize) {
        mCacheDir = cacheDir;
        maxCacheItemSize = maxItemSize;
        maxCacheByteSize = maxByteSize;
        readCacheFile();
    }

    private void readCacheFile() {

        AsyncTask<Void, Void, Void> readAsyncTask = new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                File[] cacheFiles = mCacheDir.listFiles();
                for (File file : cacheFiles) {
                    put(file.getName(), file);
                }
                return null;
            }
        };

        CommonUtils.executeAsyncTask(readAsyncTask);
    }

    /**
     * Add a bitmap to the disk cache.
     * 
     * @param key A unique identifier for the bitmap.
     * @param data The bitmap to store.
     */
    public void put(String key, Bitmap data) {
        synchronized (mLinkedHashMap) {
            if (mLinkedHashMap.get(key) == null) {
                try {
                    final String file = createFilePath(mCacheDir, key);
                    if (writeBitmapToFile(data, file)) {
                        put(key, file);
                        flushCache();
                    }
                } catch (final FileNotFoundException e) {
                    Log.e(TAG, "Error in put: " + e.getMessage());
                } catch (final IOException e) {
                    Log.e(TAG, "Error in put: " + e.getMessage());
                }
            }
        }
    }

    public void put(String key, File file) {
        synchronized (mLinkedHashMap) {
            if (mLinkedHashMap.get(key) == null) {
                put(key, file.getAbsolutePath());
                flushCache();
            }
        }
    }

    private void put(String key, String file) {
        mLinkedHashMap.put(key, file);
        cacheSize = mLinkedHashMap.size();
        cacheByteSize += new File(file).length();
    }

    /**
     * Flush the cache, removing oldest entries if the total size is over the
     * specified cache size. Note that this isn't keeping track of stale files
     * in the cache directory that aren't in the HashMap. If the images and keys
     * in the disk cache change often then they probably won't ever be removed.
     */
    private void flushCache() {
        Entry<String, String> eldestEntry;
        File eldestFile = null;
        long eldestFileSize = 0L;

        while ((cacheSize > maxCacheItemSize && maxCacheItemSize > 0)
                || cacheByteSize > maxCacheByteSize) {
            try {
                eldestEntry = mLinkedHashMap.entrySet().iterator().next();
                eldestFile = new File(eldestEntry.getValue());
                eldestFileSize = eldestFile.length();
                mLinkedHashMap.remove(eldestEntry.getKey());
                eldestFile.delete();
                cacheSize = mLinkedHashMap.size();
                cacheByteSize -= eldestFileSize;
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "flushCache - Removed cache file, " + eldestFile + ", " + eldestFileSize);
            }
        }
    }

    /**
     * 通过key获取文件路径，不存在该文件返回null
     * 
     * @param key
     * @return
     */
    public String get(String key) {
        synchronized (mLinkedHashMap) {
            String path = mLinkedHashMap.get(key);
            if (path != null) {
                if (new File(path).exists()) {
                    return path;
                } else {
                    mLinkedHashMap.remove(key);
                }
            } else {
                path = createFilePath(mCacheDir, key);
                if (new File(path).exists()) {
                    put(key, path);
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "Disk cache hit (existing file)");
                    }
                    return path;
                }
            }
            return null;
        }
    }

    /**
     * Checks if a specific key exist in the cache.
     * 
     * @param key The unique identifier for the bitmap
     * @return true if found, false otherwise
     */
    public boolean containsKey(String key) {
        // See if the key is in our HashMap
        if (mLinkedHashMap.containsKey(key)) {
            return true;
        }

        // Now check if there's an actual file that exists based on the key
        final String existingFile = createFilePath(mCacheDir, key);
        if (new File(existingFile).exists()) {
            // File found, add it to the HashMap for future use
            put(key, existingFile);
            return true;
        }
        return false;
    }

    /**
     * Removes all disk cache entries from this instance cache dir
     */
    public void clearCache() {
        SimpleDiskLruCache.clearCache(mCacheDir);
    }

    /**
     * Removes all disk cache entries from the application cache directory in
     * the uniqueName sub-directory.
     * 
     * @param context The context to use
     * @param uniqueName A unique cache directory name to append to the app
     *            cache directory
     */
    public static void clearCache(Context context, String uniqueName) {
        File cacheDir = getDiskCacheDir(context, uniqueName);
        clearCache(cacheDir);
    }

    /**
     * Removes all disk cache entries from the given directory. This should not
     * be called directly, call
     * {@link DiskLruCache#clearCache(android.content.Context, String)} or
     * {@link DiskLruCache#clearCache()} instead.
     * 
     * @param cacheDir The directory to remove the cache files from
     */
    private static void clearCache(File cacheDir) {
        final File[] files = cacheDir.listFiles(cacheFileFilter);
        for (int i = 0; i < files.length; i++) {
            files[i].delete();
        }
    }

    /**
     * Get a usable cache directory (external if available, internal otherwise).
     * 
     * @param context The context to use
     * @param uniqueName A unique directory name to append to the cache dir
     * @return The cache dir
     */
    public static File getDiskCacheDir(Context context, String uniqueName) {

        // Check if media is mounted or storage is built-in, if so, try and use
        // external cache dir
        // otherwise use internal cache dir
        String cachePath = null;
        try {
            cachePath = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
                    || !CacheUtils.isExternalStorageRemovable() ? CacheUtils.getExternalCacheDir(
                    context).getPath() : context.getCacheDir().getPath();
        } catch (Exception e) {
            return null;
        }

        return new File(cachePath + File.separator + uniqueName);
    }

    /**
     * Creates a constant cache file path given a target cache directory and an
     * image key.
     * 
     * @param cacheDir
     * @param key
     * @return
     */
    public static String createFilePath(File cacheDir, String key) {
        try {
            // Use URLEncoder to ensure we have a valid filename, a tad hacky
            // but it will do for
            // this example
            return cacheDir.getAbsolutePath() + File.separator + CACHE_FILENAME_PREFIX
                    + URLEncoder.encode(key.replace("*", ""), "UTF-8");
        } catch (final UnsupportedEncodingException e) {
            Log.e(TAG, "createFilePath - " + e);
        }

        return null;
    }

    /**
     * Create a constant cache file path using the current cache directory and
     * an image key.
     * 
     * @param key
     * @return
     */
    public String createFilePath(String key) {
        return createFilePath(mCacheDir, key);
    }

    /**
     * Sets the target compression format and quality for images written to the
     * disk cache.
     * 
     * @param compressFormat
     * @param quality
     */
    public void setCompressParams(CompressFormat compressFormat, int quality) {
        mCompressFormat = compressFormat;
        mCompressQuality = quality;
    }

    /**
     * Writes a bitmap to a file. Call
     * {@link DiskLruCache#setCompressParams(android.graphics.Bitmap.CompressFormat, int)}
     * first to set the target bitmap compression and format.
     * 
     * @param bitmap
     * @param file
     * @return
     */
    private boolean writeBitmapToFile(Bitmap bitmap, String file) throws IOException,
            FileNotFoundException {

        OutputStream out = null;
        try {
            out = new BufferedOutputStream(new FileOutputStream(file), CacheUtils.IO_BUFFER_SIZE);
            return bitmap.compress(mCompressFormat, mCompressQuality, out);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }
}
