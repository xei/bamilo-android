/**
 * 
 */
package pt.rocket.testapp.helper;

import org.json.JSONObject;

import pt.rocket.framework.enums.RequestType;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.Utils;

import android.os.Bundle;

/**
 * Example helper
 * 
 * @author Guilherme Silva
 * 
 */
public class GetLoginFormHelper extends BaseHelper {

    @Override
    public Bundle generateRequestBundle() {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_URL_KEY, "http://www.linio.com.ve/mobileapi/forms/login/");
        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.GET);
        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(Constants.BUNDLE_MD5_KEY));
        return bundle;
    }

    @Override
    public Bundle parseErrorBundle(Bundle bundle) {
        // TODO Auto-generated method stub
    	android.util.Log.d("TRACK", "parseErrorBundle GetLoginFormHelper");
    	//FIXME next line is just for test porpouse, to delete
    	bundle.putString(Constants.BUNDLE_URL_KEY, " GetLoginForm");
        return bundle;
    }

    @Override
    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
        // TODO Auto-generated method stub
    	android.util.Log.d("TRACK", "parseResponseBundle GetLoginFormHelper");
    	//FIXME next line is just for test porpouse, to delete
    	bundle.putString(Constants.BUNDLE_URL_KEY, " GetLoginForm");
        return bundle;
    }
}
