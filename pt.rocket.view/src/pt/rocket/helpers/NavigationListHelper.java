package pt.rocket.helpers;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.framework.enums.RequestType;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.Utils;
import pt.rocket.pojo.NavigationListComponent;
import pt.rocket.utils.JSONConstants;

import android.os.Bundle;

public class NavigationListHelper extends BaseHelper {

    private ArrayList<NavigationListComponent> navComponentList;
    @Override
    public Bundle generateRequestBundle() {
        // TODO Auto-generated method stub
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_URL_KEY, "http://www.linio.com.ve/mobileapi/main/getstatic?key=mobile_navigation");
        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.CATEGORIES_PRIORITY);
        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.GET);
        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(Constants.BUNDLE_MD5_KEY));
        return bundle;
    }

    @Override
    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
        // TODO Auto-generated method stub
        navComponentList = new ArrayList<NavigationListComponent>();
        try {
            JSONArray data =(JSONArray) jsonObject.getJSONArray(JSONConstants.JSON_DATA_TAG);
            int size = data.length();
            
            for (int i=0;i<size;i++){
                NavigationListComponent navComponent = new NavigationListComponent();
                navComponent.initialize(data.getJSONObject(i));
                navComponentList.add(navComponent);
            }
            
            bundle.putParcelableArrayList(Constants.BUNDLE_RESPONSE_KEY, navComponentList);
            
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return bundle;
    }

    @Override
    public Bundle parseErrorBundle(Bundle bundle) {
        // TODO Auto-generated method stub
        return bundle;
    }

}
