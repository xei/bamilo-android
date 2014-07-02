/**
 * 
 */
package pt.rocket.framework.objects;

import org.json.JSONObject;

import pt.rocket.framework.objects.CategoryTeaserGroup.TeaserCategory;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author nutzer2
 * 
 */
public class CategoryTeaserGroup extends TeaserSpecification<TeaserCategory> {

	/**
	 * @param type
	 */
	public CategoryTeaserGroup() {
		super(TeaserGroupType.CATEGORIES);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * pt.rocket.framework.objects.TeaserSpecification#parseData(org.json.JSONObject
	 * )
	 */
	@Override
	protected TeaserCategory parseData(JSONObject object) {
		TeaserCategory cat = new TeaserCategory();
		cat.initialize(object);
		return cat;
	}

	public class TeaserCategory extends Category implements ITargeting {

		/*
		 * (non-Javadoc)
		 * 
		 * @see pt.rocket.framework.objects.ITargetting#getTargetUrl()
		 */
		@Override
		public String getTargetUrl() {
			return super.getApiUrl();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see pt.rocket.framework.objects.ITargeting#getTargetType()
		 */
		@Override
		public TargetType getTargetType() {
			return TargetType.PRODUCT_LIST;
		}

		/* (non-Javadoc)
		 * @see pt.rocket.framework.objects.ITargeting#getTargetTitle()
		 */
		@Override
		public String getTargetTitle() {
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
	public CategoryTeaserGroup(Parcel in) {
		super(in);
	}
		
	/**
	 * Create parcelable 
	 */
	public static final Parcelable.Creator<CategoryTeaserGroup> CREATOR = new Parcelable.Creator<CategoryTeaserGroup>() {
        public CategoryTeaserGroup createFromParcel(Parcel in) {
            return new CategoryTeaserGroup(in);
        }

        public CategoryTeaserGroup[] newArray(int size) {
            return new CategoryTeaserGroup[size];
        }
    };

}
