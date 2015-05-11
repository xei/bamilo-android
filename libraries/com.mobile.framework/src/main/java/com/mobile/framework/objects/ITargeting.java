/**
 *
 */
package com.mobile.framework.objects;


/**
 * Interface that specifies the target of the teaser.
 *
 * @author Ralph Holland-Moritz
 * @modified sergiopereira
 */
@Deprecated
public interface ITargeting {

    public enum TargetType {
        CATALOG("catalog"),
        PRODUCT("product_detail"),
        CAMPAIGN("campaign"),
        BRAND("brands"),
        SHOP("static_page"),
        CATEGORY("categories"),
        UNKNOWN("unknown");

        private final String value;

        private TargetType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static TargetType byValue(String value) {
            for (TargetType type : TargetType.values()) {
                if (type.value.equals(value)) {
                    return type;
                }
            }
            return UNKNOWN;
        }
    }

    public String getTargetUrl();

    public TargetType getTargetType();

    public String getTargetTitle();
}
