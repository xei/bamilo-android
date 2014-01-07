/**
 * 
 */
package pt.rocket.pojo;

import org.json.JSONObject;

import android.os.Parcel;

import pt.rocket.framework.objects.Product;
import pt.rocket.pojo.ProductTeaserGroup.TeaserProduct;

/**
 * @author nutzer2
 * 
 */
public class ProductTeaserGroup extends TeaserSpecification<TeaserProduct> {

	/**
	 * @param type
	 */
	public ProductTeaserGroup() {
		super(TeaserGroupType.PRODUCT_LIST);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * pt.rocket.framework.objects.TeaserSpecification#parseData(org.json.JSONObject
	 * )
	 */
	@Override
	protected TeaserProduct parseData(JSONObject object) {
		TeaserProduct teaserProduct = new TeaserProduct();
		teaserProduct.initialize(object);
		return teaserProduct;
	}

	public class TeaserProduct extends Product implements ITargeting {

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
			return TargetType.PRODUCT;
		}

		/* (non-Javadoc)
		 * @see pt.rocket.framework.objects.ITargeting#getTargetTitle()
		 */
		@Override
		public String getTargetTitle() {
			return getName();
		}

	}

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // TODO Auto-generated method stub
        
    }

}
