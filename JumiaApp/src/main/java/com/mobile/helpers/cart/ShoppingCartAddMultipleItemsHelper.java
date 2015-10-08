package com.mobile.helpers.cart;

import android.os.Bundle;

import com.mobile.app.JumiaApplication;
import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.objects.cart.PurchaseEntity;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.EventTask;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.utils.TrackerDelegator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
        return EventTask.SMALL_TASK;
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
    public void createErrorBundleParams(BaseResponse baseResponse, Bundle bundle) {
        super.createErrorBundleParams(baseResponse, bundle);
        handleError(baseResponse, bundle);
    }

    @Override
    public void createSuccessBundleParams(BaseResponse baseResponse, Bundle bundle) {
        //TODO move to observable
        super.createSuccessBundleParams(baseResponse, bundle);
        JumiaApplication.INSTANCE.setCart(null);
        PurchaseEntity cart = (PurchaseEntity) baseResponse.getMetadata().getData();
        JumiaApplication.INSTANCE.setCart(cart);
        Print.d(TAG, "ADD CART: " + cart.getTotal());
        // Track the new cart value
        TrackerDelegator.trackCart(cart.getPriceForTracking(), cart.getCartCount(), cart.getAttributeSetIdList());

        bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, cart);
        handleSuccess(baseResponse, bundle);
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
    protected void handleSuccess(BaseResponse baseResponse, Bundle bundle) {
        Map<String, String> successMessages = baseResponse.getSuccessMessages();
        if (successMessages != null && !successMessages.isEmpty()) {
            bundle.putSerializable(Constants.BUNDLE_RESPONSE_SUCCESS_MESSAGE_KEY, checkAddedProducts(successMessages));
        }
        handleError(baseResponse, bundle);
    }

    //@Override
    protected void handleError(BaseResponse baseResponse, Bundle bundle) {
        Map<String, List<String>> errorMessages = baseResponse.getErrorMessages();
        if (errorMessages != null && !errorMessages.isEmpty()) {
            bundle.putSerializable(Constants.BUNDLE_RESPONSE_ERROR_MESSAGE_KEY, checkNotAddedProducts(errorMessages));
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
    protected ArrayList<String> checkNotAddedProducts(Map<String, List<String>> errorMessages) {
        ArrayList<String> notAdded = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : errorMessages.entrySet()) {
            String value = productBySku.get(entry.getKey());
            if (value != null) {
                notAdded.add(value);
            }
        }
        return notAdded;
    }

}
