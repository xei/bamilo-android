/**
 * 
 */
package pt.rocket.helpers;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.framework.utils.Constants;
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
public class GetChangePasswordHelper extends BaseHelper {
    
    private static String TAG = GetChangePasswordHelper.class.getSimpleName();
    
    private ArrayList<TeaserSpecification<ITargeting>> teasers = new ArrayList<TeaserSpecification<ITargeting>>();
    
    @Override
    public Bundle generateRequestBundle(Bundle args) {
        Bundle bundle = new Bundle();
//        bundle.putString(Constants.BUNDLE_URL_KEY, "http://www.linio.com.ve/mobileapi/main/getteasers/");
//        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.GET);
//        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(Constants.BUNDLE_MD5_KEY));
        return bundle;
    }

    @Override
    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
        // TODO Auto-generated method stub
    	Log.d(TAG, "parseResponseBundle GetTeasersHelper");
    	
    	
        try {// TODO add further object parsing possibilities : for example data
            // not being an array but a dictionary
           JSONArray data = jsonObject.getJSONArray(JSONConstants.JSON_DATA_TAG);
           int dataSize = data.length();
           
           for (int i = 0; i < dataSize; i++) {
               TeaserSpecification<ITargeting> teaser = (TeaserSpecification<ITargeting>) TeaserSpecification.parse(data.getJSONObject(i));
                
               teasers.add(teaser);

           }
           
           bundle.putParcelableArrayList(Constants.BUNDLE_RESPONSE_KEY, teasers);
           
           Log.i(TAG,"Teasers size "+teasers.size());
           
                     
       } catch (JSONException e) {
           // TODO Auto-generated catch block
           e.printStackTrace();
       }
    	
    	
    	
    	//FIXME next line is just for test porpouse, to delete
    	bundle.putString(Constants.BUNDLE_URL_KEY, " GetTeasersHelper");
        return bundle;
    }
    

    @Override
    public Bundle parseErrorBundle(Bundle bundle) {
        // TODO Auto-generated method stub
        android.util.Log.d("TRACK", "parseErrorBundle GetTeasersHelper");
     
        //FIXME next line is just for test porpouse, to delete
        bundle.putString(Constants.BUNDLE_URL_KEY, " GetTeasersHelper");
        return bundle;
    }

}