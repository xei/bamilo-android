package com.bamilo.android.appmodule.bamiloapp.helpers.cart;

import android.content.ContentValues;
import android.os.Bundle;
import android.text.TextUtils;

import com.bamilo.android.appmodule.bamiloapp.app.BamiloApplication;
import com.bamilo.android.appmodule.bamiloapp.helpers.SuperBaseHelper;
import com.bamilo.android.framework.service.database.LastViewedTableHelper;
import com.bamilo.android.framework.service.objects.cart.AddedItemStructure;
import com.bamilo.android.framework.service.objects.cart.PurchaseEntity;
import com.bamilo.android.framework.service.pojo.BaseResponse;
import com.bamilo.android.framework.service.pojo.RestConstants;
import com.bamilo.android.framework.service.requests.BaseRequest;
import com.bamilo.android.framework.service.requests.RequestBundle;
import com.bamilo.android.framework.service.rest.interfaces.AigApiInterface;
import com.bamilo.android.framework.service.utils.Constants;
import com.bamilo.android.framework.service.utils.EventTask;
import com.bamilo.android.framework.service.utils.EventType;
import com.bamilo.android.framework.service.utils.output.Print;

/**
 * Get Shopping Cart Items helper
 * 
 * @author Manuel Silva
 * 
 */
public class ShoppingCartAddItemHelper extends SuperBaseHelper {
    
    private static final String TAG = ShoppingCartAddItemHelper.class.getSimpleName();

    public static final String PRODUCT_POS_TAG = "item_pos";

    public static final String REMOVE_RECENTLY_VIEWED_TAG = "rmv_rv";
    
    private String mCurrentSku;
    
    private int mCurrentPos = -1;

    private boolean isToRemoveFromLastViewed;


    @Override
    public EventType getEventType() {
        return EventType.ADD_ITEM_TO_SHOPPING_CART_EVENT;
    }

    @Override
    protected EventTask setEventTask() {
        return EventTask.ACTION_TASK;
    }

    @Override
    protected RequestBundle createRequest(Bundle args) {
        // Get specific data
        mCurrentPos = args.getInt(PRODUCT_POS_TAG, -1);
        mCurrentSku = args.getString(RestConstants.SKU);
        isToRemoveFromLastViewed = args.getBoolean(REMOVE_RECENTLY_VIEWED_TAG, false);
        return super.createRequest(args);
    }

    @Override
    public void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.addItemShoppingCart);
    }

    @Override
    public void postSuccess(BaseResponse baseResponse) {
        super.postSuccess(baseResponse);
        PurchaseEntity cart = (PurchaseEntity) baseResponse.getContentData();
        BamiloApplication.INSTANCE.setCart(cart);
        Print.d(TAG, "ADD CART: " + cart.getTotal());
        AddedItemStructure addItemStruct = new AddedItemStructure();
        addItemStruct.setPurchaseEntity(cart);
        addItemStruct.setCurrentPos(mCurrentPos);
        baseResponse.getMetadata().setData(addItemStruct);
        // Validate if is to remove from LastViewed
        if (isToRemoveFromLastViewed && !TextUtils.isEmpty(mCurrentSku)) {
            LastViewedTableHelper.removeLastViewed(mCurrentSku);
        }
    }

    /**
     * Method used to create a request bundle.
     */
    public static Bundle createBundle(String sku) {
        // Item data
        ContentValues values = new ContentValues();
        values.put(RestConstants.SKU, sku);
        // Request data
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BUNDLE_DATA_KEY, values);
        return bundle;
    }
}
