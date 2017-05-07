package com.mobile.newFramework.objects.checkout;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.newFramework.pojo.RestConstants;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Class used to parse the external checkout transaction
 */
public class ExternalOrder implements Parcelable {

    public static String TAG = ExternalOrder.class.getSimpleName();

    public String number;
    public String coupon;
    public double value;
    public double valueConverted;
    public double average;
    public ArrayList<PurchaseItem> items;
    public ArrayList<String> skus;


    /**
     * Empty constructor
     */
    public ExternalOrder(JSONObject result) {
        number = result.optString(RestConstants.ORDERNr);
        value = result.optDouble(RestConstants.GRAND_TOTAL);
        valueConverted = result.optDouble(RestConstants.GRAND_TOTAL_CONVERTED);
        coupon = result.optString(RestConstants.COUPON_CODE);
        items = PurchaseItem.parseItems(result.optJSONArray(RestConstants.PRODUCTS));
        getSkusAndAverage();
    }


    private void getSkusAndAverage() {
        skus = new ArrayList<>();
        Double averageValue = 0d;
        for (PurchaseItem item : items) {
            skus.add(item.sku);
            averageValue += item.getPriceForTracking();
        }
        average = averageValue / items.size();
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
        dest.writeString(number);
        dest.writeString(coupon);
        dest.writeDouble(value);
        dest.writeDouble(valueConverted);
        dest.writeDouble(average);
        if (items == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(items);
        }
        if (skus == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(skus);
        }
    }

    /**
     * Parcel constructor
     */
    private ExternalOrder(Parcel in) {
        number = in.readString();
        coupon = in.readString();
        value = in.readDouble();
        valueConverted = in.readDouble();
        average = in.readDouble();
        if (in.readByte() == 0x01) {
            items = new ArrayList<>();
            in.readList(items, PurchaseItem.class.getClassLoader());
        } else {
            items = null;
        }
        if (in.readByte() == 0x01) {
            skus = new ArrayList<>();
            in.readList(skus, String.class.getClassLoader());
        } else {
            skus = null;
        }
    }

    /**
     * Create parcelable
     */
    public static final Creator<ExternalOrder> CREATOR = new Creator<ExternalOrder>() {
        public ExternalOrder createFromParcel(Parcel in) {
            return new ExternalOrder(in);
        }

        public ExternalOrder[] newArray(int size) {
            return new ExternalOrder[size];
        }
    };


}