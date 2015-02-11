package com.jenjinstudios.core.util;

/**
 * This class is used to map the string names of primitives to the appropriate class value.
 *
 * @author Caleb Brinkman
 */
public class TypeMapper
{
    /**
     * Get the Class that represents the primitive type of the given name.
     *
     * @param name The name of the type.  One of: <ul> <li>byte</li> <li>short</li> <li>char</li> <li>int</li>
     * <li>float</li> <li>long</li> <li>double</li> <li>String</li> <li>String[]</li> <li>byte[]</li> </ul>
     *
     * @return The Class represented by {@code name}.
     */
    public static Class getTypeForName(String name) {
        Class type = getPrimitiveClass(name);
        if (type == null)
        {
            try
            {
                type = Class.forName(name);
            } catch (ClassNotFoundException e)
            {
                type = Object.class;
            }
        }
        return type;
    }

    private static Class getPrimitiveClass(String name) {
        Class type;
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
            default:
                type = getArrayType(name);
                break;
        }
        return type;
    }

    private static Class getArrayType(String name) {
        Class type;
        switch (name)
        {
            case "byte[]":
                type = byte[].class;
                break;
            case "String[]":
                type = String[].class;
                break;
            default:
                type = null;
                break;
        }
        return type;
    }
}
