//package com.mobile.newFramework.forms;
//
//import com.mobile.newFramework.utils.EventType;
//import com.mobile.newFramework.utils.output.Print;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.Map;
//
//
///**
// * This Singleton Class defines the mapping for the order which the fields are displayed on the UI
// *
// * @author Nuno Castro
// * @version 1.00
// *
// * 2012/08/14
// *
// * COPYRIGHT (C) Rocket Internet All Rights Reserved.
// */
//public class FormsMapping {
//
//    protected final static String TAG = FormsMapping.class.getSimpleName();
//
//    /**
//     * The mapping for the login form. This mapping is based on the key form field returned by the API
//     */
//    private static final Map<String, Integer> loginForm;
//    static {
//        Map<String, Integer> currMapping = new HashMap<>();
//        currMapping.put("email", 1);
//        currMapping.put("password", 2);
//        loginForm = Collections.unmodifiableMap(currMapping);
//    }
//
//    /**
//     * The mapping for the registration form. This mapping is based on the key form field returned by the API
//     */
//    private static final Map<String, Integer> registrationForm;
//    static {
//        Map<String, Integer> currMapping = new HashMap<>();
//        currMapping.put("email", 1);
//        currMapping.put("first_name", 4);
//        currMapping.put("last_name", 5);
//        // FIXME: request rocket information about the next fields
////        currMapping.put("birthday", 6);
//        currMapping.put("day", 6); //97
//        currMapping.put("month", 7);  //98
//        currMapping.put("year", 8);  //99
//        // FIXME: ensure that this birthday is server supported
//        currMapping.put("gender", 9);  //7
//        currMapping.put("password", 2);
//        currMapping.put("password2", 3);
//        currMapping.put("newsletter_categories_subscribed", 10); //8
//
//        registrationForm = Collections.unmodifiableMap(currMapping);
//    }
//
//    /**
//     * The mapping for the create address form. This mapping is based on the key form field returned by the API
//     */
//    private static final Map<String, Integer> addressForm;
//    static {
//        Map<String, Integer> currMapping = new HashMap<>();
//        currMapping.put("first_name", 1);
//        currMapping.put("last_name", 2);
//        currMapping.put("company", 3);
//        currMapping.put("address1", 4);
//        currMapping.put("address2", 5);
//        currMapping.put("postcode", 6);
//        currMapping.put("phoneNumberFormat", 7);
//        currMapping.put("phone", 8);
//        currMapping.put("additionalPhoneNumberFormat", 9);
//        currMapping.put("additional_phone", 10);
//        currMapping.put("fk_customer_address_region", 11);
//        currMapping.put("fk_customer_address_city", 12);
//        currMapping.put("city", 13);
//        currMapping.put("is_default_shipping", 14);
//        currMapping.put("is_default_billing", 15);
//        currMapping.put("id_customer_address", 16);
//        addressForm = Collections.unmodifiableMap(currMapping);
//    }
//
//    /**
//     * This map contains the information of which map order to use on which form. This assignment is done based on the id
//     * of the form returned by the API
//     */
//    public static final Map<String, Map<String, Integer>> genericMapping;
//    static {
//        Map<String,  Map<String, Integer>> currMapping = new HashMap<>();
//        //currMapping.put("form-account-login", loginForm);
//        //currMapping.put("form-account-create", registrationForm);
//        // currMapping.put(EventType.GET_EDIT_ADDRESS_FORM_EVENT.toString(), addressForm);
//        currMapping.put("address-form", addressForm);
//        currMapping.put(EventType.LOGIN_EVENT.toString(), loginForm);
//        currMapping.put(EventType.GET_REGISTRATION_FORM_EVENT.toString(), registrationForm);
//        // currMapping.put(EventType.GET_CREATE_ADDRESS_FORM_EVENT.toString(), addressForm);
//        genericMapping = Collections.unmodifiableMap(currMapping);
//    }
//
//    protected FormsMapping() { }
//
//    /**
//     * This class defines a comparator for the FormFields, so that we can sort them with a specific order after receiving
//     * them form the API
//     * @author Nuno Castro
//     */
//    public static class byFieldOrder implements java.util.Comparator<FormField> {
//        public int compare(FormField field1, FormField field2) {
//            Map<String, Integer> sortMap = field1.getParent().fieldMapping;
//            int diff = 0;
//
//            if ( sortMap.containsKey(field1.getKey()) && sortMap.containsKey(field2.getKey()) ) {
//                diff = sortMap.get(field1.getKey()) - sortMap.get(field2.getKey());
//            }
//
//            return diff;
//        }
//       }
//
//    /**
//    * Removes all instances of form fields that are not present on the formMap and aren't required.
//    * @param form with the form fields.
//    * @param fieldMap registry.
//    */
//    public static void removeUnsortedFields(Form form, Map<String, Integer> fieldMap){
//        ArrayList<FormField> fieldsToRemove = new ArrayList<>();
//
//        for(FormField field : form.fields){
//
//        	// The meta field is kept - higher level implementation decides what to do with it
//        	if ( field.getInputType() == InputType.meta )
//        		continue;
//
//            //Only non required field can be validated.
//        	// Change this to || if the server side required=false mechanism works
//            if (!field.getValidation().required && !fieldMap.containsKey(field.getKey())) {
//            	fieldsToRemove.add(field);
//            }
//
//
//        }
//
//        // Log.d(TAG,"removeUnsortedFields: going to remove fields.");
//        for(IFormField field : fieldsToRemove) {
//            Print.d("removeUnsortedFields: removing field: " + field.getKey());
//            form.fields.remove(field);
//        }
//    }
//
//}
