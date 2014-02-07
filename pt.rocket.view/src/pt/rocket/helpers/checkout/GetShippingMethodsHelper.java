/**
 * 
 */
package pt.rocket.helpers.checkout;

import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.forms.Form;
import pt.rocket.framework.enums.RequestType;
import pt.rocket.framework.objects.ShoppingCart;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.Utils;
import pt.rocket.helpers.BaseHelper;
import pt.rocket.helpers.HelperPriorityConfiguration;
import pt.rocket.app.JumiaApplication;
import android.os.Bundle;
import android.util.Log;

/**
 * Helper used to get the shipping methods 
 * @author sergiopereira
 */
public class GetShippingMethodsHelper extends BaseHelper {
    
    private static String TAG = GetShippingMethodsHelper.class.getSimpleName();
    
    private static final EventType type = EventType.GET_SHIPPING_METHODS_EVENT;
    
//    {
//        "success": true,
//        "messages": {
//            "error": [
//                "FORM_DATA_NOT_TRANSFERRED"
//            ]
//        },
//        "session": {
//            "id": "c9n6atsld2ho4849oipg55boj6",
//            "expire": null,
//            "YII_CSRF_TOKEN": "140091d1d3703d5f3db15d402ff3692451487016"
//        },
//        "metadata": {
//            "shippingMethodForm": {
//                "id": "shippingMethodForm",
//                "name": "shippingMethodForm",
//                "method": "post",
//                "action": "https://alice-staging.jumia.co.ke/mobapi/multistep/shippingmethod/",
//                "fields": [
//                    {
//                        "key": "shipping_method",
//                        "restrictions": [],
//                        "options": {
//                            "free": [],
//                            "PickupStation": [],
//                            "UniversalShippingMatrix": []
//                        },
//                        "type": "radio",
//                        "rules": {
//                            "required": true
//                        },
//                        "value": "free",
//                        "label": "Choose Your Shipping Method",
//                        "name": "shippingMethodForm[shipping_method]",
//                        "id": "shippingMethodForm_shipping_method"
//                    },
//                    {
//                        "key": "pickup_station",
//                        "restrictions": [],
//                        "options": {
//                            "1": {
//                                "id_pickupstation": "1",
//                                "name": "Jumia Office",
//                                "pickup_id": "Jumia Office KE",
//                                "image": "[MEDIA_BASE_URL]/cms/icons/deliveryIcon1_1.png",
//                                "address": "Jumia Office Nairobi",
//                                "place": "City Center",
//                                "city": "Nairobi",
//                                "opening_hours": "9.00am - 5.00pm",
//                                "id_pickupstation_region": "15",
//                                "payment_method": [
//                                    "CashOnDelivery"
//                                ],
//                                "regions": {
//                                    "233": "Nairobi"
//                                }
//                            },
//                            "3": {
//                                "id_pickupstation": "3",
//                                "name": "Kiboga",
//                                "pickup_id": "Kiboga",
//                                "image": "[MEDIA_BASE_URL]/cms/icons/deliveryIcon1_1.png",
//                                "address": "Kiboga Main Street",
//                                "place": "Kiboga Center",
//                                "city": "Badjoraz",
//                                "opening_hours": "9:00am-3:00pm",
//                                "id_pickupstation_region": "24",
//                                "payment_method": [
//                                    "CashOnDelivery"
//                                ],
//                                "regions": {
//                                    "306": "Kiboga"
//                                }
//                            },
//                            "4": {
//                                "id_pickupstation": "4",
//                                "name": "Office Mombasa",
//                                "pickup_id": "Office Mombasa",
//                                "image": "[MEDIA_BASE_URL]/cms/google_plus_jumia_kenya.jpg",
//                                "address": "Street of Mombasa",
//                                "place": "XPTO",
//                                "city": "PORTO",
//                                "opening_hours": "09h to 18h",
//                                "id_pickupstation_region": "26",
//                                "payment_method": [
//                                    "CashOnDelivery"
//                                ],
//                                "regions": {
//                                    "235": "Coast"
//                                }
//                            }
//                        },
//                        "type": "radio",
//                        "rules": {
//                            "required": {
//                                "on": "PickupStation"
//                            }
//                        },
//                        "value": "",
//                        "label": "Pickup Station",
//                        "name": "shippingMethodForm[pickup_station]",
//                        "id": "shippingMethodForm_pickup_station"
//                    },
//                    {
//                        "key": "pickup_station_customer_address_region",
//                        "rules": {
//                            "required": {
//                                "on": "PickupStation"
//                            }
//                        },
//                        "value": "",
//                        "label": "Regions",
//                        "name": "shippingMethodForm[pickup_station_customer_address_region]",
//                        "id": "shippingMethodForm_pickup_station_customer_address_region"
//                    }
//                ]
//            },
//            "cart": {
//                "cartValue": " KSh  126,999 ",
//                "couponMoneyValue": "0",
//                "cartCount": 1,
//                "vat_value": " KSh  17,517 ",
//                "shipping_value": null,
//                "cartItems": {
//                    "DE168ELAA4SMNAFAMZ-6944": {
//                        "salesOrderItem": {},
//                        "max_quantity": 10,
//                        "quantity": 1,
//                        "delivery_time_info": "Delivery in 2-4 business days",
//                        "cart_rule_discount": 0,
//                        "cart_rule_display_names": [],
//                        "image": "http://static-staging.jumia.co.ke/p/dell-1336-4126-1-cart.jpg",
//                        "stock": "49",
//                        "unit_price": "135000.00",
//                        "original_price": null,
//                        "specialPrice": "126999.00",
//                        "configId": "6214",
//                        "configSku": "DE168ELAA4SMNAFAMZ",
//                        "name": "Latitude E6330,13.0\", Windows 8, Intel Core i5, 4GB RAM, 500GB HDD - Black",
//                        "uniqueAttributes": [
//                            {
//                                "label": "Variation",
//                                "value": ","
//                            }
//                        ],
//                        "variation": ",",
//                        "url": "http://alice-staging.jumia.co.ke/mobapi/Latitude-E6330%2C13.0%C2%9D%2C-Windows-8%2C-Intel-Core-i5%2C-4GB-RAM%2C-500GB-HDD---Black-6214.html"
//                    }
//                },
//                "messages": {
//                    "Shipping": "Free",
//                    "VAT": " KSh  17,517 "
//                }
//            }
//        }
//    }
    
    
    
    
    /*
     * (non-Javadoc)
     * @see pt.rocket.helpers.BaseHelper#generateRequestBundle(android.os.Bundle)
     */
    @Override
    public Bundle generateRequestBundle(Bundle args) {
        Log.d(TAG, "REQUEST");
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_URL_KEY, type.action);
        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.POST);
        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, type);
        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(Constants.BUNDLE_MD5_KEY));
        return bundle;
    }
   
    /*
     * (non-Javadoc)
     * @see pt.rocket.helpers.BaseHelper#parseResponseBundle(android.os.Bundle, org.json.JSONObject)
     */
    @Override
    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
        Log.i(TAG, "PARSE BUNDLE");

        try {
        
            // Get shipping methods
            JSONObject formJSON = jsonObject.getJSONObject("shippingMethodForm");
            Log.d(TAG, "FORM JSON: " + formJSON.toString());
            Form form = new Form();
            if (!form.initialize(formJSON)) Log.e(TAG, "Error initializing the form using the data");
            
            // Get cart
            JSONObject cartJSON = jsonObject.getJSONObject("cart");
            Log.d(TAG, "CAT JSON: " + cartJSON.toString());
//            ShoppingCart cart = new ShoppingCart(JumiaApplication.INSTANCE.getItemSimpleDataRegistry());
//            cart.initialize(cartJSON);
            
            bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, form);
        
        } catch (JSONException e) {
            Log.w(TAG, "PARSE EXCEPTION: " , e);
            return parseErrorBundle(bundle);
        }
        
        Log.i(TAG, "PARSE JSON: SUCCESS");
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, type);
        return bundle;
    }
    
    /*
     * (non-Javadoc)
     * @see pt.rocket.helpers.BaseHelper#parseErrorBundle(android.os.Bundle)
     */
    @Override
    public Bundle parseErrorBundle(Bundle bundle) {
        android.util.Log.d(TAG, "PARSE ERROR BUNDLE");
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, type);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }

    /*
     * (non-Javadoc)
     * @see pt.rocket.helpers.BaseHelper#parseResponseErrorBundle(android.os.Bundle)
     */
    @Override
    public Bundle parseResponseErrorBundle(Bundle bundle) {
        android.util.Log.d(TAG, "PARSE RESPONSE ERROR BUNDLE");
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, type);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }
}
