package com.mobile.framework.objects;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.framework.rest.RestConstants;
import com.mobile.framework.utils.ImageResolutionHelper;
import com.mobile.framework.utils.LogTagHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import de.akquinet.android.androlog.Log;

public class Variation implements IJSONSerializable, Parcelable, Serializable{
	private static final String TAG = LogTagHelper.create(Variation.class);

	private String sku;
	private String link;
	private String image;

	public Variation() {

	}
	
	public boolean initialize(String sku, JSONObject jsonObject) {
		this.sku = sku;
		try {
			link = jsonObject.getString(RestConstants.JSON_LINK_TAG);
			image = getImageUrl(jsonObject.getString(RestConstants.JSON_VARIATION_IMAGE_TAG));
		} catch (JSONException e) {
			Log.e(TAG, "Error initializing the variation ", e);
			return false;
		}

		return true;
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
		try {
			link = jsonObject.getString(RestConstants.JSON_LINK_TAG);
			image = getImageUrl(jsonObject.getString(RestConstants.JSON_VARIATION_IMAGE_TAG));
		} catch (JSONException e) {
			Log.e(TAG, "Error initializing the variation ", e);
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
			jsonObject.put(RestConstants.JSON_SKU_TAG, sku);
			jsonObject.put(RestConstants.JSON_LINK_TAG, link);
			jsonObject.put(RestConstants.JSON_IMAGE_TAG, image);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonObject;
	}
	
	public String getSKU() {
		return sku;
	}
	
	public void setSKU(String sku) {
		this.sku = sku;
	}

	public String getLink() {
		return link;
	}

	public String getImage() {
		return image;
	}

	private String getImageUrl(String url) {
		String modUrl = ImageResolutionHelper.replaceResolution(url);
		if(modUrl != null)
			return modUrl;
		return url;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(sku);
		dest.writeString(link);
		dest.writeString(image);
	}
	
	private Variation(Parcel in){
		sku = in.readString();
		link = in.readString();
		image = in.readString();
	}
	
    public static final Parcelable.Creator<Variation> CREATOR = new Parcelable.Creator<Variation>() {
        public Variation createFromParcel(Parcel in) {
            return new Variation(in);
        }

        public Variation[] newArray(int size) {
            return new Variation[size];
        }
    };
	
}
