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
public interface ITargeting {

    public enum TargetType {
        CATALOG(0),
        PRODUCT(1),
        CAMPAIGN(2),
        BRAND(3),
        SHOP(4),
        CATEGORY(5),
        UNKNOWN(-1);

        private final int value;

        private TargetType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static TargetType byValue(int value) {
            for (TargetType type : TargetType.values()) {
                if (type.value == value) {
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
