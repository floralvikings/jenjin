package com.jenjinstudios.world.reflection;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method as dynamic so that it can be reflectively executed by a DynamicMethodSelector.
 *
 * @author Caleb Brinkman
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DynamicMethod
{
	/**
	 * The return type of the method marked with this annotation.
	 *
	 * @return The return type of the method marked with this annotation.
	 */
	Class returnType() default Object.class;
}
