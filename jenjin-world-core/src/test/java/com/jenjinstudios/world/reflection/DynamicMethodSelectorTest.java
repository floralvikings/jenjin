package com.jenjinstudios.world.reflection;

import org.mockito.ArgumentCaptor;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

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
	 * @throws InvocationTargetException If there is an invocation exception caused by a reflective method call.
	 */
	@Test
	public void testInvokeMostSpecificMethod() throws InvocationTargetException {
		TestClass testClass = mock(TestClass.class);
		DynamicMethodSelector methodSelector = new DynamicMethodSelector(testClass);
		List list = new ArrayList(1);
		List list2 = new ArrayList(1);
		Object doSomething = methodSelector.invokeMostSpecificMethod("doSomething", list, list2);

		Assert.assertNull(doSomething, "Return value should be null");

		ArgumentCaptor<ArrayList> alArgumentCaptor = ArgumentCaptor.forClass(ArrayList.class);
		ArgumentCaptor<List> listArgumentCaptor = ArgumentCaptor.forClass(List.class);
		verify(testClass).doSomething(alArgumentCaptor.capture(), listArgumentCaptor.capture());
	}

	private static class TestClass
	{
		public void doSomething(Object obj, Object o) { }

		public void doSomething(Collection collection, Collection collection1) { }

		public void doSomething(List list, Collection collection) { }

		public void doSomething(LinkedList linkedList, Collection collection) { }

		public void doSomething(ArrayList arrayList, Object o) { }

		public void doSomething(ArrayList arrayList, Collection collection) { }

		public void doSomething(ArrayList arrayList, List list) { }

		private void doSomething(ArrayList arrayList, ArrayList list) { }
	}
}
