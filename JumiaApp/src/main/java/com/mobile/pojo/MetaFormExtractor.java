package com.mobile.pojo;

import com.mobile.forms.FormField;
import com.mobile.forms.FormFieldMeta;
import com.mobile.forms.IFormField;
import com.mobile.framework.utils.LogTagHelper;
import com.mobile.utils.InputType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.akquinet.android.androlog.Log;

public class MetaFormExtractor {
	private final static String TAG = LogTagHelper.create( MetaFormExtractor.class );
	
	private static HashMap<String, Set<String>> metaFieldMapping;
	private static HashMap<String, InputType> metaFieldInputType;
	
	private static final String KEY_BIRTHDAY = "birthday";
	
	/**
     * Extracts  the birthday field and generates a meta field
     * with the day, month and year form fields
     */
	public static ArrayList<IFormField> generateMetaFields(ArrayList<FormField> fields) {
		return generateMetaField(fields, KEY_BIRTHDAY );
	}
	
	private static ArrayList<IFormField> generateMetaField( ArrayList<FormField> fields, String metaFieldKey ) {
		IFormField foundMetaField = null;
		for( IFormField field: fields ) {
			if ( field.getKey().equals( metaFieldKey))
				foundMetaField = field;
		}
		Log.i(TAG, "code1checked MetaFormExtractor");
		if ( foundMetaField == null) {
		    Log.i(TAG, "code1checked foundMetaField == null");
			return straightCopyFields(fields);
		}
		
		Set<String> subFieldKeys = metaFieldMapping.get( metaFieldKey );
		
		FormFieldMeta newMetaField = new FormFieldMeta(foundMetaField, metaFieldInputType.get( foundMetaField.getKey()), null);

		Map<String, IFormField> newSubFields = new HashMap<>();
		ArrayList<IFormField> transformedFields = new ArrayList<>();
		
		for( IFormField field: fields ) {	
		    Log.i(TAG, "code1checked  field.getKey() : "+ field.getKey());
			if ( field.getKey().equals( newMetaField.getKey()))
				transformedFields.add( newMetaField );
			else if ( subFieldKeys.contains( field.getKey()))
				newSubFields.put( field.getKey(), field );
			else
				transformedFields.add( field );
		}
		
		if ( !newSubFields.isEmpty()) {
			newMetaField.setSubFormFields(newSubFields );
		} else {
			transformedFields.remove( newMetaField );
		}
			
		return transformedFields;
	}
	
	
	
	private static ArrayList<IFormField> straightCopyFields(ArrayList<FormField> fields) {
		ArrayList<IFormField> formFields = new ArrayList<>();
		
		for( IFormField field: fields ) {
			formFields.add( field );
		}
		
		return formFields;
	}
	
	public static void dumpIFormField( ArrayList<IFormField> formFields ) {
		for( IFormField field: formFields ) {
			Log.d( TAG, "dumpIFormField: key = " + field.getKey());
			if ( field instanceof FormFieldMeta ){
				Log.d( TAG, "dumpIFormField: isMetaField = true subKeys = " + ((FormFieldMeta)field).subFieldKeyString());
			}
		}
		
	}

	static {
		metaFieldMapping = new HashMap<>();
		metaFieldInputType = new HashMap<>();
		
		Set<String> metaFieldBirthday = new HashSet<>();
		metaFieldBirthday.add( "day" );
		metaFieldBirthday.add( "month" );
		metaFieldBirthday.add( "year" );
		metaFieldMapping.put( "birthday", metaFieldBirthday );
		metaFieldInputType.put( "birthday", InputType.metadate );
	}
	
	
}