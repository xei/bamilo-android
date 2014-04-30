/**
 * 
 */
package pt.rocket.framework.objects;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.framework.rest.RestConstants;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * Class that represent a campaign page
 * @author sergiopereira
 *
 */
public class Campaign implements IJSONSerializable, Parcelable {

	private static final String TAG = Campaign.class.getSimpleName();

	private String mBanner;

	private String mName;

	private String mStartTime;

	private String mEndTime;

	private int mCount;

	private ArrayList<CampaignItem> mItems = new ArrayList<CampaignItem>();

	/**
	 * Empty constructor
	 */
	public Campaign() { 
		// ...
	}
	
	/**
	 * Constructor
	 * @param jsonObject
	 * @throws JSONException
	 */
	public Campaign(JSONObject jsonObject) throws JSONException { 
		initialize(jsonObject);
	}
	
	/*
	 * (non-Javadoc)
	 * @see pt.rocket.framework.objects.IJSONSerializable#initialize(org.json.JSONObject)
	 */
	@Override
	public boolean initialize(JSONObject jsonObject) throws JSONException {
		Log.d(TAG, "ON INITIALIZE");
		// Get banner
		JSONObject cmsO = jsonObject.optJSONObject(RestConstants.JSON_CMS_TAG);
		JSONArray bannerA = (cmsO != null) ? cmsO.optJSONArray(RestConstants.JSON_MOBILE_BANNER_TAG) : null;
		mBanner = (bannerA != null && bannerA.length() > 0) ? bannerA.get(0).toString() : null;
	    // Get campaign
		JSONObject campaignO = jsonObject.getJSONObject(RestConstants.JSON_CAMPAIGN_TAG);
		// Get name
		mName = campaignO.optString(RestConstants.JSON_NAME_TAG);
		// Get start time
		mStartTime = campaignO.optString(RestConstants.JSON_START_TIME_TAG);
		// Get end time
		mEndTime = campaignO.optString(RestConstants.JSON_END_TIME_TAG);
		// Get product count
		mCount = campaignO.optInt(RestConstants.JSON_PRODUCT_COUNT_TAG);
		// Get data
		JSONArray itemsA = campaignO.getJSONArray(RestConstants.JSON_DATA_TAG);
		for (int i = 0; i < itemsA.length(); i++)
			mItems.add(new CampaignItem(itemsA.getJSONObject(i)));
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return mName + " " + mBanner + " " + mStartTime + " " + mEndTime + " " + mCount;
	}
	
	/*
	 * (non-Javadoc)
	 * @see pt.rocket.framework.objects.IJSONSerializable#toJSON()
	 */
	@Override
	public JSONObject toJSON() {
		return null;
	}

	/*
     * ########### Getters ###########
     */
    /**
	 * @return the mBanner
	 */
	public String getBanner() {
		return mBanner;
	}

	/**
	 * @return the mName
	 */
	public String getName() {
		return mName;
	}

	/**
	 * @return the mStartTime
	 */
	public String getStartTime() {
		return mStartTime;
	}

	/**
	 * @return the mEndTime
	 */
	public String getEndTime() {
		return mEndTime;
	}

	/**
	 * @return the mCount
	 */
	public int getCount() {
		return mCount;
	}

	/**
	 * @return the mItems
	 */
	public ArrayList<CampaignItem> getItems() {
		return mItems;
	}

	
	/*
     * ########### Setters ###########
     */
	/**
	 * @param mBanner the mBanner to set
	 */
	public void setBanner(String mBanner) {
		this.mBanner = mBanner;
	}

	/**
	 * @param mName the mName to set
	 */
	public void setName(String mName) {
		this.mName = mName;
	}

	/**
	 * @param mStartTime the mStartTime to set
	 */
	public void setStartTime(String mStartTime) {
		this.mStartTime = mStartTime;
	}

	/**
	 * @param mEndTime the mEndTime to set
	 */
	public void setEndTime(String mEndTime) {
		this.mEndTime = mEndTime;
	}

	/**
	 * @param mCount the mCount to set
	 */
	public void setCount(int mCount) {
		this.mCount = mCount;
	}

	/**
	 * @param mItems the mItems to set
	 */
	public void setItems(ArrayList<CampaignItem> mItems) {
		this.mItems = mItems;
	}

	/**
     * ########### Parcelable ###########
     */
	/*
	 * (non-Javadoc)
	 * @see android.os.Parcelable#describeContents()
	 */
	@Override
	public int describeContents() {
		return 0;
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
	 */
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(mBanner);
		dest.writeString(mName);
		dest.writeString(mStartTime);
		dest.writeString(mEndTime);
		dest.writeInt(mCount);
		dest.writeList(mItems);
	}
	
	/**
	 * Parcel constructor
	 * @param in
	 */
	public Campaign(Parcel in) {
		mBanner = in.readString();
		mName = in.readString();
		mStartTime = in.readString();
		mEndTime = in.readString();
		mCount = in.readInt();
		in.readList(mItems, CampaignItem.class.getClassLoader());
	}
	
	/**
	 * Create parcelable 
	 */
	public static final Parcelable.Creator<Campaign> CREATOR = new Parcelable.Creator<Campaign>() {
        public Campaign createFromParcel(Parcel in) {
            return new Campaign(in);
        }

        public Campaign[] newArray(int size) {
            return new Campaign[size];
        }
    };

}
