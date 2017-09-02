package com.mobile.service.utils.cache;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mobile.service.utils.CollectionUtils;
import com.mobile.service.utils.TextUtils;

import java.util.HashSet;

/**
 * Class used to save the wish list items on memory.
 * @author spereira
 */
public class WishListCache {

    /**
     * This Map is used to save wish list value on PDV to update the catalog onRecover from background.
     */
    private static HashSet<String> sCacheMap;

    /**
     * Get the current wish list from cache.
     */
    @NonNull
    public static synchronized HashSet<String> data() {
        return sCacheMap == null ? sCacheMap = new HashSet<>() : sCacheMap;
    }

    /**
     * Set a new map.
     */
    public static void set(@Nullable HashSet<String> newWishListCache) {
        sCacheMap = newWishListCache;
    }

    /**
     * Add an item.
     */
    public static void add(@Nullable String sku) {
        if(TextUtils.isNotEmpty(sku)) data().add(sku);
    }

    /**
     * Remove an item.
     */
    public static void remove(@Nullable String sku) {
        if(TextUtils.isNotEmpty(sku)) data().remove(sku);
    }

    /**
     * Clean cache
     */
    public static void clean() {
        data().clear();
        sCacheMap = null;
    }

    /**
     * Validate sku.
     */
    public static boolean has(String mSku) {
        return mSku != null && data().contains(mSku);
    }

    public static int size() {
        return CollectionUtils.size(sCacheMap);
    }

}
