package com.mobile.newFramework.forms;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.newFramework.utils.TextUtils;
import com.mobile.newFramework.utils.output.Print;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * Class that represent and entry in the form.
 *
 * @author GuilhermeSilva
 *
 */
public class FormField implements IJSONSerializable, IFormField, Parcelable {

    protected final static String TAG = FormField.class.getSimpleName();

    // Supported Types
    public static final String RADIO = "radio";
    private static final String STRING = "string";
    private static final String EMAIL = "email";
    private static final String DATE = "date";
    private static final String NUMBER = "number";
    private static final String RELATED_NUMBER = "related_number";
    private static final String PASSWORD = "password";
    private static final String LIST = "list";
    private static final String RATING = "array";
    private static final String CHECKBOX = "checkbox";
    private static final String HIDDEN = "hidden";
    private static final String CHECKBOX_LINK = "checkbox_link";
    private static final String INFO_MESSAGE = "info_message";
    private static final String ERROR_MESSAGE = "error_message";
    private static final String EMPTY = "";

    private Form mParent;
    private LinkedHashMap<String, String> mDataSetRating;
    private LinkedHashMap<String, String> mDataSet;
    private String mDataSetSource;
    private String mApiCall;
    private HashMap<String,PaymentInfo> mPaymentInfoList;
    private String mId;
    private String mKey;
    private String mName;
    private FormInputType mInputType;
    private String mLabel;
    private String mLinkText;
    private String mFormat;
    private String mScenario;
    private ArrayList<IFormField> mOptions;
    private OnDataSetReceived mDataSetListener;
    private FieldValidation mValidation;
    private String mValue;
    private HashMap<String, Form>  mPaymentFields;
    private ArrayList<NewsletterOption> mNewsletterOptions;
    private IFormField mChildFormField;
    private IFormField mParentFormField;
    private boolean isChecked;
    private boolean isPrefixField;
    private boolean isDisabled;

    @SuppressWarnings("unused")
    public interface OnDataSetReceived {
        void DataSetReceived(Map<String, String> dataSet);
    }


