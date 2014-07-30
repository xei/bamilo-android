/**
 * @author GuilhermeSilva
 * 
 * @version 1.01
 * 
 * 2012/06/18
 * 
 * Copyright (c) Rocket Internet All Rights Reserved
 */
package pt.rocket.framework.objects;

import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.framework.objects.ITargeting.TargetType;
import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.utils.LogTagHelper;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class that represents the server side product. Contains id, name,
 * description, price(deprecated), stock(deprecated), list of images, brand and
 * list of category id.
 * 
 * @author GuilhermeSilva
 * 
 */
public class TeaserBrandElement implements IJSONSerializable, Parcelable{
	
	protected final static String TAG = LogTagHelper.create( TeaserBrandElement.class );

    private String id;
    private BrandAttributes attributes;

    /**
     * simple product constructor.
     */
    public TeaserBrandElement() {
        id = "";
        attributes = new BrandAttributes();
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @return the attributes
     */
    public BrandAttributes getAttributes() {
        return attributes;
    }
    
    /* (non-Javadoc)
     * @see pt.rocket.framework.objects.IJSONSerializable#initialize(org.json.JSONObject)
     */
    @Override
    public boolean initialize(JSONObject jsonObject) {
        try {
            id = jsonObject.getString(RestConstants.JSON_ID_TAG);
            
            JSONObject attributesObject = jsonObject.optJSONObject(RestConstants.JSON_DATA_TAG);
            if(attributesObject != null){
                attributes.initialize(attributesObject);
            }
            
            JSONObject attributes2Object = jsonObject.optJSONObject(RestConstants.JSON_PROD_ATTRIBUTES_TAG);
            if(attributes2Object != null) {
                attributes.initialize(attributes2Object);
            }
           
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /* (non-Javadoc)
     * @see pt.rocket.framework.objects.IJSONSerializable#toJSON()
     */
    @Override
    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(RestConstants.JSON_ID_TAG, id);
            jsonObject.put(RestConstants.JSON_DATA_TAG, attributes.toJSON());
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return jsonObject;
    }

    /**
     * @return the brand id
     */
    public int getID() {
        return attributes.getId();
    }

    /**
     * @return the brand name
     */
    public String getName() {
        return attributes.getName();
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return attributes.getDescription();
    }

    /**
     * @return the brand url
     */
    public String getBrandUrl() {
        return attributes.getBrandUrl();
    }

    /**
     * @return the brand image
     */
    public String getImageUrl() {
        return attributes.getImageUrl();
    }
    
    /**
     * @return the brand
     */
    public TargetType getTargetType() {
        return attributes.getTargetType();
    }

        /**
	 * @return the imageTableUrl
	 */
	public String getImageTableUrl() {
		return attributes.getImageTableUrl();
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
	    dest.writeValue(attributes);
	}
	
	/**
	 * Parcel constructor
	 * @param in
	 */
	protected TeaserBrandElement(Parcel in) {
        id = in.readString();
        attributes = (BrandAttributes) in.readValue(BrandAttributes.class.getClassLoader());
    }
	
	/**
	 * Create parcelable 
	 */
	public static final Parcelable.Creator<TeaserBrandElement> CREATOR = new Parcelable.Creator<TeaserBrandElement>() {
        public TeaserBrandElement createFromParcel(Parcel in) {
            return new TeaserBrandElement(in);
        }

        public TeaserBrandElement[] newArray(int size) {
            return new TeaserBrandElement[size];
        }
    };
}
