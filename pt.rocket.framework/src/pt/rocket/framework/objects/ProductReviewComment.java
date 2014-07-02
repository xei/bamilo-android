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
    private String comments = "";
    private String name = "";
    private String date = "";
    private double rating = 0.0;
    private String optionTitle="";
    
    private ArrayList<RatingOption> ratingOptions;

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

        ratingOptions = new ArrayList<RatingOption>();
        try {
            title = jsonObject.getString(RestConstants.JSON_TITLE_TAG);
            comments = jsonObject.getString(RestConstants.JSON_DETAILS_TAG);
            name = jsonObject.getString(RestConstants.JSON_NICKNAME_TAG);
            // DateFormat dateFormat = DateFormat.getDateTimeInstance();
            // date = dateFormat.parse(jsonObject.getString(JSON_DATE_TAG));
            date = jsonObject.getString(RestConstants.JSON_DATE_TAG);

            JSONArray options = jsonObject.getJSONArray(RestConstants.JSON_OPTIONS_TAG);
            JSONObject ratingObject = null;
            int size = options.length();
            Log.i("OPTIONS"," "+size);
            rating = 0 ;
            for (int i = 0; i < size; i++) {
                RatingOption option= new RatingOption();
                option.initialize(options.getJSONObject(i));
                ratingOptions.add(option);
                rating += option.getRating();
//                optionTitle = ratingObject.getString(JSON_TYPE_TITLE_TAG);
            }
            rating /= size;
            //rating = rating * 5 / 100;
            
            Log.i("RATING"," " + rating);

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
    public String getComments() {
        return comments;
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
    
    public ArrayList<RatingOption> getRatingOptions(){
        return ratingOptions;
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
		dest.writeString(date);
		dest.writeDouble(rating);
		dest.writeString(optionTitle);
		dest.writeList(ratingOptions);
		
	}
	
	private ProductReviewComment(Parcel in) {
		 title = in.readString();
		 comments = in.readString();
		 name = in.readString();
		 date = in.readString();
		 rating = in.readDouble();
		 optionTitle = in.readString();
		 ratingOptions = new ArrayList<RatingOption>(); 
		 in.readList(ratingOptions, RatingOption.class.getClassLoader());
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
