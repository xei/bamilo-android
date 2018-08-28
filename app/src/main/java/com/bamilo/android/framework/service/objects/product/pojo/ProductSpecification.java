package com.bamilo.android.framework.service.objects.product.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.bamilo.android.framework.service.objects.IJSONSerializable;
import com.bamilo.android.framework.service.objects.RequiredJson;
import com.bamilo.android.framework.service.pojo.RestConstants;

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
public class ProductSpecification implements IJSONSerializable, Parcelable {

    private String mTitle;
    private HashMap<String,String> mSpecs;


    public ProductSpecification() {
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

    /* (non-Javadoc)
     * @see com.mobile.framework.objects.IJSONSerializable#initialize(org.json.JSONObject)
     */
    @Override
    public boolean initialize(JSONObject jsonObject) {
        try {
            mTitle = jsonObject.optString(RestConstants.HEAD_LABEL,"");
            JSONArray bodyArray = jsonObject.optJSONArray(RestConstants.BODY);

            if(bodyArray != null && bodyArray.length() > 0){
                for (int i = 0; i < bodyArray.length() ; i++) {
                    JSONObject specItemObject = bodyArray.getJSONObject(i);
                    mSpecs.put(specItemObject.getString(RestConstants.KEY), specItemObject.getString(RestConstants.VALUE));
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

    @Override
    public int getRequiredJson() {
        return RequiredJson.NONE;
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
    private ProductSpecification(Parcel in) {
        mTitle = in.readString();
        mSpecs = new HashMap<>();
        in.readMap(mSpecs, String.class.getClassLoader());

    }

	/**
	 * Create parcelable
	 */
    public static final Creator<ProductSpecification> CREATOR = new Creator<ProductSpecification>() {
        public ProductSpecification createFromParcel(Parcel in) {
            return new ProductSpecification(in);
        }

        public ProductSpecification[] newArray(int size) {
            return new ProductSpecification[size];
        }
    };



}
