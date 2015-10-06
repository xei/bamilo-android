package com.mobile.newFramework.forms;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.utils.TextUtils;
import com.mobile.newFramework.utils.output.Print;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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

    @SuppressWarnings("unused")
    public interface OnDataSetReceived {
        void DataSetReceived(Map<String, String> dataSet);
    }

    private Form mParent;
    private String mId;
    private String mKey;
    private String mName;
    private FormInputType mInputType;
    private String mLabel;
    private String mLinkText;
    private String mFormat;
    private LinkedHashMap<String, String> mDataSetRating;
    private String mScenario;
    private LinkedHashMap<String, String> mDataSet;
    private ArrayList<IFormField> mOptions;
    private String mDataSetSource;
    private OnDataSetReceived mDataSetListener;
    private FieldValidation mValidation;
    private String mValue;
    private HashMap<String, String> mDataCalls;
    private HashMap<String, Form>  mPaymentFields;
    private LinkedHashMap<Object,Object> mExtrasValues;
    private ArrayList<NewsletterOption> mNewsletterOptions;
    private HashMap<String,PaymentInfo> mPaymentInfoList;
    private IFormField mChildFormField;
    private IFormField mParentFormField;
    private boolean isChecked;

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
        this.mDataCalls = new HashMap<>();
        this.mDataSetSource = "";
        this.mParent = parent;
        this.mDataSetListener = null;
        this.mExtrasValues = new LinkedHashMap<>();
        this.mScenario = null;
        this.mLinkText = "";
        this.mDataSetRating = new LinkedHashMap<>();
        this.mPaymentInfoList = new HashMap<>();
        this.mFormat = "dd-MM-yyyy";
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
                case "string":
                case "text":
                    mInputType = FormInputType.text;
                    break;
                case "email":
                    mInputType = FormInputType.email;
                    break;
                case "date":
                    mInputType = FormInputType.date;
                    break;
                case "integer":
                case "number":
                    mInputType = FormInputType.number;
                    break;
                case "related_number":
                    mInputType = FormInputType.relatedNumber;
                    break;
                case "password":
                    mInputType = FormInputType.password;
                    break;
                case "radio":
                    mInputType = FormInputType.radioGroup;
                    break;
                case "list":
                case "select":
                    mInputType = FormInputType.list;
                    break;
                case "array":
                case "rating":
                    mInputType = FormInputType.rating;
                    break;
                case "boolean":
                case "checkbox":
                case "multi_checkbox":
                    mInputType = FormInputType.checkBox;
                    break;
                case "":
                    mInputType = FormInputType.meta;
                    break;
                case "hidden":
                    mInputType = FormInputType.hide;
                    break;
                case "checkbox_link":
                    mInputType = FormInputType.checkBoxLink;
                    break;
                case "errorMessage":
                    mInputType = FormInputType.errorMessage;
                    break;
                default:
                    return false;
            }

            // if the field is one of the supported types
            mId = jsonObject.optString(RestConstants.ID);
            mKey = jsonObject.optString(RestConstants.JSON_KEY_TAG);
            mName = jsonObject.optString(RestConstants.JSON_FIELD_NAME_TAG);
            mLabel = jsonObject.optString(RestConstants.LABEL);
            mValue = !jsonObject.isNull(RestConstants.VALUE) ? jsonObject.optString(RestConstants.VALUE) : "";
            mScenario = jsonObject.optString(RestConstants.JSON_SCENARIO_TAG);
            mLinkText = jsonObject.optString(RestConstants.JSON_LINK_TEXT_TAG);
            isChecked = jsonObject.optBoolean(RestConstants.CHECKED);
            mFormat = jsonObject.optString(RestConstants.JSON_FORMAT_TAG);
            Print.d("FORM FIELD: " + mKey + " " + mName + " " + " " + mLabel + " " + mValue + " " + mScenario);

            // Case RULES
            JSONObject validationObject = jsonObject.optJSONObject(RestConstants.JSON_RULES_TAG);
            if(validationObject != null) {
                if (!mValidation.initialize(validationObject)) {
                    //Log.e(TAG, "initialize: Error parsing the rules fields");
                    result = false;
                }
            }

            // Case "data_set" //should be more generic
            JSONArray optionsArray  = jsonObject.optJSONArray(RestConstants.JSON_DATA_SET_FORM_RATING_TAG);
            //dataSetRating.clear();
            if (optionsArray != null && optionsArray.length() > 0) {
                for (int i = 0; i < optionsArray.length(); i++) {
                    mDataSetRating.put(optionsArray.getJSONObject(i).optString(RestConstants.JSON_ID_FORM_RATING_TAG), optionsArray.getJSONObject(i).optString(RestConstants.JSON_TITLE_FORM_RATING_TAG));
                }
            }

            /**
             * Save api call (region and cities)
             */
            String apiCall = jsonObject.optString(RestConstants.API_CALL);
            if (!TextUtils.isEmpty(apiCall)) {
                mDataCalls.put(RestConstants.API_CALL, apiCall);
            }


            // TODO: Validate if this is necessary v.2.7
