package com.mobile.framework.objects.home.type;

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

}
