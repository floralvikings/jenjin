package com.jenjinstudios.world.reflection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * Used to dynamically (through reflection) choose a method based on the class of it parameter(s).
 *
 * @author Caleb Brinkman
 */
public class DynamicMethodSelector
{
	private final Class targetClass;
	private final Object object;

	/**
	 * Construct a new DynamicMethodSelector that will choose methods from the provided class.
	 *
	 * @param object The object from which to choose methods.
	 */
	public DynamicMethodSelector(Object object) {
		this.object = object;
		this.targetClass = object.getClass();
	}

	/**
	 * Invoke the most specific method specified with the method name and parameters.
	 *
	 * @param methodName The name of the method
	 * @param params The parameters
	 *
	 * @return The return value of the method invoked.
	 *
	 * @throws InvocationTargetException If there is an exception when invoking (or thrown by) the method.
	 */
	public Object invokeMostSpecificMethod(String methodName, Object... params) throws InvocationTargetException {
		Method method = findMostSpecificMethod(methodName, params);
		Object returnValue = null;

		try {
			returnValue = method.invoke(object, params);
		} catch (IllegalAccessException ignored) {
			// We have ensured the method is accessible in findMostSpecificMethod
		}

		return returnValue;
	}

	/**
	 * Find the method with the given name, that has parameters most closely matching those given.
	 *
	 * @param methodName The name of the method to find.
	 * @param parameters The parameters that might be passed into this method; the method returned will have parameters
	 * as close to these types in inheritance as possible.
	 *
	 * @return the method with parameters as close to these types in inheritance as possible.  If no method that could
	 * be invoked with these parameters exists, returns {@code null}.
	 */
	public Method findMostSpecificMethod(String methodName, Object... parameters) {
		List<Method> candidateMethods = getCandidateMethods(methodName, parameters);

		Comparator<Method> parameterSpecificity = (o1, o2) -> {
			int r = 0;
			Class<?>[] methodOneParams = o1.getParameterTypes();
			Class<?>[] methodTwoParams = o2.getParameterTypes();
			for (int i = 0; i < methodOneParams.length; i++) {
				if (methodOneParams[i].isAssignableFrom(methodTwoParams[i])) {
					r++;
				}
				if (methodTwoParams[i].isAssignableFrom(methodOneParams[i])) {
					r--;
				}
			}
			return r;
		};

		Collections.sort(candidateMethods, parameterSpecificity);

		return !candidateMethods.isEmpty() ? candidateMethods.get(0) : null;
	}

	private List<Method> getCandidateMethods(String methodName, Object... parameters) {
		Method[] methods = targetClass.getMethods();
		List<Method> temp = new LinkedList<>();
		for (Method method : methods) {
			if (method.getName().equals(methodName) && Modifier.isPublic(method.getModifiers())) {
				if (method.getParameterCount() == parameters.length) {
					boolean match = true;
					for (int i = 0; i < parameters.length; i++) {
						if (!method.getParameterTypes()[i].isInstance(parameters[i])) {
							match = false;
						}
					}
					if (match) {
						temp.add(method);
					}
				}
			}
		}
		return temp;
	}
}
