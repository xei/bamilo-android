///**
// *
// */
//package com.mobile.framework.objects;
//
//import android.os.Parcel;
//import android.os.Parcelable;
//
//import com.mobile.newFramework.pojo.JsonConstants;
//import com.mobile.framework.utils.CurrencyFormatter;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
///**
// * Class that represents the server side product. Contains id, url, name, image
// * url and price.
// *
// * @author Andre Lopes
// *
// */
//public class FeaturedProduct extends FeaturedItem implements Parcelable {
//
//	private String price;
//
//	/**
//	 * simple FeaturedProduct constructor.
//	 */
//	public FeaturedProduct() {
//		super();
//		this.price = "";
//	}
//
//	public String getPrice() {
//		return price;
//	}
//
//	/*
//	 * (non-Javadoc)
//	 *
//	 * @see
//	 * com.mobile.framework.objects.IJSONSerializable#initialize(org.json.JSONObject
//	 * )
//	 */
//	@Override
//	public boolean initialize(JSONObject jsonObject) {
//		try {
//			if (!super.initialize(jsonObject)) {
//				return false;
//			}
//
//			/*-String priceString = jsonObject.optString(RestConstants.JSON_PRICE_TAG);
//
//			double priceDouble = -1;
//			try {
//				priceDouble = Double.parseDouble(priceString);
//				price = CurrencyFormatter.formatCurrency(priceDouble);
//			} catch (NumberFormatException e) {
//				price = priceString;
//				e.printStackTrace();
//			}*/
//			// Fix NAFAMZ-7848
//			// Throw JSONException if JSON_PRICE_TAG is not present
//			String priceJSON = jsonObject.getString(JsonConstants.JSON_PRICE_TAG);
//			if (CurrencyFormatter.isNumber(priceJSON)) {
//			    price = priceJSON;
//			} else {
//			    throw new JSONException("Price is not a number!");
//			}
//
//			// get url from first image which has url
//			JSONArray imageArray = jsonObject.optJSONArray(JsonConstants.JSON_IMAGE_TAG);
//			if (imageArray != null) {
//				int imageArraySize = imageArray.length();
//				if (imageArraySize > 0) {
//					boolean isImageUrlDefined = false;
//
//					int index = 0;
//					while (!isImageUrlDefined && index < imageArraySize) {
//						JSONObject imageObject = imageArray.getJSONObject(index);
//						if (imageObject != null) {
//							imageUrl = imageObject.optString(JsonConstants.JSON_URL_TAG);
//							isImageUrlDefined = true;
//						}
//						index++;
//					}
//				}
//			}
//		} catch (JSONException e) {
//			e.printStackTrace();
//			return false;
//		}
//		return true;
//	}
//
//	/*
//	 * (non-Javadoc)
//	 *
//	 * @see com.mobile.framework.objects.IJSONSerializable#toJSON()
//	 */
//	public JSONObject toJSON() {
//		JSONObject jsonObject = super.toJSON();
//		try {
//			jsonObject.put(JsonConstants.JSON_PRICE_TAG, price);
//		} catch (JSONException e) {
//			e.printStackTrace();
//			return null;
//		}
//		return jsonObject;
//	}
//
//
//    /*
//     * ########### Parcelable ###########
//     */
//
//	@Override
//	public int describeContents() {
//		return 0;
//	}
//
//	@Override
//	public void writeToParcel(Parcel dest, int flags) {
//		super.writeToParcel(dest, flags);
//		dest.writeString(price);
//	}
//
//	private FeaturedProduct(Parcel in) {
//		super(in);
//		price = in.readString();
//	}
//
//	public static final Parcelable.Creator<FeaturedProduct> CREATOR = new Parcelable.Creator<FeaturedProduct>() {
//		public FeaturedProduct createFromParcel(Parcel in) {
//			return new FeaturedProduct(in);
//		}
//
//		public FeaturedProduct[] newArray(int size) {
//			return new FeaturedProduct[size];
//		}
//	};
//
//}
