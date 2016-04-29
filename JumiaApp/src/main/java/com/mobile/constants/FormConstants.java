package com.mobile.constants;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Constants to use for the dynamic form generation; Here we declare a constant for each form we
 * need to implement
 * <p/>
 * <br>
 * <p/>
 * Copyright (C) 2012 Rocket Internet - All Rights Reserved
 * <p/>
 * <p/>
 * Unauthorized copying of this file, via any medium is strictly prohibited <br>
 * Proprietary and confidential.
 *
 * @author Nuno Castro
 * @version 1.00
 *          <p/>
 *          2012/06/15
 */
public class FormConstants {

    public final static int ADDRESS_FORM = 1;
    public final static int LOGIN_FORM = 2;
    public final static int PAYMENT_DETAILS_FORM = 3;
    public final static int REGISTRATION_FORM = 4;
    public final static int FORGET_PASSWORD_FORM = 5;
    public final static int ADDRESS_EDIT_FORM = 6;
    public static final int RATING_FORM = 7;
    public static final int CHANGE_PASSWORD_FORM = 8;
    public static final int USER_DATA_FORM = 9;
    public static final int NEWSLETTER_FORM = 10;
    public static final int NEWSLETTER_PREFERENCES_FORM = 11;
    public static final int NEWSLETTER_UN_SUBSCRIBE_FORM = 12;
    public static final int ORDER_RETURN_REASON_FORM = 13;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({ADDRESS_FORM,
            LOGIN_FORM,
            PAYMENT_DETAILS_FORM,
            REGISTRATION_FORM,
            FORGET_PASSWORD_FORM,
            ADDRESS_EDIT_FORM,
            RATING_FORM,
            CHANGE_PASSWORD_FORM,
            USER_DATA_FORM,
            NEWSLETTER_FORM,
            NEWSLETTER_PREFERENCES_FORM,
            NEWSLETTER_UN_SUBSCRIBE_FORM,
            ORDER_RETURN_REASON_FORM
    })
    public @interface DynamicFormTypes{}
}
