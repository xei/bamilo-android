/**
 * @author GuilhermeSilva
 * @modified Manuel Silva
 * @version 1.5 - New Framework
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

import pt.rocket.framework.interfaces.IJSONSerializable;
import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.utils.CurrencyFormatter;
import de.akquinet.android.androlog.Log;

/**
 * Class that holds the attributes of the product
 * 
 * @author GuilhermeSilva
 * 
 */
public class ProductAttributes implements IJSONSerializable, Parcelable {

    private static final String TAG = ProductAttributes.class.getName();
    private String sku;
    private String name;
    private String url;
    private String description;
    private String brand;
    private String maxPrice;
    private String price;

    private String specialPrice;
    private String maxSpecialPrice;
    private Double maxSavingPercentage;

    private Integer reviews;
    private Double rating;
	private boolean isNew;

    /**
     * ProductAttributes empty constructor
     */
    public ProductAttributes() {
        sku = "";
        name = "";
        url = "";
        description = "";
        brand = "";

        maxPrice = "";
        price = "";

        specialPrice = "";
        maxSpecialPrice = "";
        maxSavingPercentage = 0.0;

        reviews = 0;
        rating = .0;
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
     * @return the sku
     */
    public String getSku() {
        return sku;
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
    public String getBrand() {
        return brand;
    }

    /**
     * @return the maxPrice
     */
    public String getMaxPrice() {
        return maxPrice;
    }

    /**
     * @return the price
     */
    public String getPrice() {
        return price;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @return the specialPrice
     */
    public String getSpecialPrice() {
        return specialPrice;
    }

    /**
     * @return the maxSpecialPrice
     */
    public String getMaxSpecialPrice() {
        return maxSpecialPrice;
    }

    /**
     * @return the discountPercentage
     */
    public Double getMaxSavingPercentage() {
        return maxSavingPercentage;
    }
    
    
    /**
     * Is new flag
     * @return true/false
     * @author sergiopereira
     */
    public boolean isNew() {
		return isNew;
	}

    /**
     * Set the is new flag
     * @author sergiopereira
     */
	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}

	/*
     * (non-Javadoc)
     * 
     * @see pt.rocket.framework.objects.IJSONSerializable#initialize(org.json.JSONObject)
     */
    @Override
    public boolean initialize(JSONObject jsonObject) {

        try {
            sku = jsonObject.getString(RestConstants.JSON_SKU_TAG);
            name = jsonObject.optString(RestConstants.JSON_PROD_NAME_TAG);
            url = jsonObject.optString(RestConstants.JSON_PROD_URL_TAG);
            description = jsonObject.optString(RestConstants.JSON_DESCRIPTION_TAG, "");
            brand = jsonObject.optString(RestConstants.JSON_BRAND_TAG);

            String priceString = jsonObject.optString(RestConstants.JSON_PRICE_TAG);
            //XXX
            double priceDouble = -1;
            try {
            	 priceDouble = Double.parseDouble(priceString);
                 price = CurrencyFormatter.formatCurrency(priceDouble);
			} catch (NumberFormatException e) {
				price = priceString;
				e.printStackTrace();
			}
            
           

            String maxPriceString = jsonObject.optString(RestConstants.JSON_MAX_PRICE_TAG, price);
            
            if (!maxPriceString.equals("")) {
                maxPrice = price;
            } else {
            	try {
            		double maxPriceDouble = Double.parseDouble(jsonObject.optString(
                            RestConstants.JSON_MAX_PRICE_TAG, price));
                     maxPrice = CurrencyFormatter.formatCurrency(maxPriceDouble);
				} catch (NumberFormatException e) {
					maxPrice = jsonObject.optString(
                            RestConstants.JSON_MAX_PRICE_TAG, price);
					e.printStackTrace();
				}
                
            }

            double specialPriceDouble = 0;
            if (!jsonObject.isNull(RestConstants.JSON_SPECIAL_PRICE_TAG)) {
            	try {
            		 specialPriceDouble = Double.parseDouble(jsonObject
                             .getString(RestConstants.JSON_SPECIAL_PRICE_TAG));
            		 specialPrice = CurrencyFormatter.formatCurrency(specialPriceDouble);
				} catch (NumberFormatException e) {
					specialPrice = jsonObject
                            .getString(RestConstants.JSON_SPECIAL_PRICE_TAG);
					e.printStackTrace();
				}
               
            } else {
            	specialPrice = price;
            }

             

            if (!jsonObject.isNull(RestConstants.JSON_MAX_SPECIAL_PRICE_TAG)) {
            	try {
            		maxSpecialPrice =
                            CurrencyFormatter.formatCurrency(Double.parseDouble(jsonObject.getString(RestConstants.JSON_MAX_SPECIAL_PRICE_TAG)));	
				} catch (NumberFormatException e) {
					maxSpecialPrice = jsonObject.getString(RestConstants.JSON_MAX_SPECIAL_PRICE_TAG);
					e.printStackTrace();
				}
                 
            } else {
                maxSpecialPrice = specialPrice;
            }

            maxSavingPercentage = jsonObject.optDouble(
                    RestConstants.JSON_MAX_SAVING_PERCENTAGE_TAG, 0);

            if (maxSavingPercentage == 0 && !price.equals(specialPrice) && priceDouble >=0) {
                maxSavingPercentage = (double) Math.round(specialPriceDouble / priceDouble);
            }

            JSONObject ratings = jsonObject.optJSONObject(RestConstants.JSON_RATINGS_TOTAL_TAG);
            if (ratings != null) {
                reviews = ratings.optInt(RestConstants.JSON_RATINGS_TOTAL_SUM_TAG);
                rating = ratings.optDouble(RestConstants.JSON_RATINGS_TOTAL_AVG_TAG);
            }
            
            // Get the is new JSON tag
            isNew = jsonObject.optBoolean(RestConstants.JSON_IS_NEW_TAG, false);

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

    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put(RestConstants.JSON_SKU_TAG, sku);
            jsonObject.put(RestConstants.JSON_PROD_NAME_TAG, name);
            jsonObject.put(RestConstants.JSON_PROD_URL_TAG, url);
            jsonObject.put(RestConstants.JSON_DESCRIPTION_TAG, description);
            jsonObject.put(RestConstants.JSON_MAX_PRICE_TAG, maxPrice);
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
        dest.writeString(sku);
        dest.writeString(name);
        dest.writeString(url);
        dest.writeString(description);
        dest.writeString(brand);

        dest.writeString(maxPrice);
        dest.writeString(price);

        dest.writeString(specialPrice);
        dest.writeString(maxSpecialPrice);
        dest.writeDouble(maxSavingPercentage);

        dest.writeInt(reviews);
        dest.writeDouble(rating);

    }
    
    
    private ProductAttributes(Parcel in) {
        sku = in.readString();
        name = in.readString();
        url = in.readString();
        description = in.readString();
        brand = in.readString();

        maxPrice = in.readString();
        price = in.readString();

        specialPrice = in.readString();
        maxSpecialPrice = in.readString();
        maxSavingPercentage = in.readDouble();

        reviews = in.readInt();
        rating = in.readDouble();
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
