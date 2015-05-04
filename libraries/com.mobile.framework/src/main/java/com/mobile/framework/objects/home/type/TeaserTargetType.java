package com.mobile.framework.objects.home.type;

import android.text.TextUtils;

/**
 * Enumeration for teaser target types
 *
 * @author Sergio Pereira
 */
public enum TeaserTargetType {

    CATALOG("catalog"),
    PDV("product_detail"),
    CAMPAIGN("campaign"),
    STATIC_PAGE("static_page"),
    UNKNOWN;

    private String mType;

    /**
     * Empty constructor
     */
    TeaserTargetType() {
        // ...
    }

    /**
     * Constructor
     * @param type The target key
     */
    TeaserTargetType(String type) {
        mType = type;
    }

    /**
     * Get the target key
     * @return String
     */
    public String getType() {
        return mType;
    }

    /**
     * Get the enum by group key.
     *
     * @param string The group key.
     * @return TeaserTargetType or UNKNOWN.
     */
    public static TeaserTargetType byString(String string) {
        TeaserTargetType result = UNKNOWN;
        for (TeaserTargetType type : TeaserTargetType.values()) {
            if (TextUtils.equals(type.mType, string)) {
                result = type;
                break;
            }
        }
        return result;
    }

}
