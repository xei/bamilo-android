package com.mobile.newFramework.objects.product;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.pojo.RestConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Class that represents the response from the get products rating
 * @author nutzer2
 * 
 */
public class ProductRatingPage implements IJSONSerializable, Parcelable {

    private String productSku;
    private String productName;
    private ArrayList<RatingStar> ratingTypes;
    private int minStarSize;
    private int maxStarSize;
    private int average;
    private String sellerUrl;
    private String sellerName;
	private int commentsCount;
	private ArrayList<ProductReviewComment> reviewComments;
	private int currentPage;
	private int totalPages;

	public ProductRatingPage() {
	    productName = "";
	    productSku = "";
	    commentsCount = 0;
	    ratingTypes = new ArrayList<>();
	    reviewComments = new ArrayList<>();
	    minStarSize = 1;
	    maxStarSize = 5;
	    average = -1;
	    sellerUrl = "";
	    sellerName = "";
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.mobile.framework.objects.IJSONSerializable#initialize(org.json.JSONObject
	 * )
	 */
	@Override
	public boolean initialize(JSONObject dataObject) throws JSONException {

		reviewComments = new ArrayList<>();

		// just used for seller reviews
		 sellerName = dataObject.optString(RestConstants.JSON_NAME_TAG);
		 sellerUrl = dataObject.optString(RestConstants.URL);

		JSONObject productObject = dataObject.optJSONObject(RestConstants.JSON_PRODUCT_TAG);
		if (productObject != null) {
            productName = productObject.optString(RestConstants.JSON_NAME_TAG);
            productSku = productObject.optString(RestConstants.SKU);
        }
        JSONObject starSizeObject = dataObject.optJSONObject(RestConstants.JSON_RATING_STAR_SIZE_TAG);

		if(starSizeObject != null){
		    minStarSize = starSizeObject.optInt(RestConstants.JSON_NAME_TAG, 1);
		    maxStarSize =  starSizeObject.optInt(RestConstants.JSON_NAME_TAG, 5);
		}

		JSONObject ratingsObject = dataObject.optJSONObject(RestConstants.REVIEW_RATING_FIELD);

		if (ratingsObject != null) {
            JSONArray ratingTypes = ratingsObject.optJSONArray(RestConstants.JSON_RATING_TYPE_TAG);
            if (ratingTypes != null && ratingTypes.length() > 0) {
                for (int i = 0; i < ratingTypes.length(); i++) {

                    JSONObject ratingType = ratingTypes.getJSONObject(i);

                    RatingStar type = new RatingStar();
                    type.initialize(ratingType);

                    this.ratingTypes.add(type);
                }
            }
        }
        JSONObject reviewsObject = dataObject.optJSONObject(RestConstants.JSON_REVIEWS_TAG);
		if(reviewsObject != null){
		    commentsCount = reviewsObject.optInt(RestConstants.JSON_TOTAL_TAG, 0);

	        // comments.
	        JSONArray comments = reviewsObject.optJSONArray(RestConstants.JSON_COMMENTS_TAG);

	        // just used for seller reviews
	        average = reviewsObject.optInt(RestConstants.JSON_RATINGS_AVERAGE_TAG, -1);

	        if(comments != null){
	            int size = comments.length();
	            ProductReviewComment reviewComment;
	            for (int i = 0; i < size; i++) {
	                reviewComment = new ProductReviewComment();
	                reviewComment.initialize(comments.getJSONObject(i));
	                reviewComments.add(reviewComment);
	            }
	        }

			JSONObject paginationObject = reviewsObject.optJSONObject(RestConstants.JSON_ORDER_PAGINATION_TAG);
			if(paginationObject != null){
				currentPage = paginationObject.getInt(RestConstants.JSON_ORDER_CURRENT_PAGE_TAG);
				totalPages = paginationObject.getInt(RestConstants.JSON_ORDER_TOTAL_PAGES_TAG);
			}

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
		return null;
	}

	@Override
	public int getRequiredJson() {
		return RequiredJson.OBJECT_DATA;
	}

	/**
	 * @return the reviewComments
	 */
	public ArrayList<ProductReviewComment> getReviewComments() {
		return reviewComments;
	}

	/**
	 * @return the commentsCount
	 */
	public int getCommentsCount() {
		return commentsCount;
	}

	public ArrayList<RatingStar> getRatingTypes() {
        return ratingTypes;
    }

    public void setRatingTypes(ArrayList<RatingStar> ratingTypes) {
        this.ratingTypes = ratingTypes;
    }

    public int getMinStarSize() {
        return minStarSize;
    }

    public void setMinStarSize(int minStarSize) {
        this.minStarSize = minStarSize;
    }

    public int getMaxStarSize() {
        return maxStarSize;
    }

    public void setMaxStarSize(int maxStarSize) {
        this.maxStarSize = maxStarSize;
    }

    /**
     * field user for seller only
     * @return
     */
	public int getAverage() {
        return average;
    }
    /**
     * field user for seller only
     * @return
     */
    public void setAverage(int average) {
        this.average = average;
    }

    @Override
	public int describeContents() {
		return 0;
	}

	public String getProductSku() {
        return productSku;
    }

    public void setProductSku(String productSku) {
        this.productSku = productSku;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }





    /**
     * field user for seller only
     * @return
     */
    public String getSellerUrl() {
        return sellerUrl;
    }

    /**
     * field user for seller only
     * @return
     */
    public void setSellerUrl(String sellerUrl) {
        this.sellerUrl = sellerUrl;
    }
    /**
     * field user for seller only
     * @return
     */
    public String getSellerName() {
        return sellerName;
    }
    /**
     * field user for seller only
     * @return
     */
    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    @Override
	public void writeToParcel(Parcel dest, int flags) {
	    dest.writeString(productName);
	    dest.writeString(productSku);
	    dest.writeList(ratingTypes);
		dest.writeInt(commentsCount);
		dest.writeList(reviewComments);
		dest.writeInt(minStarSize);
		dest.writeInt(maxStarSize);
		dest.writeInt(average);
	    dest.writeString(sellerName);
        dest.writeString(sellerUrl);
		dest.writeInt(currentPage);
		dest.writeInt(totalPages);
	}

	private ProductRatingPage(Parcel in) {
	    productName = in.readString();
	    productSku = in.readString();
	    ratingTypes = new ArrayList<>();
	    in.readList(ratingTypes, RatingStar.class.getClassLoader());
		commentsCount = in.readInt();
		reviewComments = new ArrayList<>();
		in.readList(reviewComments, ProductReviewComment.class.getClassLoader());
		minStarSize = in.readInt();
		maxStarSize = in.readInt();
		average = in.readInt();
		sellerName = in.readString();
		sellerUrl = in.readString();
		currentPage = in.readInt();
		totalPages = in.readInt();

	}

    public static final Creator<ProductRatingPage> CREATOR = new Creator<ProductRatingPage>() {
        public ProductRatingPage createFromParcel(Parcel in) {
            return new ProductRatingPage(in);
        }

        public ProductRatingPage[] newArray(int size) {
            return new ProductRatingPage[size];
        }
    };

	public int getCurrentPage() {
		return currentPage;
	}

	public int getTotalPages() {
		return totalPages;
	}
}
