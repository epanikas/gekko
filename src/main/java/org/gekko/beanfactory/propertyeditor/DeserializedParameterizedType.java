package org.gekko.beanfactory.propertyeditor;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;

public class DeserializedParameterizedType
	implements ParameterizedType
{

	private Class<?> rowType;
	private Class<?>[] typeParams;

	public DeserializedParameterizedType(Class<?> rowType, Class<?>[] typeParams)
	{
		this.rowType = rowType;
		this.typeParams = typeParams;
	}

	@Override
	public Type[] getActualTypeArguments()
	{
		return typeParams;
	}

	@Override
	public Type getRawType()
	{
		return rowType;
	}

	@Override
	public Type getOwnerType()
	{
		return null;
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("DeserializedParameterizedType [rowType=").append(rowType).append(", typeParams=")
			.append(Arrays.toString(typeParams)).append("]");
		return builder.toString();
	}

}