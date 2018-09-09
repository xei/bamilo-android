package com.bamilo.android.framework.service.objects.home;

import android.os.Parcel;
import android.os.Parcelable;

import com.bamilo.android.framework.service.objects.IJSONSerializable;
import com.bamilo.android.framework.service.objects.RequiredJson;
import com.bamilo.android.framework.service.objects.home.group.BaseTeaserGroupType;
import com.bamilo.android.framework.service.objects.home.type.TeaserGroupType;
import com.bamilo.android.framework.service.pojo.RestConstants;
import com.bamilo.android.framework.service.utils.CollectionUtils;
import com.bamilo.android.framework.service.utils.output.Print;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Class used to represent a home page response
 * @author spereira
 */
public class HomePageObject implements IJSONSerializable, Parcelable {

    public static final String TAG = HomePageObject.class.getSimpleName();

    private LinkedHashMap<String, BaseTeaserGroupType> mTeasers;

    /**
     * Empty constructor
     */
    @SuppressWarnings("unused")
    public HomePageObject() {
        super();
    }

    /*
     * ########## GETTERS ##########
     */

    /**
     * Get the list of group of teasers.
     *
     * @return An array list
     */
    public LinkedHashMap<String, BaseTeaserGroupType> getTeasers() {
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
        // Get teaser
        JSONArray data = jsonObject.getJSONArray(RestConstants.DATA);
        int size = data.length();
        if (size > 0) {
            mTeasers = new LinkedHashMap<>();
            // Save unordered response
            Map<String, BaseTeaserGroupType> map = new HashMap<>();
            for (int i = 0; i < size; i++) {
                // Get teaser group
                JSONObject json = data.getJSONObject(i);
                // Get group type
                String type = json.getString(RestConstants.TYPE);
                // Parse and create group
                BaseTeaserGroupType group = createTeaserGroupType(type, json);
                // Save into an hash map
                map.put(type, group);
            }
            // Create an array with an order
            for (TeaserGroupType type : TeaserGroupType.values()) {
                BaseTeaserGroupType group = map.get(type.getType());
                // Append case not null
                if (group != null) {
                    mTeasers.put(type.getType(), group);
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

    @Override
    public int getRequiredJson() {
        return RequiredJson.METADATA;
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
        TeaserGroupType type = TeaserGroupType.byString(groupType);
        // Validate group type
        try {
            if (type != TeaserGroupType.UNKNOWN) {
                teaserGroup = new BaseTeaserGroupType(type, json);
            } else {
                Print.w(TAG, "WARNING: RECEIVED UNKNOWN GROUP OF TEASERS");
            }
        } catch (JSONException e) {
            Print.w(TAG, "WARNING: ON PARSE GROUP TYPE: " + groupType, e);
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
        if (mTeasers == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeMap(mTeasers);

        }
    }

    private HomePageObject(Parcel in) {
        if (in.readByte() == 0x01) {
            mTeasers = new LinkedHashMap<>();
           in.readMap(mTeasers, LinkedHashMap.class.getClassLoader());
        } else {
            mTeasers = null;
        }
    }

    public static final Creator<HomePageObject> CREATOR = new Creator<HomePageObject>() {
        public HomePageObject createFromParcel(Parcel source) {
            return new HomePageObject(source);
        }

        public HomePageObject[] newArray(int size) {
            return new HomePageObject[size];
        }
    };
}

