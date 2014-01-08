/**
 * @author GuilhermeSilva
 * 
 * @version 1.01
 * 
 * 2012/06/18
 * 
 * Copyright (c) Rocket Internet All Rights Reserved
 */
package pt.rocket.framework.objects;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

import pt.rocket.framework.objects.ITargeting.TargetType;
import pt.rocket.framework.rest.RestConstants;
import de.akquinet.android.androlog.Log;

/**
 * Class that holds the attributes of the product
 * 
 * @author GuilhermeSilva
 * 
 */
public class BrandAttributes implements IJSONSerializable, Parcelable {

	private static final String TAG = TeaserBrandElement.class.getSimpleName();
	private String name;
	private int id;
	private String image_url;
	private String brand_url;
	private String description;
	private String target_type;

	/**
	 * ProductAttributes empty constructor
	 */
	public BrandAttributes() {
		name = "";
		id = -1;
		image_url = "";
		brand_url = "";
		description = "";
		target_type = "";
	}

	/**
	 * @return the reviews
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @return the rating
	 */
	public String getBrandUrl() {
		return brand_url;
	}

	/**
	 * @return the sku
	 */
	public String getImageUrl() {
		return image_url;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the brand
	 */
	public TargetType getTargetType() {
		return TargetType.BRAND;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * pt.rocket.framework.objects.IJSONSerializable#initialize(org.json.JSONObject
	 * )
	 */
	@Override
	public boolean initialize(JSONObject jsonObject) {
		try {
			name = jsonObject.getString(RestConstants.JSON_BRAND_DESCRIPTION_TAG);
			target_type = jsonObject.optString(RestConstants.JSON_TARGET_TYPE_TAG, "");
			description = jsonObject.optString(RestConstants.JSON_DESCRIPTION_TAG, "");
			brand_url = jsonObject.getJSONObject(RestConstants.JSON_IMAGE_LIST_TAG).getString(RestConstants.JSON_BRAND_URL_TAG);
			image_url = jsonObject.getJSONObject(RestConstants.JSON_IMAGE_LIST_TAG).getString(RestConstants.JSON_IMAGE_URL_TAG);
		} catch (JSONException e) {
			Log.e(TAG, "Error Parsing the product json", e);
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pt.rocket.framework.objects.IJSONSerializable#toJSON()
	 */
	@Override
	public JSONObject toJSON() {
		JSONObject jsonObject = new JSONObject();

		try {
			jsonObject.put(RestConstants.JSON_ID_TAG, id);
			jsonObject.put(RestConstants.JSON_BRAND_DESCRIPTION_TAG, name);
			jsonObject.put(RestConstants.JSON_IMAGE_URL_TAG, image_url);
			jsonObject.put(RestConstants.JSON_BRAND_URL_TAG, brand_url);
			jsonObject.put(RestConstants.JSON_DESCRIPTION_TAG, description);

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
	    dest.writeString(name);
	    dest.writeInt(id);
	    dest.writeString(image_url);
	    dest.writeString(brand_url);
	    dest.writeString(description);
	    dest.writeString(target_type);
	}
	
	/**
	 * Parcel constructor
	 * @param in
	 */
	private BrandAttributes(Parcel in) {
		name = in.readString();
		id = in.readInt();
		image_url = in.readString();
		brand_url = in.readString();
		description = in.readString();
		target_type = in.readString();
    }
	
	/**
	 * Create parcelable 
	 */
	public static final Parcelable.Creator<BrandAttributes> CREATOR = new Parcelable.Creator<BrandAttributes>() {
        public BrandAttributes createFromParcel(Parcel in) {
            return new BrandAttributes(in);
        }

        public BrandAttributes[] newArray(int size) {
            return new BrandAttributes[size];
        }
    };
}
