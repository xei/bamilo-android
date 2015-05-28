/**
 *
 */
package com.mobile.newFramework.objects.product;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.framework.rest.RestConstants;
import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class that represents the response from the get products rating
 *
 * @author nutzer2
 */
public class SuperProductRatingPage implements IJSONSerializable, Parcelable {

    private int totalPages;
    private int currentPage;
    private ProductRatingPage rating;

    public SuperProductRatingPage() {
    }



    /*
     * (non-Javadoc)
     *
     * @see
     * com.mobile.framework.objects.IJSONSerializable#initialize(org.json.JSONObject
     * )
     */
    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        rating = new ProductRatingPage();
        try {
            JSONObject dataObject = jsonObject.getJSONObject(RestConstants.JSON_DATA_TAG);
            rating.initialize(dataObject);
            JSONObject reviewsObject = dataObject.optJSONObject(RestConstants.JSON_REVIEWS_TAG);
            if (reviewsObject != null) {
                JSONObject paginationObject = reviewsObject.optJSONObject(RestConstants.JSON_ORDER_PAGINATION_TAG);
                if (paginationObject != null) {
                    int totalPages = paginationObject.optInt(RestConstants.JSON_ORDER_TOTAL_PAGES_TAG, -1);
                    int currentPage = paginationObject.optInt(RestConstants.JSON_ORDER_CURRENT_PAGE_TAG, -1);
                    if (currentPage != -1 && totalPages != -1) {
                        this.totalPages = totalPages;
                        this.currentPage = currentPage;

                    }
                }
            }
        }catch (JSONException e){
            e.printStackTrace();
        }


        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.mobile.framework.objects.IJSONSerializable#toJSON()
     */
    @Override
    public JSONObject toJSON() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public RequiredJson getRequiredJson() {
        return RequiredJson.METADATA;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(totalPages);
        dest.writeInt(currentPage);
        dest.writeValue(rating);

    }

    private SuperProductRatingPage(Parcel in) {
        totalPages = in.readInt();
        currentPage = in.readInt();
        rating = (ProductRatingPage) in.readValue(ProductRatingPage.class.getClassLoader());


    }

    public static final Creator<SuperProductRatingPage> CREATOR = new Creator<SuperProductRatingPage>() {
        public SuperProductRatingPage createFromParcel(Parcel in) {
            return new SuperProductRatingPage(in);
        }

        public SuperProductRatingPage[] newArray(int size) {
            return new SuperProductRatingPage[size];
        }
    };

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public ProductRatingPage getRating() {
        return rating;
    }

    public void setRating(ProductRatingPage rating) {
        this.rating = rating;
    }
}
