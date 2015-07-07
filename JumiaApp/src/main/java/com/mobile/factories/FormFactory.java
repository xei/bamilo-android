package com.mobile.factories;

import android.content.Context;
import android.util.LayoutDirection;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.mobile.constants.FormConstants;
import com.mobile.newFramework.forms.Form;
import com.mobile.newFramework.forms.IFormField;
import com.mobile.newFramework.utils.LogTagHelper;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.newFramework.utils.shop.ShopSelector;
import com.mobile.pojo.DynamicForm;
import com.mobile.pojo.DynamicFormItem;
import com.mobile.pojo.MetaFormExtractor;
import com.mobile.view.R;

import java.util.ArrayList;

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
    private final static String TAG = LogTagHelper.create( FormFactory.class );

    private static FormFactory factory = null;

    /**
     * TODO: To implement if re-use is necessary.
     * ATTENCION: Must remove from current parent view in order to re-use in another view : ((ViewGroup) userForm.getContainer().getParent()).removeView(userForm.getContainer());
     *
    *        private DynamicForm addressForm = null;
             private DynamicForm addressEditForm = null;
             private DynamicForm loginForm = null;
             private DynamicForm paymentForm = null;
             private DynamicForm registerForm = null;
             private DynamicForm pollForm = null;
             private DynamicForm ratingForm = null;
             private DynamicForm signupForm;
     */


    // private DynamicForm shippingForm = null;
    private float scale = 1;

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
        if (context != null && context.getResources() != null && context.getResources().getDisplayMetrics() != null) {
            scale = context.getResources().getDisplayMetrics().density;
        }
        Print.i(TAG, "code1register CREATING FORM : " + formType);
        switch (formType) {
            case FormConstants.ADDRESS_FORM:
                parent = createAddressForm(context, form, ctrlParams);
                break;
            case FormConstants.ADDRESS_EDIT_FORM:
                parent = createEditAddressForm(context, form, ctrlParams);
                break;
            case FormConstants.LOGIN_FORM:
                parent = createLoginForm(context, form, ctrlParams);
                break;
            case FormConstants.REGISTRATION_FORM:
                parent = createRegistrationForm(context, form, ctrlParams);
                break;
            case FormConstants.FORGET_PASSWORD_FORM:
                parent = createForgetPasswordForm(context, form, ctrlParams);
                break;
            case FormConstants.POLL_FORM:
                parent = createPollForm(context, form, ctrlParams);
                break;
            case FormConstants.SIGNUP_FORM:
                parent = createSignupForm(context, form, ctrlParams);
                break;
            case FormConstants.PAYMENT_DETAILS_FORM:
                parent = createPaymentMethodsForm(context, form, ctrlParams);
                break;
            case FormConstants.REVIEW_FORM:
            case FormConstants.RATING_FORM:
                parent = createRatingOptionsForm(context, form, ctrlParams);
            case FormConstants.REVIEW_SELLER_FORM:
                parent = createSellerReviewOptionsForm(context, form, ctrlParams);
                break;
            case FormConstants.CHANGE_PASSWORD_FORM:
                parent = createChangePasswordForm(context,form, ctrlParams);
                break;
        }
        return parent;
    }

    private DynamicForm createChangePasswordForm(Context context, Form form, LinearLayout.LayoutParams ctrlParams) {
        if (null == ctrlParams) {
            final int CTRLMARGIN_LEFT = 0;
            final int CTRLMARGIN_TOP = 0;
            final int CTRLMARGIN_RIGHT = 0;
            final int CTRLMARGIN_BOTTOM =0;

            ctrlParams = createParams(CTRLMARGIN_LEFT, CTRLMARGIN_TOP, CTRLMARGIN_RIGHT,CTRLMARGIN_BOTTOM);
        }

        DynamicForm dynamicForm = createGenericForm(context, form, ctrlParams);
        int margin = context.getResources().getDimensionPixelSize(R.dimen.rounded_margin_mid);
        ((LinearLayout.LayoutParams)dynamicForm.getContainer().getLayoutParams()).setMargins(margin, margin, margin, margin);
        return dynamicForm;
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

//        if (loginForm == null) {
//            loginForm = createGenericForm(context, form, ctrlParams);
//        }
//        return loginForm;
        return createGenericForm(context,form,ctrlParams);
   }

