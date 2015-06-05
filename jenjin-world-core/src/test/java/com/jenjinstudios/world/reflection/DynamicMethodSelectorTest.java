package com.jenjinstudios.world.reflection;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Test the DynamicMethodSelector class.
 *
 * @author Caleb Brinkman
 */
public class DynamicMethodSelectorTest
{
	/**
	 * Test the findMostSpecificMethod method.
	 */
	@Test
	public void testFindMostSpecificMethod() {
		DynamicMethodSelector methodSelector = new DynamicMethodSelector(new TestClass());
		List list = new ArrayList(1);
		List list2 = new ArrayList(1);
		Method doSomething = methodSelector.findMostSpecificMethod("doSomething", list, list2);

		Assert.assertNotNull(doSomething, "Method should not be null");
		Assert.assertEquals(doSomething.getParameterTypes()[0], ArrayList.class, "Parameter Types Should be Equal");
		Assert.assertEquals(doSomething.getParameterTypes()[1], List.class, "Parameter Types Should be Equal");
	}

	/**
	 * Test the invokeMostSpecificMethod method.
	 *
	 * @throws DynamicInvocationException If there is an invocation exception caused by a reflective method call.
	 */
	@Test
	public void testInvokeMostSpecificMethod() throws DynamicInvocationException {
		TestClass testClass = new TestClass();
		DynamicMethodSelector methodSelector = new DynamicMethodSelector(testClass);
		List list = new ArrayList(1);
		List list2 = new ArrayList(1);
		Object doSomething = methodSelector.invokeMostSpecificMethod("doSomething", list, list2);

		Assert.assertEquals(doSomething, true, "Return value should be true");

	}

	@SuppressWarnings("all")
	public static class TestClass
	{
		@DynamicMethod
		public void doSomething(Object obj, Object o) { }

		@DynamicMethod
		public void doSomething(Collection collection, Collection collection1) { }

		@DynamicMethod
		public void doSomething(List list, Collection collection) { }

		@DynamicMethod
		public void doSomething(LinkedList linkedList, Collection collection) { }

		@DynamicMethod
		public void doSomething(ArrayList arrayList, Object o) { }

		@DynamicMethod
		public void doSomething(ArrayList arrayList, Collection collection) { }

		@DynamicMethod
		public boolean doSomething(ArrayList arrayList, List list) { return true; }

		private void doSomething(ArrayList arrayList, ArrayList list) { }
	}
}
