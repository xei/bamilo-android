package pt.rocket.forms;

import java.util.Map;

import pt.rocket.forms.FormField.OnDataSetReceived;

public interface IFormField {

	public abstract Form getParent();

	public abstract String getId();

	public abstract String getKey();

	public abstract String getName();

	public abstract InputType getInputType();

	public abstract String getLabel();

	public abstract Map<String, String> getDataSet();

	public abstract String getDatasetSource();

	public abstract OnDataSetReceived getOnDataSetReceived();

	public abstract void setOnDataSetReceived( OnDataSetReceived listener );
	
	public abstract FieldValidation getValidation();

	public abstract String getRegEx();

	public abstract String getValue();
	
	public abstract void setValue( String value );
	
	public abstract Map<String, IFormField> getSubFormFields();
	
	public abstract void setSubFormFields( Map<String, IFormField> formFields);

}