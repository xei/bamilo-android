/**
 * 
 */
package com.mobile.helpers.checkout;

import android.os.Bundle;

import com.mobile.app.JumiaApplication;
import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.objects.checkout.CheckoutFinish;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.EventTask;
import com.mobile.newFramework.utils.EventType;

import java.util.HashMap;
import java.util.Map;

/**
 * Helper used to ...
 * @author sergiopereira
 */
public class CheckoutFinishHelper extends SuperBaseHelper {
    
    private static String TAG = CheckoutFinishHelper.class.getSimpleName();
    
    private static final String PAYMENT_FORM = "payment_form";

    public static final String USER_AGENT = "user_agent";

    @Override
    public EventType getEventType() {
        return EventType.CHECKOUT_FINISH_EVENT;
    }

    @Override
    protected EventTask setEventTask() {
        return EventTask.SMALL_TASK;
    }

    @Override
    protected RequestBundle createRequest(Bundle args) {
        return super.createRequest(args);
    }

    @Override
    protected Map<String, String> getRequestData(Bundle args) {
        Map<String, String> data = new HashMap<>();
        data.put("app", "android");
        data.put("customer_device", args.getString(USER_AGENT));
        return data;
    }

    @Override
    protected void onRequest(RequestBundle requestBundle) {
//        new CheckoutFinishOrder(requestBundle, this).execute();
        new BaseRequest(requestBundle, this).execute(AigApiInterface.checkoutFinish);
    }

