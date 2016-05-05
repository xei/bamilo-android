package com.mobile.newFramework.forms;

import com.mobile.newFramework.forms.FormField.OnDataSetReceived;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public interface IFormField {

    String getId();

    String getKey();

    String getName();

    String getPlaceHolder();

    FormInputType getInputType();

    String getLabel();

    String getText();

    String getSubText();

    HashMap<String, Form> getSubForms();

    String getSubLabel();

    String getLinkText();

    String getLinkTarget();

    String getLinkHtml();

    String getFormat();

    Map<String, String> getDataSet();

    void setOnDataSetReceived(OnDataSetReceived listener);

    FieldValidation getValidation();

    void setValidation(FieldValidation validation);

    String getValue();

    void setValue(String value);

    String getApiCall();

    Map<String, String> getDateSetRating();

    IFormField getRelatedField();

    IFormField getParentField();

    void setParentField(IFormField formField);

    ArrayList<IFormField> getOptions();

    boolean isChecked();

    boolean isPrefixField();

    boolean isDisabledField();

    boolean isVerticalOrientation();

    void setFormType(int mType);

    int getFormType();
}