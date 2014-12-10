/**
 * 
 */
package pt.rocket.framework.objects;

import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;
/**
 * @author Manuel Silva
 * 
 */
public class TeaserGroupBrands extends TeaserSpecification<TeaserBrand> {

	/**
	 * @param type
	 */
	public TeaserGroupBrands() {
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
	
    /**
     * ########### Parcelable ###########
     * @author sergiopereira
     */
	
	/**
	 * Parcel constructor
	 * @param in
	 */
	public TeaserGroupBrands(Parcel in) {
		super(in);
	}
		
	/**
	 * Create parcelable 
	 */
	public static final Parcelable.Creator<TeaserGroupBrands> CREATOR = new Parcelable.Creator<TeaserGroupBrands>() {
        public TeaserGroupBrands createFromParcel(Parcel in) {
            return new TeaserGroupBrands(in);
        }

        public TeaserGroupBrands[] newArray(int size) {
            return new TeaserGroupBrands[size];
        }
    };
	
}
