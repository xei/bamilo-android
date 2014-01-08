/**
 * 
 */
package pt.rocket.framework.objects;

import org.json.JSONObject;
/**
 * @author Manuel Silva
 * 
 */
public class BrandsTeaserGroup extends TeaserSpecification<TeaserBrand> {

	/**
	 * @param type
	 */
	public BrandsTeaserGroup() {
		super(TeaserGroupType.BRANDS_LIST);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * pt.rocket.framework.objects.TeaserSpecification#parseData(org.json.JSONObject
	 * )
	 */
	@Override
	protected TeaserBrand parseData(JSONObject object) {
		TeaserBrand teaserBrand = new TeaserBrand();
		teaserBrand.initialize(object);
		return teaserBrand;
	}
	
}
