package pt.rocket.framework.objects;

import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.framework.rest.RestConstants;

/**
 * Class that defines the carousel (slider) item
 * @author GuilhermeSilva
 */
public class CarouselItem  implements IJSONSerializable{
	// private static final String JSON_DESCRIPTION_TAG = "description";
	// private static final String JSON_URL_TAG = "url";
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
     * @see pt.rocket.framework.objects.IJSONSerializable#initialize(org.json.JSONObject)
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
     * @see pt.rocket.framework.objects.IJSONSerializable#toJSON()
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
}
