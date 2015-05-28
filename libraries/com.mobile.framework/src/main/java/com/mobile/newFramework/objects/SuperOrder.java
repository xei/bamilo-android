package com.mobile.newFramework.objects;


import com.mobile.framework.rest.RestConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Class used to save the newsletter subscription
 * @author sergiopereira
 */
public class SuperOrder implements IJSONSerializable {

    private static final String TAG = SuperOrder.class.getSimpleName();


    /**
     * Empty constructor
     */
    public SuperOrder() {
        // ...
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.framework.objects.IJSONSerializable#initialize(org.json.JSONObject)
     */
    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        try {
            ArrayList<com.mobile.framework.objects.CustomerNewsletterSubscription> subscriptions = new ArrayList<>();
            // Get subscribed newsletters
            JSONArray jsonArray = jsonObject.optJSONArray(RestConstants.JSON_SUBSCRIBED_CATEGORIES_TAG);
            if(jsonArray != null && jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    com.mobile.framework.objects.CustomerNewsletterSubscription newsletter = new com.mobile.framework.objects.CustomerNewsletterSubscription();
                    newsletter.initialize(object);
                    subscriptions.add(newsletter);
                }
            }
            // Save the newsletter subscriptions
//            if(JumiaApplication.CUSTOMER != null)
//                JumiaApplication.CUSTOMER.setNewsletterSubscriptions(subscriptions);
        } catch (JSONException e) {
            System.out.println("ON PARSING NEWSLETTER SUBSCRIPTIONS"+ e);
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.framework.objects.IJSONSerializable#toJSON()
     */
    @Override
    public JSONObject toJSON() {
        return null;
    }

    @Override
    public RequiredJson getRequiredJson() {
        return RequiredJson.METADATA;
    }

}
