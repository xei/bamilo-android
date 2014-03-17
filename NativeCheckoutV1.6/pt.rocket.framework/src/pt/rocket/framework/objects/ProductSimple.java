package pt.rocket.framework.objects;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

import pt.rocket.framework.rest.RestConstants;
import java.util.HashMap;
import java.util.Map.Entry;

/**
 * Defines a simple (variation) of a give product.
 * @author GuilhermeSilva
 *
 */
public class ProductSimple implements IJSONSerializable, Parcelable {

    public static final String SKU_TAG = "sku";
    public static final String PRICE_TAG = "price";
    public static final String SPECIAL_PRICE_TAG = "special_price";
    public static final String QUANTITY_TAG = "quantity";
    public static final String VARIATION_TAG = "variation";
    public static final String STOCK_TAG = "stock";
    public static final String MIN_DELIVERY_TIME_TAG = "min_delivery_time";
    public static final String MAX_DELIVERY_TIME_TAG = "max_delivery_time";

    private HashMap<String, String> attributes;

    /**
     * Product simple empry constructor.
     */
    public ProductSimple() {
        attributes = new HashMap<String, String>();
    }

    /* (non-Javadoc)
     * @see pt.rocket.framework.objects.IJSONSerializable#initialize(org.json.JSONObject)
     */
    @Override
    public boolean initialize(JSONObject jsonObject) {
        attributes.clear();
        try {

            JSONObject attributesObject = jsonObject.getJSONObject(RestConstants.JSON_META_TAG);
            JSONArray attibutesObjectNames = attributesObject.names();

            for (int i = 0; i < attibutesObjectNames.length(); ++i) {
                String key = attibutesObjectNames.getString(i);
                String value = attributesObject.getString(key);
                attributes.put(key, value);
            }
            
            if(attributes.containsKey("quantity")) {
                attributes.put("real_quantity", attributes.get("quantity"));
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
            JSONObject attributesObject = new JSONObject();
            for(Entry<String, String> entry : attributes.entrySet()) {
                attributesObject.put(entry.getKey(), entry.getValue());
            }
            
            jsonObject.put(RestConstants.JSON_META_TAG, attributesObject);
        } catch(JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * @return the attributes
     */
    public HashMap<String, String> getAttributes() {
        return attributes;
    }
    
    public String getAttributeByKey( String key ) {
    	return attributes.get( key );
    }
    
    /**
     * Set the attributes
     */
    public void setAttributes(HashMap<String, String> attributes) {
       this.attributes=attributes;
    }

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeMap(attributes);
		
	}
	
	private ProductSimple(Parcel in){
		attributes = (HashMap<String, String>) in.readHashMap(null);
	}
	
    public static final Parcelable.Creator<ProductSimple> CREATOR = new Parcelable.Creator<ProductSimple>() {
        public ProductSimple createFromParcel(Parcel in) {
            return new ProductSimple(in);
        }

        public ProductSimple[] newArray(int size) {
            return new ProductSimple[size];
        }
    };
}
