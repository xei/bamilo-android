package com.mobile.framework.objects.home.group;

import android.os.Parcel;

import com.mobile.framework.objects.home.type.EnumTeaserGroupType;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by spereira on 4/15/15.
 */
public class CampaignTeaserGroup extends BaseTeaserGroupType implements android.os.Parcelable {

    public static final String TAG = CampaignTeaserGroup.class.getSimpleName();

    public static final int NO_REMAINING_TIME = -1;

    private int mRemainingTime = NO_REMAINING_TIME;

    /**
     * Empty constructor
     */
    public CampaignTeaserGroup() {
        //...
    }

    /**
     * @param jsonObject
     * @throws JSONException
     */
    public CampaignTeaserGroup(JSONObject jsonObject) throws JSONException {
        super(jsonObject);
    }

    /*
     * ########## GETTERS ##########
     */

    @Override
    public EnumTeaserGroupType getType() {
        return EnumTeaserGroupType.CAMPAIGN_TEASERS;
    }

    /*
     * ########## JSON ##########
     */


    /*
     * ########## PARCELABLE ##########
     */

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    private CampaignTeaserGroup(Parcel in) {
        super(in);
    }

    public static final Creator<CampaignTeaserGroup> CREATOR = new Creator<CampaignTeaserGroup>() {
        public CampaignTeaserGroup createFromParcel(Parcel source) {
            return new CampaignTeaserGroup(source);
        }

        public CampaignTeaserGroup[] newArray(int size) {
            return new CampaignTeaserGroup[size];
        }
    };
}
