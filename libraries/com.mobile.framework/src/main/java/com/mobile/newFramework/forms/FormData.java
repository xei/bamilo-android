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
//    /**
//     * Defines the json action tag.
//     */
//    private final String JSON_ACTION_TAG = "action";
//    /**
//     * Defines the json url tag.
//     */
//    private final String JSON_URL_TAG = "url";
        
    private String id;
    private String action;
    private String url;
      
    public FormData() {
    }
    
    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @return the action
     */
    public String getAction() {
        return action;
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
            id = jsonObject.getString(RestConstants.JSON_ID_TAG);
            action = jsonObject.getString(RestConstants.JSON_ACTION_TAG);
            url = jsonObject.getString(RestConstants.JSON_URL_TAG);
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
            jsonObject.put(RestConstants.JSON_ID_TAG, id);
            jsonObject.put(RestConstants.JSON_ACTION_TAG, action);
            jsonObject.put(RestConstants.JSON_URL_TAG, url);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
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
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(action);
        dest.writeString(url);
        
    }

    /**
     * Parcel constructor
     * @param in
     */
    private FormData(Parcel in) {
        id = in.readString();
        action = in.readString();
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
