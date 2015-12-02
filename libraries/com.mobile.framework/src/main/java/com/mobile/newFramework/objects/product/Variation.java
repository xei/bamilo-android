package com.mobile.newFramework.objects.product;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.utils.ImageResolutionHelper;
import com.mobile.newFramework.utils.output.Print;

import org.json.JSONException;
import org.json.JSONObject;

public class Variation implements IJSONSerializable, Parcelable{

	private static final String TAG = Variation.class.getSimpleName();

	private String sku;
	private String link;
	private String image;

	//added new info
	private String name;
	private String brand;
	private double price;
	private double specialPrice;


	public Variation() {

	}
	
	public boolean initialize(String sku, JSONObject jsonObject) {
		this.sku = sku;
		try {
			link = jsonObject.getString(RestConstants.LINK);
			image = getImageUrl(jsonObject.getString(RestConstants.JSON_VARIATION_IMAGE_TAG));

			//added new tags
			name = jsonObject.getString(RestConstants.JSON_VARIATION_NAME_TAG);
			brand = jsonObject.getString(RestConstants.BRAND);
			price = jsonObject.getDouble(RestConstants.JSON_VARIATION_PRICE_TAG);
			specialPrice = jsonObject.getDouble(RestConstants.JSON_VARIATION_SPECIALPRICE_TAG);

		} catch (JSONException e) {
			Print.e(TAG, "Error initializing the variation ", e);
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
			link = jsonObject.optString(RestConstants.LINK);
			image = getImageUrl(jsonObject.getString(RestConstants.JSON_VARIATION_IMAGE_TAG));

			//added new tags
			sku = jsonObject.getString(RestConstants.JSON_SKU_TAG);
			name = jsonObject.getString(RestConstants.JSON_VARIATION_NAME_TAG);
			brand = jsonObject.getString(RestConstants.BRAND);
			price = jsonObject.getDouble(RestConstants.JSON_VARIATION_PRICE_TAG);
			specialPrice = jsonObject.optDouble(RestConstants.JSON_VARIATION_SPECIALPRICE_TAG);
		} catch (JSONException e) {
			Print.e(TAG, "Error initializing the variation ", e);
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
			jsonObject.put(RestConstants.SKU, sku);
			jsonObject.put(RestConstants.LINK, link);
			jsonObject.put(RestConstants.JSON_IMAGE_TAG, image);

			//added
			jsonObject.put(RestConstants.JSON_VARIATION_NAME_TAG, name);
			jsonObject.put(RestConstants.BRAND, brand);
			jsonObject.put(RestConstants.JSON_VARIATION_PRICE_TAG, price);
			jsonObject.put(RestConstants.JSON_VARIATION_SPECIALPRICE_TAG, specialPrice);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonObject;
	}

	@Override
	public int getRequiredJson() {
		return RequiredJson.NONE;
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

	//added
	public String getName() {
		return name;
	}

	public String getBrand() {
		return brand;
	}

	public double getPrice() {
		return price;
	}

	public double getSpecialPrice() {
		return specialPrice;
	}

	public boolean hasDiscount() {
		return specialPrice > 0 && specialPrice != Double.NaN;
	}


	private String getImageUrl(String url) {
		String modUrl = ImageResolutionHelper.replaceResolution(url);
		if(modUrl != null)
			return modUrl;
		return url;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(sku);
		dest.writeString(link);
		dest.writeString(image);

		//added
		dest.writeString(name);
		dest.writeString(brand);
		dest.writeDouble(price);
		dest.writeDouble(specialPrice);
	}
	
	private Variation(Parcel in){
		sku = in.readString();
		link = in.readString();
		image = in.readString();

		//added
		name = in.readString();
		brand = in.readString();
		price = in.readDouble();
		specialPrice = in.readDouble();
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
