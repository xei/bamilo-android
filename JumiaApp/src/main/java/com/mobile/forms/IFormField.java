//package com.mobile.forms;
//
//import com.mobile.newFramework.forms.Form;
//import com.mobile.newFramework.forms.FormField;
//import com.mobile.newFramework.forms.InputType;
//
//import java.util.ArrayList;
//import java.util.Map;
//
//public interface IFormField {
//
//    Form getParent();
//
//    String getId();
//
//    String getKey();
//
//    String getName();
//
//    InputType getInputType();
//
//    String getLabel();
//
//    String getLinkText();
//
//    Map<String, String> getDataSet();
//
//    ArrayList<RelatedFieldOption> getRelatedFieldOptions();
//
//    int getPreSelectedRelatedOptionPosition();
//
//    String getRelatedFieldKey();
//
//    void setOnDataSetReceived(FormField.OnDataSetReceived listener);
//
//    FieldValidation getValidation();
//
//    void setValidation(FieldValidation validation);
//
//    String getValue();
//
//    Map<String, IFormField> getSubFormFields();
//
//    void setSubFormFields(Map<String, IFormField> formFields);
//
//    Map<String, String> getDataCalls();
//
//    String getScenario();
//
//    Map<String, String> getDateSetRating();
//
//}