//    /**
//     * Create the payment methods edit form
//     *
//     * @param context The context where the form is to be inserted
//     * @param form The definition provided by the framework
//     * @return An instance of a DynamicForm with the form representation implemented
//     */
//    private DynamicForm createPaymentMethodsForm(Context context, Form form) {
//        return createPaymentMethodsForm(context, form, null);
//    }

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

            ctrlParams = createParams(CTRLMARGIN_LEFT, CTRLMARGIN_TOP, CTRLMARGIN_RIGHT,CTRLMARGIN_BOTTOM);
        }

        return createGenericForm(context, form, ctrlParams);
    }


    private DynamicForm createRatingOptionsForm(Context context, Form form, LinearLayout.LayoutParams ctrlParams) {
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
     * create the write seller review form
     * @param context
     * @param form
     * @return
     */
    private DynamicForm createSellerReviewOptionsForm(Context context, Form form, LinearLayout.LayoutParams ctrlParams) {
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
     * Create the poll form
     *
     * @param context The context where the form is to be inserted
     * @param form The definition provided by the framework
     * @return An instance of a DynamicForm with the form representation implemented
     */
    // Validate this method for poll
    private DynamicForm createPollForm(Context context, Form form, LinearLayout.LayoutParams ctrlParams) {
        if(ctrlParams == null) {
            final int CTRLMARGIN_LEFT = 0;
            final int CTRLMARGIN_TOP = (int) (5 * scale);
            final int CTRLMARGIN_RIGHT = 0;
            final int CTRLMARGIN_BOTTOM = 0;

            ctrlParams = createParams(CTRLMARGIN_LEFT, CTRLMARGIN_TOP, CTRLMARGIN_RIGHT,CTRLMARGIN_BOTTOM);
        }
        return createGenericForm(context, form, ctrlParams);
    }


    /**
     * Create the signup form
     *
     * @param context The context where the form is to be inserted
     * @param form The definition provided by the framework
     * @return An instance of a DynamicForm with the form representation implemented
     */
    // Validate this method for signup
    private DynamicForm createSignupForm(Context context, Form form, LinearLayout.LayoutParams ctrlParams) {
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
     * Create the payment methods edit form
     *
     * @param context The context where the form is to be inserted
     * @param form The definition provided by the framework
     * @return An instance of a DynamicForm with the form representation implemented
     */
    /*-private DynamicForm createShippingMethodsForm(Context context, Form form) {
        final int CTRLMARGIN_LEFT = 0;
        final int CTRLMARGIN_TOP = (int) (5 * scale);
        final int CTRLMARGIN_RIGHT = 0;
        final int CTRLMARGIN_BOTTOM = (int) (5 * scale);

        LinearLayout.LayoutParams ctrlParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        ctrlParams.setMargins(CTRLMARGIN_LEFT, CTRLMARGIN_TOP, CTRLMARGIN_RIGHT, CTRLMARGIN_BOTTOM);

        return createShippingMethodsForm(context, form, shippingForm, ctrlParams);
    }*/

    /**
     * This is used as base to create the given form. Here all the controls are instantiated.
     *
     * @param context The context where the form is to be inserted
     * @param form The definition provided by the framework
     * @param ctrlParams
     * @return n instance of a DynamicForm with the form representation implemented
     */
    private DynamicForm createGenericForm(Context context, Form form, ViewGroup.LayoutParams ctrlParams) {
        LinearLayout parent;
        Print.i(TAG, "code1form id : " + form.id + " name: " + form.name);
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

        groupLayout.setId( userForm.getNextId() );
        groupLayout.setOrientation(LinearLayout.HORIZONTAL);
        groupLayout.setLayoutParams(params);

        DynamicFormItem ctrl;

        ArrayList<IFormField> transformedFields = MetaFormExtractor.generateMetaFields( form.fields );
        MetaFormExtractor.dumpIFormField(transformedFields);

        for (IFormField frmEntry : transformedFields) {
            Print.d(TAG, "createGenericForm: " + frmEntry.getKey() + " inputType = " + frmEntry.getInputType());
            ctrl = new DynamicFormItem(userForm, context, frmEntry);

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
     * @param CTRLMARGIN_LEFT
     * @param CTRLMARGIN_TOP
     * @param CTRLMARGIN_RIGHT
     * @param CTRLMARGIN_BOTTOM
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
     *
     * @param ctrlParams
     * @param CTRLMARGIN_LEFT
     * @param CTRLMARGIN_RIGHT
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

    /**
     * This is used to create the Shipping methods form. Here all the controls are instantiated.
     *
     * @param context The context where the form is to be inserted
     * @param form The definition provided by the framework
     * @param userForm
     * @param ctrlParams
     * @return n instance of a DynamicForm with the form representation implemented
     */
    /*-private DynamicForm createShippingMethodsForm(Context context, Form form, DynamicForm userForm, ViewGroup.LayoutParams ctrlParams) {

        LinearLayout parent;
        Log.i(TAG,"code1form id : "+form.id+" name: "+form.name);
        if (null == userForm) {
            parent = new LinearLayout(context);
            LinearLayout.LayoutParams frmParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            parent.setOrientation(LinearLayout.VERTICAL);
            parent.setLayoutParams(frmParams);

            userForm = new DynamicForm(parent);
            userForm.setForm( form );

            LinearLayout groupLayout = new LinearLayout(context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            groupLayout.setId( userForm.getNextId() );
            groupLayout.setOrientation(LinearLayout.HORIZONTAL);
            groupLayout.setLayoutParams(params);

            DynamicFormItem ctrl;

            ArrayList<IFormField> transformedFields = MetaFormExtractor.generateMetaFields( form.fields );
            MetaFormExtractor.dumpIFormField(transformedFields);

            for (IFormField frmEntry : transformedFields) {
                Log.d( TAG, "createShippingMethodsForm: " + frmEntry.getKey() + " inputType = " + frmEntry.getInputType() );
                ctrl = new DynamicFormItem(userForm, context, frmEntry);
                userForm.addControl(ctrl, ctrlParams);
            }
        } else {
            ((ViewGroup) userForm.getContainer().getParent()).removeView(userForm.getContainer());
        }

        return userForm;
    }*/

}