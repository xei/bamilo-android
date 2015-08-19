package com.mobile.newFramework.forms;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.EventType;
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

    public interface OnDataSetReceived {
        void DataSetReceived(Map<String, String> dataSet);
    }

    private Form parent;

    /**
     * name of the field. Use label when showing it to the user
     */
    private String id;
    private String key;
    /**
     * Label used when displaying the entry to the user
     */
    private String name;
    /**
     * Input type of the FormEntry. Can be text or numerical
     */
    private InputType inputType;

    /**
     * Value used to show as the label to show to the user.
     */
    private String label;

    /**
     * Value used to show as the clickable text
     * (e.g. for terms and conditions)
     */
    private String linkText;

    /**
     * variable used to save the rating options
     */
    private LinkedHashMap<String, String> dataSetRating;
    /**
     * Value that defines for each scenario the Form Field should appear
     */
    private String scenario;

    private LinkedHashMap<String, String> dataSet;

    private String dataSetSource;

    private OnDataSetReceived dataSetListener;

    private FieldValidation validation;

    /**
     * TODO
     */
    private ArrayList<RelatedFieldOption> mRelatedFieldOptions;
    private int preSelectedRelatedOption = 0;
    private String mRelatedFieldKey;

    /**
     * value that is shown to the user.
     * It's empty when it is to show the label transparent instead.
     */
    private String value;

    private HashMap<String, String> dataCalls;

    //private HashMap<String, String>  dataOptions;

    private HashMap<String, Form>  paymentFields;

    private LinkedHashMap<Object,Object> extrasValues;

    public ArrayList<NewsletterOption> newsletterOptions;

    public HashMap<String,PaymentInfo> paymentInfoList;

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
        //this.dataOptions = new HashMap<>();
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
        //this.dataOptions = new HashMap<>();
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
            //Log.i(TAG, "FORM FIELD: " + jsonObject.toString());
            // get the form field
            String formFieldString = jsonObject.optString(RestConstants.JSON_TYPE_TAG);
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
                    inputType = InputType.checkBoxList;
                    break;
                case "radio_related":
                    inputType = InputType.radioRelated;
                    break;
                default:
                    return false;
            }

            // if the field is one of the supported types
            id = jsonObject.optString(RestConstants.JSON_ID_TAG); //comes empty in mobapi 1.8, necessary for tracking
            key = jsonObject.optString(RestConstants.JSON_KEY_TAG);
            name = jsonObject.getString(RestConstants.JSON_FIELD_NAME_TAG);
            label = jsonObject.optString(RestConstants.LABEL);
            value = !jsonObject.isNull(RestConstants.VALUE) ? jsonObject.optString(RestConstants.VALUE) : "";
            scenario = jsonObject.optString(RestConstants.JSON_SCENARIO_TAG); // TODO ????
            linkText = jsonObject.optString(RestConstants.JSON_LINK_TEXT_TAG);
            mRelatedFieldKey = jsonObject.optString(RestConstants.JSON_RELATED_FIELD_TAG);
            Print.d("FORM FIELD: " + key + " " + name + " " + " " + label + " " + value + " " + scenario + " RADIO RELATED:" + mRelatedFieldKey);

            // Case RULES
            JSONObject validationObject = jsonObject.optJSONObject(RestConstants.JSON_RULES_TAG);
            if(validationObject != null) {
                if (!validation.initialize(validationObject)) {
                    //Log.e(TAG, "initialize: Error parsing the rules fields");
                    result = false;
                }
            }

