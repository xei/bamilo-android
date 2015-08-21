package com.mobile.pojo;


import com.mobile.newFramework.forms.FormField;
import com.mobile.newFramework.forms.FormFieldMeta;
import com.mobile.newFramework.forms.IFormField;
import com.mobile.newFramework.forms.InputType;
import com.mobile.newFramework.utils.output.Print;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MetaFormExtractor {

	private final static String TAG = MetaFormExtractor.class.getSimpleName();

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
		for(IFormField field: fields ) {
			if ( field.getKey().equals( metaFieldKey))
				foundMetaField = field;
		}
		Print.i(TAG, "code1checked MetaFormExtractor");
		if ( foundMetaField == null) {
			Print.i(TAG, "code1checked foundMetaField == null");
			return straightCopyFields(fields);
		}

		Set<String> subFieldKeys = metaFieldMapping.get( metaFieldKey );

		FormFieldMeta newMetaField = new FormFieldMeta(foundMetaField, metaFieldInputType.get( foundMetaField.getKey()), null);

		Map<String, IFormField> newSubFields = new HashMap<>();
		ArrayList<IFormField> transformedFields = new ArrayList<>();

		for( IFormField field: fields ) {
			Print.i(TAG, "code1checked  field.getKey() : " + field.getKey());
			if ( field.getKey().equals( newMetaField.getKey()))
				transformedFields.add( newMetaField );
			else if ( subFieldKeys != null && subFieldKeys.contains( field.getKey()))
				newSubFields.put( field.getKey(), field );
			else
				transformedFields.add( field );
		}
		
		if (subFieldKeys != null &&  !newSubFields.isEmpty()) {
			newMetaField.setSubFormFields(newSubFields );
		} else if (!newMetaField.getKey().equals("birthday")) {	//changed mobapi 1.8: birthday can't be removed
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
			Print.d(TAG, "dumpIFormField: key = " + field.getKey());
			if ( field instanceof FormFieldMeta ){
				try {
					Print.d(TAG, "dumpIFormField: isMetaField = true subKeys = " + ((FormFieldMeta) field).subFieldKeyString());
				}catch(NullPointerException e){
					Print.d(TAG, "dumpIFormField: isMetaField = true and subKeys = null " + ((FormFieldMeta) field).getKey());
				}
			}
		}

	}

	static {
		metaFieldMapping = new HashMap<>();
		metaFieldInputType = new HashMap<>();

		Set<String> metaFieldBirthday = new HashSet<>();
/*		metaFieldBirthday.add( "day" );			//mobapi 1.8: birthday structure changes
		metaFieldBirthday.add( "month" );
		metaFieldBirthday.add( "year" );*/
		metaFieldMapping.put( "birthday", metaFieldBirthday );
		metaFieldInputType.put( "birthday", InputType.metadate );
	}


}