//            /**
//             * Get data from dataset as json object
//             */
//            JSONObject dataSetObject = jsonObject.optJSONObject(RestConstants.JSON_DATA_SET_TAG);
//            if(dataSetObject != null && dataSetObject.length() > 0){
//                Iterator<?> it = dataSetObject.keys();
//                while (it.hasNext()) {
//                    dataSet.put((String) dataSetObject.get(key), (String) dataSetObject.get(key));
//                }
//            }

            // Case options TODO Unify options response
            /**
             * Validate this method to save the shipping methods
             */
            JSONArray dataOptionsArray = null;
            JSONObject dataOptionsObject = null;
            if(!jsonObject.isNull("options")) {
                dataOptionsArray = jsonObject.optJSONArray("options");
                dataOptionsObject = jsonObject.optJSONObject("options");
            }


            // TODO Improve this method
            // Case shipping options from array
            if(dataOptionsArray != null){
                mExtrasValues.clear();
                mNewsletterOptions = new ArrayList<>();
                for (int i = 0; i < dataOptionsArray.length(); ++i) {

                    // Case the newsletter
                    if(mKey.equals("newsletter_categories_subscribed")) {
                        mNewsletterOptions.add(new NewsletterOption(dataOptionsArray.getJSONObject(i), mName));
                    }
                    // Case pickup station
                    else if(mKey.equals("pickup_station") && mScenario != null){
                        PickUpStationObject pStation = new PickUpStationObject();
                        pStation.initialize(dataOptionsArray.getJSONObject(i));
                        mExtrasValues.put(pStation.getIdPickupstation(), pStation);
                        mDataSet.put(pStation.getName(), pStation.getName());
                    }
                    // Case default
                    else {
                        JSONObject option = dataOptionsArray.getJSONObject(i);
                        mDataSet.put(option.optString("value"), option.optString("label"));
                    }
                }
            }
            // Case shipping options from object
            else if(dataOptionsObject != null){
                Iterator<?> it = dataOptionsObject.keys();
                while (it.hasNext()) {
                    String curKey = (String) it.next();
                    mDataSet.put(curKey, curKey);
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

            // CASE fields from related TODO use options
            dataOptionsArray = jsonObject.optJSONArray(RestConstants.JSON_FIELDS_TAG);
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

            if(mKey.equals(RestConstants.PAYMENT_METHOD) && mInputType != FormInputType.errorMessage){
                mDataSet.clear();
                mPaymentFields = new HashMap<>();
                dataOptionsObject = jsonObject.optJSONObject(RestConstants.JSON_OPTIONS_TAG);
                Iterator<?> it = dataOptionsObject.keys();
                //Clean payment method info
                while (it.hasNext()) {
                    String curKey = (String) it.next();
                    String label = dataOptionsObject.getJSONObject(curKey).getString(RestConstants.LABEL);
                    String value = dataOptionsObject.getJSONObject(curKey).getString(RestConstants.VALUE);
                    //Log.d(TAG, "FORM FIELD: CURRENT KEY " + curKey + " VALUE: " + value);
                    mDataSet.put(value, label);
                    // Info
                    JSONObject paymentDescription = dataOptionsObject.optJSONObject(curKey).optJSONObject(RestConstants.JSON_DESCRIPTION_TAG);
                    PaymentInfo mPaymentInfo = new PaymentInfo();
                    mPaymentInfo.initialize(paymentDescription);
                    mPaymentInfoList.put(label,mPaymentInfo);
                    // Sub forms
                    Print.d("code1paymentDescription : saved : " + curKey);
                    JSONObject json = dataOptionsObject.getJSONObject(curKey);
                    Form mForm = new Form();
                    mForm.initialize(json);
                    mPaymentFields.put(label, mForm);
                    Print.d("code1paymentDescription : initialized form : " + curKey);
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
            jsonObject.put(RestConstants.JSON_KEY_TAG, mKey);
            jsonObject.put(RestConstants.JSON_FIELD_NAME_TAG, mName);
            jsonObject.put(RestConstants.LABEL, mLabel);
            jsonObject.put(RestConstants.VALUE, mValue);
            jsonObject.put(RestConstants.JSON_TERMS_TAG, mLinkText);
            // validation
            jsonObject.put(RestConstants.JSON_RULES_TAG, mValidation.toJSON());
            JSONArray dataSetArray = new JSONArray();
            for (String dataSetItem : mDataSet.keySet()) {
                dataSetArray.put(dataSetItem);
            }
            jsonObject.put(RestConstants.JSON_DATA_SET_TAG, dataSetArray);
            jsonObject.put(RestConstants.JSON_DATA_SET_SOURCE_TAG, mDataSetSource);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    @Override
    public RequiredJson getRequiredJson() {
        return null;
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
    public LinkedHashMap<String, String> getDataSet() {
        return mDataSet;
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

    public HashMap<String, Form> getPaymentMethodsField(){
        return this.mPaymentFields;
    }

    public void setPaymentMethodsField(HashMap<String, Form> pFields){
        this.mPaymentFields = pFields;
    }

    @Override
    public Map<String, String> getDataCalls() {
        return mDataCalls;
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

    public Map<String, IFormField> getSubFormFields() {
        return null;
    }

    @Override
    public String getLinkText() {
        return this.mLinkText;
    }

    @Override
    public Map<String, String> getDateSetRating() {
        return mDataSetRating;
    }

    public ArrayList<NewsletterOption> getNewsletterOptions() {
        return mNewsletterOptions;
    }

    public HashMap<String, PaymentInfo> getPaymentInfoList() {
        return mPaymentInfoList;
    }

    /**
     * Listener used when the data set is received.
     */
    public void setOnDataSetReceived(OnDataSetReceived listener) {
        mDataSetListener = listener;
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
        dest.writeValue(mDataCalls);
        dest.writeValue(mPaymentFields);
        dest.writeValue(mExtrasValues);
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
        mDataCalls = (HashMap) in.readValue(HashMap.class.getClassLoader());
        mPaymentFields = (HashMap) in.readValue(HashMap.class.getClassLoader());
        mExtrasValues = (LinkedHashMap) in.readValue(LinkedHashMap.class.getClassLoader());
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

    @Override
    public String getFormat() {
        return this.mFormat;
    }


}
