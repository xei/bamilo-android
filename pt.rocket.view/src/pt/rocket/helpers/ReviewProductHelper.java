/**
 * 
 */
package pt.rocket.helpers;

import java.util.HashMap;
import java.util.Map.Entry;

import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.app.JumiaApplication;
import pt.rocket.framework.enums.RequestType;
import pt.rocket.framework.objects.ProductReviewCommentCreated;
import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.Utils;
import android.content.ContentValues;
import android.os.Bundle;

/**
 * Get Product Reviews helper
 * 
 * @author Manuel Silva
 * 
 */
public class ReviewProductHelper extends BaseHelper {

    private static String TAG = ReviewProductHelper.class.getSimpleName();

    public static final String COMMENT_CREATED = "commentCreated";
    public static final String PRODUCT_SKU = "productSKU";
    public static final String CUSTOMER_ID = "customerId";

    @Override
    public Bundle generateRequestBundle(Bundle args) {
        ContentValues values = new ContentValues();
        ProductReviewCommentCreated productReviewCreated = args.getParcelable(COMMENT_CREATED);
        productReviewCreated.addParameters(values);
        values.put(RestConstants.REVIEW_PRODUCT_SKU_FIELD, args.getString(PRODUCT_SKU));
        if (args.getInt(CUSTOMER_ID) != -1) {
            values.put(RestConstants.REVIEW_CUSTOMER_ID, args.getInt(CUSTOMER_ID));
        }
        
        for (Entry<String, HashMap<String, String>> option : JumiaApplication.INSTANCE.getRatingOptions().entrySet()) {
            
            values.put(RestConstants.REVIEW_OPTION_FIELD + option.getKey(), option.getValue().get(String.valueOf(productReviewCreated.getRating().get(option.getKey()).intValue())));

        }
        
        Bundle bundle = new Bundle();

        bundle.putString(Constants.BUNDLE_URL_KEY, EventType.REVIEW_PRODUCT_EVENT.action);
        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.POST);
        bundle.putParcelable(Constants.BUNDLE_FORM_DATA_KEY, values);
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.REVIEW_PRODUCT_EVENT);
        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(Constants.BUNDLE_MD5_KEY));
        return bundle;
    }
 
    @Override
    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
        android.util.Log.d("TRACK", "parseResponseBundle GetProductReviewsHelper");
        
        try {
            bundle.putString(Constants.BUNDLE_RESPONSE_KEY, jsonObject.getString("success"));
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace(); 
        }
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.REVIEW_PRODUCT_EVENT);
//        long elapsed = System.currentTimeMillis() - JumiaApplication.INSTANCE.timeTrackerMap.get(EventType.REVIEW_PRODUCT_EVENT);
//        Log.i("REQUEST", "event type response : "+bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY)+" time spent : "+elapsed);
//        String trackValue = bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY) + " : "+elapsed;
//        JumiaApplication.INSTANCE.writeToTrackerFile(trackValue);
        return bundle;
    }

    @Override
    public Bundle parseErrorBundle(Bundle bundle) {
        android.util.Log.d(TAG, "parseErrorBundle GetReviewProductHelper");
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.REVIEW_PRODUCT_EVENT);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }

    @Override
    public Bundle parseResponseErrorBundle(Bundle bundle) {
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.REVIEW_PRODUCT_EVENT);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }

}
