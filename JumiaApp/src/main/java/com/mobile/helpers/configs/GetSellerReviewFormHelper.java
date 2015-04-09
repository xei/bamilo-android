/**
 * 
 */
package com.mobile.helpers.configs;

import android.os.Bundle;

import com.mobile.forms.Form;
import com.mobile.framework.enums.RequestType;
import com.mobile.framework.rest.RestConstants;
import com.mobile.framework.utils.Constants;
import com.mobile.framework.utils.EventType;
import com.mobile.framework.utils.Utils;
import com.mobile.helpers.BaseHelper;
import com.mobile.helpers.HelperPriorityConfiguration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.akquinet.android.androlog.Log;

/**
 * Get Seller reviews dynamic form helper
 * 
 * @author Paulo Carvalho
 * 
 */
public class GetSellerReviewFormHelper extends BaseHelper {

    private static String TAG = GetSellerReviewFormHelper.class.getSimpleName();
    
    private static final EventType EVENT_TYPE = EventType.GET_FORM_SELLER_REVIEW_EVENT;

    public static final String PRODUCT_URL = "productUrl";

    @Override
    public Bundle generateRequestBundle(Bundle args) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_URL_KEY, EventType.GET_FORM_SELLER_REVIEW_EVENT.action);
        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.GET);
        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(EVENT_TYPE.name()));
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_FORM_SELLER_REVIEW_EVENT);
        return bundle;
    }

    @Override
    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
        Log.d("TRACK", "parseResponseBundle GetSellerReviewsHelper");
      
        final ArrayList<Form> forms = new ArrayList<>();
        JSONArray dataObject;
        
        try {
            dataObject = jsonObject
                    .getJSONArray(RestConstants.JSON_DATA_TAG);

            for (int i = 0; i < dataObject.length(); ++i) {
                Form form = new Form();
                form.setEventType(EventType.GET_FORM_SELLER_REVIEW_EVENT);
                JSONObject formObject = dataObject.getJSONObject(i);
                if (!form.initialize(formObject)) {
                    Log.e(TAG, "Error initializing the form using the data");
                }
                forms.add(form);
            }
            // formRegistry.put(action, forms);
            if (forms.size() > 0) {
                bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, forms.get(0));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_FORM_SELLER_REVIEW_EVENT);
        return bundle;
    }
    
    @Override
    public Bundle parseErrorBundle(Bundle bundle) {
        Log.d(TAG, "parseErrorBundle GetRatingsHelper");
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_FORM_SELLER_REVIEW_EVENT);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }

    @Override
    public Bundle parseResponseErrorBundle(Bundle bundle) {
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_FORM_SELLER_REVIEW_EVENT);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }
}
