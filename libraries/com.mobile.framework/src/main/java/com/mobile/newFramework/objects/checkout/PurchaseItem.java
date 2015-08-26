package com.mobile.newFramework.objects.checkout;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.newFramework.objects.cart.ShoppingCartItem;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.utils.output.Print;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PurchaseItem implements Parcelable {

    private static String TAG = PurchaseItem.class.getSimpleName();

    public String sku;
    public String name;
    public String category;
    public int quantity = 0;
    private double paidPriceConverted = 0d;

    /**
     * Empty constructor
     */
    public PurchaseItem() {
    }

    /**
     * For WebCheckout
     */
    public static ArrayList<PurchaseItem> parseItems(JSONArray itemsJson) {
        ArrayList<PurchaseItem> items = new ArrayList<>();
        if (itemsJson != null) {
            for (int i = 0; i < itemsJson.length(); i++) {
                try {
                    PurchaseItem item = new PurchaseItem();
                    item.parseItem(itemsJson.getJSONObject(i));
                    items.add(item);
                } catch (JSONException e) {
                    Print.w(TAG, "WARNING: JSE ON PARSING PURCHASE ITEM", e);
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
    private void parseItem(JSONObject itemJson) throws JSONException {
        sku = itemJson.getString(RestConstants.SKU);
        name = itemJson.getString(RestConstants.JSON_PURCHASE_NAME_TAG);
        paidPriceConverted = itemJson.optDouble(RestConstants.JSON_PAID_PRICE_CONVERTED_TAG, 0d);
        quantity = itemJson.optInt(RestConstants.JSON_QUANTITY_TAG, 0);
        category = itemJson.getString(RestConstants.JSON_CATEGORY_TAG);
        //TODO hotfix to be removed once fix happens on API side
        if (category.equals("false") || category.equals("true")) {
            category = "";
        }
    }

    /**
     * For NativeCheckout
     */
    public static List<PurchaseItem> parseItems(ArrayList<ShoppingCartItem> mItems) {
        List<PurchaseItem> items = new ArrayList<>();
        for (ShoppingCartItem item : mItems) {
            PurchaseItem mPurchaseItem = new PurchaseItem();
            mPurchaseItem.sku = item.getConfigSimpleSKU();
            mPurchaseItem.name = item.getName();
            mPurchaseItem.paidPriceConverted = item.getPriceForTracking();
            mPurchaseItem.quantity = (int) item.getQuantity();
            items.add(mPurchaseItem);
        }
        return items;
    }


    /**
     * Returns the paid price value for tracking
     *
     * @author sergiopereira
     */
    public double getPriceForTracking() {
        return paidPriceConverted;
    }

    /**
     * ########### Parcelable ###########
     *
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
        dest.writeInt(quantity);
        dest.writeDouble(paidPriceConverted);
    }

    /**
     * Parcel constructor
     */
    private PurchaseItem(Parcel in) {
        sku = in.readString();
        name = in.readString();
        category = in.readString();
        quantity = in.readInt();
        paidPriceConverted = in.readDouble();
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