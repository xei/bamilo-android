package com.bamilo.android.framework.service.objects.product;

import android.os.Parcel;
import android.os.Parcelable;

import com.bamilo.android.framework.service.objects.IJSONSerializable;
import com.bamilo.android.framework.service.objects.RequiredJson;
import com.bamilo.android.framework.service.pojo.RestConstants;

import org.json.JSONException;
import org.json.JSONObject;

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
		rating = jsonObject.optDouble(RestConstants.AVERAGE, 0.0);
		optionTitle = jsonObject.getString(RestConstants.TITLE);
		return true;
	}

	@Override
	public JSONObject toJSON() {
		return null;
	}

	@Override
	public int getRequiredJson() {
		return RequiredJson.COMPLETE_JSON;
	}

	@Override
	public int describeContents() {
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

	public static final Creator<RatingStar> CREATOR = new Creator<RatingStar>() {
		public RatingStar createFromParcel(Parcel in) {
			return new RatingStar(in);
		}

		public RatingStar[] newArray(int size) {
			return new RatingStar[size];
		}
	};

}
