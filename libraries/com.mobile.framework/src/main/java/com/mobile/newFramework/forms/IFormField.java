package com.mobile.newFramework.forms;

import com.mobile.newFramework.forms.FormField.OnDataSetReceived;

import java.util.ArrayList;
import java.util.Map;

public interface IFormField {

    Form getParent();

    String getId();

    String getKey();

    String getName();

    FormInputType getInputType();

    String getLabel();

    String getLinkText();

    String getFormat();

    Map<String, String> getDataSet();

    void setOnDataSetReceived(OnDataSetReceived listener);

    FieldValidation getValidation();

    void setValidation(FieldValidation validation);

    String getValue();

    Map<String, IFormField> getSubFormFields();

    Map<String, String> getDataCalls();

    Map<String, String> getDateSetRating();

    IFormField getRelatedField();

    IFormField getParentField();

    void setParentField(IFormField formField);

    ArrayList<IFormField> getOptions();

    boolean isDefaultSelection();

    boolean isPrefixField();
}