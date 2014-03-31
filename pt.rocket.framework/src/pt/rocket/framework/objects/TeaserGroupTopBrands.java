/**
 * 
 */
package pt.rocket.framework.objects;

import org.json.JSONObject;

import pt.rocket.framework.objects.TeaserGroupTopBrands.TeaserTopBrand;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class used to represent a group of top brands
 * @author sergiopereira
 *
 */
public class TeaserGroupTopBrands extends TeaserSpecification<TeaserTopBrand> {

	/**
	 * @param type
	 */
	public TeaserGroupTopBrands() {
		super(TeaserGroupType.TOP_BRANDS_LIST);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * pt.rocket.framework.objects.TeaserSpecification#parseData(org.json.JSONObject
	 * )
	 */
	@Override
	protected TeaserTopBrand parseData(JSONObject object) {
		TeaserTopBrand topBrand = new TeaserTopBrand();
		topBrand.initialize(object);
		return topBrand;
	}
	
	/**
	 * Teaser top brands class
	 * @author sergiopereira
	 *
	 */
	public class TeaserTopBrand extends Category implements ITargeting {
		
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
			return super.getName();
		}
		
		/*
		 * (non-Javadoc)
		 * 
		 * @see pt.rocket.framework.objects.ITargetting#getTargetUrl()
		 */
		@Override
		public String getTargetUrl() {
			// Return the brand api url
			//return super.getApiUrl();
			// Return the name used to search for this brand
			return super.getName();
		}
		
	}
	
	
    /**
     * ########### Parcelable ###########
     * @author sergiopereira
     */
    
    /*
     * (non-Javadoc)
     * @see android.os.Parcelable#describeContents()
     */
	@Override
	public int describeContents() {
		return super.describeContents();
	}

	/*
	 * (non-Javadoc)
	 * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
	 */
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
	}
	
	
    /**
     * ########### Parcelable ###########
     * @author sergiopereira
     */
	
	/**
	 * Parcel constructor
	 * @param in
	 */
	public TeaserGroupTopBrands(Parcel in) {
		super(in);
	}
		
	/**
	 * Create parcelable 
	 */
	public static final Parcelable.Creator<TeaserGroupTopBrands> CREATOR = new Parcelable.Creator<TeaserGroupTopBrands>() {
        public TeaserGroupTopBrands createFromParcel(Parcel in) {
            return new TeaserGroupTopBrands(in);
        }

        public TeaserGroupTopBrands[] newArray(int size) {
            return new TeaserGroupTopBrands[size];
        }
    };

}
