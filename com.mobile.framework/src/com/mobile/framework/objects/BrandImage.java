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

/**
 * Class that represents the server-side product brand. Contains id, name and
 * icon url.
 * 
 * @author Guilherme Silva
 * 
 */
public class BrandImage implements IJSONSerializable, Parcelable {

	private String url;
	private String width;
	private String height;
	private String format;

	/**
	 * Empty constructor
	 * 
	 * @param in
	 */
	public BrandImage() {
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @return the width
	 */
	public String getWidth() {
		return width;
	}

	/**
	 * @return the height
	 */
	public String getHeight() {
		return height;
	}

	/**
	 * @return the format
	 */
	public String getFormat() {
		return format;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mobile.framework.objects.IJSONSerializable#initialize(org.json.JSONObject
	 * )
	 */
	@Override
	public boolean initialize(JSONObject jsonObject) {
		this.url = jsonObject.optString(RestConstants.JSON_URL_TAG);
		this.width = jsonObject.optString(RestConstants.JSON_WIDTH_TAG);
		this.height = jsonObject.optString(RestConstants.JSON_HEIGHT_TAG);
		this.format = jsonObject.optString(RestConstants.JSON_FORMAT_TAG);

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
			jsonObject.put(RestConstants.JSON_URL_TAG, url);
			jsonObject.put(RestConstants.JSON_WIDTH_TAG, width);
			jsonObject.put(RestConstants.JSON_HEIGHT_TAG, height);
			jsonObject.put(RestConstants.JSON_FORMAT_TAG, format);

		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}

		return jsonObject;
	}

	/**
	 * ########### Parcelable ###########
	 * 
	 * @author sergiopereira
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.Parcelable#describeContents()
	 */
	@Override
	public int describeContents() {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
	 */
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(url);
		dest.writeString(width);
		dest.writeString(height);
		dest.writeString(format);
	}

	/**
	 * Parcel constructor
	 * 
	 * @param in
	 */
	public BrandImage(Parcel in) {
		url = in.readString();
		width = in.readString();
		height = in.readString();
		format = in.readString();
	}

	/**
	 * Create parcelable 
	 */
    public static final Parcelable.Creator<BrandImage> CREATOR = new Parcelable.Creator<BrandImage>() {
        public BrandImage createFromParcel(Parcel in) {
            return new BrandImage(in);
        }

        public BrandImage[] newArray(int size) {
            return new BrandImage[size];
        }
    };
}
