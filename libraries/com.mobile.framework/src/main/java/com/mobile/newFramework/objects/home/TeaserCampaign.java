package com.mobile.newFramework.objects.home;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.objects.catalog.ITargeting;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.utils.TextUtils;

import org.json.JSONObject;

/**
 * Class that represents a teaser campaign
 * @author sergiopereira
 *
 */
@Deprecated
public class TeaserCampaign implements ITargeting, IJSONSerializable, Parcelable {

	private String name;

	private String url;

	private String campaignId;

	/**
	 * Empty constructor
	 */
	public TeaserCampaign() {
    }

	/*
	 * (non-Javadoc)
	 *
	 * @see com.mobile.newFramework.objects.catalog.ITargeting#getTargetType()
	 */
	@Override
	public TargetType getTargetType() {
		return TargetType.CAMPAIGN;
	}

	/**
	 * Set the campaign title
	 * @author sergiopereira
	 */
	public void setTitle(String title) { this.name = title; }

	/**
	 * Set the campaign url/id
	 * @author sergiopereira
	 */
	public void setUrl(String url) {
        this.url = url;
        getCampaignKey();
    }

	/*
	 * (non-Javadoc)
	 * @see com.mobile.framework.objects.IJSONSerializable#initialize(org.json.JSONObject)
	 */
	@Override
	public boolean initialize(JSONObject jsonObject) {
		name = jsonObject.optString(RestConstants.JSON_CAMPAIGN_NAME_TAG);
        url = jsonObject.optString(RestConstants.JSON_CAMPAIGN_URL_TAG);
        getCampaignKey();
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

	@Override
	public RequiredJson getRequiredJson() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.mobile.newFramework.objects.catalog.ITargeting#getTargetValue()
	 */
	@Override
	public String getTargetValue() {
		return url;
	}

	/*
	 * (non-Javadoc)
	 * @see com.mobile.newFramework.objects.catalog.ITargeting#getTargetTitle()
	 */
	@Override
	public String getTargetTitle() {
		return name;
	}


    public String getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(String campaignId) {
        this.campaignId = campaignId;
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
        dest.writeString(campaignId);
	}

	/**
	 * Parcel constructor
	 */
	public TeaserCampaign(Parcel in) {
        name = in.readString();
        url = in.readString();
        campaignId = in.readString();
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

    /**
     * extract campaign id from the campaign url
     */
    private void getCampaignKey(){
        if(!TextUtils.isEmpty(url)){
            Uri myUri = Uri.parse(url);
            campaignId = myUri.getQueryParameter("campaign_slug");
        }
    }
}
