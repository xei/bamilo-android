/**
 * @author Guilherme Silva
 * 
 * @version 1.01
 * 
 * 2012/06/18
 * 
 * Copyright (c) Rocket Internet All Rights Reserved
 */
package com.mobile.framework.objects;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.framework.rest.RestConstants;

import com.mobile.framework.output.Print;

/**
 * Class that represents the server-side product brand. Contains id, name and
 * icon url.
 * 
 * @author Guilherme Silva
 * 
 */
public class Brand implements IJSONSerializable, Parcelable {

    private String id;
    private String url;
    private String name;
    private BrandImage image;

    public Brand() {
        this.id = "";
        this.url = "";
        this.name = "";
        this.image = new BrandImage();
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the image
     */
    public BrandImage getImage() {
        return image;
    }

    /* (non-Javadoc)
     * @see com.mobile.framework.objects.IJSONSerializable#initialize(org.json.JSONObject)
     */
    @Override
    public boolean initialize(JSONObject jsonObject) {
        try {
            id = jsonObject.getString(RestConstants.JSON_ID_TAG);

            JSONObject innerObject = jsonObject.getJSONObject(RestConstants.JSON_INNER_OBJECT_TAG);
            url = innerObject.getString(RestConstants.JSON_URL_TAG);
            name = innerObject.getString(RestConstants.JSON_NAME_TAG);
            if(name.trim().equals("") || url.trim().equals("")){
                Print.d("brands", "Brand name = " + name + "\r\nbrand url = " + url);
                return false;
            }
            

            JSONObject imageObject = innerObject.optJSONObject(RestConstants.JSON_IMAGE_TAG);

            if (imageObject != null) {
                image = new BrandImage();
                image.initialize(imageObject);
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
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put(RestConstants.JSON_URL_TAG, url);
            jsonObject.put(RestConstants.JSON_NAME_TAG, name);
            jsonObject.put(RestConstants.JSON_IMAGE_TAG, image.toJSON());

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return jsonObject;
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
	    dest.writeString(id);
	    dest.writeString(url);
	    dest.writeString(name);
	    dest.writeValue(image);
	}
	
	/**
	 * Parcel constructor
	 * @param in
	 */
    private Brand(Parcel in) {
        id = in.readString();
        url = in.readString();
        name = in.readString();
        image = (BrandImage) in.readValue(BrandImage.class.getClassLoader());
    }
    
	/**
	 * Create parcelable 
	 */
    public static final Parcelable.Creator<Brand> CREATOR = new Parcelable.Creator<Brand>() {
        public Brand createFromParcel(Parcel in) {
            return new Brand(in);
        }

        public Brand[] newArray(int size) {
            return new Brand[size];
        }
    };
}
