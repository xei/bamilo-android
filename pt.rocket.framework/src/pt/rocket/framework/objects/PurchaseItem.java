package pt.rocket.framework.objects;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.utils.LogTagHelper;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import de.akquinet.android.androlog.Log;

public class PurchaseItem implements Parcelable {
	
	private static String TAG = LogTagHelper.create(PurchaseItem.class);
	
	private static int INDEX_OFFSET = 5;

	public String sku;
	public String name;
	public String category;
	public String paidprice;
	public Double paidpriceAsDouble = 0d;
	public String quantity= "";
	public Integer quantityAsInt = 0;
	
	
	
	/**
	 * Empty constructor
	 */
	public PurchaseItem() { }

	
	private boolean parseItem( JSONObject itemsJson, int indexBegin ) {
		try {
			sku = itemsJson.getJSONObject(String.valueOf(indexBegin)).getString(RestConstants.JSON_SKU_TAG );
			name = itemsJson.getJSONObject(String.valueOf(indexBegin + 1)).getString( RestConstants.JSON_PURCHASE_NAME_TAG );
			category = itemsJson.getJSONObject(String.valueOf(indexBegin + 2)).getString( RestConstants.JSON_CATEGORY_TAG );
			paidprice = itemsJson.getJSONObject(String.valueOf(indexBegin + 3)).getString( RestConstants.JSON_PAIDPRICE_TAG );
			quantity = itemsJson.getJSONObject(String.valueOf(indexBegin + 4)).getString( RestConstants.JSON_QUANTITY_TAG );
		} catch (JSONException e) {
			Log.e(TAG, "parsing purchase item failed" + e);
			return false;
		}			
		
		try {
			paidpriceAsDouble = Double.parseDouble( paidprice );
			quantityAsInt = Integer.parseInt(quantity);
		} catch ( NumberFormatException e ) {
			Log.e(TAG, "parsing of number values failed", e );
		}
		
		return true;
	}

	public static List<PurchaseItem> parseItems(JSONObject itemsJson) {
		List<PurchaseItem> items = new ArrayList<PurchaseItem>();

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
	
	public static List<PurchaseItem> parseItems(Map<String, ShoppingCartItem> mItems) {
		List<PurchaseItem> items = new ArrayList<PurchaseItem>();
		
		for(String key: mItems.keySet()){
            ShoppingCartItem mShoppingCartItem = mItems.get(key);
            PurchaseItem mPurchaseItem = new PurchaseItem();
            mPurchaseItem.sku = mShoppingCartItem.getConfigSimpleSKU();
            mPurchaseItem.name = mShoppingCartItem.getName();
            mPurchaseItem.paidprice = mShoppingCartItem.getPrice();
            mPurchaseItem.paidpriceAsDouble = mShoppingCartItem.getPriceVal();
            mPurchaseItem.quantity = String.valueOf(mShoppingCartItem.getQuantity());
            items.add(mPurchaseItem);
        }

		return items;
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