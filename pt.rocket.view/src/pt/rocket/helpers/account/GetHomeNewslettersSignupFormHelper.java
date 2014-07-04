///**
// * 
// */
//package pt.rocket.helpers.account;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import pt.rocket.app.JumiaApplication;
//import pt.rocket.forms.HomeNewslettersSignupForm;
//import pt.rocket.framework.enums.RequestType;
//import pt.rocket.framework.rest.RestConstants;
//import pt.rocket.framework.utils.Constants;
//import pt.rocket.framework.utils.EventType;
//import pt.rocket.framework.utils.Utils;
//import pt.rocket.helpers.BaseHelper;
//import pt.rocket.helpers.HelperPriorityConfiguration;
//import android.os.Bundle;
//import android.util.Log;
//
///**
// * Helper used to get the form informations on Home to sign up an user to newsletter
// * 
// * @author Andre Lopes
// * 
// */
//public class GetHomeNewslettersSignupFormHelper extends BaseHelper {
//
//    private static String TAG = GetHomeNewslettersSignupFormHelper.class.getSimpleName();
//
//    private static final EventType mEventType = EventType.GET_HOME_NEWSLETTERS_SIGNUP_FORM_EVENT;
//
//    private static final EventType mFallBackEventType = EventType.GET_HOME_NEWSLETTERS_SIGNUP_FORM_FALLBACK_EVENT;
//
//    /*
//     * (non-Javadoc)
//     * 
//     * @see pt.rocket.helpers.BaseHelper#generateRequestBundle(android.os.Bundle)
//     */
//    @Override
//    public Bundle generateRequestBundle(Bundle args) {
//        Log.d(TAG, "REQUEST");
//        String url = mFallBackEventType.action;
//        try {
//            url = JumiaApplication.INSTANCE.getFormDataRegistry().get(mEventType.action).getUrl();
//        } catch (NullPointerException e) {
//            Log.w(TAG, "FORM DATA IS NULL THEN GET NEWSLETTER FORM FALLBACK", e);
//        }
//        Bundle bundle = new Bundle();
//        bundle.putString(Constants.BUNDLE_URL_KEY, url);
//        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_NOT_PRIORITARY);
//        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.GET);
//        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(Constants.BUNDLE_MD5_KEY));
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, mEventType);
//        return bundle;
//    }
//
//    /*
//     * (non-Javadoc)
//     * 
//     * @see pt.rocket.helpers.BaseHelper#parseResponseBundle(android.os.Bundle, org.json.JSONObject)
//     */
//    @Override
//    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
//        Log.i(TAG, "ON PARSE JSON: " + jsonObject.toString());
//        try {
//            JSONArray jsonArray = jsonObject.optJSONArray(RestConstants.JSON_DATA_TAG);
//            if(jsonArray != null && jsonArray.length() > 0) {
//                HomeNewslettersSignupForm homeNewslettersSignupForm = new HomeNewslettersSignupForm();
//
//                JSONObject data = jsonArray.getJSONObject(0);
//                if (homeNewslettersSignupForm.initialize(data)) {
//                    bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, homeNewslettersSignupForm);
//                } else {
//                    Log.e(TAG, "Error initializing the form using the data");
//                }
//            }
//        } catch (JSONException e) {
//            Log.d(TAG, "PARSE EXCEPTION: ", e);
//            return parseErrorBundle(bundle);
//        }
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, mEventType);
//        return bundle;
//    }
//
//    /*
//     * (non-Javadoc)
//     * 
//     * @see pt.rocket.helpers.BaseHelper#parseErrorBundle(android.os.Bundle)
//     */
//    @Override
//    public Bundle parseErrorBundle(Bundle bundle) {
//        Log.d(TAG, "PARSE ERROR BUNDLE");
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, mEventType);
//        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
//        return bundle;
//    }
//
//    /*
//     * (non-Javadoc)
//     * 
//     * @see pt.rocket.helpers.BaseHelper#parseResponseErrorBundle(android.os.Bundle)
//     */
//    @Override
//    public Bundle parseResponseErrorBundle(Bundle bundle) {
//        Log.d(TAG, "PARSE RESPONSE BUNDLE");
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, mEventType);
//        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
//        return bundle;
//    }
//}