    /**
     * FormField param constructor
     *
     * @param parent
     *            . Form hat encapsulates the form field.
     */
    public FormField(Form parent) {
        this.mId = "defaultId";
        this.mName = "defaultName";
        this.mInputType = FormInputType.text;
        this.mLabel = "default";
        this.mValidation = new FieldValidation();
        this.mValue = "";
        this.mDataSet = new LinkedHashMap<>();
        this.mDataSetSource = "";
        this.mParent = parent;
        this.mDataSetListener = null;
        this.mScenario = null;
        this.mLinkText = "";
        this.mDataSetRating = new LinkedHashMap<>();
        this.mPaymentInfoList = new HashMap<>();
        this.mFormat = "dd-MM-yyyy";
        this.isDisabled = false;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.mobile.framework.objects.IJSONSerializable#initialize(org.json.JSONObject )
     */
    @Override
    public boolean initialize(JSONObject jsonObject) {
        boolean result = true;
        try {
            String formFieldString = jsonObject.optString(RestConstants.TYPE);
            switch (formFieldString) {
                case STRING:
                    mInputType = FormInputType.text;
                    break;
                case EMAIL:
                    mInputType = FormInputType.email;
                    break;
                case DATE:
                    mInputType = FormInputType.date;
                    break;
                case NUMBER:
                    mInputType = FormInputType.number;
                    break;
                case RELATED_NUMBER:
                    mInputType = FormInputType.relatedNumber;
                    break;
                case PASSWORD:
                    mInputType = FormInputType.password;
                    break;
                case RADIO:
                    mInputType = FormInputType.radioGroup;
                    break;
                case LIST:
                    mInputType = FormInputType.list;
                    break;
                case RATING:
                    mInputType = FormInputType.rating;
                    break;
                case CHECKBOX:
                    mInputType = FormInputType.checkBox;
                    break;
                case HIDDEN:
                    mInputType = FormInputType.hide;
                    break;
                case CHECKBOX_LINK:
                    mInputType = FormInputType.checkBoxLink;
                    break;
                case INFO_MESSAGE:
                    mInputType = FormInputType.infoMessage;
                    break;
                case ERROR_MESSAGE:
                    mInputType = FormInputType.errorMessage;
                    break;
                case EMPTY:
                    mInputType = FormInputType.option;
                    break;
                default:
                    return false;
            }

            // if the field is one of the supported types
            mId = jsonObject.optString(RestConstants.ID);
            mKey = jsonObject.optString(RestConstants.KEY); // Used for form images
            mName = jsonObject.optString(RestConstants.NAME);
            mLabel = jsonObject.optString(RestConstants.LABEL);
            mValue = !jsonObject.isNull(RestConstants.VALUE) ? jsonObject.optString(RestConstants.VALUE) : "";
            mScenario = jsonObject.optString(RestConstants.SCENARIO);
            mLinkText = jsonObject.optString(RestConstants.LINK_TEXT);
            isChecked = jsonObject.optBoolean(RestConstants.CHECKED);
            isDisabled = jsonObject.optBoolean(RestConstants.DISABLED);
            mFormat = jsonObject.optString(RestConstants.FORMAT);
            isPrefixField = TextUtils.equals(jsonObject.optString(RestConstants.POSITION), "before");
            Print.d("FORM FIELD: " + mKey + " " + mName + " " + " " + mLabel + " " + mValue + " " + mScenario);

            // Case RULES
            JSONObject validationObject = jsonObject.optJSONObject(RestConstants.RULES);
            if(validationObject != null) {
                if (!mValidation.initialize(validationObject)) {
                    //Log.e(TAG, "initialize: Error parsing the rules fields");
                    result = false;
                }
            }

            // Case "data_set" //should be more generic
            JSONArray optionsArray  = jsonObject.optJSONArray(RestConstants.DATA_SET);
            if (optionsArray != null && optionsArray.length() > 0) {
                for (int i = 0; i < optionsArray.length(); i++) {
                    mDataSetRating.put(optionsArray.getJSONObject(i).optString(RestConstants.ID_RATING_TYPE), optionsArray.getJSONObject(i).optString(RestConstants.DISPLAY_TITLE));
                }
            }

            // Save api call
            if (jsonObject.has(RestConstants.API_CALL)) {
                // Get api call
                JSONObject apiCall = jsonObject.getJSONObject(RestConstants.API_CALL);
                // Get endpoint
                mApiCall = apiCall.getString(RestConstants.TARGET);
                // Get params
                JSONArray params = apiCall.optJSONArray(RestConstants.PARMS);
                if(CollectionUtils.isNotEmpty(params)) {
                    for (int i = 0; i < params.length(); i++) {
                        mApiCall += params.getJSONObject(i).getString(RestConstants.PARAM) + "/%s/";
                    }
                }
            }

            // Case options
            JSONArray dataOptionsArray = jsonObject.optJSONArray(RestConstants.OPTIONS);

            // Case shipping options from array
            if(dataOptionsArray != null){
                mNewsletterOptions = new ArrayList<>();
                for (int i = 0; i < dataOptionsArray.length(); ++i) {
                    // Case the newsletter
                    if(mKey.equals("newsletter_categories_subscribed")) {
                        //FIXME validate if possible to remove this array, its only used on MyAccountEmailNotificationFragment
                        //FIXME Newsletter revamp should fix this
                        mNewsletterOptions.add(new NewsletterOption(dataOptionsArray.getJSONObject(i), mName));
                    }
                    // Case default
                    JSONObject option = dataOptionsArray.getJSONObject(i);
                    mDataSet.put(option.optString(RestConstants.VALUE), option.optString(RestConstants.LABEL));
                }
            }

            /**
             * ########### RELATED FIELD ###########
             */
            // Case related data (sub form)
            JSONObject relatedDataObject = jsonObject.optJSONObject(RestConstants.RELATED_DATA);
            if (relatedDataObject != null) {
                FormField formField = new FormField(this.mParent);
                formField.initialize(relatedDataObject);
                formField.setParentField(this);
                mChildFormField = formField;
            }
            // Case fields from related number with radio group options
            dataOptionsArray = jsonObject.optJSONArray(RestConstants.FIELDS);
            if(dataOptionsArray != null) {
                mOptions = new ArrayList<>();
                for (int i = 0; i < dataOptionsArray.length(); ++i) {
                    JSONObject option = dataOptionsArray.getJSONObject(i);
                    FormFieldOption fieldOption = new FormFieldOption(mParent);
                    fieldOption.initialize(option);
                    mOptions.add(fieldOption);
                }
            }

            /**
             * ########### PAYMENT METHODS ###########
             */

            if(mKey.equals(RestConstants.PAYMENT_METHOD) && mInputType == FormInputType.radioGroup){
                mDataSet.clear();
                mPaymentFields = new HashMap<>();
                JSONArray jsonArray = jsonObject.getJSONArray(RestConstants.OPTIONS);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject field = jsonArray.getJSONObject(i);
                    String label = field.getString(RestConstants.LABEL);
                    String value = field.getString(RestConstants.VALUE);
                    mDataSet.put(value, label);
                    // Create payment info
                    JSONObject paymentDescription = field.optJSONObject(RestConstants.DESCRIPTION);
                    PaymentInfo mPaymentInfo = new PaymentInfo();
                    mPaymentInfo.initialize(paymentDescription);
                    mPaymentInfoList.put(label, mPaymentInfo);
                    // Create sub forms for required fields
                    if(field.has(RestConstants.FIELDS)){
                        Form mForm = new Form();
                        mForm.initAsSubForm(field);
                        mPaymentFields.put(label, mForm);
                    }
                }
            }

        } catch (JSONException e) {
            Print.d("Error parsing the json fields"+ e);
            result = false;
        }

        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.mobile.framework.objects.IJSONSerializable#toJSON()
     */
    @Override
    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();
        try {
            switch (mInputType) {
            case checkBox:
                jsonObject.put(RestConstants.TYPE, "boolean");
                break;
            case date:
                jsonObject.put(RestConstants.TYPE, "date");
                break;
            case email:
                jsonObject.put(RestConstants.TYPE, "email");
                break;
            case number:
                jsonObject.put(RestConstants.TYPE, "integer");
                break;
            case password:
                jsonObject.put(RestConstants.TYPE, "password");
                break;
            case radioGroup:
                jsonObject.put(RestConstants.TYPE, "radio");
                break;
            case list:
                jsonObject.put(RestConstants.TYPE, "list");
                break;
            case text:
                jsonObject.put(RestConstants.TYPE, "string");
                break;
            case checkBoxLink:
                jsonObject.put(RestConstants.TYPE, "boolean");
                break;
            case rating:
                jsonObject.put(RestConstants.TYPE, "rating");
                break;
            default:
                jsonObject.put(RestConstants.TYPE, "string");
                break;
            }

            // fields
            jsonObject.put(RestConstants.ID, mId);
            jsonObject.put(RestConstants.KEY, mKey);
            jsonObject.put(RestConstants.NAME, mName);
            jsonObject.put(RestConstants.LABEL, mLabel);
            jsonObject.put(RestConstants.VALUE, mValue);
            jsonObject.put(RestConstants.TERMS, mLinkText);
            // validation
            jsonObject.put(RestConstants.RULES, mValidation.toJSON());
            JSONArray dataSetArray = new JSONArray();
            for (String dataSetItem : mDataSet.keySet()) {
                dataSetArray.put(dataSetItem);
            }
            jsonObject.put(RestConstants.DATASET, dataSetArray);
            jsonObject.put(RestConstants.DATASET_SOURCE, mDataSetSource);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    @Override
    public int getRequiredJson() {
        return RequiredJson.NONE;
    }

    @Override
    public Form getParent() {
        return mParent;
    }

    @Override
    public String getId() {
        return mId;
    }

    @Override
    public String getKey() {
        return mKey;
    }

    @Override
    public String getName() {
        return mName;
    }

    @Override
    public FormInputType getInputType() {
        return mInputType;
    }

    @Override
    public String getLabel() {
        return mLabel;
    }

    @Override
    public String getLinkText() {
        return this.mLinkText;
    }

    @Override
    public String getFormat() {
        return this.mFormat;
    }

    @Override
    public LinkedHashMap<String, String> getDataSet() {
        return mDataSet;
    }

    /**
     * Listener used when the data set is received.
     */
    public void setOnDataSetReceived(OnDataSetReceived listener) {
        mDataSetListener = listener;
    }

    @Override
    public FieldValidation getValidation() {
        return mValidation;
    }

    public void setValidation(FieldValidation validation) {
        this.mValidation = validation;
    }

    @Override
    public String getValue() {
        return mValue;
    }

    @Override
    public void setValue(String value) {
        this.mValue = value;
    }

    @Override
    public String getApiCall() {
        return mApiCall;
    }

    @Override
    public Map<String, String> getDateSetRating() {
        return mDataSetRating;
    }

    @Override
    public IFormField getRelatedField() {
        return mChildFormField;
    }

    @Override
    public IFormField getParentField() {
        return mParentFormField;
    }

    @Override
    public void setParentField(IFormField formField) {
        mParentFormField = formField;
    }

    @Override
    public ArrayList<IFormField> getOptions() {
        return mOptions;
    }

    @Override
    public boolean isDefaultSelection() {
        return isChecked;
    }

    @Override
    public boolean isPrefixField() {
        return isPrefixField;
    }

    @Override
    public boolean isDisabledField() {
        return isDisabled;
    }

    public HashMap<String, Form> getPaymentMethodsField(){
        return this.mPaymentFields;
    }

    public ArrayList<NewsletterOption> getNewsletterOptions() {
        return mNewsletterOptions;
    }

    public HashMap<String, PaymentInfo> getPaymentInfoList() {
        return mPaymentInfoList;
    }

    /*
     * ########### PARCELABLE ###########
     */

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(mParent);
        dest.writeString(mId);
        dest.writeString(mKey);
        dest.writeString(mName);
        dest.writeValue(mInputType);
        dest.writeString(mLabel);
        dest.writeString(mLinkText);
        dest.writeValue(mDataSetRating);
        dest.writeString(mScenario);
        dest.writeValue(mDataSet);
        if (mOptions == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(mOptions);
        }
        dest.writeString(mDataSetSource);
        dest.writeValue(mDataSetListener);
        dest.writeValue(mValidation);
        dest.writeString(mValue);
        dest.writeString(mApiCall);
        dest.writeValue(mPaymentFields);
        if (mNewsletterOptions == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(mNewsletterOptions);
        }
        dest.writeValue(mPaymentInfoList);
        dest.writeValue(mChildFormField);
        dest.writeValue(mParentFormField);
        dest.writeString(mFormat);
        dest.writeByte((byte) (isPrefixField ? 1 : 0));
    }

    /**
     * Parcel constructor
     */
    @SuppressWarnings("unchecked")
    private FormField(Parcel in) {
        mParent = (Form) in.readValue(Form.class.getClassLoader());
        mId = in.readString();
        mKey = in.readString();
        mName = in.readString();
        mInputType = (FormInputType) in.readValue(FormInputType.class.getClassLoader());
        mLabel = in.readString();
        mLinkText = in.readString();
        mDataSetRating = (LinkedHashMap) in.readValue(LinkedHashMap.class.getClassLoader());
        mScenario = in.readString();
        mDataSet = (LinkedHashMap) in.readValue(LinkedHashMap.class.getClassLoader());
        if (in.readByte() == 0x01) {
            mOptions = new ArrayList<>();
            in.readList(mOptions, IFormField.class.getClassLoader());
        } else {
            mOptions = null;
        }
        mDataSetSource = in.readString();
        mDataSetListener = (OnDataSetReceived) in.readValue(OnDataSetReceived.class.getClassLoader());
        mValidation = (FieldValidation) in.readValue(FieldValidation.class.getClassLoader());
        mValue = in.readString();
        mApiCall = in.readString();
        mPaymentFields = (HashMap) in.readValue(HashMap.class.getClassLoader());
        if (in.readByte() == 0x01) {
            mNewsletterOptions = new ArrayList<>();
            in.readList(mNewsletterOptions, NewsletterOption.class.getClassLoader());
        } else {
            mNewsletterOptions = null;
        }
        mPaymentInfoList = (HashMap) in.readValue(HashMap.class.getClassLoader());
        mChildFormField = (IFormField) in.readValue(IFormField.class.getClassLoader());
        mParentFormField = (IFormField) in.readValue(IFormField.class.getClassLoader());
        mFormat = in.readString();
        isPrefixField = in.readByte() == 1;
    }

    /**
     * Create parcelable
     */
    public static final Creator<FormField> CREATOR = new Creator<FormField>() {
        public FormField createFromParcel(Parcel in) {
            return new FormField(in);
        }
        public FormField[] newArray(int size) {
            return new FormField[size];
        }
    };

}
