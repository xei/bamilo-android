package pt.rocket.framework.objects;


/**
 * Defines a teaser as it is represented in the mobile api.
 * 
 * @author GuilhermeSilva
 * 
 */

public enum TeaserGroupType {
	
	MAIN_ONE_SLIDE(0), STATIC_BANNER(1), PRODUCT_LIST(2), CATEGORIES(3), BRANDS_LIST(4), TOP_BRANDS_LIST(5), UNKNOWN(-1);

	public final int value;

	private TeaserGroupType(int value) {
		this.value = value;
	}

	public static TeaserGroupType byValue(int value) {
		for (TeaserGroupType type : TeaserGroupType.values()) {
			if (type.value == value)
				return type;
		}
		return UNKNOWN;
	}
}
