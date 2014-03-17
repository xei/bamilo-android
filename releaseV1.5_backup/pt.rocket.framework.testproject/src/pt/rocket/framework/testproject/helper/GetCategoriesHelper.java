/**
 * 
 */
package pt.rocket.framework.testproject.helper;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import com.rocket.framework.testshell.test.R;



import pt.rocket.framework.enums.RequestType;
import pt.rocket.framework.errormanager.ErrorCode;
import pt.rocket.framework.testproject.objects.Category;
import pt.rocket.framework.testproject.objects.JSONConstants;
import pt.rocket.framework.testproject.objects.XMLObject;
import pt.rocket.framework.testproject.utils.XMLUtils;
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
public class GetCategoriesHelper extends BaseHelper {
    
    private static String TAG = GetCategoriesHelper.class.getSimpleName();
    ArrayList<Category> categoriesList= new ArrayList<Category>();

    @Override
    public Bundle generateRequestBundle() {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_URL_KEY, "http://www.linio.com.ve/mobileapi/catalog/categories/");
        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.CATEGORIES_PRIORITY);
        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.GET);
        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(Constants.BUNDLE_MD5_KEY));
        return bundle;
    }

    @Override
    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
        boolean validation = true;
        
        
        try {
            XMLObject responseRules = XMLUtils.xmlParser(mContext, R.xml.get_category_rules);
            validation = XMLUtils.jsonObjectAssertion(jsonObject , responseRules);
            bundle.putBoolean(Constants.BUNDLE_JSON_VALIDATION_KEY, validation);
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
        Log.i(TAG," Failed validation ");
        Log.i(TAG,  " failedParameterMessage "+XMLUtils.getMessage());
        bundle.putString(Constants.BUNDLE_WRONG_PARAMETER_MESSAGE_KEY, XMLUtils.getMessage());
        return bundle;
    }

    @Override
    public Bundle parseErrorBundle(Bundle bundle) {
        // TODO Auto-generated method stub
        return null;
    }
}
