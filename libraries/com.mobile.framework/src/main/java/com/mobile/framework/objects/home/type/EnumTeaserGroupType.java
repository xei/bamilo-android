package com.mobile.framework.objects.home.type;

import android.text.TextUtils;

/**
 * Enumeration for teaser group types.
 *
 * @author Sergio Pereira
 */
public enum EnumTeaserGroupType {

    MAIN_TEASERS("main_teasers"),
    SMALL_TEASERS("small_teasers"),
    CAMPAIGN_TEASERS("campaigns"),
    SHOP_TEASERS("shop_teasers"),
    BRAND_TEASERS("brand_teasers"),
    SHOP_WEEK_TEASERS("shop_of_week"),
    FEATURED_STORES("featured_stores"),
    TOP_SELLERS("top_sellers"),
    UNKNOWN;

    private String mType;

    /**
     * Empty constructor
     */
    EnumTeaserGroupType() {
        // ...
    }

    /**
     * Constructor
     *
     * @param type The group key
     */
    EnumTeaserGroupType(String type) {
        mType = type;
    }

    /**
     * Get the group key
     *
     * @return String
     */
    public String getType() {
        return mType;
    }

    /**
     * Get the enum by group key.
     *
     * @param string The group key.
     * @return EnumTeaserGroupType or UNKNOWN.
     */
    public static EnumTeaserGroupType byString(String string) {
        EnumTeaserGroupType result = UNKNOWN;
        for (EnumTeaserGroupType type : EnumTeaserGroupType.values()) {
            if (TextUtils.equals(type.mType, string)) {
                result = type;
                break;
            }
        }
        return result;
    }

}