    @Override
    public void createSuccessBundleParams(BaseResponse baseResponse, Bundle bundle) {
        super.createSuccessBundleParams(baseResponse, bundle);
        CheckoutFinish checkoutFinish = (CheckoutFinish)baseResponse.getMetadata().getData();
        bundle.putString(Constants.BUNDLE_RESPONSE_KEY, checkoutFinish.getOrderNumber());
        bundle.putParcelable(PAYMENT_FORM, checkoutFinish.getPaymentMethodForm());

        // TODO move to observable
        JumiaApplication.INSTANCE.setPaymentMethodForm(checkoutFinish.getPaymentMethodForm());
        JumiaApplication.INSTANCE.getPaymentMethodForm().setOrderNumber(checkoutFinish.getOrderNumber());
    }

//    @Override
//    public void onRequestComplete(BaseResponse baseResponse) {
//        Print.i(TAG, "########### ON REQUEST COMPLETE: " + baseResponse.hadSuccess());
//        // Save customer
////        Customer customer = (Customer) baseResponse.getMetadata().getData();
//        SuperCheckoutFinish checkoutFinish = (SuperCheckoutFinish)baseResponse.getMetadata().getData();
//        JumiaApplication.INSTANCE.setPaymentMethodForm(checkoutFinish.getPaymentMethodForm());
//        JumiaApplication.INSTANCE.getPaymentMethodForm().setOrderNumber(checkoutFinish.getOrderNumber());

//        // Parse the response
//        try {
//            // Get order number
//            String mOrderNumber = jsonObject.getString("order_nr");
//            // Get payment form
//            Form mPaymentForm = null;
//            // Get payment url
//            String mPaymentUrl = null;
//            // Validate payment content
//            if (jsonObject.has("payment")) {
//                Log.d(TAG, "HAS PAYMENT DATA");
//                JSONObject jsonPayment = jsonObject.optJSONObject("payment");
//                PaymentMethodForm mPaymentMethodForm = new PaymentMethodForm();
//                mPaymentMethodForm.initialize(jsonObject);
//                JumiaApplication.INSTANCE.setPaymentMethodForm(mPaymentMethodForm);
//                if(jsonObject.has(RestConstants.JSON_ORDER_NUMBER_TAG)){
//                    JumiaApplication.INSTANCE.getPaymentMethodForm().setOrderNumber(jsonObject.optString(RestConstants.JSON_ORDER_NUMBER_TAG));
//                }
//                if (jsonPayment != null && jsonPayment.length() > 0) {
//                    Log.d(TAG, "PAYMENT DATA: " + jsonPayment.toString());
//                    // Get form
//                    JSONObject paymentForm = jsonPayment.optJSONObject("form");
//                    if(paymentForm != null) {
//                        Log.d(TAG, "PAYMENT WITH FORM: " + paymentForm.toString());
//                        mPaymentForm = new Form();
//                        mPaymentForm.initialize(paymentForm);
//                    }
//                    // Get url
//                    String paymentUrl = jsonPayment.optString("url");
//                    if(paymentUrl != null) {
//                        Log.d(TAG, "PAYMENT WITH URL: " + paymentUrl);
//                        mPaymentUrl = paymentUrl;
//                    }
//                }
//            }
//            bundle.putString(Constants.BUNDLE_RESPONSE_KEY, mOrderNumber);
//            bundle.putParcelable(PAYMENT_FORM, mPaymentForm);
//            bundle.putString("payment_url", mPaymentUrl);
//        } catch (JSONException e) {
//            e.printStackTrace();
//            return parseErrorBundle(bundle);
//        }

//        // Create bundle
//        Bundle bundle = generateSuccessBundle(baseResponse);
//        bundle.putString(Constants.BUNDLE_RESPONSE_KEY, checkoutFinish.getOrderNumber());
//        bundle.putParcelable(PAYMENT_FORM, checkoutFinish.getPaymentMethodForm());
//        //bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, JumiaApplication.CUSTOMER);
//        mRequester.onRequestComplete(bundle);
//    }





            
//    /*
//     * (non-Javadoc)
//     * @see com.mobile.helpers.BaseHelper#generateRequestBundle(android.os.Bundle)
//     */
//    @Override
//    public Bundle generateRequestBundle(Bundle args) {
//        Log.d(TAG, "REQUEST");
//        // Validate bundle
//        String userAgentData = "";
//        // Get the user agent value
//        if(args != null && args.containsKey(USER_AGENT)) userAgentData = "?" + args.getString(USER_AGENT);
//        // Create bundle
//        Bundle bundle = new Bundle();
//        bundle.putString(Constants.BUNDLE_URL_KEY, EVENT_TYPE.action + userAgentData);
//        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.POST);
//        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EVENT_TYPE);
//        bundle.putBoolean(IMetaData.MD_IGNORE_CACHE, true);
//        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(EVENT_TYPE.name()));
//        return bundle;
//    }
//
//    /*
//     * (non-Javadoc)
//     * @see com.mobile.helpers.BaseHelper#parseResponseBundle(android.os.Bundle, org.json.JSONObject)
//     */
//    @Override
//    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
//        Log.d(TAG, "PARSE BUNDLE: " + jsonObject.toString());
//        // Parse the response
//        try {
//
//
//            // Metada:
//
//            // Cash On Delivery:
//            // {
//            // "success": true,
//            // "messages": {
//            // "success": [
//            // "ORDER_SUCCESS"
//            // ]
//            // },
//            // "session": {
//            // "id": "hec322fvnb97f0kqp7mv6s2e81",
//            // "expire": null,
//            // "YII_CSRF_TOKEN": "92945c37307600580b25eee7e6d6fa690a9aa126"
//            // },
//            // "metadata": {
//            // "order_nr": "300012712",
//            // "customer_first_name": "mob nsme",
//            // "customer_last_name": "mob last",
//            // "payment": []
//            // }
//            // }
//
//            // Get order number
//            String mOrderNumber = jsonObject.getString("order_nr");
//            // // Get first name
//            // String mFirstName = jsonObject.optString("customer_first_name");
//            // // Get last name
//            // String mLastName = jsonObject.optString("customer_last_name");
//            // // Get payment EVENT_TYPE
//            // String mPaymentType = null;
//            // // Get payment methos
//            // String mPaymentMethod = null;
//            // Get payment form
//            Form mPaymentForm = null;
//            // Get payment url
//            String mPaymentUrl = null;
//
//
//            // Validate payment content
//            if (jsonObject.has("payment")) {
//                Log.d(TAG, "HAS PAYMENT DATA");
//                JSONObject jsonPayment = jsonObject.optJSONObject("payment");
//                PaymentMethodForm mPaymentMethodForm = new PaymentMethodForm();
//                mPaymentMethodForm.initialize(jsonObject);
//                JumiaApplication.INSTANCE.setPaymentMethodForm(mPaymentMethodForm);
//                if(jsonObject.has(RestConstants.JSON_ORDER_NUMBER_TAG)){
//                    JumiaApplication.INSTANCE.getPaymentMethodForm().setOrderNumber(jsonObject.optString(RestConstants.JSON_ORDER_NUMBER_TAG));
//                }
//                if (jsonPayment != null && jsonPayment.length() > 0) {
//                    Log.d(TAG, "PAYMENT DATA: " + jsonPayment.toString());
//                    // // Get EVENT_TYPE
//                    // String paymentType = jsonPayment.optString("EVENT_TYPE");
//                    // // Get method
//                    // String paymentMethod = jsonPayment.optString("method");
//                    // Get form
//                    JSONObject paymentForm = jsonPayment.optJSONObject("form");
//                    if(paymentForm != null) {
//                        Log.d(TAG, "PAYMENT WITH FORM: " + paymentForm.toString());
//                        mPaymentForm = new Form();
//                        mPaymentForm.initialize(paymentForm);
//                    }
//                    // Get url
//                    String paymentUrl = jsonPayment.optString("url");
//                    if(paymentUrl != null) {
//                        Log.d(TAG, "PAYMENT WITH URL: " + paymentUrl);
//                        mPaymentUrl = paymentUrl;
//                    }
//                }
//            }
//
//            // Paga:
//            // payment: { EVENT_TYPE, method, form, url}
//            /**
//             * IPay payment methods:
//             * M-PESA: { value: "17" }
//             * Airtel_Money: { value: "18" }
//             * yuCash: { value: "19" }
//             * MI_Cards: { value: "20" }
//             * VISA: { value: "21" }
//             * Wallety: { value: "26" }
//             */
//
//
//            // PAGA     {"form":{ "action":"https:\/\/jumiaqa1v1.mypaga.com\/paga-web\/epay\/ePay.paga", "id":"Paga", "method":"post", "fields":[ {"id":"customer_account", "value":"9272","label":"","EVENT_TYPE":"hidden","rules":null,"key":"customer_account","name":"customer_account"},{"id":"description","value":"Jumia Invoice number 300036712","label":"","EVENT_TYPE":"hidden","rules":null,"key":"description","name":"description"},{"id":"invoice","value":"300036712","label":"","EVENT_TYPE":"hidden","rules":null,"key":"invoice","name":"invoice"},        {"id":"return_url","value":"https:\/\/alice-staging.jumia.co.ke\/checkout\/paga\/confirm\/","label":"","EVENT_TYPE":"hidden","rules":null,"key":"return_url","name":"return_url"},{"id":"subtotal","value":"2899.00","label":"","EVENT_TYPE":"hidden","rules":null,"key":"subtotal","name":"subtotal"},{"id":"key","value":"ed4e40cf-a9f9-403b-bf9b-37cda286d8a7","label":"","EVENT_TYPE":"hidden","rules":null,"key":"key","name":"key"},{"id":"method","value":"paga","label":"","EVENT_TYPE":"hidden","rules":null,"key":"method","name":"method"},{"id":"request_locale","value":"en","label":"","EVENT_TYPE":"hidden","rules":null,"key":"request_locale","name":"request_locale"}],"name":"Paga"},"EVENT_TYPE":"auto-submit-external","method":"post"}
//            // VISA     {"form":{"action":"https:\/\/ipay.intrepid.co.ke\/inm\/","id":"iPay_VISA","method":"post","fields":[{"id":"order_id","value":"300089712","label":"","EVENT_TYPE":"hidden","rules":null,"key":"order_id","name":"order_id"},{"id":"invoice","value":"","label":"","EVENT_TYPE":"hidden","rules":null,"key":"invoice","name":"invoice"},{"id":"total","value":2899,"label":"","EVENT_TYPE":"hidden","rules":null,"key":"total","name":"total"},{"id":"phone1","value":"123456789","label":"","EVENT_TYPE":"hidden","rules":null,"key":"phone1","name":"phone1"},{"id":"email","value":"test02@jumia.com","label":"E-Mail","EVENT_TYPE":"email","rules":{"required":true,"min":5,"regex":"[a-zA-Z0-9äöüÄÖÜ_+.-]+@[a-zA-Z0-9äöüÄÖÜ][a-zA-Z0-9-äöüÄÖÜ.]+\\.([a-zA-Z]{2,6})","max":70},"key":"email","name":"email"},{"id":"vendor_ref","value":"jumiatest","label":"","EVENT_TYPE":"hidden","rules":null,"key":"vendor_ref","name":"vendor_ref"},{"id":"p1","value":"YV385AAAA9UQNAFAMZ-21772","label":"","EVENT_TYPE":"hidden","rules":null,"key":"p1","name":"p1"},{"id":"p2","value":"2899.00","label":"","EVENT_TYPE":"hidden","rules":null,"key":"p2","name":"p2"},{"id":"merchant","value":"Jumia Kenya","label":"","EVENT_TYPE":"hidden","rules":null,"key":"merchant","name":"merchant"},{"id":"callback","value":"https:\/\/alice-staging.jumia.co.ke\/checkout\/ipay\/confirm\/","label":"","EVENT_TYPE":"hidden","rules":null,"key":"callback","name":"callback"},{"id":"custemail","value":"","label":"","EVENT_TYPE":"hidden","rules":null,"key":"custemail","name":"custemail"},{"id":"tc","value":"","label":"I accept the Terms & Agreements","EVENT_TYPE":"checkbox","rules":{"required":{"message":"Required field","requiredValue":true}},"key":"tc","name":"tc"},{"id":"hashid","value":"0135993357804f02ab750c031a7eb956ec79d43a","label":"","EVENT_TYPE":"hidden","rules":null,"key":"hashid","name":"hashid"},{"id":"cur","value":"KES","label":"","EVENT_TYPE":"hidden","rules":null,"key":"cur","name":"cur"}],"name":"iPay_VISA"},"EVENT_TYPE":"submit-external","method":"post"}
//            // WALLETY  {"form":{"action":"https:\/\/www.wallety.com\/checkout\/checkout","id":"Wallety","method":"post","fields":[{"id":"amount","value":"4199.00","label":"","EVENT_TYPE":"hidden","rules":null,"key":"amount","name":"amount"},{"id":"desc","value":"Purchase -","label":"","EVENT_TYPE":"hidden","rules":null,"key":"desc","name":"desc"},{"id":"gid","value":"84","label":"","EVENT_TYPE":"hidden","rules":null,"key":"gid","name":"gid"},{"id":"merchinvno","value":"300088712","label":"","EVENT_TYPE":"hidden","rules":null,"key":"merchinvno","name":"merchinvno"},{"id":"redirect","value":"https:\/\/alice-staging.jumia.co.ke\/checkout\/wallety\/success","label":"","EVENT_TYPE":"hidden","rules":null,"key":"redirect","name":"redirect"},{"id":"check_sum","value":"BEAD6204A74BFAB15BCDD3B77CF15096","label":"","EVENT_TYPE":"hidden","rules":null,"key":"check_sum","name":"check_sum"},{"id":"checkout","value":"","label":"","EVENT_TYPE":"image","rules":null,"key":"checkout","name":"checkout"}],"name":"Wallety"},"EVENT_TYPE":"submit-external","method":"post"}
//            // AAIB     {"form":{"action":null,"id":"AAIB","method":"get","fields":[{"id":"vpc_AVS_City","value":"","label":"","rules":null,"key":"vpc_AVS_City","name":"vpc_AVS_City"},{"id":"vpc_AVS_Country","value":"","label":"","rules":null,"key":"vpc_AVS_Country","name":"vpc_AVS_Country"},{"id":"vpc_AVS_StateProv","value":"","label":"","rules":null,"key":"vpc_AVS_StateProv","name":"vpc_AVS_StateProv"},{"id":"vpc_AVS_Street01","value":"","label":"","rules":null,"key":"vpc_AVS_Street01","name":"vpc_AVS_Street01"},{"id":"vpc_AccessCode","value":"","label":"","rules":null,"key":"vpc_AccessCode","name":"vpc_AccessCode"},{"id":"vpc_Amount","value":"","label":"","rules":null,"key":"vpc_Amount","name":"vpc_Amount"},{"id":"vpc_Command","value":"","label":"","rules":null,"key":"vpc_Command","name":"vpc_Command"},{"id":"vpc_Currency","value":"","label":"","rules":null,"key":"vpc_Currency","name":"vpc_Currency"},{"id":"vpc_Locale","value":"","label":"","rules":null,"key":"vpc_Locale","name":"vpc_Locale"},{"id":"vpc_MerchTxnRef","value":"","label":"","rules":null,"key":"vpc_MerchTxnRef","name":"vpc_MerchTxnRef"},{"id":"vpc_Merchant","value":"","label":"","rules":null,"key":"vpc_Merchant","name":"vpc_Merchant"},{"id":"vpc_OrderInfo","value":"","label":"","rules":null,"key":"vpc_OrderInfo","name":"vpc_OrderInfo"},{"id":"vpc_ReturnURL","value":"","label":"","rules":null,"key":"vpc_ReturnURL","name":"vpc_ReturnURL"},{"id":"vpc_Version","value":"","label":"","rules":null,"key":"vpc_Version","name":"vpc_Version"},{"id":"vpc_SecureHash","value":"","label":"","rules":null,"key":"vpc_SecureHash","name":"vpc_SecureHash"}],"name":"AAIB"},"EVENT_TYPE":"auto-redirect-external","method":"post"}
//            // GLOBALPAY{"form":{"action":"https:\/\/41.203.113.80\/globalpay_demo\/paymentgatewaycapture.aspx","id":"GlobalPay","method":"post","fields":[{"id":"names","value":"mob nsme mob last","label":"","EVENT_TYPE":"hidden","rules":null,"key":"names","name":"names"},{"id":"merchantid","value":"gfd","label":"","EVENT_TYPE":"hidden","rules":null,"key":"merchantid","name":"merchantid"},{"id":"currency","value":"NGN","label":"","EVENT_TYPE":"hidden","rules":null,"key":"currency","name":"currency"},{"id":"amount","value":"4199.00","label":"","EVENT_TYPE":"hidden","rules":["1"],"key":"amount","name":"amount"},{"id":"email_address","value":"test02@jumia.com","label":"","EVENT_TYPE":"hidden","rules":null,"key":"email_address","name":"email_address"},{"id":"phone_number","value":"123456789","label":"","EVENT_TYPE":"hidden","rules":null,"key":"phone_number","name":"phone_number"},{"id":"merch_txnref","value":"300078712","label":"","EVENT_TYPE":"hidden","rules":null,"key":"merch_txnref","name":"merch_txnref"}],"name":"GlobalPay"},"EVENT_TYPE":"auto-submit-external","method":"post"}
//            // YUCASH   {"form":{"action":"https:\/\/ipay.intrepid.co.ke\/incoming\/","id":"iPay_yuCash","method":"post","fields":[{"id":"order_id","value":"300058712","label":"","EVENT_TYPE":"hidden","rules":null,"key":"order_id","name":"order_id"},{"id":"invoice","value":"","label":"","EVENT_TYPE":"hidden","rules":null,"key":"invoice","name":"invoice"},{"id":"total","value":57999,"label":"","EVENT_TYPE":"hidden","rules":null,"key":"total","name":"total"},{"id":"phone1","value":"123456789","label":"","EVENT_TYPE":"hidden","rules":null,"key":"phone1","name":"phone1"},{"id":"email","value":"msilva@jumia.com","label":"E-Mail","EVENT_TYPE":"email","rules":{"required":true,"min":5,"regex":"[a-zA-Z0-9äöüÄÖÜ_+.-]+@[a-zA-Z0-9äöüÄÖÜ][a-zA-Z0-9-äöüÄÖÜ.]+\\.([a-zA-Z]{2,6})","max":70},"key":"email","name":"email"},{"id":"vendor_ref","value":"jumiatest","label":"","EVENT_TYPE":"hidden","rules":null,"key":"vendor_ref","name":"vendor_ref"},{"id":"p1","value":"DE168ELAA562NAFAMZ-7554","label":"","EVENT_TYPE":"hidden","rules":null,"key":"p1","name":"p1"},{"id":"p2","value":"57999.00","label":"","EVENT_TYPE":"hidden","rules":null,"key":"p2","name":"p2"},{"id":"merchant","value":"Jumia Kenya","label":"","EVENT_TYPE":"hidden","rules":null,"key":"merchant","name":"merchant"},{"id":"callback","value":"https:\/\/alice-staging.jumia.co.ke\/checkout\/ipay\/confirm\/","label":"","EVENT_TYPE":"hidden","rules":null,"key":"callback","name":"callback"},{"id":"custemail","value":"","label":"","EVENT_TYPE":"hidden","rules":null,"key":"custemail","name":"custemail"},{"id":"tc","value":"","label":"I accept the Terms & Agreements","EVENT_TYPE":"checkbox","rules":{"required":{"message":"Required field","requiredValue":true}},"key":"tc","name":"tc"},{"id":"hashid","value":"","label":"","EVENT_TYPE":"hidden","rules":null,"key":"hashid","name":"hashid"},{"id":"yu","value":"","label":"yuCash Transaction ID","EVENT_TYPE":"text","rules":{"required":true},"key":"yu","name":"yu"}],"name":"iPay_yuCash"},"EVENT_TYPE":"submit-external","method":"post"}
//            // AIRTEL_M {"form":{"action":"https:\/\/ipay.intrepid.co.ke\/incoming\/","id":"iPay_Airtel_Money","method":"post","fields":[{"id":"order_id","value":"300018712","label":"","EVENT_TYPE":"hidden","rules":null,"key":"order_id","name":"order_id"},{"id":"invoice","value":"","label":"","EVENT_TYPE":"hidden","rules":null,"key":"invoice","name":"invoice"},{"id":"total","value":57999,"label":"","EVENT_TYPE":"hidden","rules":null,"key":"total","name":"total"},{"id":"phone1","value":"123456789","label":"","EVENT_TYPE":"hidden","rules":null,"key":"phone1","name":"phone1"},{"id":"email","value":"msilva@jumia.com","label":"E-Mail","EVENT_TYPE":"email","rules":{"required":true,"min":5,"regex":"[a-zA-Z0-9äöüÄÖÜ_+.-]+@[a-zA-Z0-9äöüÄÖÜ][a-zA-Z0-9-äöüÄÖÜ.]+\\.([a-zA-Z]{2,6})","max":70},"key":"email","name":"email"},{"id":"vendor_ref","value":"jumiatest","label":"","EVENT_TYPE":"hidden","rules":null,"key":"vendor_ref","name":"vendor_ref"},{"id":"p1","value":"DE168ELAA562NAFAMZ-7554","label":"","EVENT_TYPE":"hidden","rules":null,"key":"p1","name":"p1"},{"id":"p2","value":"57999.00","label":"","EVENT_TYPE":"hidden","rules":null,"key":"p2","name":"p2"},{"id":"merchant","value":"Jumia Kenya","label":"","EVENT_TYPE":"hidden","rules":null,"key":"merchant","name":"merchant"},{"id":"callback","value":"https:\/\/alice-staging.jumia.co.ke\/checkout\/ipay\/confirm\/","label":"","EVENT_TYPE":"hidden","rules":null,"key":"callback","name":"callback"},{"id":"custemail","value":"","label":"","EVENT_TYPE":"hidden","rules":null,"key":"custemail","name":"custemail"},{"id":"tc","value":"","label":"I accept the Terms & Agreements","EVENT_TYPE":"checkbox","rules":{"required":{"message":"Required field","requiredValue":true}},"key":"tc","name":"tc"},{"id":"hashid","value":"","label":"","EVENT_TYPE":"hidden","rules":null,"key":"hashid","name":"hashid"},{"id":"zap","value":"","label":"Airtel Money Transaction ID","EVENT_TYPE":"text","rules":{"required":true},"key":"zap","name":"zap"}],"name":"iPay_Airtel_Money"},"EVENT_TYPE":"submit-external","method":"post"}
//            // MI_CARDS {"form":{"action":"https:\/\/ipay.intrepid.co.ke\/inm\/","id":"iPay_MI_Cards","method":"post","fields":[{"id":"order_id","value":"300047712","label":"","EVENT_TYPE":"hidden","rules":null,"key":"order_id","name":"order_id"},{"id":"invoice","value":"","label":"","EVENT_TYPE":"hidden","rules":null,"key":"invoice","name":"invoice"},{"id":"total","value":57999,"label":"","EVENT_TYPE":"hidden","rules":null,"key":"total","name":"total"},{"id":"phone1","value":"123456789","label":"","EVENT_TYPE":"hidden","rules":null,"key":"phone1","name":"phone1"},{"id":"email","value":"msilva@jumia.com","label":"E-Mail","EVENT_TYPE":"email","rules":{"required":true,"min":5,"regex":"[a-zA-Z0-9äöüÄÖÜ_+.-]+@[a-zA-Z0-9äöüÄÖÜ][a-zA-Z0-9-äöüÄÖÜ.]+\\.([a-zA-Z]{2,6})","max":70},"key":"email","name":"email"},{"id":"vendor_ref","value":"jumiatest","label":"","EVENT_TYPE":"hidden","rules":null,"key":"vendor_ref","name":"vendor_ref"},{"id":"p1","value":"DE168ELAA562NAFAMZ-7554","label":"","EVENT_TYPE":"hidden","rules":null,"key":"p1","name":"p1"},{"id":"p2","value":"57999.00","label":"","EVENT_TYPE":"hidden","rules":null,"key":"p2","name":"p2"},{"id":"merchant","value":"Jumia Kenya","label":"","EVENT_TYPE":"hidden","rules":null,"key":"merchant","name":"merchant"},{"id":"callback","value":"https:\/\/alice-staging.jumia.co.ke\/checkout\/ipay\/confirm\/","label":"","EVENT_TYPE":"hidden","rules":null,"key":"callback","name":"callback"},{"id":"custemail","value":"","label":"","EVENT_TYPE":"hidden","rules":null,"key":"custemail","name":"custemail"},{"id":"tc","value":"","label":"I accept the Terms & Agreements","EVENT_TYPE":"checkbox","rules":{"required":{"message":"Required field","requiredValue":true}},"key":"tc","name":"tc"},{"id":"hashid","value":"9651fc7360b1a88547ee219bffc7f2b099118cf6","label":"","EVENT_TYPE":"hidden","rules":null,"key":"hashid","name":"hashid"},{"id":"cur","value":"KES","label":"","EVENT_TYPE":"hidden","rules":null,"key":"cur","name":"cur"}],"name":"iPay_MI_Cards"},"EVENT_TYPE":"submit-external","method":"post"}
//            // M-PESA   {"form":{"action":"https:\/\/ipay.intrepid.co.ke\/incoming\/","id":"iPay_M-PESA","method":"post","fields":[{"id":"order_id","value":"300067712","label":"","EVENT_TYPE":"hidden","rules":null,"key":"order_id","name":"order_id"},{"id":"invoice","value":"","label":"","EVENT_TYPE":"hidden","rules":null,"key":"invoice","name":"invoice"},{"id":"total","value":57999,"label":"","EVENT_TYPE":"hidden","rules":null,"key":"total","name":"total"},{"id":"phone1","value":"123456789","label":"","EVENT_TYPE":"hidden","rules":null,"key":"phone1","name":"phone1"},{"id":"email","value":"msilva@jumia.com","label":"E-Mail","EVENT_TYPE":"email","rules":{"required":true,"min":5,"regex":"[a-zA-Z0-9äöüÄÖÜ_+.-]+@[a-zA-Z0-9äöüÄÖÜ][a-zA-Z0-9-äöüÄÖÜ.]+\\.([a-zA-Z]{2,6})","max":70},"key":"email","name":"email"},{"id":"vendor_ref","value":"jumiatest","label":"","EVENT_TYPE":"hidden","rules":null,"key":"vendor_ref","name":"vendor_ref"},{"id":"p1","value":"DE168ELAA562NAFAMZ-7554","label":"","EVENT_TYPE":"hidden","rules":null,"key":"p1","name":"p1"},{"id":"p2","value":"57999.00","label":"","EVENT_TYPE":"hidden","rules":null,"key":"p2","name":"p2"},{"id":"merchant","value":"Jumia Kenya","label":"","EVENT_TYPE":"hidden","rules":null,"key":"merchant","name":"merchant"},{"id":"callback","value":"https:\/\/alice-staging.jumia.co.ke\/checkout\/ipay\/confirm\/","label":"","EVENT_TYPE":"hidden","rules":null,"key":"callback","name":"callback"},{"id":"custemail","value":"","label":"","EVENT_TYPE":"hidden","rules":null,"key":"custemail","name":"custemail"},{"id":"tc","value":"","label":"I accept the Terms & Agreements","EVENT_TYPE":"checkbox","rules":{"required":{"message":"Required field","requiredValue":true}},"key":"tc","name":"tc"},{"id":"hashid","value":"","label":"","EVENT_TYPE":"hidden","rules":null,"key":"hashid","name":"hashid"},{"id":"mpesa","value":"","label":"M-PESA Transaction ID","EVENT_TYPE":"text","rules":{"required":true},"key":"mpesa","name":"mpesa"}],"name":"iPay_M-PESA"},"EVENT_TYPE":"submit-external","method":"post"}
//
//            // This step only occurs if the response returned on step 6 has a key
//            // "payment".
//            // Depending on the selected payment method, client will be asked to
//            // provide payment details or redirected to the payment provider's
//            // external page to provide payment details.
//            // TODO: Filter response by method
//            // Cash On Delivery - If the payment method selected was
//            // "Cash On Delivery" this step not applicable.
//            // Paga - The follow response tell us that is need made a auto submit
//            // form for action
//            // WebPAY - In this case the form has a property named "target" that
//            // indicates the result of the form's submit has to be displayed in an
//            // iframe, whose NAME is "target" property's value
//            // GlobalPay - Auto-submit-external
//            // Wallety - Submit-external
//            // AdyenPayment - Page
//
//
//            //bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, new Order(jsonObject));
//            bundle.putString(Constants.BUNDLE_RESPONSE_KEY, mOrderNumber);
//            bundle.putParcelable(PAYMENT_FORM, mPaymentForm);
//            bundle.putString("payment_url", mPaymentUrl);
//
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//            return parseErrorBundle(bundle);
//        }
//
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EVENT_TYPE);
//        return bundle;
//    }
//
//    /*
//     * (non-Javadoc)
//     * @see com.mobile.helpers.BaseHelper#parseErrorBundle(android.os.Bundle)
//     */
//    @Override
//    public Bundle parseErrorBundle(Bundle bundle) {
//        Log.d(TAG, "PARSE ERROR BUNDLE");
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EVENT_TYPE);
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
//        Log.d(TAG, "PARSE RESPONSE BUNDLE");
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EVENT_TYPE);
//        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
//        return bundle;
//    }
//
//    @Override
//    public Bundle parseResponseErrorBundle(Bundle bundle, JSONObject jsonObject) {
//        return parseResponseErrorBundle(bundle);
//    }
}
