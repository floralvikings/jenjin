package com.jenjinstudios.world.reflection;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method in a NodeTask subclass as a task, so that it will be reflectively executed when called with a
 * parameter of the correct subclass.
 *
 * @author Caleb Brinkman
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DynamicMethod {}
