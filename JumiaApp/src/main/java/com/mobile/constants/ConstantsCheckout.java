package com.mobile.constants;

import android.support.annotation.IntDef;

import com.mobile.controllers.fragments.FragmentType;

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

    /**
     * method that associates checkout step position with specific fragment
     * @param position
     * @return fragment at the current position
     */
    public static FragmentType getFragmentType (int position) {
        FragmentType fragmentType;

        switch (position){
            case CHECKOUT_BILLING:
                fragmentType = FragmentType.CHECKOUT_MY_ADDRESSES;
                break;
            case CHECKOUT_SHIPPING:
                fragmentType = FragmentType.CHECKOUT_SHIPPING;
                break;
            case CHECKOUT_PAYMENT:
                fragmentType = FragmentType.CHECKOUT_PAYMENT;
                break;
            case CHECKOUT_ORDER:
                fragmentType = FragmentType.CHECKOUT_FINISH;
                break;
            case CHECKOUT_THANKS:
                fragmentType = FragmentType.CHECKOUT_THANKS;
                break;
            case NO_CHECKOUT:
            default:
                fragmentType = FragmentType.UNKNOWN;
                break;
        }
        return fragmentType;
    }

}
