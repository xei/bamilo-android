package com.mobile.framework.objects.home.group;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.mobile.framework.objects.IJSONSerializable;
import com.mobile.framework.objects.home.object.BaseTeaserObject;
import com.mobile.framework.objects.home.object.TeaserTopSellerObject;
import com.mobile.framework.objects.home.type.TeaserGroupType;
import com.mobile.framework.rest.RestConstants;

import org.apache.commons.collections4.CollectionUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Class used to represent the base of a group of teasers.
 * @author spereira
 */
public class BaseTeaserGroupType implements IJSONSerializable, Parcelable {

    public static final String TAG = BaseTeaserGroupType.class.getSimpleName();

    private String mTitle;

    private ArrayList<BaseTeaserObject> mData;

    private TeaserGroupType mType = TeaserGroupType.UNKNOWN;

    /**
     * Constructor
     */
    public BaseTeaserGroupType(TeaserGroupType type, JSONObject jsonObject) throws JSONException {
        mType = type;
        initialize(jsonObject);
    }

    /*
     * ########## GETTERS ##########
     */

    public TeaserGroupType getType() {
        return mType;
    }

    public ArrayList<BaseTeaserObject> getData() {
        return mData;
    }

    public boolean hasData() {
        return CollectionUtils.isNotEmpty(mData);
    }

    /*
     * ########## JSON ##########
     */

    /**
     * Initialize object using json.
     * @param jsonObject JSONObject containing the parameters of the object
     * @throws JSONException
     */
    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        Log.i(TAG, "ON INITIALIZE: " + jsonObject.toString());
        // Get title
        mTitle = jsonObject.optString(RestConstants.JSON_TITLE_TAG);
        // Get data
        JSONArray teasersData = jsonObject.getJSONArray(RestConstants.JSON_DATA_TAG);
        // Validate size
        int size = teasersData.length();
        if (size > 0) {
            mData = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                JSONObject teaserData = teasersData.optJSONObject(i);
                if (teaserData != null) {
                    BaseTeaserObject teaser = createTeaserObject(teaserData);
                    if(teaser != null)  mData.add(teaser);
                }
            }
        }
        return true;
    }

    /**
     * Create teaser object for respective json.
     * @return BaseTeaserObject or null
     */
    protected BaseTeaserObject createTeaserObject(JSONObject object) {
        // Validate type to create a specific teaser object
        BaseTeaserObject teaser;
        if(mType == TeaserGroupType.TOP_SELLERS) {
            teaser = new TeaserTopSellerObject();
        } else {
            teaser = new BaseTeaserObject();
        }
        // Initialize
        try {
            teaser.initialize(object);
        } catch (JSONException e) {
            e.printStackTrace();
            teaser = null;
        }
        return teaser;
    }

    @Override
    public JSONObject toJSON() {
        return null;
    }

    /*
     * ########## PARCELABLE ##########
     */

    @Override
    public int describeContents() {
        return 0;
    }

    protected BaseTeaserGroupType(Parcel in) {
        mType = (TeaserGroupType) in.readValue(TeaserGroupType.class.getClassLoader());
        mTitle = in.readString();
        if (in.readByte() == 0x01) {
            mData = new ArrayList<>();
            in.readList(mData, BaseTeaserObject.class.getClassLoader());
        } else {
            mData = null;
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(mType);
        dest.writeString(mTitle);
        if (mData == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(mData);
        }
    }

    public static final Creator<BaseTeaserGroupType> CREATOR = new Creator<BaseTeaserGroupType>() {
        public BaseTeaserGroupType createFromParcel(Parcel source) {
            return new BaseTeaserGroupType(source);
        }

        public BaseTeaserGroupType[] newArray(int size) {
            return new BaseTeaserGroupType[size];
        }
    };

}
