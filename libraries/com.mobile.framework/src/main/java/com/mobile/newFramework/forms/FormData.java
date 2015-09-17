package com.mobile.newFramework.forms;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.pojo.RestConstants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Defines the data from the form.
 * @author GuilhermeSilva
 * @modified Manuel Silva
 *
 */
public class FormData implements IJSONSerializable, Parcelable {

    private String url;
    private String type;
    private String md5;

    public FormData() {
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return md5
     */
    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /* (non-Javadoc)
     * @see com.mobile.framework.objects.IJSONSerializable#initialize(org.json.JSONObject)
     */
    @Override
    public boolean initialize(JSONObject jsonObject) {
        
        try {
            type = jsonObject.getString(RestConstants.TYPE);
            md5 = jsonObject.getString(RestConstants.JSON_MD5_TAG);
            url = jsonObject.getString(RestConstants.URL);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    /* (non-Javadoc)
     * @see com.mobile.framework.objects.IJSONSerializable#toJSON()
     */
    @Override
    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(RestConstants.TYPE,type);
            jsonObject.put(RestConstants.JSON_MD5_TAG, url);
            jsonObject.put(RestConstants.URL, url);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    @Override
    public RequiredJson getRequiredJson() {
        return RequiredJson.COMPLETE_JSON;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(type);
        dest.writeString(md5);
        dest.writeString(url);
        
    }

    /**
     * Parcel constructor
     */
    private FormData(Parcel in) {
        type = in.readString();
        md5 = in.readString();
        url = in.readString();
    }
    
    /**
     * Create parcelable 
     */
    public static final Creator<FormData> CREATOR = new Creator<FormData>() {
        public FormData createFromParcel(Parcel in) {
            return new FormData(in);
        }

        public FormData[] newArray(int size) {
            return new FormData[size];
        }
    };
    
}
