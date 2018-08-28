package com.bamilo.android.appmodule.bamiloapp.factories;

import android.content.Context;
import android.support.annotation.NonNull;

import com.bamilo.android.appmodule.bamiloapp.constants.FormConstants;
import com.bamilo.android.framework.service.forms.Form;
import com.bamilo.android.appmodule.bamiloapp.pojo.DynamicForm;
import com.bamilo.android.R;

/**
 * A Singleton factory for the creation of dynamic forms based on information returned by the framework <p/><br>
 *
 * Copyright (C) 2012 Rocket Internet - All Rights Reserved <p/>
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited <br>
 * Proprietary and confidential.
 *
 * @author Nuno Castro
 * @version 1.1
 *
 * 2012/06/15
 *
 * @modified spereira
 */
public class FormFactory {

    /**
     * Creates the form for a given type using a definition provided from the framework
     * @return An instance of a DynamicForm with the form representation implemented
     */
    public static DynamicForm create(@FormConstants.DynamicFormTypes int formType, @NonNull Context context, @NonNull Form form) {
        DynamicForm parent = null;
        switch (formType) {
            case FormConstants.LOGIN_FORM:
            case FormConstants.REGISTRATION_FORM:
            case FormConstants.FORGET_PASSWORD_FORM:
            case FormConstants.CHANGE_PASSWORD_FORM:
            case FormConstants.RATING_FORM:
                parent = new DynamicForm(context, form).addMarginTop(R.dimen.form_top_margin).isMandatory().showIcons(formType);
                break;
            case FormConstants.USER_DATA_FORM:
            case FormConstants.ADDRESS_EDIT_FORM:
            case FormConstants.ADDRESS_FORM:
                parent = new DynamicForm(context, form).addMarginTop(R.dimen.form_top_margin).showIcons(formType);
                break;
            case FormConstants.NEWSLETTER_UN_SUBSCRIBE_FORM:
            case FormConstants.NEWSLETTER_PREFERENCES_FORM:
                parent = new DynamicForm(context, form).addMiddleDivider().addEndDivider().showIcons(formType);
                break;
            case FormConstants.PAYMENT_DETAILS_FORM:
                parent = new DynamicForm(context, form);
                break;
            case FormConstants.ORDER_RETURN_REASON_FORM:
            case FormConstants.NEWSLETTER_FORM:
            case FormConstants.RETURN_METHOD_FORM:
            case FormConstants.RETURN_REFUND_FORM:
                parent = new DynamicForm(context, form).isMandatory().showIcons(formType);
                break;
        }
        return parent.build();
    }

}