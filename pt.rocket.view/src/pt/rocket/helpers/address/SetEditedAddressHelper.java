/**
 * 
 */
package pt.rocket.helpers.address;

import org.json.JSONObject;

import pt.rocket.framework.enums.RequestType;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.Utils;
import pt.rocket.helpers.BaseHelper;
import pt.rocket.helpers.HelperPriorityConfiguration;
import android.os.Bundle;
import android.os.Parcelable;
import de.akquinet.android.androlog.Log;

/**
 * Helper used to create an address 
 * @author sergiopereira
 */
public class SetEditedAddressHelper extends BaseHelper {
    
    private static String TAG = SetEditedAddressHelper.class.getSimpleName();
    
    public static final String FORM_CONTENT_VALUES = "form_content_values";

    private Parcelable contentValues;
    
    private static final EventType EVENT_TYPE = EventType.EDIT_ADDRESS_EVENT;
    
    // Alice_Module_Customer_Model_AddressForm[address_id]
    // Alice_Module_Customer_Model_AddressForm[first_name]
    // Alice_Module_Customer_Model_AddressForm[last_name]
    // Alice_Module_Customer_Model_AddressForm[address1]
    // Alice_Module_Customer_Model_AddressForm[address2]
    // Alice_Module_Customer_Model_AddressForm[city]
    // Alice_Module_Customer_Model_AddressForm[phone]
    // Alice_Module_Customer_Model_AddressForm[fk_customer_address_region]
    // Alice_Module_Customer_Model_AddressForm[fk_customer_address_city]
    // Alice_Module_Customer_Model_AddressForm[country]
            
    /*
     * (non-Javadoc)
     * @see pt.rocket.helpers.BaseHelper#generateRequestBundle(android.os.Bundle)
     */
    @Override
    public Bundle generateRequestBundle(Bundle args) {
        Log.d(TAG, "REQUEST");
        Bundle bundle = new Bundle();
        contentValues = args.getParcelable(FORM_CONTENT_VALUES);
        bundle.putString(Constants.BUNDLE_URL_KEY, EVENT_TYPE.action);
        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.POST);
        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EVENT_TYPE);
        bundle.putParcelable(Constants.BUNDLE_FORM_DATA_KEY, contentValues);
        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(EVENT_TYPE.name()));
        return bundle;
    }
   
    /*
     * (non-Javadoc)
     * @see pt.rocket.helpers.BaseHelper#parseResponseBundle(android.os.Bundle, org.json.JSONObject)
     */
    @Override
    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
        Log.d(TAG, "PARSE BUNDLE: " + jsonObject);
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EVENT_TYPE);
        return bundle;
    }
    
    /*
     * (non-Javadoc)
     * @see pt.rocket.helpers.BaseHelper#parseErrorBundle(android.os.Bundle)
     */
    @Override
    public Bundle parseErrorBundle(Bundle bundle) {
        Log.d(TAG, "PARSE ERROR BUNDLE");
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EVENT_TYPE);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }

    /*
     * (non-Javadoc)
     * @see pt.rocket.helpers.BaseHelper#parseResponseErrorBundle(android.os.Bundle)
     */
    @Override
    public Bundle parseResponseErrorBundle(Bundle bundle) {
        Log.d(TAG, "PARSE RESPONSE BUNDLE");
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EVENT_TYPE);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }
    
    @Override
    public Bundle parseResponseErrorBundle(Bundle bundle, JSONObject jsonObject) {
        return parseResponseErrorBundle(bundle);
    }
}
