/**
 * 
 */
package pt.rocket.framework.objects;


/**
 * @author Manuel Silva
 * 
 */

public class TeaserBrand extends TeaserBrandElement implements ITargeting {
	/*
	 * (non-Javadoc)
	 * 
	 * @see pt.rocket.framework.objects.ITargeting#getTargetType()
	 */
	@Override
	public TargetType getTargetType() {
		return TargetType.BRAND;
	}

	public String getImage() {
		return getImageUrl();
	}

	@Override
	public String getBrandUrl() {
		return getBrandUrl();
	}

	@Override
	public String getTargetUrl() {
		return getName();
	}

	@Override
	public String getTargetTitle() {
		return getName();
	}
	
	@Override
	public String getImageTableUrl() {
		return super.getImageTableUrl();
	}

}
