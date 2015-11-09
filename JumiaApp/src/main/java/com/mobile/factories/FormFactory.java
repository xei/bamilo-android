package com.mobile.factories;

import android.content.Context;
import android.util.LayoutDirection;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.mobile.constants.FormConstants;
import com.mobile.newFramework.forms.Form;
import com.mobile.newFramework.forms.IFormField;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.newFramework.utils.shop.ShopSelector;
import com.mobile.pojo.DynamicForm;
import com.mobile.pojo.DynamicFormItem;
import com.mobile.view.R;

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
 * @modified ricardosoares
 */
public class FormFactory {

    private final static String TAG = FormFactory.class.getSimpleName();

    private static FormFactory factory = null;

    /**
     * The constructor is private to prevent the creation of the object
     */
    private FormFactory() {
    }

    /**
     * Gets the Singleton for the factory
     *
     * @return The form factory
     */
    public static FormFactory getSingleton() {
        if (null == factory) {
            factory = new FormFactory();
        }
        return factory;
    }

    /**
     * Creates the form for a given type using a definition provided from the framework
     *
     * @param formType
     *            The type of form to create. This value should be placed on the FormConstants
     * @param context
     *            The context where the form is to be inserted
     * @param form
     *            The definition provided by the framework
     *
     * @return An instance of a DynamicForm with the form representation implemented
     */
    public DynamicForm CreateForm(int formType, Context context, Form form) {
        return CreateForm(formType, context, form, null);
    }


    private DynamicForm createChangePasswordForm(Context context, Form form, LinearLayout.LayoutParams ctrlParams) {
        if (null == ctrlParams) {
            final int CTRLMARGIN_LEFT = 0;
            final int CTRLMARGIN_TOP = context.getResources().getDimensionPixelSize(R.dimen.rounded_margin_mid);
            final int CTRLMARGIN_RIGHT = 0;
            final int CTRLMARGIN_BOTTOM =0;

            ctrlParams = createParams(CTRLMARGIN_LEFT, CTRLMARGIN_TOP, CTRLMARGIN_RIGHT,CTRLMARGIN_BOTTOM);
        }

        return createGenericForm(context, form, ctrlParams);
    }

    /**
     * Creates the form for a given type using a definition provided from the framework
     *
     * @param formType
     *            The type of form to create. This value should be placed on the FormConstants
     * @param context
     *            The context where the form is to be inserted
     * @param form
     *            The definition provided by the framework
     * @param ctrlParams
     *            LayoutParams to use instead of default
     *
     * @return An instance of a DynamicForm with the form representation implemented
     */
    public DynamicForm CreateForm(int formType, Context context, Form form, LinearLayout.LayoutParams ctrlParams) {
        DynamicForm parent = null;
        Print.i(TAG, "code1register CREATING FORM : " + formType);
        switch (formType) {
            case FormConstants.ADDRESS_FORM:
                parent = createAddressForm(context, form, ctrlParams);
                break;
            case FormConstants.ADDRESS_EDIT_FORM:
                parent = createEditAddressForm(context, form, ctrlParams);
                break;
            case FormConstants.LOGIN_FORM:
                form.setType(formType); // Used to show icons
                form.hideAsterisks(); // Used to hide asterisks because everything is mandatory
                parent = createLoginForm(context, form, ctrlParams);
                break;
            case FormConstants.REGISTRATION_FORM:
                form.hideAsterisks(); // Used to hide asterisks because everything is mandatory
            case FormConstants.USER_DATA_FORM:
                form.setType(formType);  // Used to show icons
                parent = createRegistrationForm(context, form, ctrlParams);
                break;
            case FormConstants.FORGET_PASSWORD_FORM:
                form.hideAsterisks(); // Used to hide asterisks because everything is mandatory
                parent = createForgetPasswordForm(context, form, ctrlParams);
                break;
            case FormConstants.PAYMENT_DETAILS_FORM:
                parent = createPaymentMethodsForm(context, form, ctrlParams);
                break;
            case FormConstants.CHANGE_PASSWORD_FORM:
                parent = createChangePasswordForm(context,form, ctrlParams);
                break;
        }
        return parent;
    }

