/**
 * @author Guilherme Silva
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

import pt.rocket.framework.rest.RestConstants;

import de.akquinet.android.androlog.Log;

/**
 * Class that represents the server-side product brand. Contains id, name and
 * icon url.
 * 
 * @author Guilherme Silva
 * 
 */
public class Brand implements IJSONSerializable {
//    private static final String JSON_INNER_OBJECT_TAG = "brand";
//    private static final String JSON_URL_TAG = "url";
//    private static final String JSON_NAME_TAG = "name";
//    private static final String JSON_IMAGE_TAG = "image";

    private String id;
    private String url;
    private String name;
    private BrandImage image;

    public Brand() {
        this.id = "";
        this.url = "";
        this.name = "";
        this.image = new BrandImage();
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the image
     */
    public BrandImage getImage() {
        return image;
    }

    /* (non-Javadoc)
     * @see pt.rocket.framework.objects.IJSONSerializable#initialize(org.json.JSONObject)
     */
    @Override
    public boolean initialize(JSONObject jsonObject) {
        try {
            id = jsonObject.getString(RestConstants.JSON_ID_TAG);

            JSONObject innerObject = jsonObject.getJSONObject(RestConstants.JSON_INNER_OBJECT_TAG);
            url = innerObject.getString(RestConstants.JSON_URL_TAG);
            name = innerObject.getString(RestConstants.JSON_NAME_TAG);
            if(name.trim().equals("") || url.trim().equals("")){
                Log.d("brands","Brand name = " + name + "\r\nbrand url = " + url);
                return false;
            }
            

            JSONObject imageObject = innerObject.optJSONObject(RestConstants.JSON_IMAGE_TAG);

            if (imageObject != null) {
                image = new BrandImage();
                image.initialize(imageObject);
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
            jsonObject.put(RestConstants.JSON_URL_TAG, url);
            jsonObject.put(RestConstants.JSON_NAME_TAG, name);
            jsonObject.put(RestConstants.JSON_IMAGE_TAG, image.toJSON());

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return jsonObject;
    }

    /**
     * Defines the image of the brand.
     * @author GuilhermeSilva
     *
     */
    public class BrandImage implements IJSONSerializable {
//        private static final String JSON_URL_TAG = "url";
//        private static final String JSON_WIDTH_TAG = "width";
//        private static final String JSON_HEIGHT_TAG = "height";
//        private static final String JSON_FORMAT_TAG = "format";

        private String url;
        private String width;
        private String height;
        private String format;

        /**
         * @return the url
         */
        public String getUrl() {
            return url;
        }

        /**
         * @return the width
         */
        public String getWidth() {
            return width;
        }

        /**
         * @return the height
         */
        public String getHeight() {
            return height;
        }

        /**
         * @return the format
         */
        public String getFormat() {
            return format;
        }

        /* (non-Javadoc)
         * @see pt.rocket.framework.objects.IJSONSerializable#initialize(org.json.JSONObject)
         */
        @Override
        public boolean initialize(JSONObject jsonObject) {
            this.url = jsonObject.optString(RestConstants.JSON_URL_TAG);
            this.width = jsonObject.optString(RestConstants.JSON_WIDTH_TAG);
            this.height = jsonObject.optString(RestConstants.JSON_HEIGHT_TAG);
            this.format = jsonObject.optString(RestConstants.JSON_FORMAT_TAG);
           
            return true;
        }

        /* (non-Javadoc)
         * @see pt.rocket.framework.objects.IJSONSerializable#toJSON()
         */
        @Override
        public JSONObject toJSON() {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(RestConstants.JSON_URL_TAG, url);
                jsonObject.put(RestConstants.JSON_WIDTH_TAG, width);
                jsonObject.put(RestConstants.JSON_HEIGHT_TAG, height);
                jsonObject.put(RestConstants.JSON_FORMAT_TAG, format);

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return null;
            }

            return jsonObject;
        }
    }
}
