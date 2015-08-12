//package com.mobile.newFramework.objects.product;
//
//import android.os.Parcel;
//import android.os.Parcelable;
//
//import com.mobile.newFramework.objects.IJSONSerializable;
//import com.mobile.newFramework.objects.RequiredJson;
//import com.mobile.newFramework.pojo.RestConstants;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.HashMap;
//import java.util.Map.Entry;
//
///**
// * Defines a simple (variation) of a give product.
// * @author GuilhermeSilva
// *
// */
//public class ProductSimple implements IJSONSerializable, Parcelable {
//
//    public static final String SKU_TAG = RestConstants.JSON_SKU_TAG;
//    public static final String PRICE_TAG = RestConstants.JSON_PRICE_TAG;
//    public static final String SPECIAL_PRICE_TAG = RestConstants.JSON_SPECIAL_PRICE_TAG;
//    public static final String QUANTITY_TAG = RestConstants.JSON_QUANTITY_TAG;
//    public static final String MIN_DELIVERY_TIME_TAG = "min_delivery_time";
//    public static final String MAX_DELIVERY_TIME_TAG = "max_delivery_time";
//
//    private HashMap<String, String> attributes;
//
//    /**
//     * Empty constructor.
//     */
//    public ProductSimple() {
//        attributes = new HashMap<>();
//    }
//
//    /* (non-Javadoc)
//     * @see com.mobile.framework.objects.IJSONSerializable#initialize(org.json.JSONObject)
//     */
//    @Override
//    public boolean initialize(JSONObject jsonObject) {
//        attributes.clear();
//        try {
//
//            JSONObject attributesObject;
//
//            //NORMAL COMPLETE SIMPLE PRODUCT
//            if(jsonObject.optJSONObject(RestConstants.JSON_META_TAG) != null){
//                attributesObject = jsonObject.getJSONObject(RestConstants.JSON_META_TAG);
//            } else {
//                //RECENTLY VIEWED SIMPLE PRODUCT
//                attributesObject = jsonObject;
//            }
//
//            JSONArray attributesObjectNames = attributesObject.names();
//
//            for (int i = 0; i < attributesObjectNames.length(); ++i) {
//                String key = attributesObjectNames.getString(i);
//                String value = attributesObject.getString(key);
//                attributes.put(key, value);
//            }
//
//            if(attributes.containsKey(RestConstants.JSON_QUANTITY_TAG)) {
//                attributes.put(RestConstants.JSON_REAL_QUANTITY_TAG, attributes.get(RestConstants.JSON_QUANTITY_TAG));
//            }
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//            return false;
//        }
//        return true;
//    }
//
//    /* (non-Javadoc)
//     * @see com.mobile.framework.objects.IJSONSerializable#toJSON()
//     */
//    @Override
//    public JSONObject toJSON() {
//        JSONObject jsonObject = new JSONObject();
//
//        try {
//            JSONObject attributesObject = new JSONObject();
//            for(Entry<String, String> entry : attributes.entrySet()) {
//                attributesObject.put(entry.getKey(), entry.getValue());
//            }
//
//            jsonObject.put(RestConstants.JSON_META_TAG, attributesObject);
//        } catch(JSONException e) {
//            e.printStackTrace();
//        }
//        return jsonObject;
//    }
//
//    @Override
//    public RequiredJson getRequiredJson() {
//        return null;
//    }
//
//    /**
//     * @return the attributes
//     */
//    public HashMap<String, String> getAttributes() {
//        return attributes;
//    }
//
//    public String getAttributeByKey( String key ) {
//    	return attributes.get(key);
//    }
//
//    /**
//     * Validate if current object has the attribute.
//     * @param key The attribute key
//     * @return true or false
//     */
//    public boolean hasAttributeByKey(String key) {
//        return attributes.containsKey(key);
//    }
//
//
//    /**
//     * Set the attributes
//     */
//    public void setAttributes(HashMap<String, String> attributes) {
//       this.attributes=attributes;
//    }
//
//	@Override
//	public int describeContents() {
//		return 0;
//	}
//
//	@Override
//	public void writeToParcel(Parcel dest, int flags) {
//		dest.writeMap(attributes);
//
//	}
//
//	private ProductSimple(Parcel in){
//		attributes = new HashMap<>();
//		in.readMap(attributes, String.class.getClassLoader());
//	}
//
//    public static final Parcelable.Creator<ProductSimple> CREATOR = new Parcelable.Creator<ProductSimple>() {
//        public ProductSimple createFromParcel(Parcel in) {
//            return new ProductSimple(in);
//        }
//
//        public ProductSimple[] newArray(int size) {
//            return new ProductSimple[size];
//        }
//    };
//}