    /**
     * Creates the form for the address details
     *
     * @param context The context where the form is to be inserted
     * @param form The definition provided by the framework
     *
     * @return An instance of a DynamicForm with the form representation implemented
     */
    private DynamicForm createAddressForm(Context context, Form form, LinearLayout.LayoutParams ctrlParams) {
        if(ctrlParams == null) {
            final int CTRLMARGIN_LEFT = 0;
            final int CTRLMARGIN_TOP = context.getResources().getDimensionPixelSize(R.dimen.form_top_margin);
            final int CTRLMARGIN_RIGHT = 0;
            final int CTRLMARGIN_BOTTOM = 0;

            ctrlParams = createParams(CTRLMARGIN_LEFT, CTRLMARGIN_TOP, CTRLMARGIN_RIGHT,CTRLMARGIN_BOTTOM);
        }
        return createGenericForm(context, form, ctrlParams);
    }

    /**
     * Creates the form for the address details
     *
     * @param context The context where the form is to be inserted
     * @param form The definition provided by the framework
     *
     * @return An instance of a DynamicForm with the form representation implemented
     */
    private DynamicForm createEditAddressForm(Context context, Form form, LinearLayout.LayoutParams ctrlParams) {
        if(ctrlParams == null) {
            final int CTRLMARGIN_LEFT = 0;
            final int CTRLMARGIN_TOP = context.getResources().getDimensionPixelSize(R.dimen.form_top_margin);
            final int CTRLMARGIN_RIGHT = 0;
            final int CTRLMARGIN_BOTTOM = 0;

            ctrlParams = createParams(CTRLMARGIN_LEFT, CTRLMARGIN_TOP, CTRLMARGIN_RIGHT,CTRLMARGIN_BOTTOM);
        }
        return createGenericForm(context, form, ctrlParams);

    }

    /**
     * Create the login form
     *
     * @param context The context where the form is to be inserted
     * @param form The definition provided by the framework
     * @return An instance of a DynamicForm with the form representation implemented
     */
    private DynamicForm createLoginForm(Context context, Form form, LinearLayout.LayoutParams ctrlParams) {
        if(ctrlParams == null) {
            final int CTRLMARGIN_LEFT = 0;
            final int CTRLMARGIN_TOP = context.getResources().getDimensionPixelSize(R.dimen.form_top_margin);
            final int CTRLMARGIN_RIGHT = 0;
            final int CTRLMARGIN_BOTTOM = 0;

            ctrlParams = createParams(CTRLMARGIN_LEFT, CTRLMARGIN_TOP, CTRLMARGIN_RIGHT,CTRLMARGIN_BOTTOM);
        }

        return createGenericForm(context,form,ctrlParams);
   }


    /**
     * Create the payment methods edit form
     *
     * @param context The context where the form is to be inserted
     * @param form The definition provided by the framework
     * @param ctrlParams LayoutParams to use instead of default
     * @return An instance of a DynamicForm with the form representation implemented
     */
    private DynamicForm createPaymentMethodsForm(Context context, Form form, LinearLayout.LayoutParams ctrlParams) {
        if (null == ctrlParams) {
            final int CTRLMARGIN_LEFT = 0;
            final int CTRLMARGIN_TOP = 0;
            final int CTRLMARGIN_RIGHT = 0;
            final int CTRLMARGIN_BOTTOM = 0;

            ctrlParams = createParams(CTRLMARGIN_LEFT, CTRLMARGIN_TOP, CTRLMARGIN_RIGHT, CTRLMARGIN_BOTTOM);
        }

        return createGenericForm(context, form, ctrlParams);
    }

    /**
     * Create the user registration form
     *
     * @param context The context where the form is to be inserted
     * @param form The definition provided by the framework
     * @return An instance of a DynamicForm with the form representation implemented
     */
    private DynamicForm createRegistrationForm(Context context, Form form, LinearLayout.LayoutParams ctrlParams) {
        if(ctrlParams == null) {
            final int CTRLMARGIN_LEFT = 0;
            final int CTRLMARGIN_TOP = context.getResources().getDimensionPixelSize(R.dimen.form_top_margin);
            final int CTRLMARGIN_RIGHT = 0;
            final int CTRLMARGIN_BOTTOM = 0;

            ctrlParams = createParams(CTRLMARGIN_LEFT, CTRLMARGIN_TOP, CTRLMARGIN_RIGHT,CTRLMARGIN_BOTTOM);
        }
        return createGenericForm(context, form, ctrlParams);
    }

