package pt.rocket.framework.forms;

import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.framework.objects.IJSONSerializable;
import pt.rocket.framework.rest.RestConstants;

/**
 * Defines the data from the form.
 * @author GuilhermeSilva
 *
 */
public class FormData implements IJSONSerializable {
//    /**
//     * Defines the json action tag.
//     */
//    private final String JSON_ACTION_TAG = "action";
//    /**
//     * Defines the json url tag.
//     */
//    private final String JSON_URL_TAG = "url";
        
    private String id;
    private String action;
    private String url;
      
    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @return the action
     */
    public String getAction() {
        return action;
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
            id = jsonObject.getString(RestConstants.JSON_ID_TAG);
            action = jsonObject.getString(RestConstants.JSON_ACTION_TAG);
            url = jsonObject.getString(RestConstants.JSON_URL_TAG);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    /* (non-Javadoc)
     * @see pt.rocket.framework.objects.IJSONSerializable#toJSON()
     */
    @Override
    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(RestConstants.JSON_ID_TAG, id);
            jsonObject.put(RestConstants.JSON_ACTION_TAG, action);
            jsonObject.put(RestConstants.JSON_URL_TAG, url);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return jsonObject;
    }

}
