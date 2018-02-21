package com.mobile.classes.models;

import android.content.ContentValues;
import android.text.TextUtils;

import com.mobile.constants.tracking.CategoryConstants;
import com.mobile.constants.tracking.EventActionKeys;
import com.mobile.service.objects.cart.PurchaseCartItem;
import com.mobile.service.objects.product.pojo.ProductBase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mohsen on 1/14/18.
 */

public class SimpleEventModelFactory {

    public static SimpleEventModel createModelForCheckoutStart(List<PurchaseCartItem> cartItems) {
        if (cartItems != null && !cartItems.isEmpty()) {
            SimpleEventModel sem = new SimpleEventModel();
            sem.category = CategoryConstants.CHECKOUT;
            sem.action = EventActionKeys.CHECKOUT_START;
            List<String> skus = new ArrayList<>();
            long totalPrice = 0;
            for (PurchaseCartItem item: cartItems) {
                skus.add(item.getSku());
                totalPrice += item.getPrice();
            }
            sem.label = TextUtils.join(",", skus);
            sem.value = totalPrice;

            return sem;
        }
        return null;
    }

    public static SimpleEventModel createModelForCatalogFilter(ContentValues filters) {
        if (filters != null) {
            SimpleEventModel sem = new SimpleEventModel();
            sem.category = CategoryConstants.CATALOG;
            sem.action = EventActionKeys.FILTER;
            sem.label = filters.toString();
            sem.value = SimpleEventModel.NO_VALUE;

            return sem;
        }
        return null;
    }

    public static SimpleEventModel createModelForPDV(ProductBase product, String sourceScreenName) {
        if (product != null) {
            SimpleEventModel sem = new SimpleEventModel();
            sem.category = sourceScreenName;
            sem.action = EventActionKeys.VIEW_PRODUCT;
            sem.label = product.getSku();
            sem.value = (long) product.getPrice();

            return sem;
        }
        return null;
    }
}
