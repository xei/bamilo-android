package com.mobile.constants;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * This Class has constants for the checkout activity, indicating which step is to be activated <p/><br> 
 *
 * Copyright (C) 2012 Rocket Internet - All Rights Reserved <p/>
 * 
 * Unauthorized copying of this file, via any medium is strictly prohibited <br>
 * Proprietary and confidential.
 * 
 * @author Nuno Castro
 * @updated sergiopereira
 * 
 * @version 1.00
 *
 */
public class ConstantsCheckout {

    /***** Checkout Type ******/
    public static final int CHECKOUT_ABOUT_YOU = 0;
    public static final int CHECKOUT_BILLING = 1;
    public static final int CHECKOUT_SHIPPING = 2;
    public static final int CHECKOUT_PAYMENT = 3;
    public static final int CHECKOUT_ORDER = 4;
    public static final int CHECKOUT_THANKS = 5;
    public static final int NO_CHECKOUT = -1;

    @IntDef({
            CHECKOUT_ABOUT_YOU,
            CHECKOUT_BILLING,
            CHECKOUT_SHIPPING,
            CHECKOUT_PAYMENT,
            CHECKOUT_ORDER,
            CHECKOUT_THANKS,
            NO_CHECKOUT,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface CheckoutType{}

}
