package pt.rocket.framework.objects;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.framework.rest.RestConstants;

import de.akquinet.android.androlog.Log;

import android.content.ContentValues;
import android.net.Uri.Builder;

/**
 * Class that represents the product review comment rating-option
 * 
 * @author nunocastro
 * @modified GuilhermeSilva
 */
public class ProductReviewComment implements IJSONSerializable {

//    private static final String JSON_TITLE_TAG = "title";
//    private static final String JSON_DETAILS_TAG = "detail";
//    private static final String JSON_NICKNAME_TAG = "nickname";
//    private static final String JSON_DATE_TAG = "created_at";
//    private static final String JSON_OPTIONS_TAG = "options";
//
//    // private static final String JSON_TYPE_ID_TAG = "type_id";
//    // private static final String JSON_TYPE_CODE_TAG = "type_code";
//    // private static final String JSON_TYPE_TITLE_TAG = "type_title";
//    // private static final String JSON_OPTION_CODE_TAG = "option_code";
//    // private static final String JSON_SIZE_STARS_BACK_TAG = "size-stars-back";
//    private static final String JSON_SIZE_STARS_FORE_TAG = "size-stars-fore";
//    private static final String JSON_TYPE_TITLE_TAG = "type_title";


    private String title = "";
    private String comments = "";
    private String name = "";
    private String date = "";
    private double rating = 0.0;
    private String optionTitle="";
    
    private ArrayList<RatingOption> ratingOptions;

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
}
