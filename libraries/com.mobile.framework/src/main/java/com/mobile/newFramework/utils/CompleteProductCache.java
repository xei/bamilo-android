//package com.mobile.newFramework.utils;
//
//import com.mobile.newFramework.objects.product.CompleteProduct;
//
//import java.util.LinkedHashMap;
//
//
//
///**
// * Structure used by the product service to keep track of the allocated complete products in memory cache.
// * @author GuilhermeSilva
// *
// */
//public class CompleteProductCache extends LinkedHashMap<String, CompleteProduct>
//{
//    /**
//     * serial used by the serialization algorithm.
//     */
//    private static final long serialVersionUID = 1L;
//
//    private final int capacity;
//    private long accessCount = 0;
//    private long hitCount = 0;
//
//    /**
//     * Constructor for the completeProductCache.
//     * @param capacity
//     */
//    public CompleteProductCache(int capacity)
//    {
//        super(capacity + 1, 1.1f, true);
//        this.capacity = capacity;
//    }
//
//    /**
//     * Constructor for the completeProductCache.
//     * @param key
//     * @return the complete product
//     */
//    public CompleteProduct get(String key)
//    {
//        accessCount++;
//        if (containsKey(key))
//        {
//            hitCount++;
//        }
//        CompleteProduct value = super.get(key);
//        return value;
//    }
//
////    /* (non-Javadoc)
////     * @see java.util.LinkedHashMap#removeEldestEntry(java.util.Map.Entry)
////     */
////    @Override
////    protected boolean removeEldestEntry(Entry<String, CompleteProduct> eldest) {
////
////        return size() > capacity;
////
////    }
//
//    /**
//     * @return the access count of the product cache.
//     */
//    public long getAccessCount()
//    {
//        return accessCount;
//    }
//
//    /**
//     * @return the hit count of the product cache.
//     */
//    public long getHitCount()
//    {
//        return hitCount;
//    }
//
//    /**
//     * @return the capacity
//     */
//    public int getCapacity() {
//        return capacity;
//    }
//
//    /**
//     * removes the eldest entry.
//     */
//    public void removeEldest() {
//        String eldestKey = "";
//        for(String key: this.keySet()){
//            eldestKey = key;
//        }
//        this.remove(eldestKey);
//    }
//}