package org.gekko.beanfactory.propertyeditor;

import java.beans.PropertyEditorSupport;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.gekko.beanfactory.BeanRef;
import org.springframework.util.Assert;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

public class JSONPropertyEditor
	extends PropertyEditorSupport
{

	private static final Logger logger = Logger.getLogger(JSONPropertyEditor.class);

	private final Pattern pattern = Pattern.compile("(\\[.+\\])?[ ]*(.+)");
	private final Pattern typeInfoPattern = Pattern.compile("\\[([^<>]+)?[ ]*(<(.+)+>)?\\]");

	private Class<?> defaultTargetClass = Object.class;

	private final List<String> standardPackagesToScan = Arrays.asList("java.lang", "java.util");
	private List<String> additionalPackagesToScan = Collections.emptyList();

	private final Gson gson = new GsonBuilder().create();

	public JSONPropertyEditor()
	{
		// empty ctor
	}

	public JSONPropertyEditor(Class<?> defaultTargetClass)
	{
		this.defaultTargetClass = defaultTargetClass;
	}

	public void setDefaultTargetClass(Class<?> defaultTargetClass)
	{
		this.defaultTargetClass = defaultTargetClass;
	}

	public void setAdditionalPackagesToScan(List<String> additionalPackagesToScan)
	{
		this.additionalPackagesToScan = additionalPackagesToScan;
	}

	@Override
	public String getAsText()
	{
		Object object = getValue();

		return gson.toJson(object);
	}

	@Override
	public void setAsText(String text)
	{

		Matcher m = pattern.matcher(text);
		m.matches();

		String jsonText = text;
		String typeInfo = "";

		String rowType = "";
		String[] typeParams = new String[]{};

		int n = m.groupCount();
		if (n > 1) {
			for (int i = 0; i <= n; ++i) {
				if (i == 1) {
					typeInfo = m.group(i);
				}
				else if (i == 2) {
					jsonText = m.group(i);
				}
			}
		}

		if (typeInfo != null && typeInfo.length() > 0) {
			Matcher m1 = typeInfoPattern.matcher(typeInfo);
			m1.matches();

			int n1 = m1.groupCount();
			for (int i = 0; i <= n1; ++i) {
				if (i == 1) {
					rowType = m1.group(i);
				}
				else if (i == 3) {
					if (m1.group(i) != null) {
						typeParams = m1.group(i).split(", ");
					}
				}
			}
		}

		Class<?> rowTypeClass = defaultTargetClass;

		if (rowType != null && rowType.length() > 0) {
			/*
			 * the target class was present in the initial string
			 */
			rowTypeClass = findClass(rowType);
		}

		Assert.notNull(rowTypeClass, "can't identify the rowType: " + rowType);

		Object value = null;

		/*
		 * make sure there are enough parameters
		 */
		if (Map.class.isAssignableFrom(rowTypeClass) && typeParams.length != 2) {
			logger.warn("insufficient type parameters for " + rowTypeClass + " : " + Arrays.asList(typeParams)
				+ "using default: String, String");
			typeParams = new String[]{String.class.getName(),String.class.getName()};
			try {
				setValue(gson
					.fromJson(jsonText, makeType(rowTypeClass, String.class.getName(), String.class.getName())));
				return;
			}
			catch (JsonParseException e) {
				try {
					setValue(gson.fromJson(jsonText,
						makeType(rowTypeClass, String.class.getName(), BeanRef.class.getName())));
					return;
				}
				catch (Exception e1) {
					// simply continue
				}
			}
		}

		if (Collection.class.isAssignableFrom(rowTypeClass) && typeParams.length != 1) {
			logger.warn("insufficient type parameters for " + rowTypeClass + " : " + Arrays.asList(typeParams)
				+ "using default: String");
			typeParams = new String[]{String.class.getName()};
			try {
				setValue(gson.fromJson(jsonText, makeType(rowTypeClass, String.class.getName())));
				return;
			}
			catch (JsonParseException e) {
				try {
					BeanRef br = gson.fromJson(jsonText, makeType(rowTypeClass, BeanRef.class.getName()));
					br.validate();
					setValue(br);
					return;
				}
				catch (Exception e1) {
					// simply continue
				}
			}
		}

		Type tp = new DeserializedParameterizedType(rowTypeClass, convertStrArrayToClassArray(typeParams));

		logger.info("converting " + jsonText + " with " + tp);

		setValue(value);
	}

	private Type makeType(Class<?> rowTypeClass, String... typeParams)
	{
		Type tp = new DeserializedParameterizedType(rowTypeClass, convertStrArrayToClassArray(typeParams));
		return tp;
	}

	private final Class<?> findClass(String clsName)
	{

		try {
			return Class.forName(clsName);
		}
		catch (ClassNotFoundException e) {
			// silence....
		}

		for (String pkg : standardPackagesToScan) {
			try {
				return Class.forName(pkg + "." + clsName);
			}
			catch (ClassNotFoundException e) {
				// silence....
			}
		}

		for (String pkg : additionalPackagesToScan) {
			try {
				return Class.forName(pkg + "." + clsName);
			}
			catch (ClassNotFoundException e) {
				// silence....
			}
		}

		throw new IllegalArgumentException("can't identify the class for " + clsName);
	}

	private final Class<?>[] convertStrArrayToClassArray(String[] strArray)
	{

		Class<?>[] clsArr = new Class<?>[strArray.length];

		int i = 0;
		for (String typeParam : strArray) {
			clsArr[i++] = findClass(typeParam);
		}

		return clsArr;

	}
}