    /**
     * Create the user forget password form
     *
     * @param context The context where the form is to be inserted
     * @param form The definition provided by the framework
     * @return An instance of a DynamicForm with the form representation implemented
     */
    private DynamicForm createForgetPasswordForm(Context context, Form form, LinearLayout.LayoutParams ctrlParams) {
        if(ctrlParams == null) {
            final int CTRLMARGIN_LEFT = 0;
            final int CTRLMARGIN_TOP = context.getResources().getDimensionPixelSize(R.dimen.form_top_margin);
            final int CTRLMARGIN_RIGHT = 0;
            final int CTRLMARGIN_BOTTOM = 0;

            ctrlParams = createParams(CTRLMARGIN_LEFT, CTRLMARGIN_TOP, CTRLMARGIN_RIGHT,CTRLMARGIN_BOTTOM);
        }
        return createGenericForm(context, form, ctrlParams);
    }

    /**
     * This is used as base to create the given form. Here all the controls are instantiated.
     *
     * @param context The context where the form is to be inserted
     * @param form The definition provided by the framework
     * @return n instance of a DynamicForm with the form representation implemented
     */
    private DynamicForm createGenericForm(Context context, Form form, ViewGroup.LayoutParams ctrlParams) {
        LinearLayout parent;
        if(context == null){
            return null;
        }
        parent = new LinearLayout(context);

        LinearLayout.LayoutParams frmParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        parent.setOrientation(LinearLayout.VERTICAL);
        parent.setLayoutParams(frmParams);

        DynamicForm userForm = new DynamicForm(parent);
        userForm.setForm( form );

        // Used for dates with day/month/year
        LinearLayout groupLayout = new LinearLayout(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        //#RTL
        if(ShopSelector.isRtl()){
            groupLayout.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);
        }

        groupLayout.setId(userForm.getNextId());
        groupLayout.setOrientation(LinearLayout.HORIZONTAL);
        groupLayout.setLayoutParams(params);

        for (IFormField frmEntry : form.getFields()) {
            Print.d(TAG, "createGenericForm: " + frmEntry.getKey() + " inputType = " + frmEntry.getInputType());
            DynamicFormItem ctrl = new DynamicFormItem(userForm, context, frmEntry);

            if (ctrl.isMeta() || ctrl.hasNoType()) {
                // Don't waste space with meta fields nor field without type
                Print.i(TAG, "Meta or no type field");
                userForm.addControl(ctrl, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1));
            } else if ( ! ctrl.isDatePart() ) {
                userForm.addControl(ctrl, ctrlParams);
            } else {
                userForm.addGroupedControl(groupLayout, ctrl, ctrlParams);
            }

        }

        return userForm;
    }

    /**
     *
     * @return Params created based on margins
     */
    private LinearLayout.LayoutParams createParams(final int CTRLMARGIN_LEFT, final int CTRLMARGIN_TOP,final int CTRLMARGIN_RIGHT, final int CTRLMARGIN_BOTTOM){
        LinearLayout.LayoutParams ctrlParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        ctrlParams.setMargins(CTRLMARGIN_LEFT, CTRLMARGIN_TOP, CTRLMARGIN_RIGHT, CTRLMARGIN_BOTTOM);
        setRtl(ctrlParams, CTRLMARGIN_LEFT, CTRLMARGIN_RIGHT);
        return ctrlParams;
    }

    /**
     * Prepare params for RTL.
     */
    private void setRtl(LinearLayout.LayoutParams ctrlParams, final int CTRLMARGIN_LEFT, final int CTRLMARGIN_RIGHT){
        //#RTL
        int currentApiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentApiVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1){
            ctrlParams.setLayoutDirection(LayoutDirection.LOCALE);
            ctrlParams.setMarginStart(CTRLMARGIN_LEFT);
            ctrlParams.setMarginEnd(CTRLMARGIN_RIGHT);
        }
    }

}