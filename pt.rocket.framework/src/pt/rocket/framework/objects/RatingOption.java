package pt.rocket.framework.objects;

import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.framework.rest.RestConstants;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Rating object to allow the individual display of each rating
 * 
 * @author josedourado
 * 
 */
public class RatingOption implements IJSONSerializable, Parcelable {

	private double rating = 0.0;
	private String optionTitle = "";
	
	public RatingOption() {
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
		rating = jsonObject.getInt(RestConstants.JSON_SIZE_STARS_FORE_TAG) / 20;
		optionTitle = jsonObject.getString(RestConstants.JSON_TYPE_TITLE_TAG);

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

	private RatingOption(Parcel in) {
		rating = in.readDouble();
		optionTitle = in.readString();
	}

	private static final Parcelable.Creator<RatingOption> CREATOR = new Parcelable.Creator<RatingOption>() {
		public RatingOption createFromParcel(Parcel in) {
			return new RatingOption(in);
		}

		public RatingOption[] newArray(int size) {
			return new RatingOption[size];
		}
	};

}
