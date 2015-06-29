package com.mobile.framework.objects;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.framework.rest.RestConstants;
import com.mobile.framework.utils.LogTagHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.akquinet.android.androlog.Log;

public class PurchaseItem implements Parcelable {
	
	private static String TAG = LogTagHelper.create(PurchaseItem.class);

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
	public static List<PurchaseItem> parseItems(JSONArray itemsJson) {
		List<PurchaseItem> items = new ArrayList<>();

        if(itemsJson != null){
            for (int i = 0; i < itemsJson.length(); i++) {
                try {
                    PurchaseItem item = new PurchaseItem();
                    if( !item.parseItem(itemsJson.getJSONObject(i)))
                        continue;
                    items.add(item);
                }catch(JSONException e){
                    e.printStackTrace();
                }
            }
        }

		return items;
	}
	
	/*--
      {
         "category":false,
         "quantity":1,
         "paidprice_converted":12.466,
         "sku":"SA729ME60CXNNGAMZ-19914",
         "paidprice":2710,
         "name":"Reading the Quran"
      },
	 */
	private boolean parseItem( JSONObject itemJson) {
		try {
			//sku
			sku = itemJson.getString(RestConstants.JSON_SKU_TAG);
			//name
			name = itemJson.getString(RestConstants.JSON_PURCHASE_NAME_TAG);
			//category
			category = itemJson.getString(RestConstants.JSON_CATEGORY_TAG);
			//TODO hotfix to be removed once fix happens on API side
			if (category.equals("false") || category.equals("true")) {
				category = "";
			}
			//price
			paidprice = itemJson.getString( RestConstants.JSON_PAID_PRICE_TAG);
			paidpriceAsDouble = itemJson.optDouble(RestConstants.JSON_PAID_PRICE_TAG, 0);
			//price tracking
			paidPriceForTracking = itemJson.optDouble(RestConstants.JSON_PAID_PRICE_CONVERTED_TAG, 0d);
			//quantity
			quantity = itemJson.getString( RestConstants.JSON_QUANTITY_TAG);
			quantityAsInt = itemJson.optInt(RestConstants.JSON_QUANTITY_TAG, 0);
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
		List<PurchaseItem> items = new ArrayList<PurchaseItem>();
		
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
