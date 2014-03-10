/**
 * FormEntry.java
 * Entry of a form object. Contains name, label, if is obligatory, the type of input it expects, max size of that input, and the regular expression used to value the form.
 * 
 * @author Guilherme Silva
 * 
 * @version 1.01
 * 
 * 2012/06/18
 * 
 * Copyright (c) Rocket Internet All Rights Reserved
 */
package pt.rocket.forms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.app.JumiaApplication;
import pt.rocket.framework.objects.IJSONSerializable;
import pt.rocket.framework.objects.PaymentInfo;
import pt.rocket.framework.objects.PickUpStationObject;
import pt.rocket.framework.objects.PollOption;
import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.helpers.GetFormsDatasetListHelper;
import pt.rocket.interfaces.IResponseCallback;
import pt.rocket.utils.InputType;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import de.akquinet.android.androlog.Log;

/**
 * Class that represent and entry in the form.
 * 
 * @author GuilhermeSilva
 * 
 */
public class FormField implements IJSONSerializable, IFormField, Parcelable {
    private final static String TAG = LogTagHelper.create(FormField.class);

    public interface OnDataSetReceived {
        public void DataSetReceived(Map<String, String> dataSet);
    }

    // private static final String JSON_TYPE_TAG = "type";
    // private static final String JSON_KEY_TAG = "key";
    // private static final String JSON_NAME_TAG = "name";
    // private static final String JSON_LABEL_TAG = "label";
    // private static final String JSON_VALIDATION_TAG = "rules";
    // private static final String JSON_DATASET_TAG = "dataset";
    // private static final String JSON_VALUE_TAG = "value";
    // private static final String JSON_DATASET_SOURCE_TAG = "dataset_source";

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
     * Value that defines for each scenario the Form Field should appear
     */
    private String scenario;
    
    private LinkedHashMap<String, String> dataSet;
    // public ArrayList<String> dataSet;
    private String datasetSource;
    private OnDataSetReceived dataset_Listener;

    private FieldValidation validation;

    @Deprecated
    /**
     * use validation.regex instead.
     */
    private String regEx;

    /**
     * value that is shown to the user. 
     * It's empty when it is to show the label transparent instead.
     */
    private String value;

    private Map<String, String> dataValues;

    private HashMap<String, String> dataCalls;

    private HashMap<String, String>  dataOptions;
    
    private HashMap<String, Form>  paymentFields;
    
