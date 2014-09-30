package pt.rocket.factories;

import java.util.ArrayList;

import pt.rocket.constants.FormConstants;
import pt.rocket.forms.Form;
import pt.rocket.forms.IFormField;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.pojo.DynamicForm;
import pt.rocket.pojo.DynamicFormItem;
import pt.rocket.pojo.MetaFormExtractor;
import pt.rocket.view.R;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import de.akquinet.android.androlog.Log;

/**
 * A Singleton factory for the creation of dynamic forms based on information returned by the framework <p/><br> 
 *
 * Copyright (C) 2012 Rocket Internet - All Rights Reserved <p/>
 * 
 * Unauthorized copying of this file, via any medium is strictly prohibited <br>
 * Proprietary and confidential.
 * 
 * @author Nuno Castro
 * @version 1.00
 * 
 * 2012/06/15
 * 
 */
public class FormFactory {
	private final static String TAG = LogTagHelper.create( FormFactory.class );

    private static FormFactory factory = null;
    private DynamicForm addressForm = null;
    private DynamicForm addressEditForm = null;
    private DynamicForm loginForm = null;
    private DynamicForm paymentForm = null;
    private DynamicForm registerForm = null;
    private DynamicForm pollForm = null;
    // private DynamicForm shippingForm = null;
    private float scale = 1;

    private DynamicForm signupForm;

    

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
        Log.i(TAG, "code1register CREATING FORM : "+formType);
        switch (formType) {
        case FormConstants.ADDRESS_FORM:
            parent = createAddressForm(context, form);
            break;
        case FormConstants.ADDRESS_EDIT_FORM:
            parent = createEditAddressForm(context, form);
            break;
        case FormConstants.LOGIN_FORM:
            parent = createLoginForm(context, form);
            break;
        case FormConstants.REGISTRATION_FORM:
            parent = createRegistrationForm(context, form);
            break;
        case FormConstants.FORGET_PASSWORD_FORM:
            parent = createForgetPasswordForm(context, form);
            break;
        case FormConstants.POLL_FORM:
            parent = createPollForm(context, form);
            break;
        case FormConstants.SIGNUP_FORM:
            parent = createSignupForm(context, form);
            break;
        /*-case FormConstants.SHIPPING_DETAILS_FORM:
            parent = createShippingMethodsForm(context, form);
            break;*/
        case FormConstants.PAYMENT_DETAILS_FORM:
            parent = createPaymentMethodsForm(context, form, ctrlParams);
            break;
        }
        
//        FontLoader.applyDefaultFont( parent.getContainer());
        
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
    private DynamicForm createAddressForm(Context context, Form form) {
        final int CTRLMARGIN_LEFT = 0;
        final int CTRLMARGIN_TOP = context.getResources().getDimensionPixelSize(R.dimen.form_top_margin);
        final int CTRLMARGIN_RIGHT = 0;
        final int CTRLMARGIN_BOTTOM = 0;

        LinearLayout.LayoutParams ctrlParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        ctrlParams.setMargins(CTRLMARGIN_LEFT, CTRLMARGIN_TOP, CTRLMARGIN_RIGHT, CTRLMARGIN_BOTTOM);

        return createGenericForm(context, form, addressForm, ctrlParams);

    }
    
    /**
     * Creates the form for the address details
     * 
     * @param context The context where the form is to be inserted
     * @param form The definition provided by the framework
     * 
     * @return An instance of a DynamicForm with the form representation implemented
     */
    private DynamicForm createEditAddressForm(Context context, Form form) {
        final int CTRLMARGIN_LEFT = 0;
        final int CTRLMARGIN_TOP = context.getResources().getDimensionPixelSize(R.dimen.form_top_margin);
        final int CTRLMARGIN_RIGHT = 0;
        final int CTRLMARGIN_BOTTOM = 0;

        LinearLayout.LayoutParams ctrlParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        ctrlParams.setMargins(CTRLMARGIN_LEFT, CTRLMARGIN_TOP, CTRLMARGIN_RIGHT, CTRLMARGIN_BOTTOM);

        return createGenericForm(context, form, addressEditForm, ctrlParams);

    }

    /**
     * Create the login form 
     * 
     * @param context The context where the form is to be inserted
     * @param form The definition provided by the framework
     * @return An instance of a DynamicForm with the form representation implemented
     */
    private DynamicForm createLoginForm(Context context, Form form) {
        final int CTRLMARGIN_LEFT = 0;
        final int CTRLMARGIN_TOP = context.getResources().getDimensionPixelSize(R.dimen.form_top_margin);
        final int CTRLMARGIN_RIGHT = 0;
        final int CTRLMARGIN_BOTTOM = 0;

        LinearLayout.LayoutParams ctrlParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        ctrlParams.setMargins(CTRLMARGIN_LEFT, CTRLMARGIN_TOP, CTRLMARGIN_RIGHT, CTRLMARGIN_BOTTOM);

        return createGenericForm(context, form, loginForm, ctrlParams);
    }

    /**
     * Create the payment methods edit form 
     * 
     * @param context The context where the form is to be inserted
     * @param form The definition provided by the framework
     * @return An instance of a DynamicForm with the form representation implemented
     */
    private DynamicForm createPaymentMethodsForm(Context context, Form form) {
        return createPaymentMethodsForm(context, form, null);
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
            ctrlParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            ctrlParams.setMargins(CTRLMARGIN_LEFT, CTRLMARGIN_TOP, CTRLMARGIN_RIGHT, CTRLMARGIN_BOTTOM);
        }

