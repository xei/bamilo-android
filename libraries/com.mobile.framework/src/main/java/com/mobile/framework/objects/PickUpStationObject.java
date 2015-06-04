package com.mobile.framework.objects;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.framework.rest.RestConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import com.mobile.framework.output.Print;
/**
 * Class that Represents a Pickup station
 * 
 * @author manuelsilva
 *
 */
public class PickUpStationObject implements Parcelable {
	private static final String TAG = PickUpStationObject.class.getName();
	private String id_pickupstation;
	private String name;
	private String pickup_id;
	private String image;
	private String address;
	private String place;
	private String city;
	private String opening_hours;
	private String id_pickupstation_region;
	private ArrayList<String> payment_method;
	private ArrayList<Region> regions;

	/**
	 * Empty Constructor
	 */
	public PickUpStationObject() {
	
	}
	
	/**
	 * @return the id_pickupstation
	 */
	public String getIdPickupstation() {
		return id_pickupstation;
	}

	/**
	 * @param id_pickupstation
	 *            the id_pickupstation to set
	 */
	public void setIdPickupstation(String id_pickupstation) {
		this.id_pickupstation = id_pickupstation;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the pickup_id
	 */
	public String getPickupId() {
		return pickup_id;
	}

	/**
	 * @param pickup_id
	 *            the pickup_id to set
	 */
	public void setPickupId(String pickup_id) {
		this.pickup_id = pickup_id;
	}

	/**
	 * @return the image
	 */
	public String getImage() {
		return image;
	}

	/**
	 * @param image
	 *            the image to set
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
	 * @param address
	 *            the address to set
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
	 * @param place
	 *            the place to set
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
	public String getOpening_hours() {
		return opening_hours;
	}

	/**
	 * @param opening_hours the opening_hours to set
	 */
	public void setOpening_hours(String opening_hours) {
		this.opening_hours = opening_hours;
	}

	/**
	 * @return the id_pickupstation_region
	 */
	public String getIdPickupstationRegion() {
		return id_pickupstation_region;
	}

	/**
	 * @param id_pickupstation_region the id_pickupstation_region to set
	 */
	public void setIdPickupstationRegion(String id_pickupstation_region) {
		this.id_pickupstation_region = id_pickupstation_region;
	}

	/**
	 * @return the payment_method
	 */
	public ArrayList<String> getPaymentMethod() {
		return payment_method;
	}

	/**
	 * @param payment_method the payment_method to set
	 */
	public void setPaymentMethod(ArrayList<String> payment_method) {
		this.payment_method = payment_method;
	}

	/**
	 * @return the regions
	 */
	public ArrayList<Region> getRegions() {
		return regions;
	}

	/**
	 * @param regions the regions to set
	 */
	public void setRegions(ArrayList<Region> regions) {
		this.regions = regions;
	}

	public void initialize(JSONObject jsonObject){
		
		this.id_pickupstation_region = jsonObject.optString(RestConstants.JSON_PICKUP_ID_PICKUP_STATION_REGION);
		this.pickup_id =  jsonObject.optString(RestConstants.JSON_PICKUP_STATION_ID);
		this.name = jsonObject.optString(RestConstants.JSON_NAME_TAG);
		this.id_pickupstation = jsonObject.optString(RestConstants.JSON_PICKUP_ID);
		this.image = jsonObject.optString(RestConstants.JSON_IMAGE_TAG);
		this.address = jsonObject.optString(RestConstants.JSON_PICKUP_ADDRESS);
		this.place = jsonObject.optString(RestConstants.JSON_PICKUP_PLACE);
		this.city = jsonObject.optString(RestConstants.JSON_PICKUP_CITY);
		this.opening_hours = jsonObject.optString(RestConstants.JSON_PICKUP_OPENING_HOURS);
		
		
		this.payment_method = new ArrayList<String>();
		JSONArray arrayPaymentMethod;
		try {
			arrayPaymentMethod = jsonObject.getJSONArray(RestConstants.JSON_PICKUP_PAYMENT_METHOD);
			for (int i = 0; i < arrayPaymentMethod.length(); i++) {
				this.payment_method.add(arrayPaymentMethod.get(i).toString());
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		this.regions = new ArrayList<Region>();
		JSONObject arrayRegions;
		try {
			arrayRegions = jsonObject.getJSONObject(RestConstants.JSON_PICKUP_REGIONS);
			Iterator<?> keys = arrayRegions.keys();
			while(keys.hasNext()) {
				String key = keys.next().toString();
				Print.i(TAG, "code1adding key : " + key);
				Region mRegion = new Region(key,arrayRegions.getString(key));
				this.regions.add(mRegion);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		
		
		
	}
	
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		
	}
	
    /**
     * Parcel constructor
     * @param in
     */
    private PickUpStationObject(Parcel in) {
    	id_pickupstation = in.readString();
    	name = in.readString();;
    	pickup_id = in.readString();
    	image = in.readString();
    	address = in.readString();
    	place = in.readString();
    	city = in.readString();
    	opening_hours = in.readString();
    	id_pickupstation_region = in.readString();
    	payment_method = new ArrayList<String>();
    	in.readList(payment_method, String.class.getClassLoader());
    	regions = new ArrayList<Region>();
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
