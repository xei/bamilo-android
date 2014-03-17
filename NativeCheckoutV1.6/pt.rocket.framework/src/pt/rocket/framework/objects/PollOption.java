package pt.rocket.framework.objects;

import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * 
 * @author sergiopereira
 *
 */
public class PollOption implements IJSONSerializable, Parcelable {

	private static final String TAG = PollOption.class.getSimpleName();
	
	private String option;
	private String value;
	private String group;
	
	/**
	 * Constructor
	 * @param jsonObject
	 */
	public PollOption(JSONObject jsonObject) {
		initialize(jsonObject);
	}

	/*
	 * (non-Javadoc)
	 * @see pt.rocket.framework.objects.IJSONSerializable#initialize(org.json.JSONObject)
	 */
	@Override
	public boolean initialize(JSONObject jsonObject) {
		option = jsonObject.optString("option");
		value = jsonObject.optString("value");
		group = jsonObject.optString("group");
		Log.d(TAG, "POLL OPTION: " + value);
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see pt.rocket.framework.objects.IJSONSerializable#toJSON()
	 */
	@Override
	public JSONObject toJSON() {
		// TODO
		return null;
	}

	/**
	 * @return the option
	 */
	public String getOption() {
		return option;
	}


	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}


	/**
	 * @return the group
	 */
	public String getGroup() {
		return group;
	}


	/**
	 * @param option the option to set
	 */
	public void setOption(String option) {
		this.option = option;
	}


	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}


	/**
	 * @param group the group to set
	 */
	public void setGroup(String group) {
		this.group = group;
	}
	
	/**
	 * ####### PARCLE ####### 
	 */

	/*
	 * (non-Javadoc)
	 * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
	 */
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(option);
		dest.writeString(value);
		dest.writeString(group);
	}

	/**
	 * Parcel constructor
	 * @param in
	 */
	private PollOption(Parcel in) {
		option = in.readString();
		value = in.readString();
		group = in.readString();
	}

	/**
	 * Create parcelable
	 */
	public static final Parcelable.Creator<PollOption> CREATOR = new Parcelable.Creator<PollOption>() {
		public PollOption createFromParcel(Parcel in) {
			return new PollOption(in);
		}

		public PollOption[] newArray(int size) {
			return new PollOption[size];
		}
	};

	/*
	 * (non-Javadoc)
	 * @see android.os.Parcelable#describeContents()
	 */
	@Override
	public int describeContents() {
		return 0;
	}

}
