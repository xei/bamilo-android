/**
 *
 */
package com.mobile.helpers.checkout;

import android.net.Uri;
import android.os.Bundle;

import com.mobile.framework.enums.RequestType;
import com.mobile.framework.objects.OrderTracker;
import com.mobile.framework.utils.Constants;
import com.mobile.framework.utils.EventType;
import com.mobile.framework.utils.Utils;
import com.mobile.helpers.BaseHelper;
import com.mobile.helpers.HelperPriorityConfiguration;

import org.json.JSONObject;

import de.akquinet.android.androlog.Log;

/**
 * Example helper
 *
 * @author Guilherme Silva
 *
 */
public class GetTrackOrderHelper extends BaseHelper {

    private static String TAG = GetTrackOrderHelper.class.getSimpleName();

    private static final EventType EVENT_TYPE = EventType.TRACK_ORDER_EVENT;

    public static final String ORDER_NR = "ordernr";

    /*
     * (non-Javadoc)
     * @see com.mobile.helpers.BaseHelper#generateRequestBundle(android.os.Bundle)
     */
    @Override
    public Bundle generateRequestBundle(Bundle args) {
        Uri uri = Uri.parse(EventType.TRACK_ORDER_EVENT.action).buildUpon()
                .appendQueryParameter("ordernr", args.getString(ORDER_NR)).build();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_URL_KEY, uri.toString());
        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.GET);
        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(EVENT_TYPE.name()));
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.TRACK_ORDER_EVENT);
        return bundle;
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.helpers.BaseHelper#parseResponseBundle(android.os.Bundle, org.json.JSONObject)
     */
    @Override
    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
        OrderTracker mOrderTracker = new OrderTracker();
        if (jsonObject != null) {
            mOrderTracker.initialize(jsonObject);
        }
        bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, mOrderTracker);
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.TRACK_ORDER_EVENT);
        return bundle;
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.helpers.BaseHelper#parseErrorBundle(android.os.Bundle)
     */
    @Override
    public Bundle parseErrorBundle(Bundle bundle) {
        Log.d(TAG, "parseErrorBundle GetTrackOrderHelper");
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.TRACK_ORDER_EVENT);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.helpers.BaseHelper#parseResponseErrorBundle(android.os.Bundle)
     */
    @Override
    public Bundle parseResponseErrorBundle(Bundle bundle) {
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.TRACK_ORDER_EVENT);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }

    @Override
    public Bundle parseResponseErrorBundle(Bundle bundle, JSONObject jsonObject) {
        return parseResponseErrorBundle(bundle);
    }
}