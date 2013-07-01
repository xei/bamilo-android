package pt.rocket.framework.objects;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.net.Uri.Builder;

/**
 * Class that represents the product review comment
 * 
 * @author nunocastro
 * @modified GuilhermeSilva
 */
public class ProductReviewComment implements IJSONSerializable {

    private static final String JSON_TITLE_TAG = "title";
    private static final String JSON_DETAILS_TAG = "detail";
    private static final String JSON_NICKNAME_TAG = "nickname";
    private static final String JSON_DATE_TAG = "created_at";
    private static final String JSON_OPTIONS_TAG = "options";

    // private static final String JSON_TYPE_ID_TAG = "type_id";
    // private static final String JSON_TYPE_CODE_TAG = "type_code";
    // private static final String JSON_TYPE_TITLE_TAG = "type_title";
    // private static final String JSON_OPTION_CODE_TAG = "option_code";
    // private static final String JSON_SIZE_STARS_BACK_TAG = "size-stars-back";
    private static final String JSON_SIZE_STARS_FORE_TAG = "size-stars-fore";


    private String title = "";
    private String comments = "";
    private String name = "";
    private String date = "";
    private double rating = 0.0;

    /*
     * (non-Javadoc)
     * 
     * @see
     * pt.rocket.framework.objects.IJSONSerializable#initialize(org.json.JSONObject
     * )
     */
    @Override
    public boolean initialize(JSONObject jsonObject) {

        try {
            title = jsonObject.getString(JSON_TITLE_TAG);
            comments = jsonObject.getString(JSON_DETAILS_TAG);
            name = jsonObject.getString(JSON_NICKNAME_TAG);
            // DateFormat dateFormat = DateFormat.getDateTimeInstance();
            // date = dateFormat.parse(jsonObject.getString(JSON_DATE_TAG));
            date = jsonObject.getString(JSON_DATE_TAG);

            JSONArray options = jsonObject.getJSONArray(JSON_OPTIONS_TAG);
            JSONObject ratingObject = null;
            int size = options.length();

            for (int i = 0; i < size; i++) {
                ratingObject = options.getJSONObject(i);
                rating += ratingObject.getInt(JSON_SIZE_STARS_FORE_TAG);
            }
            rating /= size;
            rating = rating * 5 / 100;

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
}
