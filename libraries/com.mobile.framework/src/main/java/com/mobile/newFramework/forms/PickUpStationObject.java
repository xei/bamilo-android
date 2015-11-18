package com.mobile.newFramework.forms;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.newFramework.objects.checkout.Region;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.utils.output.Print;

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
    //private String pickupStationRegionId;

    /**
     * Empty Constructor
     */
    public PickUpStationObject() {

    }

    /**
     * @return the id_pickupstation
     */
    public String getIdPickupstation() {
        return pickupId;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
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
     * @param image the image to set
     */
    public void setImage(String image) {
        this.image = image;
    }

    /**
     * @return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * @param address the address to set
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * @return the place
     */
    public String getPlace() {
        return place;
    }

    /**
     * @param place the place to set
     */
    public void setPlace(String place) {
        this.place = place;
    }

    /**
     * @return the city
     */
    public String getCity() {
        return city;
    }

    /**
     * @param city the city to set
     */
    public void setCity(String city) {
        this.city = city;
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
     * @return long
     */
    public long getShippingFee() {
        return shippingFee;
    }

    /**
     * @param regions the regions to set
     */
    public void setRegions(ArrayList<Region> regions) {
        this.regions = regions;
    }

    public void initialize(JSONObject jsonObject) {
        //this.pickupStationRegionId = jsonObject.optString(RestConstants.JSON_PICKUP_ID_PICKUP_STATION_REGION);
        this.pickupStationId = jsonObject.optString(RestConstants.JSON_PICKUP_STATION_ID);
        this.name = jsonObject.optString(RestConstants.JSON_NAME_TAG);
        this.pickupId = jsonObject.optString(RestConstants.JSON_PICKUP_ID);
        this.image = jsonObject.optString(RestConstants.JSON_IMAGE_TAG);
        this.address = jsonObject.optString(RestConstants.JSON_PICKUP_ADDRESS);
        this.place = jsonObject.optString(RestConstants.JSON_PICKUP_PLACE);
        this.city = jsonObject.optString(RestConstants.JSON_PICKUP_CITY);
        this.openingHours = jsonObject.optString(RestConstants.JSON_PICKUP_OPENING_HOURS);
        this.shippingFee = jsonObject.optLong(RestConstants.JSON_SHIPPING_FEE_TAG);

        this.paymentMethods = new ArrayList<>();
        JSONArray arrayPaymentMethod;
        try {
            arrayPaymentMethod = jsonObject.getJSONArray(RestConstants.PAYMENT_METHOD);
            for (int i = 0; i < arrayPaymentMethod.length(); i++) {
                this.paymentMethods.add(arrayPaymentMethod.get(i).toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        this.regions = new ArrayList<>();
        JSONObject arrayRegions;
        try {
            arrayRegions = jsonObject.getJSONObject(RestConstants.JSON_PICKUP_REGIONS);
            Iterator<?> keys = arrayRegions.keys();
            while (keys.hasNext()) {
                String key = keys.next().toString();
                Print.i(TAG, "code1adding key : " + key);
                Region mRegion = new Region(key, arrayRegions.getString(key));
                this.regions.add(mRegion);
            }
        } catch (JSONException e) {
            e.printStackTrace();
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
        //pickupStationRegionId = in.readString();
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
