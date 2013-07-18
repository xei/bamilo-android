package pt.rocket.framework.objects;

import java.util.Map.Entry;

import android.content.ContentValues;
import android.net.Uri.Builder;

public class ProductReviewCommentCreated {

    private static final String FORM_NAME_FIELD = "RatingForm[name]";
    private static final String FORM_TITLE_FIELD = "RatingForm[title]";
    private static final String FORM_COMMENT_FIELD = "RatingForm[comment]";
    private static final String FORM_COSTUMER_ID_FIELD = "rating-customer";
	
	private String title;
    private String comments;
    private String name;
    private String email;
    
    private double rating;
    private double ratingAppearence;
    private double ratingPrice;
//    private MyObs
    
    public ContentValues getObjectModel() {
    	ContentValues values = new ContentValues();

        values.put(FORM_NAME_FIELD, name);
        values.put(FORM_TITLE_FIELD, title);
        values.put(FORM_COMMENT_FIELD, comments);
        
        
        
        
        if(false){
            values.put(FORM_COSTUMER_ID_FIELD, email );
        }
        

        return values;
    }
    
    public void addParameters(ContentValues values) {
    	values.put(FORM_NAME_FIELD, name);
    	values.put(FORM_TITLE_FIELD, title);
    	values.put(FORM_COMMENT_FIELD, comments);
    	if(false){
            values.put(FORM_COSTUMER_ID_FIELD, email );
        }
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
    public void setEmail( String email) {
    	this.email = email;
    }
    
    /**
     * @return the rating
     */
    public double getRating() {
        return rating;
    }
    
    /**
     * @param rating
     *            the rating to set
     */
    public void setRating(double rating) {
        this.rating = rating;
    }

    /**
     * @param rating
     *            the rating to set
     */
    public void setAppearenceRating(double rating) {
        this.ratingAppearence = rating;
    }
    
    /**
     * @return the rating
     */
    public double getAppearenceRating() {
        return ratingAppearence;
    }

    /**
     * @param rating
     *            the rating to set
     */
    public void setPriceRating(double rating) {
        this.ratingPrice = rating;
    }
    
    /**
     * @return the rating
     */
    public double getPriceeRating() {
        return ratingPrice;
    }
    
    
}
