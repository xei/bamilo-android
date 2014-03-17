/**
 * 
 */
package pt.rocket.framework.objects;

import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

import pt.rocket.framework.objects.ProductTeaserGroup.TeaserProduct;

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
	
    /**
     * ########### Parcelable ###########
     * @author sergiopereira
     */
	
	/**
	 * Parcel constructor
	 * @param in
	 */
	public ProductTeaserGroup(Parcel in) {
		super(in);
	}
		
	/**
	 * Create parcelable 
	 */
	public static final Parcelable.Creator<ProductTeaserGroup> CREATOR = new Parcelable.Creator<ProductTeaserGroup>() {
        public ProductTeaserGroup createFromParcel(Parcel in) {
            return new ProductTeaserGroup(in);
        }

        public ProductTeaserGroup[] newArray(int size) {
            return new ProductTeaserGroup[size];
        }
    };

}
