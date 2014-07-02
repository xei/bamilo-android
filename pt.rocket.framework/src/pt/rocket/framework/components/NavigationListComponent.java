package pt.rocket.framework.components;

import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.framework.interfaces.IJSONSerializable;
import pt.rocket.framework.rest.RestConstants;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Navigation List Component
 * 
 * This class is used to create the components received from the server for each
 * element of the Navigation List.
 * 
 * @author manuelsilva
 * 
 */
public class NavigationListComponent implements IJSONSerializable, Parcelable {
	private final static String TAG = "";

//	private static final String JSON_ATTRIBUTES_TAG = "attributes";
//	private static final String JSON_NAME_TAG = "name";
//	private static final String JSON_NAVIGATION_URL_TAG = "navigation_url";
//	private static final String JSON_IMAGE_TAG = "image";
//	private static final String JSON_IMAGE_URL_TAG = "image_url";
    
    private int element_id;
	private String element_text;
	private String element_url;
	private String element_image_url;

	public NavigationListComponent() {
		// empty constructor
	}

	public NavigationListComponent(int id, String text, String url, String imgurl) {
		this.element_id = id;
		this.element_text = text;
		this.element_url = url;
		this.element_image_url = imgurl;
	}

	/**
	 * @param text of the element.
	 */
	public void setElementText(String text) {
		this.element_text = text;
	}

	/**
	 * @param url of the image of the element.
	 */
	public void setElementImageUrl(String url) {
		this.element_image_url = url;
	}

	/**
	 * @param url of the element.
	 */
	public void setElementUrl(String url) {
		this.element_url = url;
	}

	/**
	 * @param id of the element
	 */
	public void setElementId(int id) {
		this.element_id = id;
	}

	/**
	 * @return the id of the element
	 */
	public int getElementId() {
		return element_id;
	}

	/**
	 * @return the text of the element.
	 */
	public String getElementText() {
		return this.element_text;
	}

	/**
	 * @return the url of the image.
	 */
	public String getElementImageUrl() {
		return this.element_image_url;
	}

	/**
	 * @return the url of the navigation list.
	 */
	public String getElementUrl() {
		return this.element_url;
	}

    /* (non-Javadoc)
     * @see pt.rocket.framework.objects.IJSONSerializable#initialize(org.json.JSONObject)
     */
    @Override
    public boolean initialize(JSONObject jsonObject) {
        try {
        	
            element_id = Integer.parseInt(jsonObject.getString(RestConstants.JSON_ID_TAG));
            JSONObject attributesObject = jsonObject.getJSONObject(RestConstants.JSON_ATTRIBUTES_TAG);
            element_text = attributesObject.getString(RestConstants.JSON_NAVLIST_NAME_TAG);
            
            element_url = attributesObject.getString(RestConstants.JSON_NAVIGATION_URL_TAG);
            JSONObject imageObject = attributesObject.getJSONObject(RestConstants.JSON_IMAGE_TAG);
            element_image_url = imageObject.getString(RestConstants.JSON_IMAGE_URL_TAG);
            
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        
        return true;
    }

    /* (non-Javadoc)
     * @see pt.rocket.framework.objects.IJSONSerializable#toJSON()
     */

    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(RestConstants.JSON_ID_TAG, element_id);

            JSONObject attributesObject = new JSONObject();
            attributesObject.put(RestConstants.JSON_NAVLIST_NAME_TAG, element_text);
            attributesObject.put(RestConstants.JSON_NAVIGATION_URL_TAG, element_url);
            
            JSONObject imageObject = new JSONObject();
            imageObject.put(RestConstants.JSON_IMAGE_URL_TAG, element_image_url);
            attributesObject.put(RestConstants.JSON_IMAGE_TAG, imageObject);
            
            jsonObject.put(RestConstants.JSON_ATTRIBUTES_TAG, attributesObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }    
        
        return jsonObject;
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
    
    
}
