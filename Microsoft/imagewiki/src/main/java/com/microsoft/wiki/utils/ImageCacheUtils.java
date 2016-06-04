package com.microsoft.wiki.utils;

import android.graphics.Bitmap;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class ImageCacheUtils {
    /**
     * Last argument true for LRU ordering
     */
    private Map<String, Bitmap> mCacheMap = Collections
            .synchronizedMap(new LinkedHashMap<String, Bitmap>(10, 1.5f, true));

    /**
     * Get the bitmap if it is present in the ca
     *
     * @param id image id
     * @return image bitmap
     */
    public Bitmap get(String id) {
        return (mCacheMap.containsKey(id) ? mCacheMap.get(id) : null);
    }

    /**
     * Add the bitmap into cache map
     *
     * @param id     image id
     * @param bitmap image bitmap
     */
    public void put(String id, Bitmap bitmap) {
        try {
            if (mCacheMap.containsKey(id))
                mCacheMap.put(id, bitmap);
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    /**
     * Clear the cache map
     */
    public void clear() {
        try {
            mCacheMap.clear();
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }
}