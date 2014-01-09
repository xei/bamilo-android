/**
 * 
 */
package pt.rocket.helpers;

import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.framework.enums.RequestType;
import pt.rocket.framework.objects.Customer;
import pt.rocket.framework.objects.OrderTracker;
import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.CustomerUtils;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.Utils;
import pt.rocket.utils.JumiaApplication;
import android.content.ContentValues;
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
        return bundle;
    }

    @Override
    public Bundle parseErrorBundle(Bundle bundle) {
        // TODO Auto-generated method stub
        android.util.Log.d("TRACK", "parseErrorBundle GetLoginHelper");
        // FIXME next line is just for test porpouse, to delete
        bundle.putString(Constants.BUNDLE_URL_KEY, " GetLoginHelper");
        return bundle;
    }

    @Override
    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {

        OrderTracker mOrderTracker = new OrderTracker();
        if (jsonObject != null) {
            mOrderTracker.initialize(jsonObject);
        }

        bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, mOrderTracker);
        return bundle;
    }
}
