package com.bamilo.android.appmodule.bamiloapp.helpers.session;

import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;

import android.support.annotation.NonNull;
import com.bamilo.android.appmodule.modernbamilo.util.retrofit.RetrofitHelper;
import com.bamilo.android.appmodule.modernbamilo.userreview.UserReviewActivity;
import com.bamilo.android.appmodule.modernbamilo.userreview.UserReviewWebApi;
import com.bamilo.android.appmodule.modernbamilo.userreview.pojo.getsurveylist.GetSurveyListResponse;

import com.bamilo.android.appmodule.modernbamilo.util.retrofit.pojo.ResponseWrapper;
import com.bamilo.android.appmodule.bamiloapp.app.BamiloApplication;
import com.bamilo.android.appmodule.bamiloapp.helpers.NextStepStruct;
import com.bamilo.android.appmodule.bamiloapp.helpers.SuperBaseHelper;
import com.bamilo.android.framework.service.objects.checkout.CheckoutStepLogin;
import com.bamilo.android.framework.service.pojo.BaseResponse;
import com.bamilo.android.framework.service.requests.BaseRequest;
import com.bamilo.android.framework.service.requests.RequestBundle;
import com.bamilo.android.framework.service.rest.interfaces.AigApiInterface;
import com.bamilo.android.framework.service.utils.Constants;
import com.bamilo.android.framework.service.utils.CustomerUtils;
import com.bamilo.android.framework.service.utils.EventTask;
import com.bamilo.android.framework.service.utils.EventType;
import com.bamilo.android.framework.service.utils.cache.WishListCache;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Login helper
 */
public class LoginHelper extends SuperBaseHelper {

    boolean saveCredentials = true;

    private Context mContext;

    public LoginHelper(Context context) {
        this.mContext = context;
    }

    @Override
    public EventType getEventType() {
        return EventType.LOGIN_EVENT;
    }

    @Override
    protected EventTask setEventTask() {
        return EventTask.ACTION_TASK;
    }

    @Override
    protected RequestBundle createRequest(Bundle args) {
        saveCredentials = args.getBoolean(CustomerUtils.INTERNAL_AUTO_LOGIN_FLAG);
        return super.createRequest(args);
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
        BamiloApplication.CUSTOMER = loginCustomer.getCustomer();
        // Save credentials
        if (saveCredentials) {
            mParameters.put(CustomerUtils.INTERNAL_SIGN_UP_FLAG, false);
            BamiloApplication.INSTANCE.getCustomerUtils().storeCredentials(mParameters);
        }
        // Save new wish list
        WishListCache.set(BamiloApplication.CUSTOMER.getWishListCache());

        // Look for Review Survey:
        UserReviewWebApi webApi = RetrofitHelper.makeWebApi(mContext, UserReviewWebApi.class);
        final String userId = BamiloApplication.CUSTOMER.getIdAsString();
        Call<ResponseWrapper<GetSurveyListResponse>> call = webApi.getSurveysList(userId);
        call.enqueue(new Callback<ResponseWrapper<GetSurveyListResponse>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseWrapper<GetSurveyListResponse>> call, @NonNull Response<ResponseWrapper<GetSurveyListResponse>> response) {
                try {
                    if (response.body().getSuccess()) {
                        UserReviewActivity.start(mContext, UserReviewActivity.getTYPE_USER_REVIEW_APP_INITIAL(), userId, null);
                    }
                } catch (NullPointerException ignored) {

                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseWrapper<GetSurveyListResponse>> call, @NonNull Throwable t) {

            }
        });
    }

    public static Bundle createLoginBundle(ContentValues values) {
        // TODO VALIDATE WHAT IS USED FOR INTERNAL_AUTO_LOGIN_FLAG
        values.put(CustomerUtils.INTERNAL_AUTO_LOGIN_FLAG, true);
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BUNDLE_DATA_KEY, values);
        bundle.putBoolean(CustomerUtils.INTERNAL_AUTO_LOGIN_FLAG, true);
        return bundle;
    }
}
