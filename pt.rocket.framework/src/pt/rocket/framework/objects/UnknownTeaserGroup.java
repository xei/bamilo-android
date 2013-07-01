/**
 * 
 */
package pt.rocket.framework.objects;

import org.json.JSONObject;

/**
 * @author nutzer2
 *
 */
public class UnknownTeaserGroup extends TeaserSpecification<ITargeting> {
	
	public static final UnknownTeaserGroup INSTANCE = new UnknownTeaserGroup();

	/**
	 * @param type
	 */
	private UnknownTeaserGroup() {
		super(TeaserGroupType.UNKNOWN);
	}

	/* (non-Javadoc)
	 * @see pt.rocket.framework.objects.TeaserSpecification#parseData(org.json.JSONObject)
	 */
	@Override
	protected ITargeting parseData(JSONObject object) {
		return null;
	}
	
	/* (non-Javadoc)
	 * @see pt.rocket.framework.objects.TeaserSpecification#initialize(org.json.JSONObject)
	 */
	@Override
	public boolean initialize(JSONObject jsonObject) {
		return false;
	}

}
