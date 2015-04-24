package com.mobile.framework.objects.home;


import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.framework.objects.IJSONSerializable;
import com.mobile.framework.objects.home.group.BaseTeaserGroupType;
import com.mobile.framework.objects.home.group.BrandsTeaserGroup;
import com.mobile.framework.objects.home.group.CampaignTeaserGroup;
import com.mobile.framework.objects.home.group.FeaturedStoresTeaserGroup;
import com.mobile.framework.objects.home.group.MainTeaserGroup;
import com.mobile.framework.objects.home.group.ShopTeaserGroup;
import com.mobile.framework.objects.home.group.ShopWeekTeaserGroup;
import com.mobile.framework.objects.home.group.SmallTeaserGroup;
import com.mobile.framework.objects.home.group.TopSellersTeaserGroup;
import com.mobile.framework.objects.home.type.EnumTeaserGroupType;
import com.mobile.framework.rest.RestConstants;

import org.apache.commons.collections4.CollectionUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.akquinet.android.androlog.Log;

/**
 * TODO
 */
public class NewHomePageObject implements IJSONSerializable, Parcelable {

    public static final String TAG = NewHomePageObject.class.getSimpleName();

    private String mName;

    private ArrayList<BaseTeaserGroupType> mTeasers;

    /**
     * Empty constructor
     */
    public NewHomePageObject() {
        //...
    }

    /*
     * ########## GETTERS ##########
     */

    /**
     * Get the name.
     *
     * @return The name
     */
    public String getName() {
        return mName;
    }

    /**
     * Get the list of group of teasers.
     *
     * @return An array list
     */
    public ArrayList<BaseTeaserGroupType> getTeasers() {
        return mTeasers;
    }

    /**
     * Validate the list of group of teasers
     *
     * @return true or false
     */
    public boolean hasTeasers() {
        return CollectionUtils.isNotEmpty(mTeasers);
    }

    /*
     * ########## JSON ##########
     */

    /**
     * Initialize object.
     *
     * @param jsonObject JSONObject containing the parameters of the object
     * @throws JSONException
     */
    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        Log.i(TAG, "ON INITIALIZE");
        // Get name
        mName = jsonObject.getString(RestConstants.JSON_ACTION_NAME_TAG);
        // Get teaser
        JSONArray data = jsonObject.getJSONArray(RestConstants.JSON_DATA_TAG);
        int size = data.length();
        if (size > 0) {
            mTeasers = new ArrayList<>();
            // Save unordered response
            Map<String, BaseTeaserGroupType> map = new HashMap<>();
            for (int i = 0; i < size; i++) {
                // Get teaser group
                JSONObject json = data.getJSONObject(i);
                // Get group type
                String type = json.getString(RestConstants.JSON_TYPE_TAG);
                // Parse and create group
                BaseTeaserGroupType group = createTeaserGroupType(type, json);
                // Save into an hash map
                map.put(type, group);
            }
            // Create an array with an order
            for (EnumTeaserGroupType type : EnumTeaserGroupType.values()) {
                BaseTeaserGroupType group = map.get(type.getType());
                Log.i(TAG, "ON ADD GROUP: " + type + " DATA IS NULL: " + (group == null));
                // Append case not null
                if (group != null) {
                    mTeasers.add(group);
                }
            }
        } else {
            throw new JSONException("WARNING: Home data is empty.");
        }
        return true;
    }

    /**
     * Convert object to Json
     *
     * @return null
     */
    @Override
    public JSONObject toJSON() {
        return null;
    }

    /**
     * Parse json that represents a teaser.
     *
     * @param json - the json object
     * @return TeaserSpecification
     */
    private BaseTeaserGroupType createTeaserGroupType(String groupType, JSONObject json) {
        BaseTeaserGroupType teaserGroup = null;
        // Get type
        EnumTeaserGroupType type = EnumTeaserGroupType.byString(groupType);
        Log.i(TAG, "CREATE TEASER GROUP: " + type.toString() + " " + json.toString());
        // Validate group type
        try {
            switch (type) {
                case MAIN_TEASERS:
                    teaserGroup = new MainTeaserGroup(json);
                    break;
                case SHOP_TEASERS:
                    teaserGroup = new ShopTeaserGroup(json);
                    break;
                case SHOP_WEEK_TEASERS:
                    teaserGroup = new ShopWeekTeaserGroup(json);
                    break;
                case SMALL_TEASERS:
                    teaserGroup = new SmallTeaserGroup(json);
                    break;
                case CAMPAIGN_TEASERS:
                    teaserGroup = new CampaignTeaserGroup(json);
                    break;
                case FEATURED_STORES:
                    teaserGroup = new FeaturedStoresTeaserGroup(json);
                    break;
                case BRAND_TEASERS:
                    teaserGroup = new BrandsTeaserGroup(json);
                    break;
                case TOP_SELLERS:
                    teaserGroup = new TopSellersTeaserGroup(json);
                    break;
                default:
                    Log.w(TAG, "WARNING: RECEIVED UNKNOWN GROUP OF TEASERS: " + json.toString());
                    break;
            }
        } catch (JSONException e) {
            Log.w(TAG, "WARNING: ON PARSE GROUP TYPE: " + groupType, e);
        }
        // Discard groups with items
        if (teaserGroup != null && !teaserGroup.hasData()) {
            Log.w(TAG, "WARNING: DISCARDED GROUP EMPTY: " + groupType);
            teaserGroup = null;
        }
        // Return the group or null
        return teaserGroup;
    }

    /*
     * ########## PARCELABLE ##########
     */

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        if (mTeasers == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(mTeasers);
        }
    }

    private NewHomePageObject(Parcel in) {
        mName = in.readString();
        if (in.readByte() == 0x01) {
            mTeasers = new ArrayList<>();
            in.readList(mTeasers, BaseTeaserGroupType.class.getClassLoader());
        } else {
            mTeasers = null;
        }
    }

    public static final Parcelable.Creator<NewHomePageObject> CREATOR = new Parcelable.Creator<NewHomePageObject>() {
        public NewHomePageObject createFromParcel(Parcel source) {
            return new NewHomePageObject(source);
        }

        public NewHomePageObject[] newArray(int size) {
            return new NewHomePageObject[size];
        }
    };
}
