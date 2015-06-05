package com.mobile.framework.objects;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.newFramework.pojo.RestConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Class that represents the bucket list from a Product details specifications
 * 
 * @author Paulo Carvalho
 * 
 */
public class ProductDetailsSpecification implements IJSONSerializable, Parcelable {

    private String mTitle;
    private HashMap<String,String> mSpecs;


    public ProductDetailsSpecification() {
        this.mTitle = "";
        this.mSpecs = new HashMap<>();
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * @return the specifications
     */
    public HashMap<String,String> getSpecifications() {
        return mSpecs;
    }

    /**
     * set the title
     */
    public void setTitle(String title) {
        this.mTitle = title;
    }

    /**
     * set specifications
     */
    public void setSpecifications(HashMap<String,String> specs) {
        this.mSpecs = specs;
    }

    /* (non-Javadoc)
     * @see com.mobile.framework.objects.IJSONSerializable#initialize(org.json.JSONObject)
     */
    @Override
    public boolean initialize(JSONObject jsonObject) {
        try {
            mTitle = jsonObject.optString(RestConstants.JSON_HEAD_LABEL_TAG,"");
            JSONArray bodyArray = jsonObject.optJSONArray(RestConstants.JSON_BODY_TAG);

            if(bodyArray != null && bodyArray.length() > 0){
                for (int i = 0; i < bodyArray.length() ; i++) {
                    JSONObject specItemObject = bodyArray.getJSONObject(i);
                    mSpecs.put(specItemObject.getString(RestConstants.JSON_KEY_TAG), specItemObject.getString(RestConstants.JSON_VALUE_TAG));
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /* (non-Javadoc)
     * @see com.mobile.framework.objects.IJSONSerializable#toJSON()
     */
    @Override
    public JSONObject toJSON() {
        return null;
    }

    /**
     * ########### Parcelable ###########
     * @author sergiopereira
     */

    /*
     * (non-Javadoc)
     * @see android.os.Parcelable#describeContents()
     */
	@Override
	public int describeContents() {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
	 */
	@Override
	public void writeToParcel(Parcel dest, int flags) {
	    dest.writeString(mTitle);
        dest.writeMap(mSpecs);

	}

	/**
	 * Parcel constructor
	 * @param in
	 */
    private ProductDetailsSpecification(Parcel in) {
        mTitle = in.readString();
        mSpecs = new HashMap<String, String>();
        in.readMap(mSpecs, String.class.getClassLoader());

    }

	/**
	 * Create parcelable
	 */
    public static final Creator<ProductDetailsSpecification> CREATOR = new Creator<ProductDetailsSpecification>() {
        public ProductDetailsSpecification createFromParcel(Parcel in) {
            return new ProductDetailsSpecification(in);
        }

        public ProductDetailsSpecification[] newArray(int size) {
            return new ProductDetailsSpecification[size];
        }
    };



}
