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
    
    @Override
    public Bundle generateRequestBundle(Bundle args) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_URL_KEY, EventType.GET_TEASERS_EVENT.action);
        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.GET);
        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(Constants.BUNDLE_MD5_KEY));
        return bundle;
    }

    @Override
    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
        // TODO Auto-generated method stub
    	Log.d(TAG, "parseResponseBundle GetTeasersHelper");
    	
    	
        try {
            JSONArray dataArray = jsonObject.getJSONArray(RestConstants.JSON_DATA_TAG);
            int dataArrayLenght = dataArray.length();
            ArrayList<Homepage> homepageSpecifications = new ArrayList<Homepage>();
            for (int i = 0; i < dataArrayLenght; ++i) {
                Homepage homepage = new Homepage();
                homepage.initialize(dataArray.getJSONObject(i));
                homepageSpecifications.add(homepage);
            }
           bundle.putParcelableArrayList(Constants.BUNDLE_RESPONSE_KEY, homepageSpecifications);
           
           Log.i(TAG,"Teasers size "+homepageSpecifications.size());
           
                     
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