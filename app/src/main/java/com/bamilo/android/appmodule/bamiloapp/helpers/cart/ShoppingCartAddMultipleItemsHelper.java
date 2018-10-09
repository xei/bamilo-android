package com.bamilo.android.appmodule.bamiloapp.helpers.cart;

import android.os.Bundle;

import com.bamilo.android.appmodule.bamiloapp.app.BamiloApplication;
import com.bamilo.android.appmodule.bamiloapp.helpers.SuperBaseHelper;
import com.bamilo.android.framework.service.objects.cart.PurchaseEntity;
import com.bamilo.android.framework.service.pojo.BaseResponse;
import com.bamilo.android.framework.service.requests.BaseRequest;
import com.bamilo.android.framework.service.requests.RequestBundle;
import com.bamilo.android.framework.service.rest.interfaces.AigApiInterface;
import com.bamilo.android.framework.service.utils.Constants;
import com.bamilo.android.framework.service.utils.EventTask;
import com.bamilo.android.framework.service.utils.EventType;

import java.util.ArrayList;

public class ShoppingCartAddMultipleItemsHelper extends SuperBaseHelper {

    private static String TAG = ShoppingCartAddMultipleItemsHelper.class.getSimpleName();


    @Override
    public EventType getEventType() {
        return EventType.ADD_ITEMS_TO_SHOPPING_CART_EVENT;
    }

    @Override
    protected EventTask setEventTask() {
        return EventTask.ACTION_TASK;
    }


    @Override
    public void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.addMultipleItemsShoppingCart);
    }


    @Override
    public void postSuccess(BaseResponse baseResponse) {
        super.postSuccess(baseResponse);
        BamiloApplication.INSTANCE.setCart(null);
        PurchaseEntity cart = (PurchaseEntity) baseResponse.getContentData();
        BamiloApplication.INSTANCE.setCart(cart);

        AddMultipleStruct addMultipleStruct = new AddMultipleStruct();
        addMultipleStruct.setPurchaseEntity(cart);
        baseResponse.getMetadata().setData(addMultipleStruct);
    }

    /**
     * Method used to create a request bundle.
     */
    public static Bundle createBundle(ArrayList <String> requestDataList) {
        // Request data
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(Constants.BUNDLE_ARRAY_KEY, requestDataList);
        return bundle;
    }

    public class AddMultipleStruct {
        private PurchaseEntity purchaseEntity;
        private ArrayList<String> successMessages;
        private ArrayList<String> errorMessages;

        public PurchaseEntity getPurchaseEntity() {
            return purchaseEntity;
        }

        void setPurchaseEntity(PurchaseEntity purchaseEntity) {
            this.purchaseEntity = purchaseEntity;
        }

        public ArrayList<String> getSuccessMessages() {
            return successMessages;
        }

        void setSuccessMessages(ArrayList<String> successMessages) {
            this.successMessages = successMessages;
        }

        public ArrayList<String> getErrorMessages() {
            return errorMessages;
        }

        void setErrorMessages(ArrayList<String> errorMessages) {
            this.errorMessages = errorMessages;
        }
    }
}
