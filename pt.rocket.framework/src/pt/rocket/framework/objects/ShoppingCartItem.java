package pt.rocket.framework.objects;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.utils.CurrencyFormatter;
import pt.rocket.framework.utils.ImageResolutionHelper;
import android.os.Parcel;
import android.os.Parcelable;
import de.akquinet.android.androlog.Log;

/**
 * Shopping Cart Item used when an item is added to the shopping cart
 * 
 * @author GuilhermeSilva
 * 
 */
public class ShoppingCartItem implements IJSONSerializable, Parcelable {

	private static final String TAG = ShoppingCartItem.class.getSimpleName();

	private String imageUrl;
	private String productUrl;
	private String configSKU;
	private String configSimpleSKU;
	private long quantity;
	private int maxQuantity;
	private String configId;
	private String name;
	private long stock;
	private double taxAmount;
	private Map<String, String> simpleData;
	private String variation;
	private String price;
	private String specialPrice;
	private Double savingPercentage;
	private double priceVal = 0;
	private double specialPriceVal = 0;
	private double mPriceValueConverted = 0;
	private double mSpecialPriceConverted = 0;

	/**
	 * @param simpleData
	 *            registry
	 */
	public ShoppingCartItem(HashMap<String, String> simpleData) {
		this.configSimpleSKU = null;
		this.simpleData = simpleData;
	}

