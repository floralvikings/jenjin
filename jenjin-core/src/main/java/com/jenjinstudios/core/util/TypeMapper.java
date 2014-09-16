package com.jenjinstudios.core.util;

/**
 * @author Caleb Brinkman
 */
public class TypeMapper
{
	public static Class getTypeForName(String name) {
		Class type;
		try
		{
			type = Class.forName(name);
		} catch (ClassNotFoundException e)
		{
			type = getPrimitiveClass(name);
			if (type == null)
			{
				switch (name)
				{
					case "byte[]":
						type = byte[].class;
						break;
					case "String[]":
						type = String[].class;
						break;
					default:
						type = Object.class;
						break;
				}
			}
		}
		return type;
	}

	private static Class getPrimitiveClass(String name) {
		Class type = null;
		switch (name)
		{
			case "boolean":
				type = Boolean.class;
				break;
			case "byte":
				type = Byte.class;
				break;
			case "short":
				type = Short.class;
				break;
			case "int":
				type = Integer.class;
				break;
			case "long":
				type = Long.class;
				break;
			case "float":
				type = Float.class;
				break;
			case "double":
				type = Double.class;
				break;
			case "String":
				type = String.class;
				break;
		}
		return type;
	}
}
