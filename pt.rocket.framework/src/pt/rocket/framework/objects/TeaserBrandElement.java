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

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.framework.objects.ITargeting.TargetType;
import pt.rocket.framework.utils.CurrencyFormatter;
import pt.rocket.framework.utils.LogTagHelper;
import de.akquinet.android.androlog.Log;

/**
 * Class that represents the server side product. Contains id, name,
 * description, price(deprecated), stock(deprecated), list of images, brand and
 * list of category id.
 * 
 * @author GuilhermeSilva
 * 
 */
public class TeaserBrandElement implements IJSONSerializable {
	private final static String TAG = LogTagHelper.create( TeaserBrandElement.class );

    private static final String JSON_ATTRIBUTES_TAG = "data";
    private static final String JSON_ATTRIBUTES_TWO_TAG = "attributes";

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
            id = jsonObject.getString(JSON_ID_TAG);
            
            JSONObject attributesObject = jsonObject.optJSONObject(JSON_ATTRIBUTES_TAG);
            if(attributesObject != null){
                attributes.initialize(attributesObject);
            }
            
            JSONObject attributes2Object = jsonObject.optJSONObject(JSON_ATTRIBUTES_TWO_TAG);
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
            jsonObject.put(JSON_ID_TAG, id);
            jsonObject.put(JSON_ATTRIBUTES_TAG, attributes.toJSON());
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return jsonObject;
    }

    /**
     * Class that holds the attributes of the product
     * 
     * @author GuilhermeSilva
     * 
     */
    public class BrandAttributes implements IJSONSerializable {
        private static final String JSON_NAME_TAG = "description";
        private static final String JSON_ID_TAG = "id";
        private static final String JSON_IMAGE_LIST_TAG = "image_list";
        private static final String JSON_IMAGE_URL_TAG = "image_url";
        private static final String JSON_BRAND_URL_TAG = "brand_url";
        private static final String JSON_DESCRIPTION_TAG = "description";
        private static final String JSON_TARGET_TYPE_TAG = "target_type";

        private String name;
        private int id;
        private String image_url;
        private String brand_url;
        private String description;
        private String target_type;
        /**
         * ProductAttributes empty constructor
         */
        public BrandAttributes() {
        	name = "";
            id = -1;
            image_url = "";
            brand_url = "";
            description = "";
            target_type = "";
        }

        /**
		 * @return the reviews
		 */
		public Integer getId() {
			return id;
		}

		/**
		 * @return the rating
		 */
		public String getBrandUrl() {
			return brand_url;
		}

		/**
         * @return the sku
         */
        public String getImageUrl() {
            return image_url;
        }

        /**
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * @return the description
         */
        public String getDescription() {
            return description;
        }

        /**
         * @return the brand
         */
        public TargetType getTargetType() {
            return TargetType.BRAND;
        }

        /* (non-Javadoc)
         * @see pt.rocket.framework.objects.IJSONSerializable#initialize(org.json.JSONObject)
         */
        @Override
        public boolean initialize(JSONObject jsonObject) {
                        
            try {
            	
        		name = jsonObject.getString(JSON_NAME_TAG);
        		target_type = jsonObject.optString(JSON_TARGET_TYPE_TAG, "");
                
                              
                description = jsonObject.optString(JSON_DESCRIPTION_TAG, "");
                
                brand_url = jsonObject.getJSONObject(JSON_IMAGE_LIST_TAG).getString(JSON_BRAND_URL_TAG);
                
                image_url = jsonObject.getJSONObject(JSON_IMAGE_LIST_TAG).getString(JSON_IMAGE_URL_TAG);
            } catch (JSONException e) {
            	Log.e(TAG, "Error Parsing the product json", e);
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
                jsonObject.put(JSON_ID_TAG, id);
                jsonObject.put(JSON_NAME_TAG, name);
                jsonObject.put(JSON_IMAGE_URL_TAG, image_url);
                jsonObject.put(JSON_BRAND_URL_TAG, brand_url);
                jsonObject.put(JSON_DESCRIPTION_TAG, description);

            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
            return jsonObject;
        }
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
}
