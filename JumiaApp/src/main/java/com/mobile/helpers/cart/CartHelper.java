package com.mobile.helpers.cart;

import com.mobile.newFramework.objects.cart.PurchaseCartItem;
import com.mobile.newFramework.objects.cart.PurchaseEntity;

import java.util.ArrayList;

/**
 * Created by Narbeh M. on 4/30/17.
 */

public final class CartHelper {

    public static String getCategoriesCommaSeparated(PurchaseEntity cart) {
        ArrayList<PurchaseCartItem> carts=  cart.getCartItems();
        String categories = "";
        for(PurchaseCartItem cat : carts) {
            categories += cat.getCategories();
        }

        return categories;
    }
}
