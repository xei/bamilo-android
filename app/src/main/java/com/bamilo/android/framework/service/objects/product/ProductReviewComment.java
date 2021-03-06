package com.bamilo.android.framework.service.objects.product;

import android.os.Parcel;
import android.os.Parcelable;

import com.bamilo.android.framework.service.objects.IJSONSerializable;
import com.bamilo.android.framework.service.objects.RequiredJson;
import com.bamilo.android.framework.service.pojo.RestConstants;

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

        ratingStars = new ArrayList<>();
        try {
            title = jsonObject.getString(RestConstants.TITLE);
            comment = jsonObject.getString(RestConstants.COMMENT);
            name = jsonObject.getString(RestConstants.NAME);
            date = jsonObject.getString(RestConstants.DATE);
            totalStars = jsonObject.getInt(RestConstants.TOTAL_STARS);
            //only for seller reviews
            average = jsonObject.optInt(RestConstants.AVERAGE,-1);

            JSONArray stars = jsonObject.optJSONArray(RestConstants.STARS);
            if (stars != null) {
                int size = stars.length();
                rating = 0;
                for (int i = 0; i < size; i++) {
                    RatingStar option = new RatingStar();
                    option.initialize(stars.getJSONObject(i));
                    ratingStars.add(option);
                    rating += option.getRating();
                }
                rating /= size;
                //rating = rating * 5 / 100;
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
		 ratingStars = new ArrayList<>();
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
