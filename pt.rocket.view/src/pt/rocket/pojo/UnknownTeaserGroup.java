/**
 * 
 */
package pt.rocket.pojo;

import org.json.JSONObject;

import android.os.Parcel;

/**
 * @author nutzer2
 *
 */
public class UnknownTeaserGroup extends TeaserSpecification<ITargeting> {
	
	public static final UnknownTeaserGroup INSTANCE = new UnknownTeaserGroup();

	/**
	 * @param type
	 */
	private UnknownTeaserGroup() {
		super(TeaserGroupType.UNKNOWN);
	}

	/* (non-Javadoc)
	 * @see pt.rocket.framework.objects.TeaserSpecification#parseData(org.json.JSONObject)
	 */
	@Override
	protected ITargeting parseData(JSONObject object) {
		return null;
	}
	
	/* (non-Javadoc)
	 * @see pt.rocket.framework.objects.TeaserSpecification#initialize(org.json.JSONObject)
	 */
	@Override
	public boolean initialize(JSONObject jsonObject) {
		return false;
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
