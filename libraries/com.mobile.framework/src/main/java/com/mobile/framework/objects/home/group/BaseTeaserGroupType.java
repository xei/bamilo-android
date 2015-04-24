package com.mobile.framework.objects.home.group;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.mobile.framework.objects.IJSONSerializable;
import com.mobile.framework.objects.home.object.BaseTeaserObject;
import com.mobile.framework.objects.home.type.EnumTeaserGroupType;
import com.mobile.framework.rest.RestConstants;

import org.apache.commons.collections4.CollectionUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by spereira on 4/15/15.
 */
public abstract class BaseTeaserGroupType implements IJSONSerializable, Parcelable {

    public static final String TAG = BaseTeaserGroupType.class.getSimpleName();

    private ArrayList<BaseTeaserObject> mData;

    /**
     *
     */
    public BaseTeaserGroupType() {
        //...
    }

    /**
     * @param jsonObject
     * @throws JSONException
     */
    public BaseTeaserGroupType(JSONObject jsonObject) throws JSONException {
        initialize(jsonObject);
    }

    /*
     * ########## GETTERS ##########
     */

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
     * TODO
     *
     * @param jsonObject JSONObject containing the parameters of the object
     * @return
     * @throws JSONException
     */
    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        Log.i(TAG, "ON INITIALIZE: " + jsonObject.toString());
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
     *
     * @param object
     * @return
     */
    protected BaseTeaserObject createTeaserObject(JSONObject object) {
        BaseTeaserObject teaser = new BaseTeaserObject();
        try {
            teaser.initialize(object);
        } catch (JSONException e) {
            e.printStackTrace();
            teaser = null;
        }
        return teaser;
    }

    /**
     * TODO
     *
     * @return
     */
    @Override
    public JSONObject toJSON() {
        return null;
    }



    /*
     * ########## ABSTRACT ##########
    */
    //protected abstract BaseTeaserObject createTeaserObject(JSONObject object);

    public abstract EnumTeaserGroupType getType();

    /*
     * ########## PARCELABLE ##########
     */

    protected BaseTeaserGroupType(Parcel in) {
        if (in.readByte() == 0x01) {
            mData = new ArrayList<>();
            in.readList(mData, BaseTeaserObject.class.getClassLoader());
        } else {
            mData = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (mData == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(mData);
        }
    }

}
