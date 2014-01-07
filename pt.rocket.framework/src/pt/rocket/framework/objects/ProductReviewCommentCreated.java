package pt.rocket.framework.objects;

import java.util.HashMap;
import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class used to inser a new review in the api.
 */
public class ProductReviewCommentCreated implements Parcelable {

	private static final String FORM_NAME_FIELD = "RatingForm[name]";
	private static final String FORM_TITLE_FIELD = "RatingForm[title]";
	private static final String FORM_COMMENT_FIELD = "RatingForm[comment]";
	private static final String FORM_COSTUMER_ID_FIELD = "rating-customer";

	private String title;
	private String comments;
	private String name;
	private String email;

	private HashMap<String, Double> rating;
	
	public ProductReviewCommentCreated() {
	
	}

	public ContentValues getObjectModel() {
		ContentValues values = new ContentValues();

		values.put(FORM_NAME_FIELD, name);
		values.put(FORM_TITLE_FIELD, title);
		values.put(FORM_COMMENT_FIELD, comments);

		return values;
	}

	public void addParameters(ContentValues values) {
		values.put(FORM_NAME_FIELD, name);
		values.put(FORM_TITLE_FIELD, title);
		values.put(FORM_COMMENT_FIELD, comments);

	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the comments
	 */
	public String getComments() {
		return comments;
	}

	/**
	 * @param comments
	 *            the comments to set
	 */
	public void setComments(String comments) {
		this.comments = comments;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 
	 * @param email
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the rating
	 */
	public HashMap<String, Double> getRating() {
		return rating;
	}

	/**
	 * @param rating
	 *            the rating to set
	 */
	public void setRating(HashMap<String, Double> rating) {
		this.rating = rating;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(title);
		dest.writeString(comments);
		dest.writeString(name);
		dest.writeString(email);

	}

	private ProductReviewCommentCreated(Parcel in){
		title = in.readString();
		comments = in.readString();
		name = in.readString();
		email = in.readString();
	}
	
	private static final Parcelable.Creator<ProductReviewCommentCreated> CREATOR = new Parcelable.Creator<ProductReviewCommentCreated>() {
		public ProductReviewCommentCreated createFromParcel(Parcel in) {
			return new ProductReviewCommentCreated(in);
		}

		public ProductReviewCommentCreated[] newArray(int size) {
			return new ProductReviewCommentCreated[size];
		}
	};

}
