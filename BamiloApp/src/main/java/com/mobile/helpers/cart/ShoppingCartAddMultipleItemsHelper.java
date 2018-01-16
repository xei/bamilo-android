package com.mobile.helpers.cart;

import android.os.Bundle;

import com.mobile.app.BamiloApplication;
import com.mobile.helpers.SuperBaseHelper;
import com.mobile.service.objects.cart.PurchaseEntity;
import com.mobile.service.pojo.BaseResponse;
import com.mobile.service.requests.BaseRequest;
import com.mobile.service.requests.RequestBundle;
import com.mobile.service.rest.interfaces.AigApiInterface;
import com.mobile.service.utils.Constants;
import com.mobile.service.utils.EventTask;
import com.mobile.service.utils.EventType;
import com.mobile.service.utils.output.Print;

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
        //TODO move to observable
        super.postSuccess(baseResponse);
        BamiloApplication.INSTANCE.setCart(null);
        PurchaseEntity cart = (PurchaseEntity) baseResponse.getContentData();
        BamiloApplication.INSTANCE.setCart(cart);
        Print.d(TAG, "ADD CART: " + cart.getTotal());

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