        return createGenericForm(context, form, paymentForm, ctrlParams);
    }

    /**
     * Create the user registration form 
     * 
     * @param context The context where the form is to be inserted
     * @param form The definition provided by the framework
     * @return An instance of a DynamicForm with the form representation implemented
     */
    private DynamicForm createRegistrationForm(Context context, Form form) {
        final int CTRLMARGIN_LEFT = 0;
        final int CTRLMARGIN_TOP = context.getResources().getDimensionPixelSize(R.dimen.form_top_margin);
        final int CTRLMARGIN_RIGHT = 0;
        final int CTRLMARGIN_BOTTOM = 0;

        LinearLayout.LayoutParams ctrlParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        ctrlParams.setMargins(CTRLMARGIN_LEFT, CTRLMARGIN_TOP, CTRLMARGIN_RIGHT, CTRLMARGIN_BOTTOM);

        return createGenericForm(context, form, registerForm, ctrlParams);
    }
    
    /**
     * Create the user forget password form 
     * 
     * @param context The context where the form is to be inserted
     * @param form The definition provided by the framework
     * @return An instance of a DynamicForm with the form representation implemented
     */
    private DynamicForm createForgetPasswordForm(Context context, Form form) {
        final int CTRLMARGIN_LEFT = 0;
        final int CTRLMARGIN_TOP = context.getResources().getDimensionPixelSize(R.dimen.form_top_margin);
        final int CTRLMARGIN_RIGHT = 0;
        final int CTRLMARGIN_BOTTOM = 0;

        LinearLayout.LayoutParams ctrlParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        ctrlParams.setMargins(CTRLMARGIN_LEFT, CTRLMARGIN_TOP, CTRLMARGIN_RIGHT, CTRLMARGIN_BOTTOM);

        return createGenericForm(context, form, registerForm, ctrlParams);
    }
    
    /**
     * Create the poll form 
     * 
     * @param context The context where the form is to be inserted
     * @param form The definition provided by the framework
     * @return An instance of a DynamicForm with the form representation implemented
     */
    // Validate this method for poll
    private DynamicForm createPollForm(Context context, Form form) {
        final int CTRLMARGIN_LEFT = 0;
        final int CTRLMARGIN_TOP = (int) (5 * scale);
        final int CTRLMARGIN_RIGHT = 0;
        final int CTRLMARGIN_BOTTOM = 0;

        LinearLayout.LayoutParams ctrlParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        ctrlParams.setMargins(CTRLMARGIN_LEFT, CTRLMARGIN_TOP, CTRLMARGIN_RIGHT, CTRLMARGIN_BOTTOM);

        return createGenericForm(context, form, pollForm, ctrlParams);
    }
    
    
    /**
     * Create the signup form 
     * 
     * @param context The context where the form is to be inserted
     * @param form The definition provided by the framework
     * @return An instance of a DynamicForm with the form representation implemented
     */
    // Validate this method for signup
    private DynamicForm createSignupForm(Context context, Form form) {
        final int CTRLMARGIN_LEFT = 0;
        final int CTRLMARGIN_TOP = context.getResources().getDimensionPixelSize(R.dimen.form_top_margin);
        final int CTRLMARGIN_RIGHT = 0;
        final int CTRLMARGIN_BOTTOM = 0;

        LinearLayout.LayoutParams ctrlParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        ctrlParams.setMargins(CTRLMARGIN_LEFT, CTRLMARGIN_TOP, CTRLMARGIN_RIGHT, CTRLMARGIN_BOTTOM);

        return createGenericForm(context, form, signupForm, ctrlParams);
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
     * @param userForm 
     * @param ctrlParams
     * @return n instance of a DynamicForm with the form representation implemented
     */
    private DynamicForm createGenericForm(Context context, Form form, DynamicForm userForm, ViewGroup.LayoutParams ctrlParams) {

        LinearLayout parent;
        Log.i(TAG,"code1form id : "+form.id+" name: "+form.name);
        if(context == null){
            return null;
        }
        if (null == userForm) {
            parent = new LinearLayout(context);
            LinearLayout.LayoutParams frmParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            parent.setOrientation(LinearLayout.VERTICAL);
            parent.setLayoutParams(frmParams);

            userForm = new DynamicForm(parent);
            userForm.setForm( form );

            // Used for dates with day/moth/year
            LinearLayout groupLayout = new LinearLayout(context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            groupLayout.setId( userForm.getNextId() );
            groupLayout.setOrientation(LinearLayout.HORIZONTAL);
            groupLayout.setLayoutParams(params);
            
            DynamicFormItem ctrl;
            
            ArrayList<IFormField> transformedFields = MetaFormExtractor.generateMetaFields( form.fields );
            MetaFormExtractor.dumpIFormField(transformedFields);

            for (IFormField frmEntry : transformedFields) {
            	Log.d( TAG, "createGenericForm: " + frmEntry.getKey() + " inputType = " + frmEntry.getInputType() );
                ctrl = new DynamicFormItem(userForm, context, frmEntry);
                
                if (ctrl.isMeta()) {
                    // Don't waste space with meta fields
                    Log.i(TAG, "Meta field");
                    userForm.addControl(ctrl, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1));
                } else if ( ! ctrl.isDatePart() ) {                
                    userForm.addControl(ctrl, ctrlParams);
                } else {
                    userForm.addGroupedControl(groupLayout, ctrl, ctrlParams);
                }
            }
        } else {
            ((ViewGroup) userForm.getContainer().getParent()).removeView(userForm.getContainer());
        }
        
        return userForm;
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