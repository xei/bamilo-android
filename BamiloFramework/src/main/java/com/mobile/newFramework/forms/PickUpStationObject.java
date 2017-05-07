package com.mobile.newFramework.forms;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.newFramework.objects.checkout.Region;
import com.mobile.newFramework.pojo.RestConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Class that Represents a Pickup station
 *
 * @author manuelsilva
 */
public class PickUpStationObject implements Parcelable {

    private static final String TAG = PickUpStationObject.class.getName();

    private String pickupId;
    private String name;
    private String pickupStationId;
    private String image;
    private String address;
    private String place;
    private String city;
    private String openingHours;
    private ArrayList<String> paymentMethods;
    private ArrayList<Region> regions;
    private long shippingFee;

    /**
     * Empty Constructor
     */
    public PickUpStationObject() {

    }

    /**
     * @return the id_pickupstation
     */
    public String getIdPickupStation() {
        return pickupId;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the pickup_id
     */
    public String getPickupStationId() {
        return pickupStationId;
    }

    /**
     * @return the image
     */
    public String getImage() {
        return image;
    }

    /**
     * @return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * @return the place
     */
    public String getPlace() {
        return place;
    }

    /**
     * @return the city
     */
    public String getCity() {
        return city;
    }

    /**
     * @return the opening_hours
     */
    public String getOpeningHours() {
        return openingHours;
    }

    /**
     * @return the regions
     */
    public ArrayList<Region> getRegions() {
        return regions;
    }

    /**
     * Get the shipping fee
     *
     * @return long
     */
    public long getShippingFee() {
        return shippingFee;
    }

    public void initialize(JSONObject jsonObject) throws JSONException {
        // Save info
        this.pickupStationId = jsonObject.optString(RestConstants.ID_PICKUPSTATION);
        this.name = jsonObject.optString(RestConstants.NAME);
        this.pickupId = jsonObject.optString(RestConstants.PICKUP_ID);
        this.image = jsonObject.optString(RestConstants.IMAGE);
        this.address = jsonObject.optString(RestConstants.ADDRESS);
        this.place = jsonObject.optString(RestConstants.PLACE);
        this.city = jsonObject.optString(RestConstants.CITY);
        this.openingHours = jsonObject.optString(RestConstants.OPENING_HOURS);
        this.shippingFee = jsonObject.optLong(RestConstants.SHIPPING_FEE);
        // Save payment methods
        this.paymentMethods = new ArrayList<>();
        JSONArray arrayPaymentMethod = jsonObject.getJSONArray(RestConstants.PAYMENT_METHOD);
        for (int i = 0; i < arrayPaymentMethod.length(); i++) {
            this.paymentMethods.add(arrayPaymentMethod.getString(i));
        }
        // Save regions
        this.regions = new ArrayList<>();
        JSONObject arrayRegions = jsonObject.getJSONObject(RestConstants.REGIONS);
        Iterator<?> keys = arrayRegions.keys();
        while (keys.hasNext()) {
            String key = keys.next().toString();
//            //Print.i(TAG, "code1adding key : " + key);
            Region mRegion = new Region(key, arrayRegions.getString(key));
            this.regions.add(mRegion);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    /**
     * Parcel constructor
     */
    private PickUpStationObject(Parcel in) {
        pickupId = in.readString();
        name = in.readString();
        pickupStationId = in.readString();
        image = in.readString();
        address = in.readString();
        place = in.readString();
        city = in.readString();
        openingHours = in.readString();
        paymentMethods = new ArrayList<>();
        in.readList(paymentMethods, String.class.getClassLoader());
        regions = new ArrayList<>();
        in.readList(regions, Region.class.getClassLoader());
    }

    /**
     * Create parcelable
     */
    public static final Parcelable.Creator<PickUpStationObject> CREATOR = new Parcelable.Creator<PickUpStationObject>() {
        public PickUpStationObject createFromParcel(Parcel in) {
            return new PickUpStationObject(in);
        }

        public PickUpStationObject[] newArray(int size) {
            return new PickUpStationObject[size];
        }
    };

}
