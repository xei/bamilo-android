/**
 * 
 */
package pt.rocket.framework.testproject.helper;

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
public class GetRatingsFormHelper extends BaseHelper {

	@Override
	public Bundle generateRequestBundle() {
		Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_URL_KEY, "http://www.linio.com.ve/mobileapi/forms/rating/");
        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.GET);
        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(Constants.BUNDLE_MD5_KEY));
        return bundle;
	}


	@Override
	public Bundle parseErrorBundle(Bundle bundle) {
		// TODO Auto-generated method stub
		return bundle;
	}


	@Override
	public Bundle parseResponseBundle(Bundle bundle,JSONObject jsonObject) {
		// TODO Auto-generated method stub
		return bundle;
	}


    @Override
    public Bundle parseResponseErrorBundle(Bundle bundle) {
        // TODO Auto-generated method stub
        return bundle;
    }
}
