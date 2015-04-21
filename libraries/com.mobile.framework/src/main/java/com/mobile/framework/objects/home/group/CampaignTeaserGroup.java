package com.mobile.framework.objects.home.group;

import android.os.Parcel;

import com.mobile.framework.objects.home.object.BaseTeaserObject;
import com.mobile.framework.objects.home.type.EnumTeaserGroupType;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by spereira on 4/15/15.
 */
public class CampaignTeaserGroup extends BaseTeaserType implements android.os.Parcelable {

    public static final String TAG = CampaignTeaserGroup.class.getSimpleName();

    public static final int NO_REMAINING_TIME = -1;

    private BaseTeaserObject mMainCampaign;

    private int mRemainingTime = NO_REMAINING_TIME;

    private ArrayList<BaseTeaserObject> mMoreCampaigns;

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

    /**
     * @param jsonObject JSONObject containing the parameters of the object
     * @return
     * @throws JSONException

    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        Log.i(TAG, "ON INITIALIZE: " + jsonObject.toString());
        // Get main campaign
        JSONObject mainCampaign = jsonObject.getJSONObject("main_campaign");
        // Create main campaign
        mMainCampaign = createTeaserObject(mainCampaign);
        // Get more campaigns
        JSONArray moreCampaigns = jsonObject.getJSONArray("see_more");
        // Validate size
        int size = moreCampaigns.length();
        if (size > 0) {
            mMoreCampaigns = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                JSONObject otherCampaign = moreCampaigns.optJSONObject(i);
                if (otherCampaign != null) {
                    mMoreCampaigns.add(createTeaserObject(otherCampaign));
                }
            }
        }
        return true;
    }
     */

    /*
    @Override
    protected BaseTeaserObject createTeaserObject(JSONObject object) {
        return null;
    }
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
