package com.mobile.newFramework.forms;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.framework.rest.RestConstants;
import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Defines the super object to handle data from the form.
 * @author Paulo Carvalho
 *
 */
public class SuperFormData implements IJSONSerializable, Parcelable {


    public SuperFormData() {
    }


    /* (non-Javadoc)
     * @see com.mobile.framework.objects.IJSONSerializable#initialize(org.json.JSONObject)
     */
    @Override
    public boolean initialize(JSONObject jsonObject) {
        JSONArray dataArray;
        try {
            dataArray = jsonObject.getJSONArray(RestConstants.JSON_DATA_TAG);
            int dataArrayLength = dataArray.length();
            for (int i = 0; i < dataArrayLength; ++i) {
                JSONObject formDataObject = dataArray.getJSONObject(i);
                FormData formData = new FormData();
                formData.initialize(formDataObject);
//                JumiaApplication.INSTANCE.getFormDataRegistry().put(formData.getAction(), formData);
            }
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

        return null;
    }

    @Override
    public RequiredJson getRequiredJson() {
        return RequiredJson.METADATA;
    }

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }

    /**
     * Parcel constructor
     * @param in
     */
    private SuperFormData(Parcel in) {

    }
    
    /**
     * Create parcelable 
     */
    public static final Creator<SuperFormData> CREATOR = new Creator<SuperFormData>() {
        public SuperFormData createFromParcel(Parcel in) {
            return new SuperFormData(in);
        }

        public SuperFormData[] newArray(int size) {
            return new SuperFormData[size];
        }
    };
    
}