	/**
	 * @param configSimpleSKU
	 *            of the product simple
	 * @param simpleData
	 *            registry
	 */
	public ShoppingCartItem(String configSimpleSKU, HashMap<String, String> simpleData) {
		this.configSimpleSKU = configSimpleSKU;
		this.simpleData = simpleData;
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
		Log.d(TAG, "ON INITIALIZE");

		try {
			imageUrl = getImageUrl(jsonObject.getString(RestConstants.JSON_ITEM_IMAGE_TAG));
			productUrl = jsonObject.optString(RestConstants.JSON_PRODUCT_URL_TAG);
			configSKU = jsonObject.getString(RestConstants.JSON_CONFIG_SKU_TAG);
			quantity = jsonObject.getLong(RestConstants.JSON_QUANTITY_TAG);
			maxQuantity = jsonObject.getInt(RestConstants.JSON_MAX_QUANTITY);
			configId = jsonObject.getString(RestConstants.JSON_CONFIG_ID);
			name = jsonObject.getString(RestConstants.JSON_ITEM_NAME_TAG);
			stock = Long.parseLong(jsonObject.getString(RestConstants.JSON_STOCK_TAG));
			variation = jsonObject.optString(RestConstants.JSON_VARIATION_TAG);

			/*-if (!jsonObject.isNull(RestConstants.JSON_ITEM_PRICE_TAG)) {
			    priceVal = jsonObject.getDouble(RestConstants.JSON_ITEM_PRICE_TAG);                
			}
			price = CurrencyFormatter.formatCurrency(priceVal);*/
			// Fix NAFAMZ-7848
			// Throw JSONException if JSON_PRICE_TAG is not present
			String priceJSON = jsonObject.getString(RestConstants.JSON_ITEM_PRICE_TAG);
			if (CurrencyFormatter.isNumber(priceJSON)) {
				priceVal = jsonObject.getDouble(RestConstants.JSON_ITEM_PRICE_TAG);
				price = CurrencyFormatter.formatCurrency(priceJSON);
			} else {
				// throw new JSONException("Price is not a number!");
				Log.w(TAG, "WARNING: Price is not a number!");
				price = "";
			}
			
			mPriceValueConverted = jsonObject.optDouble(RestConstants.JSON_ITEM_PRICE_CONVERTED_TAG, 0d);

			/*-if (!jsonObject.isNull(RestConstants.JSON_ITEM_SPECIAL_PRICE_TAG) && jsonObject.getDouble(RestConstants.JSON_ITEM_SPECIAL_PRICE_TAG) > 0 ) {
			    specialPriceVal = jsonObject.getDouble(RestConstants.JSON_ITEM_SPECIAL_PRICE_TAG);                
			}
			else {
			    specialPriceVal = priceVal;
			//                specialPrice = CurrencyFormatter.formatCurrency(0.0);
			}
			specialPrice = CurrencyFormatter.formatCurrency(specialPriceVal);*/
			// Fix NAFAMZ-7848
			String specialPriceJSON = jsonObject.optString(RestConstants.JSON_ITEM_SPECIAL_PRICE_TAG);
			if (CurrencyFormatter.isNumber(specialPriceJSON)) {
				specialPriceVal = jsonObject.getDouble(RestConstants.JSON_ITEM_SPECIAL_PRICE_TAG);
				specialPrice = CurrencyFormatter.formatCurrency(specialPriceJSON);
			} else {
				specialPriceVal = priceVal;
				specialPrice = price;
			}
			
			mSpecialPriceConverted = jsonObject.optDouble(RestConstants.JSON_ITEM_SPECIAL_PRICE_CONVERTED_TAG, 0d);

			taxAmount = jsonObject.optDouble(RestConstants.JSON_TAX_AMOUNT_TAG, 0);
			savingPercentage = 100 - specialPriceVal / priceVal * 100;

			// cartRuleDiscount =
			// jsonObject.getDouble(RestConstants.JSON_CART_RULE_DISCOUNT );

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
	@Override
	public JSONObject toJSON() {
		return null;
	}

	/**
	 * @return the imageUrl
	 */
	public String getImageUrl() {
		return imageUrl;
	}

	/**
	 * @return the productUrl
	 */
	public String getProductUrl() {
		return productUrl;
	}

	/**
	 * @return The product SKU (short form)
	 */
	public String getConfigSKU() {
		return configSKU;
	}

	/**
	 * @return The configuration/variant SKU of the product
	 */
	public String getConfigSimpleSKU() {
		return configSimpleSKU;
	}

	/*
	 * @param quantity of the product
	 */
	public void setQuantity(long quantity) {
		this.quantity = quantity;
	}

	/**
	 * @return the quantity
	 */
	public long getQuantity() {
		return quantity;
	}

	/*
	 * @param max quantity of the product
	 */
	public void setMaxQuantity(int maxQuantity) {
		this.maxQuantity = maxQuantity;
	}

	/**
	 * @return the maxQuantity
	 */
	public int getMaxQuantity() {
		return maxQuantity;
	}

	/**
	 * @return the configId
	 */
	public String getConfigId() {
		return configId;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the stock
	 */
	public long getStock() {
		return stock;
	}

	/**
	 * @return the special price
	 */
	public String getSpecialPrice() {
		return specialPrice;
	}

	/**
	 * @return the special price
	 */
	public Double getSpecialPriceVal() {
		return specialPriceVal;
	}

	/**
	 * @return the price
	 */
	public String getPrice() {
		return price;
	}

	/**
	 * @return the price
	 */
	public Double getPriceVal() {
		return priceVal;
	}

	/**
	 * @return the taxAmount
	 */
	public double getTaxAmount() {
		return taxAmount;
	}

	/**
	 * @return the savingPercentage
	 */
	public double getSavingPercentage() {
		return savingPercentage;
	}

	// public double getCartRuleDiscount() {
	// return cartRuleDiscount;
	// }
	//
	// public void setCartRuleDiscount( double cartRuleDiscount ) {
	// this.cartRuleDiscount = cartRuleDiscount;
	// }

	/**
	 * @return the simpleData
	 */
	public Map<String, String> getSimpleData() {
		return simpleData;
	}

	/**
	 * @param simpleData
	 *            the simpleData to set
	 */
	public void setSimpleData(Map<String, String> simpleData) {
		this.simpleData = simpleData;
	}

	public String getVariation() {
		return variation;
	}

	public void setVariation(String variation) {
		this.variation = variation;
	}

	private String getImageUrl(String url) {
		String modUrl = ImageResolutionHelper.replaceResolution(url);
		if (modUrl != null)
			return modUrl;
		return url;
	}

	/**
	 * @return the priceValueEuroConverted
	 */
	public double getPriceValueEuroConverted() {
		return mPriceValueConverted;
	}

	/**
	 * @param priceValueEuroConverted
	 *            the priceValueEuroConverted to set
	 */
	public void setPriceValueEuroConverted(double priceValueEuroConverted) {
		this.mPriceValueConverted = priceValueEuroConverted;
	}

	/**
	 * @return the specialPriceValueEuroConverted
	 */
	public double getSpecialPriceConverted() {
		return mSpecialPriceConverted;
	}

	/**
	 * @param specialPriceValueEuroConverted
	 *            the specialPriceValueEuroConverted to set
	 */
	public void setSpecialPriceConverted(double specialPriceValueEuroConverted) {
		this.mSpecialPriceConverted = specialPriceValueEuroConverted;
	}

	/**
	 * Return the price or special price used for tracking
	 * 
	 * @author sergiopereira
	 */
	public double getPriceForTracking() {
		Log.i(TAG, "ORIGIN PRICE VALUES: " + priceVal + " " + specialPriceVal);
		Log.i(TAG, "PRICE VALUE FOR TRACKING: " + mPriceValueConverted + " " + mSpecialPriceConverted);
		return mSpecialPriceConverted > 0 ? mSpecialPriceConverted : mPriceValueConverted;
	}

	/**
	 * ########### Parcelable ###########
	 * 
	 * @author sergiopereira
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
		dest.writeString(imageUrl);
		dest.writeString(productUrl);
		dest.writeString(configSKU);
		dest.writeString(configSimpleSKU);
		dest.writeLong(quantity);
		dest.writeInt(maxQuantity);
		dest.writeString(configId);
		dest.writeString(name);
		dest.writeLong(stock);
		dest.writeString(specialPrice);
		dest.writeDouble(savingPercentage);
		dest.writeString(price);
		dest.writeDouble(taxAmount);
		dest.writeMap(simpleData);
		dest.writeString(variation);
		dest.writeDouble(priceVal);
		dest.writeDouble(specialPriceVal);
		dest.writeDouble(mPriceValueConverted);
		dest.writeDouble(mSpecialPriceConverted);
	}

	/**
	 * Parcel constructor
	 * 
	 * @param in
	 */
	private ShoppingCartItem(Parcel in) {
		imageUrl = in.readString();
		productUrl = in.readString();
		configSKU = in.readString();
		configSimpleSKU = in.readString();
		quantity = in.readLong();
		maxQuantity = in.readInt();
		configId = in.readString();
		name = in.readString();
		stock = in.readLong();
		specialPrice = in.readString();
		savingPercentage = in.readDouble();
		price = in.readString();
		taxAmount = in.readDouble();
		simpleData = new HashMap<String, String>();
		in.readMap(simpleData, String.class.getClassLoader());
		variation = in.readString();
		priceVal = in.readDouble();
		specialPriceVal = in.readDouble();
		mPriceValueConverted = in.readDouble();
		mSpecialPriceConverted = in.readDouble();
	}

	/**
	 * Create parcelable
	 */
	public static final Parcelable.Creator<ShoppingCartItem> CREATOR = new Parcelable.Creator<ShoppingCartItem>() {
		public ShoppingCartItem createFromParcel(Parcel in) {
			return new ShoppingCartItem(in);
		}

		public ShoppingCartItem[] newArray(int size) {
			return new ShoppingCartItem[size];
		}
	};

}
