package com.mobile.newFramework.objects.product;


import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.utils.output.Print;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class that represents the server side product. Contains id, name,
 * description, price(deprecated), stock(deprecated), list of images, brand and
 * list of category id.
 *
 * @author GuilhermeSilva
 * @modified Manuel Silva
 *
 */
public class Product extends NewProductBase implements IJSONSerializable, Parcelable {

    public final static String TAG = Product.class.getName();

    private int reviews;
    private double rating;

    /**
     * simple product constructor.
     */
    public Product() {
        // ....
    }

    /* (non-Javadoc)
     * @see com.mobile.framework.objects.IJSONSerializable#initialize(org.json.JSONObject)
     */
    @Override
    public boolean initialize(JSONObject jsonObject) {
        try {
            super.initialize(jsonObject);
            JSONObject ratings = jsonObject.optJSONObject(RestConstants.JSON_RATINGS_TOTAL_TAG);
            if (ratings != null) {
                reviews = ratings.optInt(RestConstants.JSON_RATINGS_TOTAL_SUM_TAG);
                rating = ratings.optDouble(RestConstants.JSON_RATINGS_TOTAL_AVG_TAG);
            }
        } catch (JSONException e) {
            Print.e(TAG, "ERROR: JSE ON PARSE PRODUCT", e);
            return false;
        }
        return true;
    }

    /* (non-Javadoc)
     * @see com.mobile.framework.objects.IJSONSerializable#toJSON()
     */
    public JSONObject toJSON() {
        return null;
    }

    /**
     * @return the rating.
     */
    public double getRating() {
        return rating;
    }

    public int getReviews() {
        return reviews;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(reviews);
        dest.writeDouble(rating);
    }

    protected Product(Parcel in) {
        reviews = in.readInt();
        rating = in.readDouble();
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
