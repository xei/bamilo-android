package com.mobile.newFramework.objects.product;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.utils.output.Print;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


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
    private int totalStars = -1;
    private ArrayList<RatingStar> ratingStars;

    public ProductReviewComment() {

	}

    /*
     * (non-Javadoc)
     *
     * @see
     * com.mobile.framework.objects.IJSONSerializable#initialize(org.json.JSONObject
     * )
     */
    @Override
    public boolean initialize(JSONObject jsonObject) {

        ratingStars = new ArrayList<RatingStar>();
        try {
            title = jsonObject.getString(RestConstants.TITLE);
            comment = jsonObject.getString(RestConstants.JSON_COMMENT_TAG);
            name = jsonObject.getString(RestConstants.JSON_NAME_TAG);
            date = jsonObject.getString(RestConstants.JSON_COMMENT_DATE_TAG);
            totalStars = jsonObject.getInt(RestConstants.JSON_TOTAL_STARS_TAG);
            //only for seller reviews
            average = jsonObject.optInt(RestConstants.JSON_RATINGS_AVERAGE_TAG,-1);

            JSONArray stars = jsonObject.optJSONArray(RestConstants.JSON_STARS_TAG);
            if (stars != null) {
                int size = stars.length();
                Print.d("STAR " + size);
                rating = 0;
                for (int i = 0; i < size; i++) {
                    RatingStar option = new RatingStar();
                    option.initialize(stars.getJSONObject(i));
                    ratingStars.add(option);
                    rating += option.getRating();
                }
                rating /= size;
                //rating = rating * 5 / 100;
                Print.d("RATING" + rating);
            }

        } catch (JSONException e) {
            e.printStackTrace();
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
        return RequiredJson.COMPLETE_JSON;
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

    public int getTotalStars() { return totalStars; }

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

    public static final Creator<ProductReviewComment> CREATOR = new Creator<ProductReviewComment>() {
        public ProductReviewComment createFromParcel(Parcel in) {
            return new ProductReviewComment(in);
        }

        public ProductReviewComment[] newArray(int size) {
            return new ProductReviewComment[size];
        }
    };
}
