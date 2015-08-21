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

    private Form parent;
    private String id;
    private String key;
    private String name;
    private InputType inputType;
    private String label;
    private String linkText;
    private LinkedHashMap<String, String> dataSetRating;
    private String scenario;
    private LinkedHashMap<String, String> dataSet;
    private ArrayList<IFormField> mOptions;
    private String dataSetSource;
    private OnDataSetReceived dataSetListener;
    private FieldValidation validation;
    private String value;
    private HashMap<String, String> dataCalls;
    private HashMap<String, Form>  paymentFields;
    private LinkedHashMap<Object,Object> extrasValues;
    public ArrayList<NewsletterOption> newsletterOptions;
    public HashMap<String,PaymentInfo> paymentInfoList;
    private IFormField mChildFormField;
    private IFormField mParentFormField;

    /**
     * FormField param constructor
     *
     * @param parent
     *            . Form hat encapsulates the form field.
     */
    public FormField(Form parent) {
        this.id = "defaultId";
        this.name = "defaultName";
        this.inputType = InputType.text;
        this.label = "default";
        this.validation = new FieldValidation();
        this.value = "";
        this.dataSet = new LinkedHashMap<>();
        this.dataCalls = new HashMap<>();
        this.dataSetSource = "";
        this.parent = parent;
        this.dataSetListener = null;
        this.extrasValues = new LinkedHashMap<>();
        this.scenario = null;
        this.linkText = "";
        this.dataSetRating = new LinkedHashMap<>();
        this.paymentInfoList = new HashMap<>();
    }

    /**
     * Form param constructor.
     *
     * @param parent     . Form that encapsulates the field.
     * @param name       . Name of the field
     * @param id         . Id of the form field.
     * @param obligatory . Specifies if the field is required.
     * @param inputType  . The input type for edit text
     */
    public FormField(Form parent, String name, String id, boolean obligatory, InputType inputType) {
        this.id = id;
        this.name = name;
        this.inputType = inputType;
        this.value = "";
        this.validation = new FieldValidation();
        this.validation.isRequired = obligatory;
        this.dataSet = new LinkedHashMap<>();
        this.dataCalls = new HashMap<>();
        this.dataSetSource = "";
        this.parent = parent;
        this.dataSetListener = null;
        this.extrasValues = new LinkedHashMap<>();
        this.scenario = null;
        this.dataSetRating = new LinkedHashMap<>();
        this.paymentInfoList = new HashMap<>();
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
                    inputType = InputType.text;
                    break;
                case "email":
                    inputType = InputType.email;
                    break;
                case "date":
                    inputType = InputType.date;
                    break;
                case "integer":
                case "number":
                    inputType = InputType.number;
                    break;
                case "related_number":
                    inputType = InputType.relatedNumber;
                    break;
                case "password":
                    inputType = InputType.password;
                    break;
                case "radio":
                    inputType = InputType.radioGroup;
                    break;
                case "list":
                case "select":
                    inputType = InputType.list;
                    break;
                case "array":
                case "rating":
                    inputType = InputType.rating;
                    break;
                case "boolean":
                case "checkbox":
                    inputType = InputType.checkBox;
                    break;
                case "":
                    inputType = InputType.meta;
                    break;
                case "hidden":
                    inputType = InputType.hide;
                    break;
                case "checkbox_link":
                    inputType = InputType.checkBoxLink;
                    break;
                default:
                    return false;
            }

            // if the field is one of the supported types
            id = jsonObject.optString(RestConstants.ID);
            key = jsonObject.optString(RestConstants.JSON_KEY_TAG);
            name = jsonObject.optString(RestConstants.JSON_FIELD_NAME_TAG);
            label = jsonObject.optString(RestConstants.LABEL);
            value = !jsonObject.isNull(RestConstants.VALUE) ? jsonObject.optString(RestConstants.VALUE) : "";
            scenario = jsonObject.optString(RestConstants.JSON_SCENARIO_TAG); // TODO ????
            linkText = jsonObject.optString(RestConstants.JSON_LINK_TEXT_TAG);

            Print.d("FORM FIELD: " + key + " " + name + " " + " " + label + " " + value + " " + scenario);

            // Case RULES
            JSONObject validationObject = jsonObject.optJSONObject(RestConstants.JSON_RULES_TAG);
            if(validationObject != null) {
                if (!validation.initialize(validationObject)) {
                    //Log.e(TAG, "initialize: Error parsing the rules fields");
                    result = false;
                }
            }

            // Case "data_set" //should be more generic
            JSONArray optionsArray  = jsonObject.optJSONArray(RestConstants.JSON_DATA_SET_FORM_RATING_TAG);
            //dataSetRating.clear();
            if (optionsArray != null && optionsArray.length() > 0) {
                for (int i = 0; i < optionsArray.length(); i++) {
                    dataSetRating.put(optionsArray.getJSONObject(i).optString(RestConstants.JSON_ID_FORM_RATING_TAG), optionsArray.getJSONObject(i).optString(RestConstants.JSON_TITLE_FORM_RATING_TAG));
                }
            }

            /**
             * Save api call (region and cities)
             */
            String apiCall = jsonObject.optString(RestConstants.API_CALL);
            if (!TextUtils.isEmpty(apiCall)) {
                dataCalls.put(RestConstants.API_CALL, apiCall);
            }

            /**
             * Get data from dataset as json object
             */
            JSONObject dataSetObject = jsonObject.optJSONObject(RestConstants.JSON_DATA_SET_TAG);
            if(dataSetObject != null && dataSetObject.length() > 0){
                Iterator<?> it = dataSetObject.keys();
                while (it.hasNext()) {
                    dataSet.put((String) dataSetObject.get(key), (String) dataSetObject.get(key));
                }
            }

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

            /**
             * Method to save the newsletter options
             */
            if(key.equals("newsletter_categories_subscribed") && dataOptionsArray != null) {
                newsletterOptions = new ArrayList<>();
                for (int i = 0; i < dataOptionsArray.length(); i++) {
                    newsletterOptions.add(new NewsletterOption(dataOptionsArray.getJSONObject(i), name));
                }
                dataOptionsArray = null;
                dataOptionsObject = null;
            }

            // Case shipping options from array
            if(dataOptionsArray != null){
                extrasValues.clear();
                mOptions = new ArrayList<>();
                for (int i = 0; i < dataOptionsArray.length(); ++i) {

                    // TODO
                    if(key.equals("gender")) {
                        JSONObject genderOption =dataOptionsArray.getJSONObject(i);
                        dataSet.put(genderOption.optString("label"), genderOption.optString("value"));
                    }
                    else if(scenario != null){
                        PickUpStationObject pStation = new PickUpStationObject();
                        pStation.initialize(dataOptionsArray.getJSONObject(i));
                        extrasValues.put(pStation.getIdPickupstation(), pStation);
                        dataSet.put(pStation.getName(), pStation.getName());
                    } else {

                        JSONObject option = dataOptionsArray.getJSONObject(i);
                        dataSet.put(option.optString("label"), option.optString("value"));

                        //dataSet.put(dataOptionsArray.getString(i), dataOptionsArray.getString(i));

//                        FormFieldOption fieldOption = new FormFieldOption(parent);
//                        fieldOption.initialize(option);
//                        mOptions.add(fieldOption);

                    }
                    //Log.d(TAG, "FORM FIELD: CURRENT KEY " + dataOptionsArray.getString(i));
                }
            }
            // Case shipping options from object
            else if(dataOptionsObject != null){
                Iterator<?> it = dataOptionsObject.keys();
                while (it.hasNext()) {
                    String curKey = (String) it.next();
                    dataSet.put(curKey, curKey);
                }
            }

            // Case related data (sub form)
            JSONObject relatedDataObject = jsonObject.optJSONObject(RestConstants.RELATED_DATA);
            if (relatedDataObject != null) {
                FormField formField = new FormField(this.parent);
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
                    FormFieldOption fieldOption = new FormFieldOption(parent);
                    fieldOption.initialize(option);
                    mOptions.add(fieldOption);
                }
            }

            /**
             * ########### PAYMENT METHODS ###########
             */

            if(key.equals(RestConstants.PAYMENT_METHOD)){
                dataSet.clear();
                paymentFields = new HashMap<>();
                dataOptionsObject = jsonObject.optJSONObject(RestConstants.JSON_OPTIONS_TAG);
                Iterator<?> it = dataOptionsObject.keys();
                //Clean payment method info
                while (it.hasNext()) {
                    String curKey = (String) it.next();
                    String label = dataOptionsObject.getJSONObject(curKey).getString(RestConstants.LABEL);
                    String value = dataOptionsObject.getJSONObject(curKey).getString(RestConstants.VALUE);
                    //Log.d(TAG, "FORM FIELD: CURRENT KEY " + curKey + " VALUE: " + value);
                    dataSet.put(value, label);
                    // Info
                    JSONObject paymentDescription = dataOptionsObject.optJSONObject(curKey).optJSONObject(RestConstants.JSON_DESCRIPTION_TAG);
                    PaymentInfo mPaymentInfo = new PaymentInfo();
                    mPaymentInfo.initialize(paymentDescription);
                    paymentInfoList.put(label,mPaymentInfo);
                    // Sub forms
                    Print.d("code1paymentDescription : saved : " + curKey);
                    JSONObject json = dataOptionsObject.getJSONObject(curKey);
                    Form mForm = new Form();
                    mForm.initialize(json);
                    paymentFields.put(label, mForm);
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
            switch (inputType) {
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
            jsonObject.put(RestConstants.ID, id);
            jsonObject.put(RestConstants.JSON_KEY_TAG, key);
            jsonObject.put(RestConstants.JSON_FIELD_NAME_TAG, name);
            jsonObject.put(RestConstants.LABEL, label);
            jsonObject.put(RestConstants.VALUE, value);
            jsonObject.put(RestConstants.JSON_TERMS_TAG, linkText);
            // validation
            jsonObject.put(RestConstants.JSON_RULES_TAG, validation.toJSON());
            JSONArray dataSetArray = new JSONArray();
            for (String dataSetItem : dataSet.keySet()) {
                dataSetArray.put(dataSetItem);
            }
            jsonObject.put(RestConstants.JSON_DATA_SET_TAG, dataSetArray);
            jsonObject.put(RestConstants.JSON_DATA_SET_SOURCE_TAG, dataSetSource);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    @Override
    public RequiredJson getRequiredJson() {
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.mobile.framework.forms.IFormField#getParent()
     */
    @Override
    public Form getParent() {
        return parent;
    }

    public void setParent(Form parent) {
        this.parent = parent;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.mobile.framework.forms.IFormField#getId()
     */
    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.mobile.framework.forms.IFormField#getKey()
     */
    @Override
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.mobile.framework.forms.IFormField#getName()
     */
    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.mobile.framework.forms.IFormField#getName()
     */
    @Override
    public String getScenario() {
        return scenario;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.mobile.framework.forms.IFormField#getInputType()
     */
    @Override
    public InputType getInputType() {
        return inputType;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.mobile.framework.forms.IFormField#getLabel()
     */
    @Override
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.mobile.framework.forms.IFormField#getDataSet()
     */
    @Override
    public LinkedHashMap<String, String> getDataSet() {
        return dataSet;
    }

    @Override
    public ArrayList<IFormField> getOptions() {
        return mOptions;
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
        return this.paymentFields;
    }

    public void setPaymentMethodsField(HashMap<String, Form> pFields){
        this.paymentFields = pFields;
    }

    @Override
    public Map<String, String> getDataCalls() {
        return dataCalls;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.mobile.framework.forms.IFormField#getValidation()
     */
    @Override
    public FieldValidation getValidation() {
        return validation;
    }

    public void setValidation(FieldValidation validation) {
        this.validation = validation;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.mobile.framework.forms.IFormField#getValue()
     */
    @Override
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Map<String, IFormField> getSubFormFields() {
        return null;
    }

    public void setSubFormFields(Map<String, IFormField> subFormFields) {
        // noop
    }

    @Override
    public String getLinkText() {
        return this.linkText;
    }


    @Override
    public Map<String, String> getDateSetRating() {
        return dataSetRating;
    }

    /**
     * Listener used when the data set is received.
     */
    public void setOnDataSetReceived(OnDataSetReceived listener) {
        dataSetListener = listener;
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
        dest.writeValue(parent);
        dest.writeString(id);
        dest.writeString(key);
        dest.writeString(name);
        dest.writeValue(inputType);
        dest.writeString(label);
        dest.writeString(linkText);
        dest.writeValue(dataSetRating);
        dest.writeString(scenario);
        dest.writeValue(dataSet);
        if (mOptions == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(mOptions);
        }
        dest.writeString(dataSetSource);
        dest.writeValue(dataSetListener);
        dest.writeValue(validation);
        dest.writeString(value);
        dest.writeValue(dataCalls);
        dest.writeValue(paymentFields);
        dest.writeValue(extrasValues);
        if (newsletterOptions == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(newsletterOptions);
        }
        dest.writeValue(paymentInfoList);
        dest.writeValue(mChildFormField);
        dest.writeValue(mParentFormField);
    }

    /**
     * Parcel constructor
     */
    @SuppressWarnings("unchecked")
    private FormField(Parcel in) {
        parent = (Form) in.readValue(Form.class.getClassLoader());
        id = in.readString();
        key = in.readString();
        name = in.readString();
        inputType = (InputType) in.readValue(InputType.class.getClassLoader());
        label = in.readString();
        linkText = in.readString();
        dataSetRating = (LinkedHashMap) in.readValue(LinkedHashMap.class.getClassLoader());
        scenario = in.readString();
        dataSet = (LinkedHashMap) in.readValue(LinkedHashMap.class.getClassLoader());
        if (in.readByte() == 0x01) {
            mOptions = new ArrayList<>();
            in.readList(mOptions, IFormField.class.getClassLoader());
        } else {
            mOptions = null;
        }
        dataSetSource = in.readString();
        dataSetListener = (OnDataSetReceived) in.readValue(OnDataSetReceived.class.getClassLoader());
        validation = (FieldValidation) in.readValue(FieldValidation.class.getClassLoader());
        value = in.readString();
        dataCalls = (HashMap) in.readValue(HashMap.class.getClassLoader());
        paymentFields = (HashMap) in.readValue(HashMap.class.getClassLoader());
        extrasValues = (LinkedHashMap) in.readValue(LinkedHashMap.class.getClassLoader());
        if (in.readByte() == 0x01) {
            newsletterOptions = new ArrayList<>();
            in.readList(newsletterOptions, NewsletterOption.class.getClassLoader());
        } else {
            newsletterOptions = null;
        }
        paymentInfoList = (HashMap) in.readValue(HashMap.class.getClassLoader());
        mChildFormField = (IFormField) in.readValue(IFormField.class.getClassLoader());
        mParentFormField = (IFormField) in.readValue(IFormField.class.getClassLoader());
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
