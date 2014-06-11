package pt.rocket.helpers;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.framework.components.NavigationListComponent;
import pt.rocket.framework.enums.RequestType;
import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.Utils;
import android.os.Bundle;

public class NavigationListHelper extends BaseHelper {

    private static final String TAG = NavigationListHelper.class.getSimpleName();
    
    private static final String SEARCH_TAG = "Search";
    
    private static final String BASKET_TAG = "Basket";
    
    private static final String CATEGORIES_TAG = "Categories";

    private boolean isToIgnoreSearch = true;
    
    private boolean isToIgnoreBasket = true;
    
    private boolean isToIgnoreCategories = true;
    
    /*
     * (non-Javadoc)
     * @see pt.rocket.helpers.BaseHelper#generateRequestBundle(android.os.Bundle)
     */
    @Override
    public Bundle generateRequestBundle(Bundle args) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_URL_KEY, EventType.GET_NAVIGATION_LIST_COMPONENTS_EVENT.action);
        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.GET);
        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(Constants.BUNDLE_MD5_KEY));
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_NAVIGATION_LIST_COMPONENTS_EVENT);
        return bundle;
    }

    /*
     * (non-Javadoc)
     * @see pt.rocket.helpers.BaseHelper#parseResponseBundle(android.os.Bundle, org.json.JSONObject)
     */
    @Override
    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
        try {
            ArrayList<NavigationListComponent> components = new ArrayList<NavigationListComponent>();
            JSONArray dataArray = jsonObject.getJSONArray(RestConstants.JSON_DATA_TAG);
            NavigationListComponent component;
            int dataArrayLenght = dataArray.length();
            
            for (int i = 0; i < dataArrayLenght; ++i) {
                
                component = new NavigationListComponent();
                component.initialize(dataArray.getJSONObject(i));

                // Don't add "Search" to the Navigation List
                if (isToIgnoreSearch && component != null) {
                    if (SEARCH_TAG.equalsIgnoreCase(component.getElementText())) {
                        continue;
                    }
                }
                
                // Don't add "Basket" to the Navigation List
                if (isToIgnoreBasket && component != null) {
                    if (BASKET_TAG.equalsIgnoreCase(component.getElementText())) {
                        continue;
                    }
                }
                
                // Don't add "Categories" to the Navigation List
                if (isToIgnoreCategories && component != null) {
                    if (CATEGORIES_TAG.equalsIgnoreCase(component.getElementText())) {
                        continue;
                    }
                }
                
                components.add(component);
            } 

            components.add(new NavigationListComponent(0, null, "loginout", null));
            
            bundle.putParcelableArrayList(Constants.BUNDLE_RESPONSE_KEY, components);
            
        } catch (JSONException e) {
            e.printStackTrace();
        }
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_NAVIGATION_LIST_COMPONENTS_EVENT);
        return bundle;
    }

    /*
     * (non-Javadoc)
     * @see pt.rocket.helpers.BaseHelper#parseErrorBundle(android.os.Bundle)
     */
    @Override
    public Bundle parseErrorBundle(Bundle bundle) {
        android.util.Log.d(TAG, "parseErrorBundle GetNAvListHelper");
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_NAVIGATION_LIST_COMPONENTS_EVENT);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }

    /*
     * (non-Javadoc)
     * @see pt.rocket.helpers.BaseHelper#parseResponseErrorBundle(android.os.Bundle)
     */
    @Override
    public Bundle parseResponseErrorBundle(Bundle bundle) {
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_NAVIGATION_LIST_COMPONENTS_EVENT);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }


}
