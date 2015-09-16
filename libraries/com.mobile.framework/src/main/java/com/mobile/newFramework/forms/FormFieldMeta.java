//package com.mobile.newFramework.forms;
//
//import com.mobile.newFramework.forms.FormField.OnDataSetReceived;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Set;
//
//public class FormFieldMeta implements IFormField {
//	private IFormField metaFormField;
//	private Map<String, IFormField> subFormFields;
//	private InputType inputType;
//
//	public FormFieldMeta( IFormField metaFormField, InputType inputType, Map<String, IFormField> subFormFields) {
//		this.metaFormField = metaFormField;
//		this.inputType = inputType;
//		this.subFormFields = subFormFields;
//	}
//
//
//	@Override
//	public Form getParent() {
//		return metaFormField.getParent();
//	}
//
//	@Override
//	public String getId() {
//		return metaFormField.getId();
//	}
//
//	@Override
//	public String getKey() {
//		return metaFormField.getKey();
//	}
//
//	@Override
//	public String getName() {
//		return metaFormField.getName();
//	}
//
//	@Override
//	public InputType getInputType() {
//		return inputType;
//	}
//
//	@Override
//	public String getLabel() {
//		return metaFormField.getLabel();
//	}
//
//	@Override
//	public HashMap<String, String> getDataSet() {
//		return null;
//	}
//
//	@Override
//	public FormField getRelatedField() {
//		return metaFormField.getRelatedField();
//	}
//
////	@Override
////	public ArrayList<RelatedFieldOption> getRelatedFieldOptions() {
////		return metaFormField.getRelatedFieldOptions();
////	}
////
////	@Override
////	public int getPreSelectedRelatedOptionPosition() {
////		return metaFormField.getPreSelectedRelatedOptionPosition();
////	}
////
////	@Override
////	public String getRelatedFieldKey() {
////		return null;
////	}
//
//	@Override
//	public void setOnDataSetReceived(OnDataSetReceived dataSetReceived) {
//		// noop
//	}
//
//	@Override
//	public FieldValidation getValidation() {
//		Set<String> keySet = subFormFields.keySet();
//		if ( !keySet.iterator().hasNext())
//			return null;
//
//		IFormField field = subFormFields.get( keySet.iterator().next());
//		return field.getValidation();
//	}
//
//	@Override
//	public void setValidation(FieldValidation validation) {
//		metaFormField.setValidation(validation);
//	}
//
//	@Override
//	public String getValue() {
//		return null;
//	}
//
//	@Override
//	public Map<String, IFormField> getSubFormFields() {
//		return subFormFields;
//	}
//
//
//	@Override
//	public void setSubFormFields(Map<String, IFormField> formFields) {
//		this.subFormFields = formFields;
//	}
//
//	public String subFieldKeyString() {
//		StringBuilder sb = new StringBuilder();
//		for( String key: subFormFields.keySet()) {
//			IFormField field = subFormFields.get( key );
//			sb.append( field.getKey()).append( " " );
//
//		}
//
//		return sb.toString();
//	}
//
//    @Override
//    public Map<String, String> getDataCalls() {
//        return metaFormField.getDataCalls();
//    }
//
//
//    @Override
//    public String getScenario() {
//        return metaFormField.getScenario();
//    }
//
//
//    @Override
//    public String getLinkText() {
//        return metaFormField.getLinkText();
//    }
//
//
//    @Override
//    public Map<String, String> getDateSetRating() {
//        return metaFormField.getDateSetRating();
//    }
//
//
//
//}