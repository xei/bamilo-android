package com.mobile.helpers.cart;

import android.os.Bundle;

import com.mobile.app.JumiaApplication;
import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.objects.cart.PurchaseEntity;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.newFramework.utils.EventTask;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.utils.TrackerDelegator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ShoppingCartAddMultipleItemsHelper extends SuperBaseHelper {

    private static String TAG = ShoppingCartAddMultipleItemsHelper.class.getSimpleName();

    public static String PRODUCT_LIST_TAG = "productList";

    public static String getProductListSimpleSkuTag(int counter) {
        return PRODUCT_LIST_TAG + "[" + counter + "]" + "[" + ShoppingCartAddItemHelper.PRODUCT_SKU_TAG + "]";
    }

    public static String getProductListSkuTag(int counter) {
        return PRODUCT_LIST_TAG + "[" + counter + "]" + "[" + ShoppingCartAddItemHelper.PRODUCT_TAG + "]";
    }

    public static String ADD_ITEMS = "add_items";

    private HashMap<String, String> productBySku;


    @Override
    public EventType getEventType() {
        return EventType.ADD_ITEMS_TO_SHOPPING_CART_EVENT;
    }

    @Override
    protected EventTask setEventTask() {
        return EventTask.ACTION_TASK;
    }

    @Override
    protected Map<String, String> getRequestData(Bundle args) {
        productBySku = (HashMap<String, String>) args.getSerializable(ADD_ITEMS);
        return createValues(productBySku);
    }

    @Override
    public void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.addMultipleItemsShoppingCart);
    }

    @Override
    public void postError(BaseResponse baseResponse) {
        super.postError(baseResponse);
        AddMultipleStruct addMultipleStruct = new AddMultipleStruct();
        handleError(baseResponse, addMultipleStruct);
        baseResponse.getMetadata().setData(addMultipleStruct);
    }

    @Override
    public void postSuccess(BaseResponse baseResponse) {
        //TODO move to observable
        super.postSuccess(baseResponse);
        JumiaApplication.INSTANCE.setCart(null);
        PurchaseEntity cart = (PurchaseEntity) baseResponse.getContentData();
        JumiaApplication.INSTANCE.setCart(cart);
        Print.d(TAG, "ADD CART: " + cart.getTotal());
        // Track the new cart value
        TrackerDelegator.trackCart(cart.getPriceForTracking(), cart.getCartCount(), cart.getAttributeSetIdList());

//        bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, cart);
        AddMultipleStruct addMultipleStruct = new AddMultipleStruct();
        addMultipleStruct.setPurchaseEntity(cart);
        handleSuccess(baseResponse, addMultipleStruct);
        baseResponse.getMetadata().setData(addMultipleStruct);
    }

    private Map<String, String> createValues(HashMap<String, String> values) {
        int counter = 0;
        Map<String, String> data = new HashMap<>();
        for (Map.Entry<String, String> entry : values.entrySet()) {
            data.put(getProductListSkuTag(counter), entry.getKey());
            data.put(getProductListSimpleSkuTag(counter), entry.getValue());
            counter++;
        }
        return data;
    }


    //@Override
    protected void handleSuccess(BaseResponse baseResponse, AddMultipleStruct struct) {
        Map successMessages = baseResponse.getSuccessMessages();
        if (CollectionUtils.isNotEmpty(successMessages)) {
            struct.setSuccessMessages(checkAddedProducts(successMessages));
        }
        handleError(baseResponse, struct);
    }

    //@Override
    protected void handleError(BaseResponse baseResponse, AddMultipleStruct struct) {
        Map errorMessages = baseResponse.getErrorMessages();
        if (CollectionUtils.isNotEmpty(errorMessages)) {
            struct.setErrorMessages(checkNotAddedProducts(errorMessages));
        }
    }

    /**
     * Check added products from result object
     *
     * @return Array of products sku's
     */
    protected ArrayList<String> checkAddedProducts(Map<String, String> successMessages) {
        ArrayList<String> added = new ArrayList<>();
        for (Map.Entry<String, String> entry : successMessages.entrySet()) {
            String value = productBySku.get(entry.getKey());
            if (value != null) {
                added.add(value);
            }
        }
        return added;
    }

    /**
     * Check products that were not added from result object
     *
     * @return Array of products sku's
     */
    protected ArrayList<String> checkNotAddedProducts(Map<String, String> errorMessages) {
        ArrayList<String> notAdded = new ArrayList<>();
        for (Map.Entry<String, String> entry : errorMessages.entrySet()) {
            String value = productBySku.get(entry.getKey());
            if (value != null) {
                notAdded.add(value);
            }
        }
        return notAdded;
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
