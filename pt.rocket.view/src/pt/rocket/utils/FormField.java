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
package pt.rocket.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.framework.interfaces.IJSONSerializable;
import pt.rocket.framework.rest.RestConstants;
import de.akquinet.android.androlog.Log;

/**
 * Class that represent and entry in the form.
 * 
 * @author GuilhermeSilva
 * 
 */
public class FormField implements IJSONSerializable, IFormField  {
	private final static String TAG = "";
	
    public interface OnDataSetReceived {
        public void DataSetReceived(Map<String, String> dataSet);
    }

//    private static final String JSON_TYPE_TAG = "type";
//    private static final String JSON_KEY_TAG = "key";
//    private static final String JSON_NAME_TAG = "name";
//    private static final String JSON_LABEL_TAG = "label";
//    private static final String JSON_VALIDATION_TAG = "rules";
//    private static final String JSON_DATASET_TAG = "dataset";
//    private static final String JSON_VALUE_TAG = "value";
//    private static final String JSON_DATASET_SOURCE_TAG = "dataset_source";
    private static final String JSON_DATASET_TRANSLATION_TAG = "dataset_translation";

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

    private Map<String, String> dataSet;
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
     * value that is shown to the user. It's empty when it is to show the label
     * transparent instead.
     */
    private String value;

