package com.mobile.newFramework.objects.home.type;

/**
 * Enumeration for teaser target types
 *
 * @author Sergio Pereira
 */
public enum TeaserTargetType {

    CATALOG("catalog"),
    PRODUCT_DETAIL("product_detail"),
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
        try {
            result = valueOf(string.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            //...
        }
        return result;
    }

}
