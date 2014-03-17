package pt.rocket.framework.objects;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

import pt.rocket.framework.rest.RestConstants;

public class ProductSimpleUnique implements IJSONSerializable, Parcelable {

    private String sku;
    private int quantity;
    private String variation;

    /*
     * (non-Javadoc)
     * 
     * @see
     * pt.rocket.framework.objects.IJSONSerializable#initialize(org.json.JSONObject
     * )
     */
    @Override
    public boolean initialize(JSONObject jsonObject) {
        try {
            sku = jsonObject.getString(RestConstants.JSON_SKU_TAG);
            quantity = jsonObject.getInt(RestConstants.JSON_QUANTITY_TAG);
            variation = jsonObject.optString(RestConstants.JSON_VARIATION_TAG);

        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.framework.objects.IJSONSerializable#toJSON()
     */
    @Override
    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(RestConstants.JSON_SKU_TAG, sku);
            jsonObject.put(RestConstants.JSON_QUANTITY_TAG, quantity);
            jsonObject.put(RestConstants.JSON_VARIATION_TAG, variation);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * @return the sku
     */
    public String getSku() {
        return sku;
    }

    /**
     * @return the quantity
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * @return the variation
     */
    public String getVariation() {
        return variation;
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
	    dest.writeInt(quantity);
	    dest.writeString(variation);
	}
	
	/**
	 * Parcel constructor
	 * @param in
	 */
	private ProductSimpleUnique(Parcel in) {
	    sku = in.readString();
	    quantity = in.readInt();
	    variation = in.readString();
    }
		
	/**
	 * Create parcelable 
	 */
	public static final Parcelable.Creator<ProductSimpleUnique> CREATOR = new Parcelable.Creator<ProductSimpleUnique>() {
        public ProductSimpleUnique createFromParcel(Parcel in) {
            return new ProductSimpleUnique(in);
        }

        public ProductSimpleUnique[] newArray(int size) {
            return new ProductSimpleUnique[size];
        }
    };
	
}
