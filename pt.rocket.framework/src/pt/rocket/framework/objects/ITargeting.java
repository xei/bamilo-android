/**
 * 
 */
package pt.rocket.framework.objects;


/**
 * Interface that specifies the target of the teaser.
 * @author Ralph Holland-Moritz
 *
 */
public interface ITargeting {
	
	public enum TargetType {
		PRODUCT_LIST(0), 
		PRODUCT(1), 
		CATEGORY(2), 
		BRAND(3),
		CAMPAIGN(4),
		UNKNOWN(-1);

		private final int value;

		private TargetType(int value) {
			this.value = value;
		}

		public static TargetType byValue(int value) {
			for (TargetType type : TargetType.values()) {
				if (type.value == value)
					return type;
			}
			return UNKNOWN;
		}
	}
	
	public String getTargetUrl();
	public TargetType getTargetType();
	public String getTargetTitle();
}
