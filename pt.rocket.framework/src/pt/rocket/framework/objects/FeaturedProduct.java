/**
 * 
 */
package pt.rocket.framework.objects;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.utils.CurrencyFormatter;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class that represents the server side product. Contains id, url, name, image
 * url and price.
 * 
 * @author Andre Lopes
 * 
 */
public class FeaturedProduct extends FeaturedItem {

	private String price;

	/**
	 * simple FeaturedProduct constructor.
	 */
	public FeaturedProduct() {
		super();

		this.price = "";
	}

	public String getPrice() {
		return price;
	}

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
			if (!super.initialize(jsonObject)) {
				return false;
			}

			String priceString = jsonObject.optString(RestConstants.JSON_PRICE_TAG);

			double priceDouble = -1;
			try {
				priceDouble = Double.parseDouble(priceString);
				price = CurrencyFormatter.formatCurrency(priceDouble);
			} catch (NumberFormatException e) {
				price = priceString;
				e.printStackTrace();
			}

			// get url from first image which has url
			JSONArray imageArray = jsonObject.optJSONArray(RestConstants.JSON_IMAGE_TAG);
			if (imageArray != null) {
				int imageArraySize = imageArray.length();
				if (imageArraySize > 0) {
					boolean isImageUrlDefined = false;

					int index = 0;
					while (!isImageUrlDefined && index < imageArraySize) {
						JSONObject imageObject = imageArray.getJSONObject(index);
						if (imageObject != null) {
							imageUrl = imageObject.optString(RestConstants.JSON_URL_TAG);
							isImageUrlDefined = true;
						}
						index++;
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pt.rocket.framework.objects.IJSONSerializable#toJSON()
	 */
	public JSONObject toJSON() {
		JSONObject jsonObject = super.toJSON();
		try {
			jsonObject.put(RestConstants.JSON_PRICE_TAG, price);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return jsonObject;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);

		dest.writeValue(price);
	}

	private FeaturedProduct(Parcel in) {
		super(in);
		price = in.readString();
	}

	public static final Parcelable.Creator<FeaturedProduct> CREATOR = new Parcelable.Creator<FeaturedProduct>() {
		public FeaturedProduct createFromParcel(Parcel in) {
			return new FeaturedProduct(in);
		}

		public FeaturedProduct[] newArray(int size) {
			return new FeaturedProduct[size];
		}
	};

}
