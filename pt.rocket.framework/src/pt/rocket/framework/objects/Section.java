package pt.rocket.framework.objects;

import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.framework.rest.RestConstants;

/**
 * Represents a section of the api.
 * For each section, there is a service.
 * Each section contain name, url. 
 * @author GuilhermeSilva
 *
 */
public class Section implements IJSONSerializable {
	
//    /**
//     * Tag of the name value.
//     */
//    private static String JSON_NAME_TAG = "section_name";
//    /**
//     * Tag of the url tag.
//     */
//    private static String JSON_URL_TAG = "url";
    
    /**
     * Name of the section.
     */
    private String name;
    
    /**
     * Url of the newest version of the section.
     */
    private String url;

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /* (non-Javadoc)
     * @see pt.rocket.framework.objects.IJSONSerializable#initialize(org.json.JSONObject)
     */
    @Override
    public boolean initialize(JSONObject jsonObject) {
        try {
            name = jsonObject.getString(RestConstants.JSON_NAME_TAG);
            url = jsonObject.getString(RestConstants.JSON_URL_TAG);            
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
            jsonObject.put(RestConstants.JSON_NAME_TAG, name);
            jsonObject.put(RestConstants.JSON_URL_TAG, url);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}
