package pt.rocket.helpers;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.framework.enums.RequestType;
import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.Utils;
import pt.rocket.pojo.NavigationListComponent;

import android.os.Bundle;

public class NavigationListHelper extends BaseHelper {

    private ArrayList<NavigationListComponent> navComponentList;
    @Override
    public Bundle generateRequestBundle(Bundle args) {
        // TODO Auto-generated method stub
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_URL_KEY, EventType.GET_NAVIGATION_LIST_COMPONENTS_EVENT.action);
        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_NOT_PRIORITARY);
        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.GET);
        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(Constants.BUNDLE_MD5_KEY));
        return bundle;
    }

    @Override
    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
        // TODO Auto-generated method stub
        navComponentList = new ArrayList<NavigationListComponent>();
        try {
            ArrayList<NavigationListComponent> components = new ArrayList<NavigationListComponent>();
            JSONArray dataArray = jsonObject
                    .getJSONArray(RestConstants.JSON_DATA_TAG);
            NavigationListComponent component;
            int dataArrayLenght = dataArray.length();
            for (int i = 0; i < dataArrayLenght; ++i) {
                component = new NavigationListComponent();
                component.initialize(dataArray.getJSONObject(i));
                components.add(component);
            }

            components.add(new NavigationListComponent(0, null, "loginout", null));
            
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
