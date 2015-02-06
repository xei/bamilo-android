package com.mobile.framework.objects;

import org.json.JSONException;
import org.json.JSONObject;

import com.mobile.framework.rest.RestConstants;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class that defines the carousel (slider) item
 * @author GuilhermeSilva
 */
public class CarouselItem  implements IJSONSerializable, Parcelable{
	
    private String id;
    private String description;
    private String url;
    
    /**
     * Empty constructor.
     */
    public CarouselItem() {
        id = "";
        description = "";
        url = "";
    }
    
    /**
     * @param description title of the carousel item.
     * @param url of its content.
     */
    public CarouselItem(String description, String url) {
        this.id = "";
        this.description = description;
        this.url = url;
    }

    /* (non-Javadoc)
     * @see com.mobile.framework.objects.IJSONSerializable#initialize(org.json.JSONObject)
     */
    @Override
    public boolean initialize(JSONObject jsonObject) {
        try {
            id = jsonObject.getString(RestConstants.JSON_ID_TAG);
            
            JSONObject dataObject = jsonObject.getJSONObject(RestConstants.JSON_DATA_TAG);
            
            description = dataObject.getString(RestConstants.JSON_DESCRIPTION_TAG);
            url = dataObject.getString(RestConstants.JSON_URL_TAG);
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
 
    /* (non-Javadoc)
     * @see com.mobile.framework.objects.IJSONSerializable#toJSON()
     */
    @Override
    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put(RestConstants.JSON_ID_TAG, id);
            
            JSONObject dataObject = new JSONObject();
            dataObject.put(RestConstants.JSON_DESCRIPTION_TAG, description);
            dataObject.put(RestConstants.JSON_URL_TAG, url);
            
            jsonObject.put(RestConstants.JSON_DATA_TAG, dataObject);
        } catch(JSONException e) {
            e.printStackTrace();
        }        
        return jsonObject;
    }
    
    /**
     * @return the description, also the title of the carousel item.
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * @return the url.
     */
    public String getUrl() {
        return url;
    }
    
    /**
     * @return the id of the carousel item.
     */
    public String getId() {
        return id;
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
	    dest.writeString(id);
	    dest.writeString(description);
	    dest.writeString(url);
	}
	
	/**
	 * Parcel constructor
	 * @param in
	 */
	private CarouselItem(Parcel in) {
        id = in.readString();
        description = in.readString();
        url = in.readString();
    }
		
	/**
	 * Create parcelable 
	 */
	public static final Parcelable.Creator<CarouselItem> CREATOR = new Parcelable.Creator<CarouselItem>() {
        public CarouselItem createFromParcel(Parcel in) {
            return new CarouselItem(in);
        }

        public CarouselItem[] newArray(int size) {
            return new CarouselItem[size];
        }
    };
}
