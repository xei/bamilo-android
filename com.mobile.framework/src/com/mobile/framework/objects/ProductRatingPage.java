/**
 * 
 */
package com.mobile.framework.objects;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.framework.rest.RestConstants;

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

	public ProductRatingPage() {
	    productName = "";
	    productSku = "";
	    commentsCount = 0;
	    ratingTypes = new ArrayList<RatingStar>();
	    reviewComments = new ArrayList<ProductReviewComment>();
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
		reviewComments = new ArrayList<ProductReviewComment>();

		// just used for seller reviews
		 sellerName = dataObject.optString(RestConstants.JSON_NAME_TAG);
		 sellerUrl = dataObject.optString(RestConstants.JSON_URL_TAG);
		
		JSONObject productObject = dataObject.optJSONObject(RestConstants.JSON_PRODUCT_TAG);
		if (productObject != null) {
            productName = productObject.optString(RestConstants.JSON_NAME_TAG);
            productSku = productObject.optString(RestConstants.JSON_SKU_TAG);
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
	        average = reviewsObject.optInt(RestConstants.JSON_RATINGS_AVERAGE_TAG,-1);
	        
	        if(comments != null){
	            int size = comments.length();
	            ProductReviewComment reviewComment = null;
	            for (int i = 0; i < size; i++) {
	                reviewComment = new ProductReviewComment();
	                reviewComment.initialize(comments.getJSONObject(i));
	                reviewComments.add(reviewComment);
	            }
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
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub
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
	}

	private ProductRatingPage(Parcel in) {
	    productName = in.readString();
	    productSku = in.readString();
	    ratingTypes = new ArrayList<RatingStar>();
	    in.readList(ratingTypes, RatingStar.class.getClassLoader());
		commentsCount = in.readInt();
		reviewComments = new ArrayList<ProductReviewComment>();
		in.readList(reviewComments, ProductReviewComment.class.getClassLoader());
		minStarSize = in.readInt();
		maxStarSize = in.readInt();
		average = in.readInt();
		sellerName = in.readString();
		sellerUrl = in.readString();
		
	}
	
    public static final Parcelable.Creator<ProductRatingPage> CREATOR = new Parcelable.Creator<ProductRatingPage>() {
        public ProductRatingPage createFromParcel(Parcel in) {
            return new ProductRatingPage(in);
        }

        public ProductRatingPage[] newArray(int size) {
            return new ProductRatingPage[size];
        }
    };
	
}
