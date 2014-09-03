/**
 * 
 */
package pt.rocket.helpers.checkout;

import org.json.JSONObject;

import pt.rocket.framework.enums.RequestType;
import pt.rocket.framework.objects.OrderTracker;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.Utils;
import pt.rocket.helpers.BaseHelper;
import pt.rocket.helpers.HelperPriorityConfiguration;
import android.net.Uri;
import android.os.Bundle;

/**
 * Example helper
 * 
 * @author Guilherme Silva
 * 
 */
public class GetTrackOrderHelper extends BaseHelper {
    private static String TAG = GetTrackOrderHelper.class.getSimpleName();

    public static final String ORDER_NR = "ordernr";

    @Override
    public Bundle generateRequestBundle(Bundle args) {
        Uri uri = Uri.parse(EventType.TRACK_ORDER_EVENT.action).buildUpon()
                .appendQueryParameter("ordernr", args.getString(ORDER_NR)).build();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_URL_KEY, uri.toString());
        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.GET);
        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(Constants.BUNDLE_MD5_KEY));
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.TRACK_ORDER_EVENT);
        return bundle;
    }



    @Override
    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {

        OrderTracker mOrderTracker = new OrderTracker();
        if (jsonObject != null) {
            mOrderTracker.initialize(jsonObject);
        }

        bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, mOrderTracker);
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.TRACK_ORDER_EVENT);
//        long elapsed = System.currentTimeMillis() - JumiaApplication.INSTANCE.timeTrackerMap.get(EventType.TRACK_ORDER_EVENT);
//        Log.i("REQUEST", "event type response : "+bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY)+" time spent : "+elapsed);
//        String trackValue = bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY) + " : "+elapsed;
//        JumiaApplication.INSTANCE.writeToTrackerFile(trackValue);
        return bundle;
    }
    
    @Override
    public Bundle parseErrorBundle(Bundle bundle) {
        de.akquinet.android.androlog.Log.d(TAG, "parseErrorBundle GetTrackOrderHelper");
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.TRACK_ORDER_EVENT);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }

    @Override
    public Bundle parseResponseErrorBundle(Bundle bundle) {
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.TRACK_ORDER_EVENT);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }
    
}
