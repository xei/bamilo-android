package com.bamilo.android.framework.service.objects.product;

import android.os.Parcel;
import android.os.Parcelable;

import com.bamilo.android.framework.service.objects.IJSONSerializable;
import com.bamilo.android.framework.service.objects.RequiredJson;
import com.bamilo.android.framework.service.pojo.RestConstants;
import com.bamilo.android.framework.service.utils.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class used to represent a Brand
 * @author alexandrapires
 */
public class Brand implements Parcelable, IJSONSerializable {

    private String mName;
    private int mId;
    private String mUrlKey;
    private String mTarget;
    private String mImageUrl;

    public Brand(String name, int id, String key){
        super();
        mName = name;
        mId = id;
        mUrlKey = key;
    }

    public Brand(){
        super();
    }

    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        mName = jsonObject.getString(RestConstants.NAME);
        mId = jsonObject.optInt(RestConstants.ID);
        mTarget = jsonObject.optString(RestConstants.TARGET);
        mUrlKey  = jsonObject.optString(RestConstants.URL_KEY);
        mImageUrl = jsonObject.optString(RestConstants.IMAGE);
        return true;
    }

    public String getName() {
        return mName;
    }

    public int getId() {
        return mId;
    }

    public String getTarget() {
        return mTarget;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public String getUrlKey() {
        return mUrlKey;
    }

    public boolean hasTarget(){
        return TextUtils.isNotEmpty(mTarget);
    }

    @Override
    public JSONObject toJSON() {
        return null;
    }

    @Override
    public int getRequiredJson() {
        return RequiredJson.NONE;
    }

    @Override
    public int describeContents() {
        return 0;
    }



    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeInt(mId);
        dest.writeString(mTarget);
        dest.writeString(mImageUrl);

    }

    protected Brand(Parcel in) {
        mName = in.readString();
        mId = in.readInt();
        mTarget = in.readString();
        mImageUrl = in.readString();

    }

    public static final Creator<Brand> CREATOR = new Creator<Brand>() {
        public Brand createFromParcel(Parcel in) {
            return new Brand(in);
        }

        public Brand[] newArray(int size) {
            return new Brand[size];
        }
    };
}
