package com.mobile.newFramework.objects.catalog;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.utils.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class that represents the server-side featured item. Contains id, url, name
 * and image url.
 *
 * @author Andre Lopes
 *
 */
public class FeaturedItem implements IJSONSerializable, Parcelable {

    private String target;
    private String name;
    protected String imageUrl;

    public FeaturedItem() {
        this.target = "";
        this.name = "";
        this.imageUrl = "";
    }

    /**
     * @return the url
     */
    public String getTarget() {
        return target;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the imageUrl
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.mobile.framework.objects.IJSONSerializable#initialize(org.json.JSONObject )
     */
    @Override
    public boolean initialize(JSONObject jsonObject) {
        target = jsonObject.optString(RestConstants.TARGET);
        name = jsonObject.optString(RestConstants.NAME);
        imageUrl = jsonObject.optString(RestConstants.IMAGE);
        // concat brand and name instead of using only name
        String brand = jsonObject.optString(RestConstants.BRAND);
        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(brand)) {
            name = brand + " " + name;
        }
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.mobile.framework.objects.IJSONSerializable#toJSON()
     */
    @Override
    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put(RestConstants.URL, target);
            jsonObject.put(RestConstants.NAME, name);
            jsonObject.put(RestConstants.IMAGE, imageUrl);

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return jsonObject;
    }

    @Override
    public int getRequiredJson() {
        return RequiredJson.NONE;
    }

	/*
	 * ########### Parcelable ###########
	 */

    protected FeaturedItem(Parcel in) {
        target = in.readString();
        name = in.readString();
        imageUrl = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(target);
        dest.writeString(name);
        dest.writeString(imageUrl);
    }

    public static final Parcelable.Creator<FeaturedItem> CREATOR = new Parcelable.Creator<FeaturedItem>() {
        @Override
        public FeaturedItem createFromParcel(Parcel in) {
            return new FeaturedItem(in);
        }

        @Override
        public FeaturedItem[] newArray(int size) {
            return new FeaturedItem[size];
        }
    };
}

