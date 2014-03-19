/**
 * 
 */
package pt.rocket.testapp.helper;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//import de.akquinet.android.androlog.Log;

import pt.rocket.framework.enums.RequestType;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.Utils;
import pt.rocket.testapp.objects.Category;
import pt.rocket.testapp.objects.JSONConstants;

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
        try {// TODO add further object parsing possibilities : for example data
             // not being an array but a dictionary
        	android.util.Log.d("TRACK", "parseResponseBundle GetCategoriesHelper");
            JSONArray data = jsonObject.getJSONArray(JSONConstants.JSON_DATA_TAG);
            int dataSize = data.length();
            for (int i = 0; i < dataSize; i++) {
                Category category = new Category();
                category.initialize(data.getJSONObject(i));
                categoriesList.add(category);
            }
            
            Log.i(TAG,"Categories size => "+categoriesList.size());
            
            bundle.putParcelableArrayList(Constants.BUNDLE_RESPONSE_KEY, categoriesList);
            
            //FIXME next line is just for test porpouse, to delete
            bundle.putString(Constants.BUNDLE_URL_KEY, " GetCategories");
            
            
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return bundle;
    }

    @Override
    public Bundle parseErrorBundle(Bundle bundle) {
        // TODO Auto-generated method stub
    	android.util.Log.d("TRACK", "parseErrorBundle GetCategoriesHelper");
        //FIXME next line is just for test porpouse, to delete
        bundle.putString(Constants.BUNDLE_URL_KEY, " GetCategories");
        return bundle;
    }
}
