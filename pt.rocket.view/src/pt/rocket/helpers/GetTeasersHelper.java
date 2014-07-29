/**
 * 
 */
package pt.rocket.helpers;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.framework.enums.RequestType;
import pt.rocket.framework.objects.Homepage;
import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.Utils;
import android.os.Bundle;
import de.akquinet.android.androlog.Log;

/**
 * Example helper
 * 
 * @author Guilherme Silva
 * @modified Manuel Silva
 */
public class GetTeasersHelper extends BaseHelper {
    private static String TAG = GetTeasersHelper.class.getSimpleName();
    
    public static final String MD5_KEY = "md5";
    
    @Override
    public Bundle generateRequestBundle(Bundle args) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_URL_KEY, EventType.GET_TEASERS_EVENT.action);
        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.GET);
        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(Constants.BUNDLE_MD5_KEY));
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_TEASERS_EVENT);
        return bundle;
    }

    @Override
    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
        
        // Get MD5
        String md5 = jsonObject.optString(RestConstants.JSON_MD5_TAG);
        bundle.putString(MD5_KEY, md5);
        
        Log.d(TAG, "parseResponseBundle GetTeasersHelper");
        try {
            JSONArray dataArray = jsonObject.getJSONArray(RestConstants.JSON_DATA_TAG);
            int dataArrayLength = dataArray.length();
            if (dataArrayLength > 0) {
                int defaultHomePage = 0;
                ArrayList<Homepage> homepageSpecifications = new ArrayList<Homepage>();
                for (int i = 0; i < dataArrayLength; ++i) {
                    Homepage homepage = new Homepage();
                    homepage.initialize(dataArray.getJSONObject(i));
                    // Validate if is the default home page
                    if(homepage != null &&  homepage.isDefaultHomepage()) defaultHomePage = i;
                    homepageSpecifications.add(homepage);
                }
                bundle.putParcelableArrayList(Constants.BUNDLE_RESPONSE_KEY, homepageSpecifications);
                bundle.putInt(RestConstants.JSON_HOMEPAGE_DEFAULT_TAG, defaultHomePage);
                //Log.i(TAG, "Teasers size: " + homepageSpecifications.size());
            } else {
                Log.e(TAG, "Teasers size: 0");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return parseErrorBundle(bundle);
        }

    	bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_TEASERS_EVENT);
        return bundle;
    }
    

    @Override
    public Bundle parseErrorBundle(Bundle bundle) {
        android.util.Log.d(TAG, "parseErrorBundle GetTeasersHelper");
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_TEASERS_EVENT);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }

    @Override
    public Bundle parseResponseErrorBundle(Bundle bundle) {
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_TEASERS_EVENT);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }
}