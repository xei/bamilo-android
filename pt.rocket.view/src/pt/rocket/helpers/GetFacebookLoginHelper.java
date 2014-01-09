/**
 * 
 */
package pt.rocket.helpers;

import org.json.JSONObject;

import pt.rocket.framework.enums.RequestType;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.Utils;

import android.os.Bundle;
import android.util.Log;

/**
 * Example helper
 * 
 * @author Guilherme Silva
 * 
 */
public class GetFacebookLoginHelper extends BaseHelper {
    private static String TAG=GetFacebookLoginHelper.class.getSimpleName();

    @Override
    public Bundle generateRequestBundle(Bundle args) {
        Bundle bundle = new Bundle();
//        bundle.putString(Constants.BUNDLE_URL_KEY, "http:/customer/login/");
        bundle.putString(Constants.BUNDLE_URL_KEY, "http://www.linio.com.ve/mobileapi/customer/login/");
        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.GET);
        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(Constants.BUNDLE_MD5_KEY));
        return bundle;
    }

    @Override
    public Bundle parseErrorBundle(Bundle bundle) {
        // TODO Auto-generated method stub
    	android.util.Log.d("TRACK", "parseErrorBundle GetLoginHelper");
    	//FIXME next line is just for test porpouse, to delete
    	bundle.putString(Constants.BUNDLE_URL_KEY, " GetLoginHelper");
        return bundle;
    }

    @Override
    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
        // TODO Auto-generated method stub
        Log.i(TAG,"parseResponseBundle");
        return bundle;
    }
}
