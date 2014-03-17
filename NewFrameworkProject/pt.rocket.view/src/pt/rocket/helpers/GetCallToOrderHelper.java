/**
 * 
 */
package pt.rocket.helpers;

import org.json.JSONObject;

import pt.rocket.framework.enums.RequestType;
import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.Utils;
import pt.rocket.utils.JumiaApplication;
import android.os.Bundle;
import de.akquinet.android.androlog.Log;

/**
 * Example helper
 * 
 * @author Guilherme Silva
 * @modified Manuel Silva
 */
public class GetCallToOrderHelper extends BaseHelper {

    private static String TAG = GetCallToOrderHelper.class.getSimpleName();

    @Override
    public Bundle generateRequestBundle(Bundle args) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_URL_KEY, EventType.GET_CALL_TO_ORDER_PHONE.action);
        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.GET);
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_CALL_TO_ORDER_PHONE);
        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_NOT_PRIORITARY);
        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(Constants.BUNDLE_MD5_KEY));
        return bundle;
    }

    @Override
    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
        // TODO Auto-generated method stub
        Log.d(TAG, "parseResponseBundle GetTeasersHelper");

        String phone = "";
        if (null != jsonObject) {
            phone = jsonObject.optString(RestConstants.JSON_CALL_PHONE_TAG).split("-")[0];
        }

        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_CALL_TO_ORDER_PHONE);
        bundle.putString(Constants.BUNDLE_URL_KEY, phone);
//        long elapsed = System.currentTimeMillis() - JumiaApplication.INSTANCE.timeTrackerMap.get(EventType.GET_CALL_TO_ORDER_PHONE);
//        Log.i("REQUEST", "event type response : "+bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY)+" time spent : "+elapsed);
//        String trackValue = bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY) + " : "+elapsed;
//        JumiaApplication.INSTANCE.writeToTrackerFile(trackValue);
        return bundle;
    }

    @Override
    public Bundle parseErrorBundle(Bundle bundle) {
        // TODO Auto-generated method stub
        android.util.Log.d("TRACK", "parseErrorBundle GetTeasersHelper");

        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_CALL_TO_ORDER_PHONE);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }
    
    @Override
    public Bundle parseResponseErrorBundle(Bundle bundle) {
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_CALL_TO_ORDER_PHONE);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }

}
