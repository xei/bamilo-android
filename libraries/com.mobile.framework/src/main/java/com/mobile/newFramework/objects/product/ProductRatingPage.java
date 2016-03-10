package com.mobile.newFramework.objects.product;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.pojo.IntConstants;
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
	private final ArrayList<RatingStar> ratingTypes;
	private int minStarSize;
	private int maxStarSize;
	private int average;
	private String sellerUrl;
	private String sellerName;
	private int commentsCount;
	private ArrayList<ProductReviewComment> reviewComments;
	private int currentPage;
	private int totalPages;

	//added
	private JSONObject byStarsObject;
	private int mBasedOn;

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
		mBasedOn = 0;
	}

	public int getMaxStarSize() {
		return maxStarSize;
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
		sellerName = dataObject.optString(RestConstants.NAME);
		sellerUrl = dataObject.optString(RestConstants.URL);

		JSONObject productObject = dataObject.optJSONObject(RestConstants.PRODUCT);
		if (productObject != null) {
			productName = productObject.optString(RestConstants.NAME);
			productSku = productObject.optString(RestConstants.SKU);
		}
		JSONObject starSizeObject = dataObject.optJSONObject(RestConstants.STARS_SIZE);
		if(starSizeObject != null){
			minStarSize = starSizeObject.optInt(RestConstants.NAME, 1);
			maxStarSize =  starSizeObject.optInt(RestConstants.NAME, 5);
		}
		// Ratings
		JSONObject ratingsObject = dataObject.optJSONObject(RestConstants.RATINGS);
		if (ratingsObject != null) {
			mBasedOn = ratingsObject.optInt(RestConstants.BASED_ON);
			JSONArray ratingTypes = ratingsObject.optJSONArray(RestConstants.BY_TYPE);
			if (ratingTypes != null && ratingTypes.length() > 0) {
				for (int i = 0; i < ratingTypes.length(); i++) {
					JSONObject ratingType = ratingTypes.getJSONObject(i);
					RatingStar type = new RatingStar();
					type.initialize(ratingType);
					this.ratingTypes.add(type);
				}
			}
			//added by_stars for ratings page
			byStarsObject = ratingsObject.optJSONObject(RestConstants.BY_STARS);
		}
		// Reviews
		JSONObject reviewsObject = dataObject.optJSONObject(RestConstants.REVIEWS);
		if(reviewsObject != null){
			commentsCount = reviewsObject.optInt(RestConstants.TOTAL, 0);
			// comments.
			JSONArray comments = reviewsObject.optJSONArray(RestConstants.COMMENTS);
			// just used for seller reviews
			average = reviewsObject.optInt(RestConstants.AVERAGE, -1);
			if(comments != null){
				int size = comments.length();
				ProductReviewComment reviewComment;
				for (int i = 0; i < size; i++) {
					reviewComment = new ProductReviewComment();
					reviewComment.initialize(comments.getJSONObject(i));
					reviewComments.add(reviewComment);
				}
			}
			JSONObject paginationObject = reviewsObject.optJSONObject(RestConstants.PAGINATION);
			if(paginationObject != null){
				currentPage = paginationObject.getInt(RestConstants.CURRENT_PAGE);
				totalPages = paginationObject.getInt(RestConstants.TOTAL_PAGES);
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
		return RequiredJson.METADATA;
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


	public int getBasedOn() { return mBasedOn;}

	/**
	 * field user for seller only
	 */
	public int getAverage() {
		return average;
	}


	@Override
	public int describeContents() {
		return 0;
	}

	public String getProductSku() {
		return productSku;
	}

	public String getProductName() {
		return productName;
	}


	/**
	 * Get number of ratings / star throught star name
	 */
	public String getByStarValue(String name) {
		return byStarsObject != null ? byStarsObject.optString(name) : String.valueOf(IntConstants.DEFAULT_POSITION);
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
		dest.writeInt(mBasedOn);
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
		mBasedOn = in.readInt();
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
