package pt.rocket.framework.objects;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.utils.LogTagHelper;
import android.text.TextUtils;
import de.akquinet.android.androlog.Log;

public class PurchaseItem {
	private static String TAG = LogTagHelper.create(PurchaseItem.class);
	private static int INDEX_OFFSET = 5;
//	private static String JSON_TAG_SKU = "sku";
//	private static String JSON_TAG_NAME = "name";
//	private static String JSON_TAG_CATEGORY = "category";
//	private static String JSON_TAG_PAIDPRICE = "paidprice";
//	private static String JSON_TAG_QUANTITY = "quantity";

	public String sku;
	public String name;
	public String category;
	public String paidprice;
	public Double paidpriceAsDouble = 0d;
	public String quantity= "";
	public Integer quantityAsInt = 0;
	
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
}