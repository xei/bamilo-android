/**
 * 
 */
package com.mobile.helpers.products;

import android.content.ContentValues;
import android.os.Bundle;

import com.mobile.framework.enums.RequestType;
import com.mobile.framework.objects.CompleteProduct;
import com.mobile.framework.rest.RestConstants;
import com.mobile.framework.utils.Constants;
import com.mobile.framework.utils.EventType;
import com.mobile.framework.utils.Utils;
import com.mobile.helpers.BaseHelper;
import com.mobile.helpers.HelperPriorityConfiguration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.akquinet.android.androlog.Log;

/**
 * Validate one or more products on the API and actualize their information
 * 
 * @author Manuel Silva
 * @modified sergiopereira
 * 
 */
public class ValidateProductHelper extends BaseHelper {
    
    private static String TAG = ValidateProductHelper.class.getSimpleName();
    
    private static final EventType EVENT_TYPE = EventType.VALIDATE_PRODUCTS;

    public static final String VALIDATE_PRODUCTS_CONTENT_VALUES = "contentValues";

    public static final String VALIDATE_PRODUCTS_KEY = "products[]";

    @Override
    public Bundle generateRequestBundle(Bundle args) {

        ContentValues values = new ContentValues();
        values = args.getParcelable(VALIDATE_PRODUCTS_CONTENT_VALUES);

        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_URL_KEY, EVENT_TYPE.action);
        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.POST);
        bundle.putParcelable(Constants.BUNDLE_FORM_DATA_KEY, values);
        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(EVENT_TYPE.name()));
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.VALIDATE_PRODUCTS);
        return bundle;
    }
    
    @Override
    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
        Log.d(TAG, "parseResponseBundle GetValidateProductsHelper");

       try {
           boolean status = false;
           Log.d(TAG, ":" + jsonObject.toString(4));

           JSONArray validProductsArray = jsonObject.optJSONArray(RestConstants.JSON_VALID_TAG);
           if(validProductsArray != null){
               ArrayList<CompleteProduct> validProducts = new ArrayList<>();
               if(validProductsArray.length() > 0){
                   for (int i = 0; i < validProductsArray.length() ; i++) {
                       CompleteProduct product = new CompleteProduct();
                       status = product.initialize(validProductsArray.getJSONObject(i));
                       
                       // Validate product initialization
//                       if(!status){
//                           return parseErrorBundle(bundle);
//                       }
                       validProducts.add(product);
                   }
               }
               bundle.putParcelableArrayList(Constants.BUNDLE_RESPONSE_KEY, validProducts);
               bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.VALIDATE_PRODUCTS);
           }

       }catch (JSONException e){
           e.printStackTrace();
       }

        return bundle;
    }

    @Override
    public Bundle parseErrorBundle(Bundle bundle) {
        Log.d(TAG, "parseErrorBundle GetValidateProductsHelper");
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.VALIDATE_PRODUCTS);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }

    @Override
    public Bundle parseResponseErrorBundle(Bundle bundle) {
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.VALIDATE_PRODUCTS);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }
    
    @Override
    public Bundle parseResponseErrorBundle(Bundle bundle, JSONObject jsonObject) {
        return parseResponseErrorBundle(bundle);
    }
}
