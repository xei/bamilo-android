/**
 * 
 */
package pt.rocket.framework.objects;

import org.json.JSONObject;
import pt.rocket.framework.objects.BrandsTeaserGroup.TeaserBrand;
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

	public class TeaserBrand extends Brand implements ITargeting {

		/*
		 * (non-Javadoc)
		 * 
		 * @see pt.rocket.framework.objects.ITargetting#getTargetUrl()
		 */
		@Override
		public String getTargetUrl() {
			return getUrl();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see pt.rocket.framework.objects.ITargeting#getTargetType()
		 */
		@Override
		public TargetType getTargetType() {
			return TargetType.BRAND;
		}

		/* (non-Javadoc)
		 * @see pt.rocket.framework.objects.ITargeting#getTargetTitle()
		 */
		@Override
		public String getTargetTitle() {
			return getName();
		}

	}

}
