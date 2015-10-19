package com.mobile.helpers.session;

import android.content.ContentValues;
import android.os.Bundle;

import com.mobile.app.JumiaApplication;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.helpers.NextStepStruct;
import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.objects.checkout.CheckoutStepLogin;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.CustomerUtils;
import com.mobile.newFramework.utils.EventTask;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.cache.WishListCache;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.utils.TrackerDelegator;
import com.mobile.view.BaseActivity;

import java.util.Map;

/**
 * Example helper
 */
public class GetLoginHelper extends SuperBaseHelper {
    
    private static String TAG = GetLoginHelper.class.getSimpleName();

    boolean saveCredentials = true;

    private ContentValues mContentValues;

    @Override
    public EventType getEventType() {
        return EventType.LOGIN_EVENT;
    }

    @Override
    protected EventTask setEventTask() {
        return EventTask.SMALL_TASK;
    }

    @Override
    protected RequestBundle createRequest(Bundle args) {
        saveCredentials = args.getBoolean(CustomerUtils.INTERNAL_AUTO_LOGIN_FLAG);
        mContentValues = args.getParcelable(Constants.BUNDLE_DATA_KEY);
        return super.createRequest(args);
    }

    @Override
    protected Map<String, String> getRequestData(Bundle args) {
        return CollectionUtils.convertContentValuesToMap(mContentValues);
    }

    @Override
    protected void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.loginCustomer);
    }

    @Override
    public void postSuccess(BaseResponse baseResponse) {
        super.postSuccess(baseResponse);
        CheckoutStepLogin loginCustomer = ((CheckoutStepLogin) baseResponse.getMetadata().getData());
        NextStepStruct nextStepStruct = new NextStepStruct(loginCustomer);
        baseResponse.getMetadata().setData(nextStepStruct);
        
        //TODO move to observable
        // Save customer
        JumiaApplication.CUSTOMER = loginCustomer.getCustomer();
        // Save credentials
        if (saveCredentials) {
            Print.i(TAG, "SAVE CUSTOMER CREDENTIALS");
            mContentValues.put(CustomerUtils.INTERNAL_SIGN_UP_FLAG, false);
            mContentValues.put(CustomerUtils.INTERNAL_FACEBOOK_FLAG, false);
            JumiaApplication.INSTANCE.getCustomerUtils().storeCredentials(mContentValues);
            Print.i(TAG, "GET CUSTOMER CREDENTIALS: " + JumiaApplication.INSTANCE.getCustomerUtils().getCredentials());
        }
        // Save new wish list
        WishListCache.set(JumiaApplication.CUSTOMER.getWishListCache());
    }

    public static Bundle createAutoLoginBundle() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BUNDLE_DATA_KEY, JumiaApplication.INSTANCE.getCustomerUtils().getCredentials());
        bundle.putBoolean(CustomerUtils.INTERNAL_AUTO_LOGIN_FLAG, true);
        bundle.putSerializable(Constants.BUNDLE_EVENT_TASK, EventTask.NORMAL_TASK);
        return bundle;
    }

    public static Bundle createLoginBundle(ContentValues values) {
        // TODO VALIDATE WHAT IS USED FOR INTERNAL_AUTO_LOGIN_FLAG
        values.put(CustomerUtils.INTERNAL_AUTO_LOGIN_FLAG, true);
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BUNDLE_DATA_KEY, values);
        bundle.putBoolean(CustomerUtils.INTERNAL_AUTO_LOGIN_FLAG, true);
        bundle.putSerializable(Constants.BUNDLE_EVENT_TASK, EventTask.NORMAL_TASK);
        return bundle;
    }



    public static void validateNextStep(BaseActivity activity, boolean isInCheckoutProcess, FragmentType mParentFragmentType, FragmentType nextStepFromParent, FragmentType nextStepFromApi) {
        // Case next step from api
        if(isInCheckoutProcess) {
            goToCheckoutNextStepFromApi(activity, mParentFragmentType, nextStepFromApi);
        } else {
            goToNextStepFromParent(activity, nextStepFromParent);
        }
    }

    private static void goToNextStepFromParent(BaseActivity activity, FragmentType nextStepFromParent) {
        // Validate the next step
        if (nextStepFromParent != null && nextStepFromParent != FragmentType.UNKNOWN) {
            Print.i(TAG, "NEXT STEP FROM PARENT: " + nextStepFromParent.toString());
            FragmentController.getInstance().popLastEntry(FragmentType.LOGIN.toString());
            Bundle args = new Bundle();
            args.putBoolean(TrackerDelegator.LOGIN_KEY, true);
            activity.onSwitchFragment(nextStepFromParent, args, FragmentController.ADD_TO_BACK_STACK);
        } else {
            Print.i(TAG, "NEXT STEP FROM PARENT: BACK");
            activity.onBackPressed();
        }
    }

    /**
     * Method used to switch the checkout step
     */
    private static void goToCheckoutNextStepFromApi(BaseActivity activity, FragmentType mParentFragmentType, FragmentType nextStepType) {
        // Get next step
        if (nextStepType == null || nextStepType == FragmentType.UNKNOWN) {
            Print.w(TAG, "NEXT STEP FROM API IS NULL");
            //super.showFragmentErrorRetry();
            activity.onBackPressed();
        } else {
            Print.i(TAG, "NEXT STEP FROM API: " + nextStepType.toString());
            // Case comes from MY_ACCOUNT
            if(mParentFragmentType == FragmentType.MY_ACCOUNT) {
                if(nextStepType == FragmentType.CREATE_ADDRESS) nextStepType = FragmentType.MY_ACCOUNT_CREATE_ADDRESS;
                else if(nextStepType == FragmentType.EDIT_ADDRESS) nextStepType = FragmentType.MY_ACCOUNT_EDIT_ADDRESS;
                else if(nextStepType == FragmentType.MY_ADDRESSES) nextStepType = FragmentType.MY_ACCOUNT_MY_ADDRESSES;
            }
            // Clean stack for new native checkout on the back stack (auto login)
            activity.removeAllNativeCheckoutFromBackStack();
            activity.onSwitchFragment(nextStepType, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
        }
    }


}
