/**
 * 
 */
package pt.rocket.pojo;

import org.json.JSONObject;

import android.os.Parcel;

import pt.rocket.pojo.CategoryTeaserGroup.TeaserCategory;


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
