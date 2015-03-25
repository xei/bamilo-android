package com.mobile.forms;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.mobile.framework.objects.IJSONSerializable;
import com.mobile.framework.rest.RestConstants;
import com.mobile.framework.utils.LogTagHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;

import de.akquinet.android.androlog.Log;

public class HomeNewslettersSignupForm implements IJSONSerializable, Parcelable {
    private final static String TAG = LogTagHelper.create( HomeNewslettersSignupForm.class );

    // public fields
    public String action;

    public String emailField;
    public String categoryField;
    public FieldValidation emailValidation;
    public LinkedHashMap<String, String> categories;

    // flag used to insure this object is properly initialized
    public boolean isValid;

    /**
     * Form empty constructor.
     */
    public HomeNewslettersSignupForm() {
        this.action = "";
        this.emailField = "";
        this.categoryField = "";
        this.emailValidation = new FieldValidation();
        this.categories = new LinkedHashMap<>();
        this.isValid = false;
    }

    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        try {
            if (jsonObject != null) {
                action = jsonObject.optString(RestConstants.JSON_ACTION_TAG);
                JSONArray fieldsObject = jsonObject.getJSONArray(RestConstants.JSON_FIELDS_TAG);
                if (fieldsObject != null && fieldsObject.length() == 2) {
                    // categories
                    JSONObject categoryObject = fieldsObject.getJSONObject(0);
                    if (categoryObject != null) {
                        categoryField = categoryObject.getString(RestConstants.JSON_NAME_TAG);
                        JSONArray optionsObject = categoryObject.getJSONArray(RestConstants.JSON_OPTIONS_TAG);
                        if (optionsObject != null && optionsObject.length() > 0) {
                            for (int i = 0; i < optionsObject.length(); i++) {
                                JSONObject optionObject = optionsObject.getJSONObject(i);
                                if (optionObject != null) {
                                    // flag to set if option will be used
                                    boolean isAnOption = true;

                                    Boolean isDefault = optionObject.optBoolean(RestConstants.JSON_IS_DEFAULT_TAG);
                                    if (isDefault == null || !isDefault) {
                                        isAnOption = false;
                                    }
                                    String key = optionObject.optString(RestConstants.JSON_VALUE_TAG, "");
                                    String value = optionObject.optString(RestConstants.JSON_LABEL_TAG, "");
                                    if (TextUtils.isEmpty(key) || TextUtils.isEmpty(value)) {
                                        isAnOption = false;
                                    }

                                    if (isAnOption) {
                                        categories.put(key, value);
                                    } else {
                                        Log.i(TAG, "Option: " + value + " (" + key + ") will not be used!");
                                    }
                                }
                            }
                        }
                    }
                    // email and email rules
                    JSONObject emailObject = fieldsObject.getJSONObject(1);
                    if (emailObject != null) {
                        emailField = emailObject.optString(RestConstants.JSON_NAME_TAG);
                        JSONObject rulesObject = emailObject.getJSONObject(RestConstants.JSON_RULES_TAG);
                        if (rulesObject != null && rulesObject.length() > 0) {
                            emailValidation.initialize(rulesObject);
                        }
                    }
                } else {
                    Log.e(TAG, "initialize: error parsing jsonobject - No fields!" );
                    return false;
                }
                // set after initialization
                isValid = true;
            }
        } catch (JSONException e) {
            Log.e(TAG, "initialize: error parsing jsonobject", e);
            return false;
        }

        return true;
    }

    @Override
    public JSONObject toJSON() {
        return null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(action);
        dest.writeString(emailField);
        dest.writeString(categoryField);
        dest.writeParcelable(emailValidation, flags);
        dest.writeMap(categories);
        dest.writeValue(isValid);
    }

    /**
     * Parcel constructor
     * 
     * @param in
     */
    private HomeNewslettersSignupForm(Parcel in) {
        
        action = in.readString();
        emailField = in.readString();
        categoryField = in.readString();
        emailValidation = in.readParcelable(FieldValidation.class.getClassLoader());
        // read Hashmap
        categories = new LinkedHashMap<>();
        int numberCategories = in.readInt();
        for (int i = 0; i< numberCategories ; i++){
            String key = in.readString();
            String value = in.readString();
            categories.put(key, value);
        }
        // read boolean
        in.readBooleanArray(new boolean[] {isValid});
    }

    /**
     * Create parcelable
     */
    public static final Parcelable.Creator<HomeNewslettersSignupForm> CREATOR = new Parcelable.Creator<HomeNewslettersSignupForm>() {
        public HomeNewslettersSignupForm createFromParcel(Parcel in) {
            return new HomeNewslettersSignupForm(in);
        }

        public HomeNewslettersSignupForm[] newArray(int size) {
            return new HomeNewslettersSignupForm[size];
        }
    };
}
