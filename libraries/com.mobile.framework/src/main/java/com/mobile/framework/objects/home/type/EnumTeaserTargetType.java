package com.mobile.framework.objects.home.type;

import android.text.TextUtils;

/**
 * Enumeration for teaser target types
 *
 * @author Sergio Pereira
 */
public enum EnumTeaserTargetType {

    CATALOG("catalog"),
    PDV("product_detail"),
    CAMPAIGN("campaign"),
    PAGE("static_page"),
    UNKNOWN;

    private String mType;

    /**
     * Empty constructor
     */
    EnumTeaserTargetType() {
        // ...
    }

    /**
     * Constructor
     * @param type The target key
     */
    EnumTeaserTargetType(String type) {
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
     * @return EnumTeaserTargetType or UNKNOWN.
     */
    public static EnumTeaserTargetType byString(String string) {
        EnumTeaserTargetType result = UNKNOWN;
        for (EnumTeaserTargetType type : EnumTeaserTargetType.values()) {
            if (TextUtils.equals(type.mType, string)) {
                result = type;
                break;
            }
        }
        return result;
    }

}
