/**
 * @author Manuel Silva
 * 
 */
package pt.rocket.framework.testproject.helper;

import java.io.IOException;

import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;
import pt.rocket.framework.enums.RequestType;
import pt.rocket.framework.ErrorCode;
import pt.rocket.framework.testproject.objects.XMLObject;
import pt.rocket.framework.testproject.utils.XMLUtils;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.Utils;
import android.os.Bundle;
import android.util.Log;

import com.rocket.framework.testshell.test.R;

/**
 * Get Product Information helper
 * 
 * @author Manuel Silva
 * @modified Bruno Teixeira
 */
public class GetApiInfoHelper extends BaseHelper {
    
    private static String TAG = GetApiInfoHelper.class.getSimpleName();
    
    public static final String API_INFO_OUTDATEDSECTIONS = "outDatedSections";

    public String country = "";
    
    @Override
    public Bundle generateRequestBundle(Bundle args) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_URL_KEY, args.getString(BaseHelper.KEY_COUNTRY));
        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.GET);
        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(Constants.BUNDLE_MD5_KEY));
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_API_INFO);
        country = args.getString(KEY_COUNTRY_TAG);
        bundle.putString(KEY_COUNTRY_TAG, country);
        return bundle;
    }
    
    @Override
    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
        boolean validation = true;
        
        try {
            XMLObject responseRules = XMLUtils.xmlParser(mContext, R.xml.get_api_info);
            validation = XMLUtils.jsonObjectAssertion(jsonObject , responseRules);
            bundle.putString(Constants.BUNDLE_WRONG_PARAMETER_MESSAGE_KEY,XMLUtils.getMessage());
            bundle.putBoolean(Constants.BUNDLE_JSON_VALIDATION_KEY, validation);
            bundle.putString(KEY_COUNTRY_TAG, country);
            if(!validation)
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
        Log.i(TAG," Failed validation "+XMLUtils.getMessage());
        Log.i(TAG,  " failedParameterMessage ");
        bundle.putString(Constants.BUNDLE_WRONG_PARAMETER_MESSAGE_KEY, XMLUtils.getMessage());
        bundle.putSerializable(Constants.BUNDLE_ERROR_KEY, ErrorCode.ERROR_PARSING_SERVER_DATA);
        bundle.putString(KEY_COUNTRY_TAG, country);
        return bundle;
    }

    @Override
    public Bundle parseErrorBundle(Bundle bundle) {
    	bundle.putString(KEY_COUNTRY_TAG, country);
        return bundle;
    }
}
