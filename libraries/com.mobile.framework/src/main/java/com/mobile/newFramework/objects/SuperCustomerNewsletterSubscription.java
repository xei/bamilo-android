package com.mobile.newFramework.objects;


import com.mobile.framework.rest.RestConstants;
import com.mobile.newFramework.objects.user.CustomerNewsletterSubscription;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Class used to save the newsletter subscription
 * @author sergiopereira
 */
public class SuperCustomerNewsletterSubscription extends ArrayList<CustomerNewsletterSubscription> implements IJSONSerializable {

    private static final String TAG = SuperCustomerNewsletterSubscription.class.getSimpleName();

    /**
     * Empty constructor
     */
    public SuperCustomerNewsletterSubscription() {
        // ...
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.framework.objects.IJSONSerializable#initialize(org.json.JSONObject)
     */
    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        try {

            JSONObject object = jsonObject.optJSONArray(RestConstants.JSON_SUBSCRIBED_CATEGORIES_TAG).getJSONObject(0);
            CustomerNewsletterSubscription newsletter = new CustomerNewsletterSubscription();
            newsletter.initialize(object);
            add(newsletter);

//            // Get subscribed newsletters
//            JSONArray jsonArray = jsonObject.optJSONArray(RestConstants.JSON_SUBSCRIBED_CATEGORIES_TAG).getJSONObject(0);
//            if(jsonArray != null && jsonArray.length() > 0) {
//                for (int i = 0; i < jsonArray.length(); i++) {
//                    JSONObject object = jsonArray.getJSONObject(i);
//                    newsletter = new CustomerNewsletterSubscription();
//                    newsletter.initialize(object);
//                    add(newsletter);
//                }
//            }
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


//    public CustomerNewsletterSubscription getNewsletter() {
//        return newsletter;
//    }
//
//    public void setNewsletter(CustomerNewsletterSubscription newsletter) {
//        this.newsletter = newsletter;
//    }
}
