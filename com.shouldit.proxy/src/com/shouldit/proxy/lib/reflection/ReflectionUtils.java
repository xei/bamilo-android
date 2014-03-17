package com.shouldit.proxy.lib.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.shouldit.proxy.lib.LogWrapper;

import android.util.Log;

public class ReflectionUtils
{
	public static final String TAG = "ReflectionUtils";
	
	public static Field getField(Field [] fields, String fieldName) throws Exception
	{
		Field f = null;
		
		for (Field lf:fields)
		{
			String currentFieldName = lf.getName(); 
			if(currentFieldName.equals(fieldName))
			{
				f = lf;
				break;
			}
		}
		
		if (f == null)
        	throw new Exception(new String(fieldName + " field not found!"));
		
		return f;
	}

	static void describeClassOrInterface(Class className, String name)
	{
		displayModifiers(className.getModifiers());
		displayFields(className.getDeclaredFields());
		displayMethods(className.getDeclaredMethods());

		if (className.isInterface()) 
		{
			LogWrapper.d(TAG, "Interface: " + name);
		} 
		else 
		{
			LogWrapper.d(TAG, "Class: " + name);
			displayInterfaces(className.getInterfaces());
			displayConstructors(className.getDeclaredConstructors());
		}
	}

	static void displayModifiers(int m)
	{
		LogWrapper.d(TAG, "Modifiers: " + Modifier.toString(m));
	}

	static void displayInterfaces(Class[] interfaces)
	{
		if (interfaces.length > 0) 
		{
			LogWrapper.d(TAG, "Interfaces: ");
			for (int i = 0; i < interfaces.length; ++i)
				LogWrapper.d("", interfaces[i].getName());
		}
	}

	static void displayFields(Field[] fields)
	{
		if (fields.length > 0) 
		{
			LogWrapper.d(TAG, "Fields: ");
			for (int i = 0; i < fields.length; ++i)
				LogWrapper.d(TAG, fields[i].toString());
		}
	}

	static void displayConstructors(Constructor[] constructors)
	{
		if (constructors.length > 0) 
		{
			LogWrapper.d(TAG, "Constructors: ");
			for (int i = 0; i < constructors.length; ++i)
				LogWrapper.d(TAG, constructors[i].toString());
		}
	}

	static void displayMethods(Method[] methods)
	{
		if (methods.length > 0) 
		{
			LogWrapper.d(TAG, "Methods: ");
			for (int i = 0; i < methods.length; ++i)
				LogWrapper.d(TAG, methods[i].toString());
		}
	}

	public static Field[] getAllFields(Class klass)
	{
		List<Field> fields = new ArrayList<Field>();
		fields.addAll(Arrays.asList(klass.getDeclaredFields()));
		
		if (klass.getSuperclass() != null) 
		{
			fields.addAll(Arrays.asList(getAllFields(klass.getSuperclass())));
		}
		
		return fields.toArray(new Field[] {});
	}
}