//            /**
//             * Validate and hide the city key for create/edit address form.<br>
//             * WARNING: In Uganda(Jumia) and Pakistan(Daraz), sometimes the city comes as a list and crashes the application.
//             * @author sergiopereira
//             */
//            if(key != null && key.equals(RestConstants.JSON_CITY_TAG) && inputType == InputType.list) {
//                inputType = InputType.hide;
//            }

            // Case "data_set" //should be more generic
            JSONArray optionsArray  = jsonObject.optJSONArray(RestConstants.JSON_DATA_SET_FORM_RATING_TAG);
            dataSetRating.clear();
            if (optionsArray != null && optionsArray.length() > 0) {
                for (int i = 0; i < optionsArray.length(); i++) {
                    //Log.e("RATING","key:"+optionsArray.getJSONObject(i).optString(RestConstants.JSON_ID_FORM_RATING_TAG)+" value:"+ optionsArray.getJSONObject(i).optString(RestConstants.JSON_TITLE_FORM_RATING_TAG));
                    dataSetRating.put(optionsArray.getJSONObject(i).optString(RestConstants.JSON_ID_FORM_RATING_TAG), optionsArray.getJSONObject(i).optString(RestConstants.JSON_TITLE_FORM_RATING_TAG));
                }
            }

            dataSet.clear();

            JSONArray dataSetArray = null;
            JSONObject dataSetObject = null;

            // Case "dataset" TODO Unify dataset response
            if (!jsonObject.isNull(RestConstants.JSON_DATA_SET_TAG)) {
                dataSetArray = jsonObject.optJSONArray(RestConstants.JSON_DATA_SET_TAG);
                dataSetObject = jsonObject.optJSONObject(RestConstants.JSON_DATA_SET_TAG);
            }

            /**
             * Save api call (region and cities)
             */
            dataCalls.clear();
            String apiCall = jsonObject.optString(RestConstants.API_CALL);
            if (!TextUtils.isEmpty(apiCall)) {
                dataCalls.put(RestConstants.API_CALL, apiCall);
            }

            /**
             * Get data from dataset as json object
             */
            if(dataSetObject != null && dataSetObject.length() > 0){
                Iterator<?> it = dataSetObject.keys();
                while (it.hasNext()) {
                    dataSet.put((String) dataSetObject.get(key), (String) dataSetObject.get(key));
                }
            /**
             * Get data from dataset as json array
             */
            } else if (dataSetArray != null && dataSetArray.length() > 0) {
                mRelatedFieldOptions = new ArrayList<>();
                for (int i = 0; i < dataSetArray.length(); ++i) {
                    // New options
                    JSONObject dataItem = dataSetArray.optJSONObject(i);
                    if(dataItem != null) {
                        RelatedFieldOption formFieldOption = new RelatedFieldOption();
                        formFieldOption.initialize(dataItem);
                        mRelatedFieldOptions.add(formFieldOption);
                        if(formFieldOption.isDefault()) {
                            preSelectedRelatedOption = i;
                        }
                    }
                    // Case old TODO: Remove this case in the next release
                    else {
                        String label = dataSetArray.getString(i);
                        dataSet.put(label, label);
                    }
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
                //if(dataOptionsArray != null) Log.i(TAG, "code1options : array : "+dataOptionsArray.toString());
                //if(dataOptionsObject != null) Log.i(TAG,"code1options json "+dataOptionsObject.toString());
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
            //dataOptions.clear();
            if(dataOptionsArray != null){
                extrasValues.clear();
                for (int i = 0; i < dataOptionsArray.length(); ++i) {
                    //alexandrapires: mobapi 1.8: gender cames as option
                    if(key.equals("gender"))
                    {
                        JSONObject genderOption =dataOptionsArray.getJSONObject(i);
                        dataSet.put(genderOption.optString("label"), genderOption.optString("value"));
                    }

                    else if(scenario != null){
                        PickUpStationObject pStation = new PickUpStationObject();
                        pStation.initialize(dataOptionsArray.getJSONObject(i));
                        extrasValues.put(pStation.getIdPickupstation(), pStation);
                        dataSet.put(pStation.getName(), pStation.getName());
                    } else {
                        dataSet.put(dataOptionsArray.getString(i), dataOptionsArray.getString(i));
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

            /**
             * ########### PAYMENT METHODS ###########
             */

            if(key.equals("payment_method")){
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

                    JSONObject paymentDescription = dataOptionsObject.optJSONObject(curKey).optJSONObject(RestConstants.JSON_DESCRIPTION_TAG);
                    PaymentInfo mPaymentInfo = new PaymentInfo();
                    mPaymentInfo.initialize(paymentDescription);

                    paymentInfoList.put(label,mPaymentInfo);

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
                jsonObject.put(RestConstants.JSON_TYPE_TAG, "boolean");
                break;
            case date:
                jsonObject.put(RestConstants.JSON_TYPE_TAG, "date");
                break;
            case email:
                jsonObject.put(RestConstants.JSON_TYPE_TAG, "email");
                break;
            case number:
                jsonObject.put(RestConstants.JSON_TYPE_TAG, "integer");
                break;
            case password:
                jsonObject.put(RestConstants.JSON_TYPE_TAG, "password");
                break;
            case radioGroup:
                jsonObject.put(RestConstants.JSON_TYPE_TAG, "radio");
                break;
            case list:
                jsonObject.put(RestConstants.JSON_TYPE_TAG, "list");
                break;
            case text:
                jsonObject.put(RestConstants.JSON_TYPE_TAG, "string");
                break;
            case checkBoxList:
                jsonObject.put(RestConstants.JSON_TYPE_TAG, "boolean");
                break;
            case rating:
                jsonObject.put(RestConstants.JSON_TYPE_TAG, "rating");
                break;
            default:
                jsonObject.put(RestConstants.JSON_TYPE_TAG, "string");
                break;
            }

            // fields
            jsonObject.put(RestConstants.JSON_ID_TAG, id);
            jsonObject.put(RestConstants.JSON_KEY_TAG, key);
            jsonObject.put(RestConstants.JSON_FIELD_NAME_TAG, name);
            jsonObject.put(RestConstants.LABEL, label);
            jsonObject.put(RestConstants.VALUE, value);
            jsonObject.put(RestConstants.JSON_TERMS_TAG, linkText);
            // validation
            jsonObject.put(RestConstants.JSON_RULES_TAG, validation.toJSON());


            //FIXME add rating data set to Json Object

            // dataset
            JSONArray dataSetArray = new JSONArray();
            for (String dataSetItem : dataSet.keySet()) {
                dataSetArray.put(dataSetItem);
            }

            jsonObject.put(RestConstants.JSON_DATA_SET_TAG, dataSetArray);

            // datasetsource
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

    public void setInputType(InputType inputType) {
        this.inputType = inputType;
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
    public ArrayList<RelatedFieldOption> getRelatedFieldOptions() {
        return this.mRelatedFieldOptions;
    }
    @Override
    public int getPreSelectedRelatedOptionPosition() {
        return this.preSelectedRelatedOption;
    }

    @Override
    public String getRelatedFieldKey() {
        return this.mRelatedFieldKey;
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

    /**
     * Listener used when the data set is received.
     *
     * @param listener
     */
    public void setOnDataSetReceived(OnDataSetReceived listener) {
        dataSetListener = listener;
    }

    public void handleSuccessEvent(Bundle bundle) {
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        switch (eventType) {
        case GET_FORMS_DATA_SET_LIST_EVENT:
            Print.d("Received GET_FORMS_DATASET_LIST_EVENT");

            Print.d("Received GET_FORMS_DATASET_LIST_EVENT  ==> SUCCESS");

            dataSet = (LinkedHashMap<String, String>) bundle.getSerializable(Constants.BUNDLE_RESPONSE_KEY);

            if (null != dataSetListener) {
                dataSetListener.DataSetReceived(dataSet);
            }
        default:
            break;
        }
    }

    public void handleErrorEvent(Bundle bundle) {
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        switch (eventType) {
        case GET_FORMS_DATA_SET_LIST_EVENT:
            Print.d("Received GET_FORMS_DATASET_LIST_EVENT  ==> FAIL");
        default:
            break;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeSerializable(inputType);
        dest.writeString(label);
        dest.writeValue(validation);
        dest.writeString(value);
        dest.writeMap(dataSet);
        dest.writeString(dataSetSource);
        dest.writeValue(parent);
        dest.writeValue(dataSetListener);
        dest.writeMap(extrasValues);
        dest.writeString(linkText);
        dest.writeMap(dataSetRating);
    }

    /**
     * Parcel constructor
     * @param in
     */
    private FormField(Parcel in) {
        id = in.readString();
        name = in.readString();
        inputType = (InputType) in.readSerializable();
        label = in.readString();
        validation = (FieldValidation) in.readValue(FieldValidation.class.getClassLoader());
        value = in.readString();
        dataSet = (LinkedHashMap<String, String>) in.readHashMap(null);
        dataSetSource = in.readString();
        parent = (Form) in.readValue(Form.class.getClassLoader());
        dataSetListener = (OnDataSetReceived) in.readValue(OnDataSetReceived.class.getClassLoader());
        extrasValues = (LinkedHashMap<Object, Object>) in.readHashMap(null);
        linkText = in.readString();
        dataSetRating = (LinkedHashMap<String, String>) in.readHashMap(null);
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
    public String getLinkText() {
        return this.linkText;
    }


    @Override
    public Map<String, String> getDateSetRating() {
        return dataSetRating;
    }

}
