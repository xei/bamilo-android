/**
 * 
 */
package pt.rocket.helpers;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.framework.enums.RequestType;
import pt.rocket.framework.objects.Promotion;
import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.Utils;
import pt.rocket.pojo.ITargeting;
import pt.rocket.pojo.TeaserSpecification;
import pt.rocket.utils.JSONConstants;
import android.os.Bundle;
import de.akquinet.android.androlog.Log;

/**
 * Example helper
 * 
 * @author Guilherme Silva
 * @modified Manuel Silva
 */
public class GetPromotionsHelper extends BaseHelper {

    private static String TAG = GetPromotionsHelper.class.getSimpleName();

    @Override
    public Bundle generateRequestBundle(Bundle args) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_URL_KEY, EventType.GET_PROMOTIONS.action);
        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.GET);
        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(Constants.BUNDLE_MD5_KEY));
        return bundle;
    }

    @Override
    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
        // TODO Auto-generated method stub
        Log.d(TAG, "parseResponseBundle GetPromotionsHelper");

        Promotion promo = new Promotion();
        try {

            if (null != jsonObject) {
                promo.initialize(jsonObject.getJSONObject(RestConstants.JSON_DATA_TAG));
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, promo);
        return bundle;
    }

    @Override
    public Bundle parseErrorBundle(Bundle bundle) {
        // TODO Auto-generated method stub
        android.util.Log.d("TRACK", "parseErrorBundle GetTeasersHelper");

        // FIXME next line is just for test porpouse, to delete
        bundle.putString(Constants.BUNDLE_URL_KEY, " GetTeasersHelper");
        return bundle;
    }

}
