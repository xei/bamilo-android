package pt.rocket.framework.objects;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Rating object to allow the individual display of each rating
 * @author josedourado
 *
 */
public class RatingOption implements IJSONSerializable{
    
    private static final String JSON_SIZE_STARS_FORE_TAG = "size-stars-fore";
    private static final String JSON_TYPE_TITLE_TAG = "type_title";
    
    private double rating = 0.0;
    private String optionTitle="";
    
    public void RatingOption(){
        rating=0.0;
        optionTitle="";
    }
    
    public double getRating(){
        return rating;
    }
    
    public void setRating(double rating){
        this.rating=rating;
    }
    
    public String getTitle(){
        return optionTitle;
    }
    
    public void setTitle(String title){
        optionTitle = title;
    }

    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        // TODO Auto-generated method stub
        rating = jsonObject.getInt(JSON_SIZE_STARS_FORE_TAG);
        optionTitle = jsonObject.getString(JSON_TYPE_TITLE_TAG);
        
        return true;
    }

    @Override
    public JSONObject toJSON() {
        // TODO Auto-generated method stub
        return null;
    }

}