    /**
     * FormField param constructor
     * 
     * @param parent
     *            . Form hat encapsulates the form field.
     */
    public FormField(Form parent) {
        id = "defaultId";
        name = "defaultName";
        inputType = InputType.text;
        label = "default";
        validation = new FieldValidation();
        value = "";
        dataSet = new HashMap<String, String>();
        // dataSet = new ArrayList<String>();
        datasetSource = "";
        this.parent = parent;
        this.dataset_Listener = null;
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
     *            . Regular expression used to validate the form field.
     */
    public FormField(Form parent, String name, String id, boolean obligatory, InputType inputType, int maxSize, String regEx) {
        this.id = id;
        this.name = name;
        this.inputType = inputType;
        value = "";

        validation = new FieldValidation();
        validation.required = obligatory;
        dataSet = new HashMap<String, String>();
        // dataSet = new ArrayList<String>();
        datasetSource = "";
        this.parent = parent;
        this.dataset_Listener = null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * pt.rocket.framework.objects.IJSONSerializable#initialize(org.json.JSONObject
     * )
     */
    
    public boolean initialize(JSONObject jsonObject) {
        boolean result = true;

        try {
            // get the form field
            String formFieldString = jsonObject.optString(RestConstants.JSON_TYPE_TAG);

            if (formFieldString.equals("string")) {
            	String formKeyString = jsonObject.optString("key");
  
            	JSONObject rulesObject = jsonObject.optJSONObject("rules");
            	if(rulesObject!=null){
                	boolean semi= rulesObject.optBoolean("semiRequired", false);
                	if(semi){
//                      	android.util.Log.e("formKeyString", ":"+formKeyString);
                		inputType = InputType.semiRequired;
                	}else{
                		inputType = InputType.text;	
                	}
            	}else{
            		inputType = InputType.text;
            	}

                
            } else if (formFieldString.equals("email")) {
                inputType = InputType.email;
            } else if (formFieldString.equals("date")) {
                inputType = InputType.date;
            } else if (formFieldString.equals("integer") || formFieldString.equals("number")) {
                inputType = InputType.number;
            } else if (formFieldString.equals("password")) {
                inputType = InputType.password;
            } else if (formFieldString.equals("radio") || formFieldString.equals("list")) {
                inputType = InputType.radioGroup;
            } else if (formFieldString.equals("boolean") || formFieldString.equals("checkbox")) {
                inputType = InputType.checkBox;
            } else if (formFieldString.equals("")) {
            	inputType = InputType.meta;
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

                JSONObject validationObject = jsonObject.optJSONObject(RestConstants.JSON_VALIDATION_TAG);

                if (validationObject != null) {

                    if (!validation.initialize(validationObject)) {
                    	Log.e( TAG, "initialize: Error parsing the rules fields" );
                        result = false;
                    }
                }

                dataSet.clear();
                JSONArray dataSetArray = null;
                if (!jsonObject.isNull(RestConstants.JSON_DATASET_TAG)) {
                    dataSetArray = jsonObject.optJSONArray(RestConstants.JSON_DATASET_TAG);
                }

                Map<String, String> datasetTranslations = new HashMap<String, String>();
                if (!jsonObject.isNull(JSON_DATASET_TRANSLATION_TAG)) {
                    JSONObject dataSetObj = jsonObject.optJSONObject(JSON_DATASET_TRANSLATION_TAG);
                    Iterator<String> keys = dataSetObj.keys();
                    while (keys.hasNext()) {
                    	String key = keys.next();
                    	datasetTranslations.put(key, dataSetObj.getString(key));
                    }
                }
                
                if (dataSetArray != null && dataSetArray.length() > 0) {
                    for (int i = 0; i < dataSetArray.length(); ++i) {
                        String dataSetKey = dataSetArray.getString(i);
                        String dataSetValue;
                        //by default, key is value - but we need translated value to show in the form, eg for gender
                        if (datasetTranslations.get(dataSetKey) != null) {
                        	dataSetValue = datasetTranslations.get(dataSetKey);
                        } else {
                        	dataSetValue = dataSetKey;
                        }
                        dataSet.put(dataSetKey, dataSetValue);
                    }
                } else {
                    if (!jsonObject.isNull(RestConstants.JSON_DATASET_SOURCE_TAG)) {
                        datasetSource = jsonObject.optString(RestConstants.JSON_DATASET_SOURCE_TAG);
                        if (!datasetSource.equals("")) {
                          //  EventManager.getSingleton().triggerRequestEvent(new GetFormsDatasetListEvent(key, datasetSource), this);
                        }
                    }
                }
            }

        } catch (JSONException e) {
        	Log.e( TAG, "Error parsing the json fields" , e );
            result = false;
        }

        regEx = validation.regex;
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.framework.objects.IJSONSerializable#toJSON()
     */

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

    /* (non-Javadoc)
	 * @see pt.rocket.framework.forms.IFormField#getParent()
	 */
    @Override
	public Form getParent() {
		return parent;
	}

	public void setParent(Form parent) {
		this.parent = parent;
	}

	/* (non-Javadoc)
	 * @see pt.rocket.framework.forms.IFormField#getId()
	 */
	@Override
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see pt.rocket.framework.forms.IFormField#getKey()
	 */
	@Override
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	/* (non-Javadoc)
	 * @see pt.rocket.framework.forms.IFormField#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/* (non-Javadoc)
	 * @see pt.rocket.framework.forms.IFormField#getInputType()
	 */
	@Override
	public InputType getInputType() {
		return inputType;
	}

	public void setInputType(InputType inputType) {
		this.inputType = inputType;
	}

	/* (non-Javadoc)
	 * @see pt.rocket.framework.forms.IFormField#getLabel()
	 */
	@Override
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	/* (non-Javadoc)
	 * @see pt.rocket.framework.forms.IFormField#getDataSet()
	 */
	@Override
	public Map<String, String> getDataSet() {
		return dataSet;
	}

	public void setDataSet(HashMap<String, String> dataSet) {
		this.dataSet = dataSet;
	}

	/* (non-Javadoc)
	 * @see pt.rocket.framework.forms.IFormField#getDatasetSource()
	 */
	@Override
	public String getDatasetSource() {
		return datasetSource;
	}

	public void setDatasetSource(String datasetSource) {
		this.datasetSource = datasetSource;
	}

	/* (non-Javadoc)
	 * @see pt.rocket.framework.forms.IFormField#getValidation()
	 */
	@Override
	public FieldValidation getValidation() {
		return validation;
	}

	public void setValidation(FieldValidation validation) {
		this.validation = validation;
	}

	/* (non-Javadoc)
	 * @see pt.rocket.framework.forms.IFormField#getRegEx()
	 */
	@Override
	public String getRegEx() {
		return regEx;
	}

	public void setRegEx(String regEx) {
		this.regEx = regEx;
	}

	/* (non-Javadoc)
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
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setSubFormFields(Map<String, IFormField> formFields) {
        // TODO Auto-generated method stub
        
    }


}