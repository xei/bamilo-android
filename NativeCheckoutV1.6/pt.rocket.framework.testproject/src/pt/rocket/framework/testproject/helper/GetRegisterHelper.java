/**
 * 
 */
package pt.rocket.framework.testproject.helper;

import java.io.IOException;

import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import pt.rocket.framework.enums.RequestType;
import pt.rocket.framework.testproject.objects.XMLObject;
import pt.rocket.framework.testproject.utils.XMLUtils;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.CustomerUtils;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.Utils;
import android.content.ContentValues;
import android.os.Bundle;
import android.util.Log;

import com.rocket.framework.testshell.test.R;

/**
 * Example helper
 * 
 * @author Guilherme Silva
 * 
 */
public class GetRegisterHelper extends BaseHelper {
	private static String TAG = GetRegisterHelper.class.getSimpleName();
	public static final String REGISTER_CONTENT_VALUES = "contentValues";
	boolean saveCredentials = true;
	ContentValues contentValues;

	@Override
	public Bundle generateRequestBundle(Bundle args) {
		Bundle bundle = new Bundle();
		contentValues = args.getParcelable(REGISTER_CONTENT_VALUES);
		contentValues.put(CustomerUtils.INTERNAL_AUTOLOGIN_FLAG, true);
		bundle.putString(Constants.BUNDLE_URL_KEY, args.getString(KEY_COUNTRY));
		bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.POST);
		bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.REGISTER_ACCOUNT_EVENT);
		bundle.putParcelable(Constants.BUNDLE_FORM_DATA_KEY, contentValues);
		bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(Constants.BUNDLE_MD5_KEY));
		return bundle;
	}

	@Override
	public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
		boolean validation = true;

		try {
			XMLObject responseRules = XMLUtils.xmlParser(mContext, R.xml.get_register_rules);
			validation = XMLUtils.jsonObjectAssertion(jsonObject, responseRules);
			bundle.putBoolean(Constants.BUNDLE_JSON_VALIDATION_KEY, validation);
			if (!validation)
				return parseResponseErrorBundle(bundle);
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return bundle;
	}

	@Override
	public Bundle parseResponseErrorBundle(Bundle bundle) {
		// TODO Auto-generated method stub
		Log.i(TAG, " Failed validation ");
		Log.i(TAG, " failedParameterMessage " + XMLUtils.getMessage());
		bundle.putString(Constants.BUNDLE_WRONG_PARAMETER_MESSAGE_KEY, XMLUtils.getMessage());
		return bundle;
	}

	@Override
	public Bundle parseErrorBundle(Bundle bundle) {
		// TODO Auto-generated method stub
		return bundle;
	}
}
