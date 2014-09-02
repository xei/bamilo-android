/**
 * 
 */
package pt.rocket.framework.objects;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.framework.rest.RestConstants;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import de.akquinet.android.androlog.Log;

/**
 * Class used to fill the sugestions screen when no results are found after a search
 * 
 * @author Andre Lopes
 * 
 */
public class FeaturedBox implements IJSONSerializable, Parcelable {

	private static final String TAG = FeaturedBox.class.getSimpleName();

	private String productsTitle;
	private ArrayList<FeaturedItem> products;
	private String brandsTitle;
	private ArrayList<FeaturedItem> brands;
	private String searchTips;
	private String errorMessage;
	private String noticeMessage;

	public FeaturedBox() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * pt.rocket.framework.objects.IJSONSerializable#initialize(org.json.JSONObject
	 * )
	 */
	@Override
	public boolean initialize(JSONObject metadataObject) throws JSONException {
		Log.d(TAG, "FILTER: FEATURED BOX");

		JSONObject data = metadataObject.getJSONObject(RestConstants.JSON_DATA_TAG);
		if (data != null) {
			//one List for all products
			products = new ArrayList<FeaturedItem>();

			JSONArray featuredBoxObject = data.getJSONArray(RestConstants.JSON_FEATURED_BOX_TAG);
			if (featuredBoxObject != null && featuredBoxObject.length() > 0) {
				// get products only from the first list
				JSONObject productsCategoryObject = featuredBoxObject.getJSONObject(0);
				if (productsCategoryObject != null) {
					// get title
					if (TextUtils.isEmpty(productsTitle)) {
						productsTitle = productsCategoryObject.optString(RestConstants.JSON_TITLE_TAG);
					}

					JSONArray productsObject = productsCategoryObject.getJSONArray(RestConstants.JSON_PRODUCTS_TAG);
					if (productsObject != null && productsObject.length() > 0) {
						// get products
						for (int j = 0; j < productsObject.length(); j++) {
							JSONObject productObject = productsObject.getJSONObject(j);
							FeaturedProduct product = new FeaturedProduct();

							// only use products properly initialized
							if (product.initialize(productObject)) {
								products.add(product);
							}
						}
					}
				}
			}

			// one list for all brands
			brands = new ArrayList<FeaturedItem>();

			JSONArray featuredBrandboxObject = data.getJSONArray(RestConstants.JSON_FEATURED_BRANDBOX_TAG);
			if (featuredBrandboxObject != null && featuredBrandboxObject.length() > 0) {
				// get brands only from the first list
				JSONObject brandsCategoryObject = featuredBrandboxObject.getJSONObject(0);
				if (brandsCategoryObject != null) {
					// get title from fist list of brands
					if (TextUtils.isEmpty(brandsTitle)) {
						brandsTitle = brandsCategoryObject.optString(RestConstants.JSON_TITLE_TAG);
					}

					JSONArray brandsObject = brandsCategoryObject.getJSONArray(RestConstants.JSON_BRANDS_TAG);
					if (brandsObject != null && brandsObject.length() > 0) {
						// get brands
						for (int j = 0; j < brandsObject.length(); j++) {
							JSONObject brandObject = brandsObject.getJSONObject(j);
							FeaturedBrand brand = new FeaturedBrand();

							// only use brands properly initialized
							if (brand.initialize(brandObject)) {
								brands.add(brand);
							}
						}
					}
				}
			}

			JSONObject searchTipsObject = data.getJSONObject(RestConstants.JSON_FEATURED_SEARCH_TIPS_TAG);
			if (searchTipsObject != null) {
				searchTips = searchTipsObject.getString(RestConstants.JSON_TEXT_TAG);
			}

			errorMessage = data.getString(RestConstants.JSON_ERROR_MESSAGE_TAG);

			noticeMessage = data.getString(RestConstants.JSON_NOTICE_MESSAGE_TAG);
		}

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pt.rocket.framework.objects.IJSONSerializable#toJSON()
	 */
	@Override
	public JSONObject toJSON() {
		return null;
	}

	public String getProductsTitle() {
		return productsTitle;
	}

	public ArrayList<FeaturedItem> getProducts() {
		return products;
	}

	public String getBrandsTitle() {
		return brandsTitle;
	}

	public ArrayList<FeaturedItem> getBrands() {
		return brands;
	}

	public String getSearchTips() {
		return searchTips;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public String getNoticeMessage() {
		return noticeMessage;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(productsTitle);
		dest.writeList(products);
		dest.writeString(brandsTitle);
		dest.writeList(brands);
		dest.writeString(searchTips);
		dest.writeString(errorMessage);
		dest.writeString(noticeMessage);
	}

	private FeaturedBox(Parcel in) {
		productsTitle = in.readString();
		products = new ArrayList<FeaturedItem>();
		in.readList(products, FeaturedProduct.class.getClassLoader());
		brandsTitle = in.readString();
		brands = new ArrayList<FeaturedItem>();
		in.readList(brands, FeaturedBrand.class.getClassLoader());
		searchTips = in.readString();
		errorMessage = in.readString();
		noticeMessage = in.readString();
	}

	public static final Parcelable.Creator<FeaturedBox> CREATOR = new Parcelable.Creator<FeaturedBox>() {
		public FeaturedBox createFromParcel(Parcel in) {
			return new FeaturedBox(in);
		}

		public FeaturedBox[] newArray(int size) {
			return new FeaturedBox[size];
		}
	};
}
