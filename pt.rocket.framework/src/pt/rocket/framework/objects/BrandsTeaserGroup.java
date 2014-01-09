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
	
    /**
     * ########### Parcelable ###########
     * @author sergiopereira
     */
	
	/**
	 * Parcel constructor
	 * @param in
	 */
	public BrandsTeaserGroup(Parcel in) {
		super(in);
	}
		
	/**
	 * Create parcelable 
	 */
	public static final Parcelable.Creator<BrandsTeaserGroup> CREATOR = new Parcelable.Creator<BrandsTeaserGroup>() {
        public BrandsTeaserGroup createFromParcel(Parcel in) {
            return new BrandsTeaserGroup(in);
        }

        public BrandsTeaserGroup[] newArray(int size) {
            return new BrandsTeaserGroup[size];
        }
    };
	
}
