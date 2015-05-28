package com.mobile.framework.objects;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.mobile.framework.rest.RestConstants;
import com.mobile.framework.utils.LogTagHelper;
import com.mobile.newFramework.objects.ShoppingCartItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.akquinet.android.androlog.Log;

public class PurchaseItem implements Parcelable {
	
	private static String TAG = LogTagHelper.create(PurchaseItem.class);
	
	private static int INDEX_OFFSET = 5;

	public String sku;
	public String name;
	public String category;
	public String paidprice;
	public String quantity= "";
	public Integer quantityAsInt = 0;
	public Double paidpriceAsDouble = 0d;
	private double paidPriceForTracking = 0d;
	
	/**
	 * Empty constructor
	 */
	public PurchaseItem() { }
	
	/**
	 * For WebCheckout
	 */
	public static List<PurchaseItem> parseItems(JSONObject itemsJson) {
		List<PurchaseItem> items = new ArrayList<>();

		int indexBegin = 0;
		while (!TextUtils.isEmpty(itemsJson.optString(String.valueOf(indexBegin)))) {
			PurchaseItem item = new PurchaseItem();
			if( !item.parseItem( itemsJson, indexBegin))
				continue;
			items.add(item);
			indexBegin += INDEX_OFFSET;
		}

		for (PurchaseItem item : items) {
			Log.d(TAG, "parseItems: sku = " + item.sku + " name = " + item.name + " category = " + item.category + " paidprice = "
					+ item.paidprice + " quantity = " + item.quantity);
		}

		return items;
	}
	
	/*--
	"0": { "sku": "PP447HLADTYYMEAMZ-289976" },
	"1": { "name": "دستمال کاغذی با طرح گربه" },
	"2": { "category": "خانه و آشپزخانه" },
	"3": { "paidprice": 37000 },
	"4": { "paidprice_converted": 0.88095238095238 },
	"5": { "quantity": 1 }
	 */
	private boolean parseItem( JSONObject itemsJson, int indexBegin ) {
		try {
			// 0: sku
			sku = itemsJson.getJSONObject(String.valueOf(indexBegin)).getString(RestConstants.JSON_SKU_TAG );
			// 1: name
			name = itemsJson.getJSONObject(String.valueOf(indexBegin + 1)).getString( RestConstants.JSON_PURCHASE_NAME_TAG );
			// 2: category
			category = itemsJson.getJSONObject(String.valueOf(indexBegin + 2)).getString( RestConstants.JSON_CATEGORY_TAG );
			// 3: price
			JSONObject prcObj = itemsJson.getJSONObject(String.valueOf(indexBegin + 3));
			paidprice = prcObj.getString( RestConstants.JSON_PAID_PRICE_TAG);
			paidpriceAsDouble = prcObj.optDouble(RestConstants.JSON_PAID_PRICE_TAG, 0);
			// 4: price tracking
			JSONObject prcTrck = itemsJson.getJSONObject(String.valueOf(indexBegin + 4));
			paidPriceForTracking = prcTrck.optDouble(RestConstants.JSON_PAID_PRICE_CONVERTED_TAG, 0d);
			// 5: quantity
			JSONObject qtObj = itemsJson.getJSONObject(String.valueOf(indexBegin + 5));
			quantity = qtObj.getString( RestConstants.JSON_QUANTITY_TAG);
			quantityAsInt = qtObj.optInt(RestConstants.JSON_QUANTITY_TAG, 0);
		} catch (JSONException e) {
			Log.e(TAG, "parsing purchase item failed" + e);
			return false;
		}
		return true;
	}
	
	/**
	 * For NativeCheckout
	 */
	public static List<PurchaseItem> parseItems(Map<String, ShoppingCartItem> mItems) {
		List<PurchaseItem> items = new ArrayList<>();
		
		for(String key: mItems.keySet()){
            ShoppingCartItem mShoppingCartItem = mItems.get(key);
            PurchaseItem mPurchaseItem = new PurchaseItem();
            mPurchaseItem.sku = mShoppingCartItem.getConfigSimpleSKU();
            mPurchaseItem.name = mShoppingCartItem.getName();
            mPurchaseItem.paidprice = mShoppingCartItem.getPrice();
            mPurchaseItem.paidpriceAsDouble = mShoppingCartItem.getPriceVal();
            mPurchaseItem.paidPriceForTracking = mShoppingCartItem.getPriceForTracking();
            mPurchaseItem.quantity = String.valueOf(mShoppingCartItem.getQuantity());
            mPurchaseItem.quantityAsInt = (int) mShoppingCartItem.getQuantity();
            items.add(mPurchaseItem);
            
			Log.d(TAG, "PURCHASE: sku = " + mPurchaseItem.sku + 
					" name = " + mPurchaseItem.name + 
					" category = " + mPurchaseItem.category + 
					" paidprice = " + mPurchaseItem.paidprice + 
					" quantity = " + mPurchaseItem.quantity +
					" quantityAsInt = " + mPurchaseItem.quantityAsInt);
        }

		return items;
	}
	
	
	/**
	 * Returns the paid price value for tracking
	 * @author sergiopereira
	 */
	public double getPriceForTracking() {
		return paidPriceForTracking;
	}
	
    /**
     * ########### Parcelable ###########
     * @author sergiopereira
     */
    
    /*
     * (non-Javadoc)
     * @see android.os.Parcelable#describeContents()
     */
	@Override
	public int describeContents() {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
	 */
	@Override
	public void writeToParcel(Parcel dest, int flags) {
	    dest.writeString(sku);
	    dest.writeString(name);
	    dest.writeString(category);
	    dest.writeString(paidprice);
	    dest.writeDouble(paidpriceAsDouble);
	    dest.writeString(quantity);
	    dest.writeInt(quantityAsInt);
	    dest.writeDouble(paidPriceForTracking);
	}
	
	/**
	 * Parcel constructor
	 * @param in
	 */
	private PurchaseItem(Parcel in) {
		sku = in.readString();
		name = in.readString();
		category = in.readString();
		paidprice = in.readString();
		paidpriceAsDouble  = in.readDouble();
		quantity = in.readString();
		quantityAsInt = in.readInt();
		paidPriceForTracking = in.readDouble();
    }

	/**
	 * Create parcelable 
	 */
	public static final Parcelable.Creator<PurchaseItem> CREATOR = new Parcelable.Creator<PurchaseItem>() {
        public PurchaseItem createFromParcel(Parcel in) {
            return new PurchaseItem(in);
        }

        public PurchaseItem[] newArray(int size) {
            return new PurchaseItem[size];
        }
    };
	
	
}
