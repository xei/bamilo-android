/**
 * 
 */
package pt.rocket.framework.objects;

import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.framework.rest.RestConstants;
import android.os.Parcel;
import android.os.Parcelable;
import de.akquinet.android.androlog.Log;

/**
 * Class that represents the server-side featured item. Contains id, url, name
 * and image url.
 * 
 * @author Andre Lopes
 * 
 */
public class FeaturedItem implements IJSONSerializable, Parcelable {

	private String id;
	private String url;
	private String name;
	protected String imageUrl;

	public FeaturedItem() {
		this.id = "";
		this.url = "";
		this.name = "";
		this.imageUrl = "";
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the imageUrl
	 */
	public String getImageUrl() {
		return imageUrl;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pt.rocket.framework.objects.IJSONSerializable#initialize(org.json.JSONObject )
	 */
	@Override
	public boolean initialize(JSONObject jsonObject) {
		id = jsonObject.optString(RestConstants.JSON_ID_TAG);

		url = jsonObject.optString(RestConstants.JSON_URL_TAG);

		name = jsonObject.optString(RestConstants.JSON_NAME_TAG);

		if (name.trim().equals("") || url.trim().equals("")) {
			Log.d("Featured Items", "Item name = " + name + "\r\nItem url = " + url);
			return false;
		}

		imageUrl = jsonObject.optString(RestConstants.JSON_IMAGE_TAG);

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pt.rocket.framework.objects.IJSONSerializable#toJSON()
	 */
	@Override
	public JSONObject toJSON() {
		JSONObject jsonObject = new JSONObject();

		try {
			jsonObject.put(RestConstants.JSON_URL_TAG, url);
			jsonObject.put(RestConstants.JSON_NAME_TAG, name);
			jsonObject.put(RestConstants.JSON_IMAGE_TAG, imageUrl);

		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}

		return jsonObject;
	}

	/**
	 * ########### Parcelable ###########
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.Parcelable#describeContents()
	 */
	@Override
	public int describeContents() {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
	 */
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeString(url);
		dest.writeString(name);
		dest.writeValue(imageUrl);
	}

	/**
	 * Parcel constructor
	 * 
	 * @param in
	 */
	protected FeaturedItem(Parcel in) {
		id = in.readString();
		url = in.readString();
		name = in.readString();
		imageUrl = in.readString();
	}

	/**
	 * Create parcelable
	 */
	public static final Parcelable.Creator<FeaturedItem> CREATOR = new Parcelable.Creator<FeaturedItem>() {
		public FeaturedItem createFromParcel(Parcel in) {
			return new FeaturedItem(in);
		}

		public FeaturedItem[] newArray(int size) {
			return new FeaturedItem[size];
		}
	};
}
