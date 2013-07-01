package pt.rocket.framework.forms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import pt.rocket.framework.forms.FormField.OnDataSetReceived;

public class FormFieldMeta implements IFormField {
	private IFormField metaFormField;
	private Map<String, IFormField> subFormFields;
	private InputType inputType;
	
	public FormFieldMeta( IFormField metaFormField, InputType inputType, Map<String, IFormField> subFormFields) {
		this.metaFormField = metaFormField;
		this.inputType = inputType;
		this.subFormFields = subFormFields;
	}
	

	@Override
	public Form getParent() {
		return metaFormField.getParent();
	}

	@Override
	public String getId() {
		return metaFormField.getId();
	}

	@Override
	public String getKey() {
		return metaFormField.getKey();
	}

	@Override
	public String getName() {
		return metaFormField.getName();
	}

	@Override
	public InputType getInputType() {
		return inputType;
	}

	@Override
	public String getLabel() {
		return metaFormField.getLabel();
	}

	@Override
	public HashMap<String, String> getDataSet() {
		return null;
	}

	@Override
	public String getDatasetSource() {
		return null;
	}

	@Override
	public OnDataSetReceived getOnDataSetReceived() {
		return null;
	}
	
	@Override
	public void setOnDataSetReceived(OnDataSetReceived dataset_Listener) {
		// noop
	}

	@Override
	public FieldValidation getValidation() {
		Set<String> keySet = subFormFields.keySet();
		if ( !keySet.iterator().hasNext())
			return null;
		
		IFormField field = subFormFields.get( keySet.iterator().next());
		return field.getValidation();
	}

	@Override
	public String getRegEx() {
		return null;
	}

	@Override
	public String getValue() {
		return null;
	}
	
	@Override
	public void setValue( String value ) {
		// noop
	}


	@Override
	public Map<String, IFormField> getSubFormFields() {
		return subFormFields;
	}


	@Override
	public void setSubFormFields(Map<String, IFormField> formFields) {
		this.subFormFields = formFields;
	}
	
	public String subFieldKeyString() {
		StringBuilder sb = new StringBuilder();
		for( String key: subFormFields.keySet()) {
			IFormField field = subFormFields.get( key );
			sb.append( field.getKey()).append( " " );

		}
				
		return sb.toString();
	}
	
	

}
