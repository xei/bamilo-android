package pt.rocket.forms;

import org.json.JSONObject;

import pt.rocket.framework.interfaces.IJSONSerializable;

/**
 * 
 * @author sergiopereira
 * 
 */
public class NewsletterOption implements IJSONSerializable {

    public static final String TAG = NewsletterOption.class.getSimpleName();
    
    public boolean isDefaut;
    
    public String value;
    
    public String label;
    
    public boolean isSubscrided;
    
    public String name;

    /**
     * @author sergiopereira
     */
    public NewsletterOption(JSONObject object, String name) {
        initialize(object);
        this.name = name.replace("[]", "[" + value + "]");
    }
    
    /*
     * (non-Javadoc)
     * @see pt.rocket.framework.interfaces.IJSONSerializable#initialize(org.json.JSONObject)
     */
    @Override
    public boolean initialize(JSONObject object) {
        isDefaut = object.optBoolean("is_default");
        value = object.optString("value");
        label = object.optString("label");
        isSubscrided = object.optBoolean("user_subscribed");
        return true;
    }
    
    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return label;
    }

}
