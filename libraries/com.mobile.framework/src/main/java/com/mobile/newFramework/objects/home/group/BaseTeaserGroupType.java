package com.mobile.newFramework.objects.home.group;


import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.objects.home.object.BaseTeaserObject;
import com.mobile.newFramework.objects.home.object.TeaserFormObject;
import com.mobile.newFramework.objects.home.object.TeaserTopSellerObject;
import com.mobile.newFramework.objects.home.type.TeaserGroupType;
import com.mobile.newFramework.pojo.RestConstants;

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

    private boolean mHasData = false;

    private ArrayList<BaseTeaserObject> mData;

    private TeaserGroupType mType = TeaserGroupType.UNKNOWN;

    /**
     * Empty Constructor
     */
    public BaseTeaserGroupType() {
    }

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
        return mHasData;
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
//        Log.i(TAG, "ON INITIALIZE: " + jsonObject.toString());
        // Get title
        mTitle = jsonObject.optString(RestConstants.JSON_TITLE_TAG);
        // Get Data flag
        mHasData = jsonObject.optBoolean(RestConstants.HAS_DATA);
        mData = new ArrayList<>();
        // Form Teaser
        //FIXME validation should be changed from !mHasData to mHasData
        if(!mHasData && mType == TeaserGroupType.FORM_NEWSLETTER){
            mData.add(getFormTeaser(jsonObject));
        }
        // Get data
        else if(mHasData){
            JSONArray teasersData = jsonObject.getJSONArray(RestConstants.JSON_DATA_TAG);
            // Validate size
            int size = teasersData.length();
            if (size > 0) {
                for (int i = 0; i < size; i++) {
                    JSONObject teaserData = teasersData.optJSONObject(i);
                    if (teaserData != null) {
                        BaseTeaserObject teaser = createTeaserObject(teaserData);
                        if(teaser != null)  mData.add(teaser);
                    }
                }
            }

        } else {
            //FIXME to be uncomment after fixes
//            mData.add(getNoDataTeasers(jsonObject));
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
            teaser = new TeaserTopSellerObject(mType.ordinal());
        } else {
            teaser = new BaseTeaserObject(mType.ordinal());
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

    /**
     * create specific teasers that comes with has_data flag at false.
     * this is specified for rich relevance teaser and form teaser
     * @param jsonObject
     * @return
     */
    private BaseTeaserObject getNoDataTeasers(JSONObject jsonObject){
        // Validate type to create a specific teaser object
        BaseTeaserObject teaser;
        teaser = new BaseTeaserObject(mType.ordinal());
        // Initialize
        try {
            teaser.initialize(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
            teaser = null;
        }
        return teaser;
    }

    /**
     *  Creates a form teaser
     * @param jsonObject
     * @return
     */
    private BaseTeaserObject getFormTeaser(JSONObject jsonObject){
        BaseTeaserObject teaser;
        teaser = new TeaserFormObject(mType.ordinal());
//        // Create Home page Newsletter form
//        if(mType == TeaserGroupType.FORM_NEWSLETTER){
//            teaser = new TeaserFormObject(mType.ordinal());
//        }

        // Initialize
        try {
            teaser.initialize(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return teaser;
    }

    @Override
    public JSONObject toJSON() {
        return null;
    }

    @Override
    public RequiredJson getRequiredJson() {
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
        mHasData = in.readByte() == 1;
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
        dest.writeByte((byte) (mHasData ? 1 : 0));
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