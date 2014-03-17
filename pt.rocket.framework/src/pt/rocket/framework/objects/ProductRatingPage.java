/**
 * 
 */
package pt.rocket.framework.objects;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.framework.rest.RestConstants;

/**
 * Class that represents the response from the get products rating
 * @author nutzer2
 * 
 */
public class ProductRatingPage implements IJSONSerializable {

//	private static final String JSON_AGGREGATEDATA_TAG = "aggregatedData";
//	private static final String JSON_STARS_TAG = "stars";
//	private static final String JSON_SIZE_STARS_FORE_TAG = "size-stars-fore";
//	private static final String JSON_COMMENTS_COUNT_TAG = "commentsCount";
//	private static final String JSON_COMMENTS_TAG = "comments";

	private double productRating;
	private int commentsCount;
	private ArrayList<ProductReviewComment> reviewComments;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * pt.rocket.framework.objects.IJSONSerializable#initialize(org.json.JSONObject
	 * )
	 */
	@Override
	public boolean initialize(JSONObject dataObject) throws JSONException {
		reviewComments = new ArrayList<ProductReviewComment>();
		productRating = 0;

		JSONObject aggregatedataObject = dataObject
				.optJSONObject(RestConstants.JSON_AGGREGATEDATA_TAG);
		if (aggregatedataObject != null) {
			JSONArray stars = aggregatedataObject.getJSONArray(RestConstants.JSON_STARS_TAG);

			JSONObject rating = null;
			int size = stars.length();

			for (int i = 0; i < size; i++) {
				rating = stars.getJSONObject(i);
				productRating += rating.getInt(RestConstants.JSON_SIZE_STARS_FORE_TAG);
			}
			productRating /= size;
			productRating /= 100;
		}

		commentsCount = dataObject.optInt(RestConstants.JSON_COMMENTS_COUNT_TAG, 0);

		// comments.
		JSONArray comments = dataObject.getJSONArray(RestConstants.JSON_COMMENTS_TAG);
		int size = comments.length();
		ProductReviewComment reviewComment = null;
		for (int i = 0; i < size; i++) {
			reviewComment = new ProductReviewComment();
			reviewComment.initialize(comments.getJSONObject(i));
			reviewComments.add(reviewComment);
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
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @return the productRating
	 */
	public double getProductRating() {
		return productRating;
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

}
