package com.bamilo.android.framework.service.objects.catalog;


import com.bamilo.android.framework.service.utils.TextUtils;

/**
 * Interface that specifies the target of the teaser.
 *
 * @author Ralph Holland-Moritz
 * @modified sergiopereira
 * @see com.mobile.service.objects.home.type.TeaserTargetType
 */
@Deprecated
public interface ITargeting {

    enum TargetType {
        CATALOG("catalog"),
        PRODUCT("product_detail"),
        CAMPAIGN("campaign"),
        BRAND("brands"),
        SHOP("static_page"),
        CATEGORY("categories"),
        UNKNOWN("unknown");

        private final String value;

        TargetType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static TargetType byValue(String value) {
            for (TargetType type : TargetType.values()) {
                if (TextUtils.equals(type.value, value)) {
                    return type;
                }
            }
            return UNKNOWN;
        }
    }

    String getTargetValue();

    TargetType getTargetType();

    String getTargetTitle();
}
