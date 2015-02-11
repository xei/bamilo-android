/**
 * 
 */
package com.mobile.framework.objects;

import org.json.JSONObject;

import com.mobile.framework.rest.RestConstants;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class that represents a teaser campaign
 * @author sergiopereira
 *
 */
public class TeaserCampaign implements ITargeting, IJSONSerializable, Parcelable {
	
	private String name;
	
	private String url;

	/**
	 * Empty constructor
	 */
	public TeaserCampaign() { }
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mobile.framework.objects.ITargeting#getTargetType()
	 */
	@Override
	public TargetType getTargetType() {
		return TargetType.CAMPAIGN;
	}
	
	/**
	 * Set the campaign title
	 * @param title
	 * @author sergiopereira
	 */
	public void setTitle(String title) { this.name = title; }
	
	/**
	 * Set the campaign url/id 
	 * @param url
	 * @author sergiopereira
	 */
	public void setUrl(String url) { this.url = url; }
	
	/*
	 * (non-Javadoc)
	 * @see com.mobile.framework.objects.IJSONSerializable#initialize(org.json.JSONObject)
	 */
	@Override
	public boolean initialize(JSONObject jsonObject) {
		name = jsonObject.optString(RestConstants.JSON_CAMPAIGN_NAME_TAG);
        url = jsonObject.optString(RestConstants.JSON_CAMPAIGN_URL_TAG);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see com.mobile.framework.objects.IJSONSerializable#toJSON()
	 */
	@Override
	public JSONObject toJSON() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.mobile.framework.objects.ITargeting#getTargetUrl()
	 */
	@Override
	public String getTargetUrl() {
		return url;
	}

	/*
	 * (non-Javadoc)
	 * @see com.mobile.framework.objects.ITargeting#getTargetTitle()
	 */
	@Override
	public String getTargetTitle() {
		return name;
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
		dest.writeString(name);
		dest.writeString(url);
	}
	
	/**
	 * Parcel constructor
	 * @param in
	 */
	public TeaserCampaign(Parcel in) {
        name = in.readString();
        url = in.readString();
	}
	
	/**
	 * Create parcelable 
	 */
	public static final Parcelable.Creator<TeaserCampaign> CREATOR = new Parcelable.Creator<TeaserCampaign>() {
        public TeaserCampaign createFromParcel(Parcel in) {
            return new TeaserCampaign(in);
        }

        public TeaserCampaign[] newArray(int size) {
            return new TeaserCampaign[size];
        }
    };

}