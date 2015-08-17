package com.mobile.helpers.products;

import android.os.Bundle;

import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.objects.product.ValidProductList;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.EventTask;
import com.mobile.newFramework.utils.EventType;

/**
 * Validate one or more products on the API and actualize their information
 *
 * @author Paulo Carvalho
 */
public class ValidateProductHelper extends SuperBaseHelper {

    protected static final String TAG = ValidateProductHelper.class.getSimpleName();

    public static final String VALIDATE_PRODUCTS_KEY = "products[%d]";

    @Override
    public EventType getEventType() {
        return EventType.VALIDATE_PRODUCTS;
    }

    @Override
    protected EventTask setEventTask() {
        return EventTask.SMALL_TASK;
    }

    @Override
    protected void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.validateProducts);
    }

    @Override
    public void createSuccessBundleParams(BaseResponse baseResponse, Bundle bundle) {
        super.createSuccessBundleParams(baseResponse, bundle);
        ValidProductList validProducts = (ValidProductList) baseResponse.getMetadata().getData();
        bundle.putParcelableArrayList(Constants.BUNDLE_RESPONSE_KEY, validProducts);
    }

}
