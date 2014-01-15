package pt.rocket.helpers;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.akquinet.android.androlog.Log;

import pt.rocket.framework.components.NavigationListComponent;
import pt.rocket.framework.enums.RequestType;
import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.Utils;


import android.os.Bundle;

public class NavigationListHelper extends BaseHelper {

    private static final String TAG = NavigationListHelper.class.getSimpleName();
    
    @Override
    public Bundle generateRequestBundle(Bundle args) {
        // TODO Auto-generated method stub
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_URL_KEY, EventType.GET_NAVIGATION_LIST_COMPONENTS_EVENT.action);
        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.GET);
        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(Constants.BUNDLE_MD5_KEY));
        return bundle;
    }

    @Override
    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
        try {
            ArrayList<NavigationListComponent> components = new ArrayList<NavigationListComponent>();
            JSONArray dataArray = jsonObject
                    .getJSONArray(RestConstants.JSON_DATA_TAG);
            NavigationListComponent component;
            int dataArrayLenght = dataArray.length();
            for (int i = 0; i < dataArrayLenght; ++i) {
                Log.i(TAG, "code1 parsing components ...");
                component = new NavigationListComponent();
                component.initialize(dataArray.getJSONObject(i));
                components.add(component);
                Log.i(TAG, "code1 parsing component : "+component.getElementText());
            } 

            components.add(new NavigationListComponent(0, null, "loginout", null));
            
            bundle.putParcelableArrayList(Constants.BUNDLE_RESPONSE_KEY, components);
            
        } catch (JSONException e) {
            e.printStackTrace();
        }
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_NAVIGATION_LIST_COMPONENTS_EVENT);
        return bundle;
    }

    
    @Override
    public Bundle parseErrorBundle(Bundle bundle) {
        android.util.Log.d(TAG, "parseErrorBundle GetNAvListHelper");
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_NAVIGATION_LIST_COMPONENTS_EVENT);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }

    @Override
    public Bundle parseResponseErrorBundle(Bundle bundle) {
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_NAVIGATION_LIST_COMPONENTS_EVENT);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }


}
