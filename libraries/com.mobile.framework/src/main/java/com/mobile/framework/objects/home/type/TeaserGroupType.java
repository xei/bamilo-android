//package com.mobile.framework.objects.home.type;
//
///**
// * Enumeration for teaser group types.
// *
// * @author Sergio Pereira
// */
//public enum TeaserGroupType {
//
//    MAIN_TEASERS("main_teasers"),
//    SMALL_TEASERS("small_teasers"),
//    CAMPAIGNS("campaigns"),
//    SHOP_TEASERS("shop_teasers"),
//    BRAND_TEASERS("brand_teasers"),
//    SHOP_OF_WEEK("shop_of_week"),
//    FEATURED_STORES("featured_stores"),
//    TOP_SELLERS("top_sellers"),
//    UNKNOWN;
//
//    private String mType;
//
//    /**
//     * Empty constructor
//     */
//    TeaserGroupType() {
//        // ...
//    }
//
//    /**
//     * Constructor
//     *
//     * @param type The group key
//     */
//    TeaserGroupType(String type) {
//        mType = type;
//    }
//
//    /**
//     * Get the group key
//     *
//     * @return String
//     */
//    public String getType() {
//        return mType;
//    }
//
//    /**
//     * Get the enum by group key.
//     *
//     * @param string The group key.
//     * @return TeaserGroupType or UNKNOWN.
//     */
//    public static TeaserGroupType byString(String string) {
//        TeaserGroupType result = UNKNOWN;
//        try {
//            result = valueOf(string.toUpperCase());
//        } catch (IllegalArgumentException | NullPointerException e) {
//            //...
//        }
//        return result;
//    }
//
//}
