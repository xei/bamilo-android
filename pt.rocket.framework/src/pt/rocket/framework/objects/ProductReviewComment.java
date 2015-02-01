package pt.rocket.framework.objects;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.framework.rest.RestConstants;
import android.os.Parcel;
import android.os.Parcelable;
import de.akquinet.android.androlog.Log;

/**
 * Class that represents the product review comment
 * 
 * @author nunocastro
 * @modified GuilhermeSilva
 */
public class ProductReviewComment implements IJSONSerializable, Parcelable {

    private String title = "";
    private String comment = "";
    private String name = "";
    private String date = "";
    private double rating = 0.0;
    private String optionTitle="";
    private int average = -1;
    private ArrayList<RatingStar> ratingStars;

    public ProductReviewComment() {
	
	}
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * pt.rocket.framework.objects.IJSONSerializable#initialize(org.json.JSONObject
     * )
     */
    @Override
    public boolean initialize(JSONObject jsonObject) {

        ratingStars = new ArrayList<RatingStar>();
        try {
            title = jsonObject.getString(RestConstants.JSON_TITLE_TAG);
            comment = jsonObject.getString(RestConstants.JSON_COMMENT_TAG);
            name = jsonObject.getString(RestConstants.JSON_NAME_TAG);
            date = jsonObject.getString(RestConstants.JSON_COMMENT_DATE_TAG);
            //only for seller reviews
            average = jsonObject.optInt(RestConstants.JSON_RATINGS_AVERAGE_TAG,-1);

            JSONArray stars = jsonObject.optJSONArray(RestConstants.JSON_STARS_TAG);
            if (stars != null) {
                int size = stars.length();
                Log.i("STAR", " " + size);
                rating = 0;
                for (int i = 0; i < size; i++) {
                    RatingStar option = new RatingStar();
                    option.initialize(stars.getJSONObject(i));
                    ratingStars.add(option);
                    rating += option.getRating();
                }
                rating /= size;
                //rating = rating * 5 / 100;
                Log.i("RATING", " " + rating);
            }

        } catch (JSONException e) {
            e.printStackTrace();
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
        return null;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return the comments
     */
    public String getComment() {
        return comment;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the date
     */
    public String getDate() {
        return date;
    }
    
    /**
     * @return the rating
     */
    public double getRating() {
        return rating;
    }
    
    public ArrayList<RatingStar> getRatingStars(){
        return ratingStars;
    }

	public int getAverage() {
        return average;
    }

    public void setAverage(int average) {
        this.average = average;
    }

    @Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(title);
		dest.writeString(comment);
		dest.writeString(name);
		dest.writeString(date);
		dest.writeDouble(rating);
		dest.writeString(optionTitle);
		dest.writeList(ratingStars);
		dest.writeInt(average);
		
	}
	
	private ProductReviewComment(Parcel in) {
		 title = in.readString();
		 comment = in.readString();
		 name = in.readString();
		 date = in.readString();
		 rating = in.readDouble();
		 optionTitle = in.readString();
		 ratingStars = new ArrayList<RatingStar>(); 
		 in.readList(ratingStars, RatingStar.class.getClassLoader());
		 average = in.readInt();
	}
	
    public static final Parcelable.Creator<ProductReviewComment> CREATOR = new Parcelable.Creator<ProductReviewComment>() {
        public ProductReviewComment createFromParcel(Parcel in) {
            return new ProductReviewComment(in);
        }

        public ProductReviewComment[] newArray(int size) {
            return new ProductReviewComment[size];
        }
    };
}
