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
		return super.getImageUrl();
	}

	@Override
	public String getBrandUrl() {
		return super.getBrandUrl();
	}

	@Override
	public String getTargetUrl() {
		return super.getName();
	}

	@Override
	public String getTargetTitle() {
		return super.getName();
	}
	
	@Override
	public String getImageTableUrl() {
		return super.getImageTableUrl();
	}

}
