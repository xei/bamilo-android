/**
 * 
 */
package com.mobile.framework.objects;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.framework.objects.TeaserGroupProducts.TeaserProduct;

import org.json.JSONObject;

/**
 * @author nutzer2
 * 
 */
@Deprecated
public class TeaserGroupProducts extends TeaserSpecification<TeaserProduct> {

	/**
	 * @param type
	 */
	public TeaserGroupProducts() {
		super(TeaserGroupType.PRODUCT_LIST);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mobile.framework.objects.TeaserSpecification#parseData(org.json.JSONObject
	 * )
	 */
	@Override
	protected TeaserProduct parseData(JSONObject object) {
		TeaserProduct teaserProduct = new TeaserProduct();
		teaserProduct.initialize(object);
		return teaserProduct;
	}

	public static class TeaserProduct extends Product implements ITargeting, Parcelable {

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.mobile.framework.objects.ITargetting#getTargetUrl()
		 */
		@Override
		public String getTargetUrl() {
			return getUrl();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.mobile.framework.objects.ITargeting#getTargetType()
		 */
		@Override
		public TargetType getTargetType() {
			return TargetType.PRODUCT;
		}

		/* (non-Javadoc)
		 * @see com.mobile.framework.objects.ITargeting#getTargetTitle()
		 */
		@Override
		public String getTargetTitle() {
			return getName();
		}
		
		public TeaserProduct() {
		    
		}
		
		private TeaserProduct(Parcel in) {
		    super(in);
		}

	    public static final Parcelable.Creator<TeaserProduct> CREATOR = new Parcelable.Creator<TeaserProduct>() {
	        public TeaserProduct createFromParcel(Parcel in) {
	            return new TeaserProduct(in);
	        }

	        public TeaserProduct[] newArray(int size) {
	            return new TeaserProduct[size];
	        }
	    };		
		
	}
	
    /**
     * ########### Parcelable ###########
     * @author sergiopereira
     */
	
	/**
	 * Parcel constructor
	 * @param in
	 */
	public TeaserGroupProducts(Parcel in) {
		super(in);
	}
		
	/**
	 * Create parcelable 
	 */
	public static final Parcelable.Creator<TeaserGroupProducts> CREATOR = new Parcelable.Creator<TeaserGroupProducts>() {
        public TeaserGroupProducts createFromParcel(Parcel in) {
            return new TeaserGroupProducts(in);
        }

        public TeaserGroupProducts[] newArray(int size) {
            return new TeaserGroupProducts[size];
        }
    };

}
