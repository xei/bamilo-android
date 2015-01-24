package pt.rocket.framework.objects;

import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.framework.rest.RestConstants;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Star object to allow the individual display of each rating
 * 
 * @author josedourado
 * 
 */
public class RatingStar implements IJSONSerializable, Parcelable {

	private double rating = 0.0;
	private String optionTitle = "";
	
	public RatingStar() {
		rating = 0.0;
		optionTitle = "";
	}

	public double getRating() {
		return rating;
	}

	public void setRating(double rating) {
		this.rating = rating;
	}

	public String getTitle() {
		return optionTitle;
	}

	public void setTitle(String title) {
		optionTitle = title;
	}

	@Override
	public boolean initialize(JSONObject jsonObject) throws JSONException {
		// TODO Auto-generated method stub
		rating = jsonObject.optDouble(RestConstants.JSON_RATINGS_AVERAGE_TAG, 0.0);
		optionTitle = jsonObject.getString(RestConstants.JSON_TITLE_TAG);

		return true;
	}

	@Override
	public JSONObject toJSON() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeDouble(rating);
		dest.writeString(optionTitle);

	}

	private RatingStar(Parcel in) {
		rating = in.readDouble();
		optionTitle = in.readString();
	}

	public static final Parcelable.Creator<RatingStar> CREATOR = new Parcelable.Creator<RatingStar>() {
		public RatingStar createFromParcel(Parcel in) {
			return new RatingStar(in);
		}

		public RatingStar[] newArray(int size) {
			return new RatingStar[size];
		}
	};

}