    private LinkedHashMap<Object,Object> extrasValues; 

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
        this.dataSet = new LinkedHashMap<String, String>();
        this.dataValues = new HashMap<String, String>();
        this.dataCalls = new HashMap<String, String>();
        this.dataOptions = new HashMap<String, String>();
        this.datasetSource = "";
        this.parent = parent;
        this.dataset_Listener = null;
        this.extrasValues = new LinkedHashMap<Object, Object>();
        this.scenario = null;
    }

    /**
     * Form param cosntructor.
     * 
     * @param parent
     *            . Form that encapsulates the field.
     * @param name
     *            . Name of the field
     * @param id
     *            . Id of the form field.
     * @param obligatory
     *            . Specifies if the field is required.
     * @param inputType
     *            . Input type of the field.
     * @param maxSize
     *            . Max size of the characters.
     * @param regEx
     *            . Reguslar expression used to validate the form field.
     */
    public FormField(Form parent, String name, String id, boolean obligatory, InputType inputType, int maxSize, String regEx) {
        this.id = id;
        this.name = name;
        this.inputType = inputType;
        this.value = "";
        this.validation = new FieldValidation();
        this.validation.required = obligatory;
        this.dataSet = new LinkedHashMap<String, String>();
        this.dataValues = new HashMap<String, String>();
        this.dataCalls = new HashMap<String, String>();
        this.dataOptions = new HashMap<String, String>();
        this.datasetSource = "";
        this.parent = parent;
        this.dataset_Listener = null;
        this.extrasValues = new LinkedHashMap<Object, Object>();
        this.scenario = null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.framework.objects.IJSONSerializable#initialize(org.json.JSONObject )
     */
    @Override
    public boolean initialize(JSONObject jsonObject) {
        boolean result = true;

        try {
            // get the form field
            String formFieldString = jsonObject.optString(RestConstants.JSON_TYPE_TAG);

            if (formFieldString.equals("string") || formFieldString.equals("text")) {
                inputType = InputType.text;
            } else if (formFieldString.equals("email")) {
                inputType = InputType.email;
            } else if (formFieldString.equals("date")) {
                inputType = InputType.date;
            } else if (formFieldString.equals("integer") || formFieldString.equals("number")) {
                inputType = InputType.number;
            } else if (formFieldString.equals("password")) {
                inputType = InputType.password;
            } else if(formFieldString.equals("radio")){
                inputType = InputType.radioGroup;
            } else if (formFieldString.equals("list") || formFieldString.equals("select")) {
                inputType = InputType.list;
            } else if (formFieldString.equals("boolean") || formFieldString.equals("checkbox")) {
                inputType = InputType.checkBox;
            } else if (formFieldString.equals("")) {
            	inputType = InputType.meta;
            } else if (formFieldString.equals("hidden")) {
                inputType = InputType.hide;
            } else {
            	return false;
            }

            // if the field is one of the supported types
            if (result) {
                id = jsonObject.optString(RestConstants.JSON_ID_TAG);
                key = jsonObject.optString(RestConstants.JSON_KEY_TAG);
                name = jsonObject.getString(RestConstants.JSON_FIELD_NAME_TAG);
                label = jsonObject.optString(RestConstants.JSON_LABEL_TAG);
                value = !jsonObject.isNull(RestConstants.JSON_VALUE_TAG) ? jsonObject.optString(RestConstants.JSON_VALUE_TAG) : "";
                scenario = jsonObject.optString(RestConstants.JSON_SCENARIO_TAG);
                
                Log.d(TAG, "FORM FIELD: " + key + " " + name + " " + " " + label + " " + value);

                
                JSONObject validationObject = jsonObject.optJSONObject(RestConstants.JSON_VALIDATION_TAG);

                if (validationObject != null) {

                    if (!validation.initialize(validationObject)) {
                    	Log.e( TAG, "initialize: Error parsing the rules fields" );
                        result = false;
                    }
                }
                
                dataSet.clear();
                dataCalls.clear();
                JSONArray dataSetArray = null;
                JSONObject dataSetObject = null;
                if (!jsonObject.isNull(RestConstants.JSON_DATASET_TAG)) {
                    dataSetArray = jsonObject.optJSONArray(RestConstants.JSON_DATASET_TAG);
                    dataSetObject = jsonObject.optJSONObject(RestConstants.JSON_DATASET_TAG);
                }
                
                                
                /**
                 * Get data from dataset as json object
                 */
                if(dataSetObject != null && dataSetObject.length() > 0){
                    Iterator<?> it = dataSetObject.keys();
                    while (it.hasNext()) {
                        String curKey = (String) it.next();
                        // Validate keys
                        if(curKey.equals(RestConstants.JSON_API_CALL_TAG)){
                            dataCalls.put(curKey, (String) dataSetObject.get(RestConstants.JSON_API_CALL_TAG));
                        } else{
                            dataSet.put((String) dataSetObject.get(key), (String) dataSetObject.get(key));
                        }
                    }
                /**
                 * Get data from dataset as json array
                 */  
                } else if (dataSetArray != null && dataSetArray.length() > 0) {
                    for (int i = 0; i < dataSetArray.length(); ++i) {
                        dataSet.put(dataSetArray.getString(i), dataSetArray.getString(i));
                        Log.i(TAG, "code1put : "+dataSetArray.getString(i) );
                    }
                } else {
                    if (!jsonObject.isNull(RestConstants.JSON_DATASET_SOURCE_TAG)) {
                        datasetSource = jsonObject.optString(RestConstants.JSON_DATASET_SOURCE_TAG);
                        if (!datasetSource.equals("")) {
                            Bundle bundle = new Bundle();
                            bundle.putString(GetFormsDatasetListHelper.KEY, key);
                            bundle.putString(GetFormsDatasetListHelper.URL, datasetSource);
                            JumiaApplication.INSTANCE.sendRequest(new GetFormsDatasetListHelper(), bundle, responseCallback);
                        }
                    }
                }
                
                if(dataSet != null) {
                    Log.i(TAG, "code1put : "+dataSet.toString() );
                }
                
                /**
                 * TODO: Validate this method to get the poll values
                 */
                dataValues.clear();
                JSONArray dataValuesArray = null;
                if (!jsonObject.isNull("values")) {
                    dataValuesArray = jsonObject.optJSONArray("values");
                }
                if (dataValuesArray != null && dataValuesArray.length() > 0) {
                    for (int i = 0; i < dataValuesArray.length(); ++i) {
                        PollOption option = new PollOption(dataValuesArray.getJSONObject(i));
                        dataValues.put(option.getOption(), option.getValue());
                    }
                }
                
                
                /**
                 * Validate this method to save the shipping methods
                 */
                JSONArray dataOptionsArray = null;
                JSONObject dataOptionsObject = null;
                if(!jsonObject.isNull("options")) {
                    dataOptionsArray = jsonObject.optJSONArray("options");
                    dataOptionsObject = jsonObject.optJSONObject("options");
                    if(dataOptionsArray != null)
                        Log.i(TAG, "code1options : array : "+dataOptionsArray.toString());
                    if(dataOptionsObject != null)
                        Log.i(TAG,"code1options json "+dataOptionsObject.toString());
                }

                dataOptions.clear();
                if(dataOptionsArray != null){
                    for (int i = 0; i < dataOptionsArray.length(); ++i) {
                        if(scenario != null){
                            extrasValues.clear();
                            for (int j = 0; j < dataOptionsArray.length(); j++) {
                                PickUpStationObject pStation = new PickUpStationObject();
                                pStation.initialize(dataOptionsArray.getJSONObject(j));
                                extrasValues.put(pStation.getIdPickupstation(), pStation);
                                dataSet.put(pStation.getName(), pStation.getName());
                            }
                            
                        } else {
                            dataSet.put(dataOptionsArray.getString(i), dataOptionsArray.getString(i));    
                        }
                        Log.d(TAG, "FORM FIELD: CURRENT KEY " + dataOptionsArray.getString(i));
                        
                    }
                }else if(dataOptionsObject != null){
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
                    paymentFields = new HashMap<String, Form>();
                    dataOptionsObject = jsonObject.optJSONObject(RestConstants.JSON_OPTIONS_TAG);
                    Iterator<?> it = dataOptionsObject.keys();
                    //Clean payment method info
                    JumiaApplication.INSTANCE.setPaymentMethodForm(null);
                    JumiaApplication.INSTANCE.setPaymentsInfoList(new HashMap<String,PaymentInfo>());
                    while (it.hasNext()) {
                        
                        String curKey = (String) it.next();
                        String value = dataOptionsObject.getJSONObject(curKey).getString(RestConstants.JSON_VALUE_TAG);
                        Log.d(TAG, "FORM FIELD: CURRENT KEY " + curKey + " VALUE: " + value);
                        dataSet.put(value, curKey);
                        
                        
                        JSONObject paymentDescription = dataOptionsObject.optJSONObject(curKey).optJSONObject(RestConstants.JSON_DESCRIPTION_TAG);
                        PaymentInfo mPaymentInfo = new PaymentInfo();
                        mPaymentInfo.initialize(paymentDescription);
                        JumiaApplication.INSTANCE.getPaymentsInfoList().put(curKey,mPaymentInfo);
                        
                        Log.i(TAG, "code1paymentDescription : saved : "+curKey);
                        JSONObject json = dataOptionsObject.getJSONObject(curKey);
                        Form mForm = new Form();
                        mForm.initialize(json);
                        paymentFields.put(curKey, mForm);
                        Log.i(TAG, "code1paymentDescription : initialized form : "+curKey);
                    }
                }
                
                
                /**
                 * ########################################################
                 */
                
            }

        } catch (JSONException e) {
        	Log.e( TAG, "Error parsing the json fields" , e );
            result = false;
        }

        regEx = validation.regex;
        return result;
    }

    IResponseCallback responseCallback = new IResponseCallback() {
        
        @Override
        public void onRequestError(Bundle bundle) {
            handleErrorEvent(bundle);
        }
        
        @Override
        public void onRequestComplete(Bundle bundle) {
            handleSuccessEvent(bundle);
            
        }
    };
    
    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.framework.objects.IJSONSerializable#toJSON()
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
            default:
                jsonObject.put(RestConstants.JSON_TYPE_TAG, "string");
                break;
            }

            // fields
            jsonObject.put(RestConstants.JSON_ID_TAG, id);
            jsonObject.put(RestConstants.JSON_KEY_TAG, key);
            jsonObject.put(RestConstants.JSON_FIELD_NAME_TAG, name);
            jsonObject.put(RestConstants.JSON_LABEL_TAG, label);
            jsonObject.put(RestConstants.JSON_VALUE_TAG, value);

            // validation
            jsonObject.put(RestConstants.JSON_VALIDATION_TAG, validation.toJSON());

            // dataset

            JSONArray dataSetArray = new JSONArray();
            for (String dataSetItem : dataSet.keySet()) {
                dataSetArray.put(dataSetItem);
            }

            jsonObject.put(RestConstants.JSON_DATASET_TAG, dataSetArray);

            // datasetsource
            jsonObject.put(RestConstants.JSON_DATASET_SOURCE_TAG, datasetSource);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.framework.forms.IFormField#getParent()
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
     * @see pt.rocket.framework.forms.IFormField#getId()
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
     * @see pt.rocket.framework.forms.IFormField#getKey()
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
     * @see pt.rocket.framework.forms.IFormField#getName()
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
     * @see pt.rocket.framework.forms.IFormField#getName()
     */
    @Override
    public String getScenario() {
        return scenario;
    }

    public void setScenario(String scenario) {
        this.name = scenario;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.framework.forms.IFormField#getInputType()
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
     * @see pt.rocket.framework.forms.IFormField#getLabel()
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
     * @see pt.rocket.framework.forms.IFormField#getDataSet()
     */
    @Override
    public LinkedHashMap<String, String> getDataSet() {
        return dataSet;
    }

    public void setDataSet(LinkedHashMap<String, String> dataSet) {
        this.dataSet = dataSet;
    }
    
    
    public HashMap<String, Form> getPaymentMethodsField(){
        return this.paymentFields;
    }
    
    public void setPaymentMethodsField(HashMap<String, Form> pFields){
        this.paymentFields = pFields;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.framework.forms.IFormField#getDataSet()
     */
    @Override
    public Map<String, String> getDataValues() {
        return dataValues;
    }

    public void setDataSet(Map<String, String> dataValues) {
        this.dataValues = dataValues;
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.framework.forms.IFormField#getDatasetSource()
     */
    @Override
    public String getDatasetSource() {
        return datasetSource;
    }

    public void setDatasetSource(String datasetSource) {
        this.datasetSource = datasetSource;
    }
    
    @Override
    public Map<String, String> getDataCalls() {
        return dataCalls;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.framework.forms.IFormField#getValidation()
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
     * @see pt.rocket.framework.forms.IFormField#getRegEx()
     */
    @Override
    public String getRegEx() {
        return regEx;
    }

    public void setRegEx(String regEx) {
        this.regEx = regEx;
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.framework.forms.IFormField#getValue()
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
        dataset_Listener = listener;
    }

    @Override
    public OnDataSetReceived getOnDataSetReceived() {
        return dataset_Listener;
    }

    public void handleSuccessEvent(Bundle bundle) {
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        switch (eventType) {
        case GET_FORMS_DATASET_LIST_EVENT:
            Log.d(TAG, "Received GET_FORMS_DATASET_LIST_EVENT");

            Log.d(TAG, "Received GET_FORMS_DATASET_LIST_EVENT  ==> SUCCESS");

            dataSet = (LinkedHashMap<String, String>) bundle.getSerializable(Constants.BUNDLE_RESPONSE_KEY);

            if (null != dataset_Listener) {
                dataset_Listener.DataSetReceived(dataSet);
            }
        }
    }

    public void handleErrorEvent(Bundle bundle) {
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        switch (eventType) {
        case GET_FORMS_DATASET_LIST_EVENT:
            Log.d(TAG, "Received GET_FORMS_DATASET_LIST_EVENT  ==> FAIL");
        }
    }

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
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
        dest.writeString(datasetSource);
        dest.writeValue(parent);
        dest.writeValue(dataset_Listener);
        dest.writeMap(extrasValues);
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
        datasetSource = in.readString();
        parent = (Form) in.readValue(Form.class.getClassLoader());
        dataset_Listener = in.readParcelable(null);
        extrasValues = (LinkedHashMap<Object, Object>) in.readHashMap(null);
    }
    
    /**
     * @return the extrasValues
     */
    public LinkedHashMap<Object, Object> getExtrasValues() {
        return extrasValues;
    }

    /**
     * Some of the Form Fields have extra content that needs to be set after the user selects an option
     * This(extrasValues) will keep a list of objects with that info.
     * 
     * @param extrasValues the extrasValues to set
     */
    public void setExtrasValues(LinkedHashMap<Object, Object> extrasValues) {
        this.extrasValues = extrasValues;
    }

    /**
     * Create parcelable 
     */
    public static final Parcelable.Creator<FormField> CREATOR = new Parcelable.Creator<FormField>() {
        public FormField createFromParcel(Parcel in) {
            return new FormField(in);
        }

        public FormField[] newArray(int size) {
            return new FormField[size];
        }
    };
}
