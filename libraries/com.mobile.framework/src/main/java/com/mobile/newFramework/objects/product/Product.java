package com.mobile.newFramework.objects.product;


import android.database.sqlite.SQLiteException;
import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.newFramework.database.FavouriteTableHelper;
import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.utils.shop.CurrencyFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Class that represents the server side product. Contains id, name,
 * description, price(deprecated), stock(deprecated), list of images, brand and
 * list of category id.
 *
 * @author GuilhermeSilva
 * @modified Manuel Silva
 *
 */
public class Product extends BaseProduct implements IJSONSerializable, Parcelable {

    public final static String TAG = Product.class.getName();

//    private String id;

    private Double maxSavingPercentage;
    private Integer reviews;
    private Double rating;
    private boolean isNew;

    private ArrayList<Image> images;
    private String firstImageURL;
    private ArrayList<Image> imagesTablet;

    /**
     * simple product constructor.
     */
    public Product() {
//        id = "";
        images = new ArrayList<>();
        imagesTablet = new ArrayList<>();
        maxSavingPercentage = 0.0;
        reviews = 0;
        rating = .0;
        rating = 0.0;
    }

    /**
     * @return the id
     */
//    public String getId() {
//        return id;
//    }

    /**
     * @return the images of the simple product.
     */
    public ArrayList<Image> getImages() {
        return images;
    }

    public String getFirstImageURL() {
        return firstImageURL;
    }

    public ArrayList<Image> getImagesTablet() {
        return imagesTablet;
    }

    /* (non-Javadoc)
     * @see com.mobile.framework.objects.IJSONSerializable#initialize(org.json.JSONObject)
     */
    @Override
    public boolean initialize(JSONObject jsonObject) {
        try {
//            id = jsonObject.getString(RestConstants.JSON_ID_TAG);

            //BaseProduct attributes

            sku = jsonObject.getString(RestConstants.JSON_SKU_TAG);
            name = jsonObject.optString(RestConstants.JSON_PROD_NAME_TAG);
            url = jsonObject.optString(RestConstants.JSON_PROD_URL_TAG);
//            description = jsonObject.optString(RestConstants.JSON_DESCRIPTION_TAG, "");
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
            isFavourite = jsonObject.optBoolean(RestConstants.JSON_IS_WISHLIST, false);


            //Product attributes

            images.clear();
            imagesTablet.clear();

            // New method
            if (jsonObject.has(RestConstants.JSON_TEASER_IMAGES_TAG)){
                JSONArray newImageArray = jsonObject.optJSONArray(RestConstants.JSON_IMAGES_TAG);
                if (newImageArray != null) {
                    for (int i = 0; i < newImageArray.length(); i++) {
                        // Validate device type
                        JSONObject jsonImage = newImageArray.optJSONObject(i);
                        String device = jsonImage.optString(RestConstants.JSON_IMAGE_DEVICE_TYPE_TAG, RestConstants.JSON_PHONE_TAG);
//                        Log.d("IMAGE TEASER", "DEVICE: " + device);
                        if(device.equalsIgnoreCase(RestConstants.JSON_PHONE_TAG)) {
                            String imageUrl = jsonImage.optString(RestConstants.JSON_TEASER_IMAGE_URL_TAG);
                            Image image = new Image(imageUrl, null, null, null);
                            images.add(image);
                        } else if(device.equalsIgnoreCase(RestConstants.JSON_TABLET_TAG)){
                            String imageTableUrl = jsonImage.optString(RestConstants.JSON_TEASER_IMAGE_URL_TAG);
                            Image image = new Image(imageTableUrl, null, null, null);
                            imagesTablet.add(image);
                        }
                    }
                }

            } else {

                // Old method
                JSONArray imageArray = jsonObject.optJSONArray(RestConstants.JSON_IMAGES_TAG);
                if (imageArray != null) {
                    int imageArrayLenght = imageArray.length();
                    for (int i = 0; i < imageArrayLenght; ++i) {
                        JSONObject imageObject = imageArray.getJSONObject(i);
                        Image image = new Image();
                        image.initialize(imageObject);
                        images.add(image);
                    }
                }
            }

            firstImageURL = "";
            if (0 < images.size()) {
                firstImageURL = images.get(0).getUrl();
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
    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();
        try {
//            jsonObject.put(RestConstants.JSON_ID_TAG, id);
//            jsonObject.put(RestConstants.JSON_DATA_TAG, attributes.toJSON());
//            jsonObject.put(RestConstants.JSON_PROD_ATTRIBUTES_TAG, attributes.toJSON());

            JSONArray imageArray = new JSONArray();
            for(Image image : images) {
                imageArray.put(image.toJSON());
            }
            jsonObject.put(RestConstants.JSON_IMAGES_TAG, imageArray);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return jsonObject;
    }

    @Override
    public RequiredJson getRequiredJson() {
        return null;
    }

    /**
     * @return the product sku
     */
    public String getSKU() {
        return sku;
    }

    /**
     * @return the product name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the product brand
     */
    public String getBrand() {
        return brand;
    }

    /**
     * @return the description
     */
//    public String getDescription() {
//        return attributes.getDescription();
//    }

    /**
     * @return the price
     */
    public String getPrice() {
        return price;
    }

    /**
     * @return the price
     */
    public double getPriceAsDouble() {
        return priceDouble;
    }

    /**
     * @return the price
     */
    public double getSpecialPriceAsDouble() {
        return specialPriceDouble;
    }

    /**
     * Validate if product has special price
     */
    public boolean hasDiscountPercentage() {
        return maxSavingPercentage > 0;
    }

    /**
     * @return the max saving percentage
     */
    public Double getMaxSavingPercentage() {
        return maxSavingPercentage;
    }

    /**
     * @return the rating.
     */
    public Double getRating() {
        return rating;
    }

    public Integer getReviews() {
        return reviews;
    }

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
     * @return the specialPrice
     */
    public double getPriceConverted() {
        return priceConverted;
    }

    /**
     * @return the specialPrice
     */
    public double getSpecialPriceConverted() {
        return specialPriceConverted;
    }

    public boolean isNew() {
        return isNew;
    }

    /**
     * Return the paid price for tracking.
     *
     * @return double
     * @author sergiopereira
     */
    public double getPriceForTracking() {
        return specialPriceConverted > 0 ? specialPriceConverted : priceConverted;
    }

    /**
     * @return the maxSpecialPrice
     */
    /*--public String getMaxSpecialPrice() {
        return attributes.getMaxSpecialPrice();
    }*/

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeString(id);
        dest.writeDouble(maxSavingPercentage);
        dest.writeInt(reviews);
        dest.writeDouble(rating);
        dest.writeList(images);
        dest.writeList(imagesTablet);
    }

    protected Product(Parcel in) {
//        id = in.readString();
        maxSavingPercentage = in.readDouble();
        reviews = in.readInt();
        rating = in.readDouble();
        try {
            isFavourite = FavouriteTableHelper.verifyIfFavourite(sku);
        } catch (InterruptedException | SQLiteException |  IllegalMonitorStateException e) {
            e.printStackTrace();
        }

        images = new ArrayList<>();
        in.readList(images, Image.class.getClassLoader());
        imagesTablet = new ArrayList<>();
        in.readList(imagesTablet, Image.class.getClassLoader());

        firstImageURL = "";
        if (0 < images.size()) {
            firstImageURL = images.get(0).getUrl();
        }
    }

    public static final Parcelable.Creator<Product> CREATOR = new Parcelable.Creator<Product>() {
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        public Product[] newArray(int size) {
            return new Product[size];
        }
    };
}
