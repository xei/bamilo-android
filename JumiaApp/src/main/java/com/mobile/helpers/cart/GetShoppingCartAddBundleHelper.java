/**
 * @author Manuel Silva
 * 
 */
package com.mobile.helpers.cart;

import android.content.ContentValues;
import android.os.Bundle;

import com.mobile.app.JumiaApplication;
import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.objects.cart.AddedItemStructure;
import com.mobile.newFramework.objects.cart.PurchaseEntity;
import com.mobile.newFramework.objects.product.BundleList;
import com.mobile.newFramework.objects.product.pojo.ProductBundle;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.EventTask;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.utils.TrackerDelegator;
import com.mobile.utils.emarsys.EmarsysTracker;
import com.mobile.utils.pushwoosh.PushWooshTracker;

import java.util.ArrayList;

/**
 * Add product bundle to cart
 * 
 * @author Manuel Silva
 * @Modified Paulo Carvalho
 * 
 */
public class GetShoppingCartAddBundleHelper extends SuperBaseHelper {
    
    private static String TAG = GetShoppingCartAddBundleHelper.class.getSimpleName();

    @Override
    public EventType getEventType() {
        return EventType.ADD_PRODUCT_BUNDLE;
    }

    @Override
    protected EventTask setEventTask() {
        return EventTask.ACTION_TASK;
    }

    @Override
    public void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.addBundleShoppingCart);
    }

    @Override
    public void postSuccess(BaseResponse baseResponse) {
        super.postSuccess(baseResponse);
        Print.i(TAG,"ADD BUNDLE POST SUCCESS");
        JumiaApplication.INSTANCE.setCart(null);
        PurchaseEntity cart = (PurchaseEntity) baseResponse.getContentData();
        JumiaApplication.INSTANCE.setCart(cart);
        Print.d(TAG, "ADD CART: " + cart.getTotal());
        // Track the new cart value
        TrackerDelegator.trackAddToCart(cart);

        AddedItemStructure addItemStruct = new AddedItemStructure();
        addItemStruct.setPurchaseEntity(cart);
        baseResponse.getMetadata().setData(addItemStruct);
    }


    /**
     * Method used to create a request bundle.
     */
    public static Bundle createBundle(BundleList bundleList) {
        // Item data
        ContentValues values = new ContentValues();
        //id 869
        values.put(RestConstants.BUNDLE_ID, bundleList.getBundleId());
        ArrayList<String> array = new ArrayList<>();
        for (ProductBundle bundleListProduct : bundleList.getProducts()) {
            if(bundleListProduct.isChecked()){
                String sku = bundleListProduct.getSelectedSimple().getSku();
                array.add(sku);
            }
        }
        // Request data
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BUNDLE_DATA_KEY, values);
        bundle.putStringArrayList(Constants.BUNDLE_ARRAY_KEY, array);
        return bundle;
    }

}
