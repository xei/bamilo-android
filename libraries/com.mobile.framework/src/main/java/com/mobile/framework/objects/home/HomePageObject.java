//package com.mobile.framework.objects.home;
//
//
//import android.os.Parcel;
//import android.os.Parcelable;
//
//import com.mobile.framework.objects.IJSONSerializable;
//import com.mobile.framework.objects.home.group.BaseTeaserGroupType;
//import com.mobile.framework.objects.home.type.TeaserGroupType;
//import com.mobile.framework.rest.RestConstants;
//
//import org.apache.commons.collections4.CollectionUtils;
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Map;
//
//import com.mobile.framework.output.Log;
//
///**
// * Class used to represent a home page response
// * @author spereira
// */
//public class HomePageObject implements IJSONSerializable, Parcelable {
//
//    public static final String TAG = HomePageObject.class.getSimpleName();
//
//    private String mName;
//
//    private ArrayList<BaseTeaserGroupType> mTeasers;
//
//    /**
//     * Empty constructor
//     */
//    public HomePageObject() {
//        //...
//    }
//
//    /*
//     * ########## GETTERS ##########
//     */
//
//    /**
//     * Get the name.
//     *
//     * @return The name
//     */
//    public String getName() {
//        return mName;
//    }
//
//    /**
//     * Get the list of group of teasers.
//     *
//     * @return An array list
//     */
//    public ArrayList<BaseTeaserGroupType> getTeasers() {
//        return mTeasers;
//    }
//
//    /**
//     * Validate the list of group of teasers
//     *
//     * @return true or false
//     */
//    public boolean hasTeasers() {
//        return CollectionUtils.isNotEmpty(mTeasers);
//    }
//
//    /*
//     * ########## JSON ##########
//     */
//
//    /**
//     * Initialize object.
//     *
//     * @param jsonObject JSONObject containing the parameters of the object
//     * @throws JSONException
//     */
//    @Override
//    public boolean initialize(JSONObject jsonObject) throws JSONException {
//        Log.i(TAG, "ON INITIALIZE");
//        // Get name
//        mName = jsonObject.getString(RestConstants.JSON_ACTION_NAME_TAG);
//        // Get teaser
//        JSONArray data = jsonObject.getJSONArray(RestConstants.JSON_DATA_TAG);
//        int size = data.length();
//        if (size > 0) {
//            mTeasers = new ArrayList<>();
//            // Save unordered response
//            Map<String, BaseTeaserGroupType> map = new HashMap<>();
//            for (int i = 0; i < size; i++) {
//                // Get teaser group
//                JSONObject json = data.getJSONObject(i);
//                // Get group type
//                String type = json.getString(RestConstants.JSON_TYPE_TAG);
//                // Parse and create group
//                BaseTeaserGroupType group = createTeaserGroupType(type, json);
//                // Save into an hash map
//                map.put(type, group);
//            }
//            // Create an array with an order
//            for (TeaserGroupType type : TeaserGroupType.values()) {
//                BaseTeaserGroupType group = map.get(type.getType());
//                Log.i(TAG, "ON ADD GROUP: " + type + " DATA IS NULL: " + (group == null));
//                // Append case not null
//                if (group != null) {
//                    mTeasers.add(group);
//                }
//            }
//        } else {
//            throw new JSONException("WARNING: Home data is empty.");
//        }
//        return true;
//    }
//
//    /**
//     * Convert object to Json
//     *
//     * @return null
//     */
//    @Override
//    public JSONObject toJSON() {
//        return null;
//    }
//
//    /**
//     * Parse json that represents a teaser.
//     *
//     * @param json - the json object
//     * @return TeaserSpecification
//     */
//    private BaseTeaserGroupType createTeaserGroupType(String groupType, JSONObject json) {
//        BaseTeaserGroupType teaserGroup = null;
//        // Get type
//        TeaserGroupType type = TeaserGroupType.byString(groupType);
//        Log.i(TAG, "CREATE TEASER GROUP: " + type.toString() + " " + json.toString());
//        // Validate group type
//        try {
//            if( type != TeaserGroupType.UNKNOWN) {
//                teaserGroup = new BaseTeaserGroupType(type, json);
//            } else {
//                Log.w(TAG, "WARNING: RECEIVED UNKNOWN GROUP OF TEASERS: " + json.toString());
//            }
//        } catch (JSONException e) {
//            Log.w(TAG, "WARNING: ON PARSE GROUP TYPE: " + groupType, e);
//        }
//        // Discard groups with items
//        if (teaserGroup != null && !teaserGroup.hasData()) {
//            Log.w(TAG, "WARNING: DISCARDED GROUP EMPTY: " + groupType);
//            teaserGroup = null;
//        }
//        // Return the group or null
//        return teaserGroup;
//    }
//
//    /*
//     * ########## PARCELABLE ##########
//     */
//
//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeString(mName);
//        if (mTeasers == null) {
//            dest.writeByte((byte) (0x00));
//        } else {
//            dest.writeByte((byte) (0x01));
//            dest.writeList(mTeasers);
//        }
//    }
//
//    private HomePageObject(Parcel in) {
//        mName = in.readString();
//        if (in.readByte() == 0x01) {
//            mTeasers = new ArrayList<>();
//            in.readList(mTeasers, BaseTeaserGroupType.class.getClassLoader());
//        } else {
//            mTeasers = null;
//        }
//    }
//
//    public static final Parcelable.Creator<HomePageObject> CREATOR = new Parcelable.Creator<HomePageObject>() {
//        public HomePageObject createFromParcel(Parcel source) {
//            return new HomePageObject(source);
//        }
//
//        public HomePageObject[] newArray(int size) {
//            return new HomePageObject[size];
//        }
//    };
//}
