package pt.rocket.forms;

import java.util.LinkedHashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.framework.objects.IJSONSerializable;
import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.utils.LogTagHelper;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
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
        this.categories = new LinkedHashMap<String, String>();
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
        dest.writeString(emailField);
        dest.writeString(categoryField);
        dest.writeParcelable(emailValidation, flags);
        dest.writeMap(categories);
    }
}
