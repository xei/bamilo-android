/**
 * @author GuilhermeSilva
 * @modified Manuel Silva
 * @version 1.5 - New Framework
 * 
 * 2012/06/18
 * 
 * Copyright (c) Rocket Internet All Rights Reserved
 */
package com.mobile.framework.objects;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.framework.database.FavouriteTableHelper;
import com.mobile.framework.interfaces.IJSONSerializable;
import com.mobile.framework.rest.RestConstants;
import com.mobile.framework.utils.CurrencyFormatter;

import org.json.JSONException;
import org.json.JSONObject;

import com.mobile.framework.output.Print;

/**
 * Class that holds the attributes of the product
 * 
 * @author GuilhermeSilva
 * 
 */
public class ProductAttributes extends BaseProduct implements IJSONSerializable {

	private static final String TAG = ProductAttributes.class.getName();
	private String description;
	private Double maxSavingPercentage;
	private Integer reviews;
	private Double rating;
	private boolean isNew;
	private boolean isFavourite;
	/**
	 * ProductAttributes empty constructor
	 */
	public ProductAttributes() {
		super();
		description = "";
		maxSavingPercentage = 0.0;
		reviews = 0;
		rating = .0;
		rating = 0.0;
	}

	/**
	 * @return the reviews
	 */
	public Integer getReviews() {
		return reviews;
	}

	/**
	 * @return the rating
	 */
	public Double getRating() {
		return rating;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * @return the discountPercentage
	 */
	public Double getMaxSavingPercentage() {
		return maxSavingPercentage;
	}

	/**
	 * Is new flag
	 * 
	 * @return true/false
	 * @author sergiopereira
	 */
	public boolean isNew() {
		return isNew;
	}

	/**
	 * Set the is new flag
	 * 
	 * @author sergiopereira
	 */
	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}

	/**
	 * Is favourite flag
	 * 
	 * @return true/false
	 */
	public boolean isFavourite() {
		return isFavourite;
	}

	/**
	 * Set the is favourite flag
	 * 
	 * @param isFavourite
	 */
	public void setFavourite(boolean isFavourite) {
		this.isFavourite = isFavourite;
	}

	/**
	 * Return the paid price for tracking.
	 * 
	 * @return double
	 * @author sergiopereira
	 */
	public double getPriceForTracking() {
		Print.i(TAG, "ORIGIN PRICE VALUES: " + priceDouble + " " + specialPriceDouble);
		Print.i(TAG, "PRICE VALUE FOR TRACKING: " + priceConverted + " " + specialPriceConverted);
		return specialPriceConverted > 0 ? specialPriceConverted : priceConverted;
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
			sku = jsonObject.getString(RestConstants.JSON_SKU_TAG);
			name = jsonObject.optString(RestConstants.JSON_PROD_NAME_TAG);
			url = jsonObject.optString(RestConstants.JSON_PROD_URL_TAG);
			description = jsonObject.optString(RestConstants.JSON_DESCRIPTION_TAG, "");
			brand = jsonObject.optString(RestConstants.JSON_BRAND_TAG);

			// Throw JSONException if JSON_PRICE_TAG is not present
			String priceJSON = jsonObject.getString(RestConstants.JSON_PRICE_TAG);
			if (CurrencyFormatter.isNumber(priceJSON)) {
				price = priceJSON;
				priceDouble = jsonObject.getDouble(RestConstants.JSON_PRICE_TAG);
			} else {
				throw new JSONException("Price is not a number!");
			}

			priceConverted = jsonObject.optDouble(RestConstants.JSON_PRICE_CONVERTED_TAG, 0d);

			// 
			String specialPriceJSON = jsonObject.optString(RestConstants.JSON_SPECIAL_PRICE_TAG);
			if (CurrencyFormatter.isNumber(specialPriceJSON)) {
				specialPrice = specialPriceJSON;
				specialPriceDouble = jsonObject.getDouble(RestConstants.JSON_SPECIAL_PRICE_TAG);
			} else {
				specialPrice = price;
				specialPriceDouble = priceDouble;
			}

			specialPriceConverted = jsonObject.optDouble(RestConstants.JSON_SPECIAL_PRICE_CONVERTED_TAG, 0d);


			maxSavingPercentage = jsonObject.optDouble(RestConstants.JSON_MAX_SAVING_PERCENTAGE_TAG, 0);

			if (maxSavingPercentage == 0 && !price.equals(specialPrice) && priceDouble >= 0) {
				maxSavingPercentage = (double) Math.round(specialPriceDouble / priceDouble);
			}

			JSONObject ratings = jsonObject.optJSONObject(RestConstants.JSON_RATINGS_TOTAL_TAG);
			if (ratings != null) {
				reviews = ratings.optInt(RestConstants.JSON_RATINGS_TOTAL_SUM_TAG);
				rating = ratings.optDouble(RestConstants.JSON_RATINGS_TOTAL_AVG_TAG);
			}

			// Get the is new JSON tag
			isNew = jsonObject.optBoolean(RestConstants.JSON_IS_NEW_TAG, false);

			/*
			try {
				isFavourite = FavouriteTableHelper.verifyIfFavourite(sku);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			*/

		} catch (JSONException e) {
			Print.e(TAG, "Error Parsing the product json", e);
			return false;
		}

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mobile.framework.objects.IJSONSerializable#toJSON()
	 */

	public JSONObject toJSON() {
		JSONObject jsonObject = new JSONObject();

		try {
			jsonObject.put(RestConstants.JSON_SKU_TAG, sku);
			jsonObject.put(RestConstants.JSON_PROD_NAME_TAG, name);
			jsonObject.put(RestConstants.JSON_PROD_URL_TAG, url);
			jsonObject.put(RestConstants.JSON_DESCRIPTION_TAG, description);
			/*--jsonObject.put(RestConstants.JSON_MAX_PRICE_TAG, maxPrice);*/
			jsonObject.put(RestConstants.JSON_PRICE_TAG, price);

		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return jsonObject;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
		dest.writeString(description);
		dest.writeDouble(maxSavingPercentage);
		dest.writeInt(reviews);
		dest.writeDouble(rating);
	}

	private ProductAttributes(Parcel in) {
        super(in);
		description = in.readString();
		/*--maxPrice = in.readString();*/
		/*--maxSpecialPrice = in.readString();*/
		maxSavingPercentage = in.readDouble();
		reviews = in.readInt();
		rating = in.readDouble();

		try {
			isFavourite = FavouriteTableHelper.verifyIfFavourite(sku);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	public static final Parcelable.Creator<ProductAttributes> CREATOR = new Parcelable.Creator<ProductAttributes>() {
		public ProductAttributes createFromParcel(Parcel in) {
			return new ProductAttributes(in);
		}

		public ProductAttributes[] newArray(int size) {
			return new ProductAttributes[size];
		}
	};
}
