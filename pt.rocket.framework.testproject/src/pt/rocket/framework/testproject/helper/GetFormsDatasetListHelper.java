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
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.Utils;
import android.os.Bundle;

import com.rocket.framework.testshell.test.R;

import de.akquinet.android.androlog.Log;

/**
 * Example helper
 * 
 * @author Guilherme Silva
 * @modified Manuel Silva
 */
public class GetFormsDatasetListHelper extends BaseHelper {

	private static String TAG = GetFormsDatasetListHelper.class.getSimpleName();

	public static final String URL = "url";
	public static final String KEY = "key";
	private String JSON_ID_TAG;

	@Override
	public Bundle generateRequestBundle(Bundle args) {
		Bundle bundle = new Bundle();
		JSON_ID_TAG = args.getString(KEY);
		bundle.putString(Constants.BUNDLE_URL_KEY, args.getString(URL));
		bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.GET);
		bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(Constants.BUNDLE_MD5_KEY));
		bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_FORMS_DATASET_LIST_EVENT);
		return bundle;
	}

	@Override
	public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
		boolean validation = true;

		try {
			XMLObject responseRules = XMLUtils.xmlParser(mContext, R.xml.get_category_rules);
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
		return null;
	}
}