//package com.mobile.helpers.configs;
//
//import android.os.Bundle;
//
//import com.mobile.components.NavigationListComponent;
//import com.mobile.framework.enums.RequestType;
//import com.mobile.framework.rest.RestConstants;
//import com.mobile.framework.utils.Constants;
//import com.mobile.framework.utils.EventType;
//import com.mobile.framework.utils.Utils;
//import com.mobile.helpers.BaseHelper;
//import com.mobile.helpers.HelperPriorityConfiguration;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//
//import de.akquinet.android.androlog.Log;
//
//public class NavigationListHelper extends BaseHelper {
//
//    private static final String TAG = NavigationListHelper.class.getSimpleName();
//
//    private static final EventType EVENT_TYPE = EventType.GET_NAVIGATION_LIST_COMPONENTS_EVENT;
//
//    private static final String SEARCH_TAG = "Search";
//
//    private static final String BASKET_TAG = "Basket";
//
//    private static final String CATEGORIES_TAG = "Categories";
//
//    private boolean isToIgnoreSearch = true;
//
//    private boolean isToIgnoreBasket = true;
//
//    private boolean isToIgnoreCategories = true;
//
//    /*
//     * (non-Javadoc)
//     * @see com.mobile.helpers.BaseHelper#generateRequestBundle(android.os.Bundle)
//     */
//    @Override
//    public Bundle generateRequestBundle(Bundle args) {
//        Bundle bundle = new Bundle();
//        bundle.putString(Constants.BUNDLE_URL_KEY, EventType.GET_NAVIGATION_LIST_COMPONENTS_EVENT.action);
//        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
//        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.GET);
//        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(EVENT_TYPE.name()));
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_NAVIGATION_LIST_COMPONENTS_EVENT);
//        return bundle;
//    }
//
//    /*
//     * (non-Javadoc)
//     * @see com.mobile.helpers.BaseHelper#parseResponseBundle(android.os.Bundle, org.json.JSONObject)
//     */
//    @Override
//    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
//        try {
//            ArrayList<NavigationListComponent> components = new ArrayList<>();
//            JSONArray dataArray = jsonObject.getJSONArray(RestConstants.JSON_DATA_TAG);
//            NavigationListComponent component;
//            int dataArrayLenght = dataArray.length();
//
//            for (int i = 0; i < dataArrayLenght; ++i) {
//
//                component = new NavigationListComponent();
//                component.initialize(dataArray.getJSONObject(i));
//
//                // Don't add "Search" to the Navigation List
//                if (isToIgnoreSearch) {
//                    if (SEARCH_TAG.equalsIgnoreCase(component.getElementText())) {
//                        continue;
//                    }
//                }
//
//                // Don't add "Basket" to the Navigation List
//                if (isToIgnoreBasket) {
//                    if (BASKET_TAG.equalsIgnoreCase(component.getElementText())) {
//                        continue;
//                    }
//                }
//
//                // Don't add "Categories" to the Navigation List
//                if (isToIgnoreCategories) {
//                    if (CATEGORIES_TAG.equalsIgnoreCase(component.getElementText())) {
//                        continue;
//                    }
//                }
//
//                components.add(component);
//            }
//
//            components.add(new NavigationListComponent(0, null, "loginout", null));
//
//            bundle.putParcelableArrayList(Constants.BUNDLE_RESPONSE_KEY, components);
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_NAVIGATION_LIST_COMPONENTS_EVENT);
//        return bundle;
//    }
//
//    /*
//     * (non-Javadoc)
//     * @see com.mobile.helpers.BaseHelper#parseErrorBundle(android.os.Bundle)
//     */
//    @Override
//    public Bundle parseErrorBundle(Bundle bundle) {
//        Log.d(TAG, "parseErrorBundle GetNAvListHelper");
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_NAVIGATION_LIST_COMPONENTS_EVENT);
//        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
//        return bundle;
//    }
//
//    /*
//     * (non-Javadoc)
//     * @see com.mobile.helpers.BaseHelper#parseResponseErrorBundle(android.os.Bundle)
//     */
//    @Override
//    public Bundle parseResponseErrorBundle(Bundle bundle) {
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_NAVIGATION_LIST_COMPONENTS_EVENT);
//        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
//        return bundle;
//    }
//
//    @Override
//    public Bundle parseResponseErrorBundle(Bundle bundle, JSONObject jsonObject) {
//        return parseResponseErrorBundle(bundle);
//    }
//}
