package com.mobile.constants;

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

    // Checkout Steps
    
    public static final String CHECKOUT_THANKS_ORDER_NR = "order_nr";  
    public static final String CHECKOUT_THANKS_ORDER_SHIPPING = "transaction_shipping"; 
    public static final String CHECKOUT_THANKS_ORDER_TAX = "transaction_tax"; 
    public static final String CHECKOUT_THANKS_PAYMENT_METHOD = "payment_method";
    public static final String CHECKOUT_THANKS_ORDER_TOTAL = "order_grand_total";
    
    public static final int CHECKOUT_ABOUT_YOU = -1;
    public static final int CHECKOUT_BILLING = -2;
    public static final int CHECKOUT_SHIPPING = -3;
    public static final int CHECKOUT_PAYMENT = -4;
    public static final int CHECKOUT_ORDER = -5;
    public static final int CHECKOUT_THANKS = -6;
    public static final int CHECKOUT_NO_SET_HEADER = -7;
    
    public static final int NO_CHECKOUT = 0;

    public static final int TAB_CHECKOUT_ABOUT_YOU = 0;
    public static final int TAB_CHECKOUT_BILLING = 1;
    public static final int TAB_CHECKOUT_SHIPPING = 2;
    public static final int TAB_CHECKOUT_PAYMENT = 3;

}
