package com.jenjinstudios.core.io;

import com.jenjinstudios.core.xml.ArgumentType;
import com.jenjinstudios.core.xml.MessageType;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.LinkedList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests the {@code Message class}.
 *
 * @author Caleb Brinkman
 */
@SuppressWarnings("MagicNumber")
public class MessageTest
{
	/**
	 * Test a successful invocation of the Message varargs constructor.
	 */
	@Test
	public void testVarargsConstructor() {
		MessageType messageType = createMessageTypeMock();
		Message message = new Message(messageType, "123", 456, 789L);
		Assert.assertEquals(message.getArgs()[0], "123", "Arguments should be equal.");
	}

	/**
	 * Test the varargs constructor with an invalid number of arguments.
	 */
	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testVaragsConstructorInvalidCount() {
		MessageType messageType = createMessageTypeMock();
		//noinspection ResultOfObjectAllocationIgnored
		new Message(messageType, "123", 456);
	}

	/**
	 * Test the varargs constructor with an invalid argument type.
	 */
	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testVarargsConstructorInvalidType() {
		MessageType messageType = createMessageTypeMock();
		//noinspection ResultOfObjectAllocationIgnored
		new Message(messageType, "123", 456, "Should be a long");
	}

	/**
	 * Test setting an argument with an invalid name.
	 */
	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testSetInvalidArgument() {
		MessageType messageType = createMessageTypeMock();
		Message message = new Message(messageType);
		message.setArgument("NonExistent Argument Name", "foobar");
	}

	/**
	 * Test the getArgument method.
	 */
	@Test
	public void testGetArgument() {
		MessageType messageType = createMessageTypeMock();
		Message message = new Message(messageType, "123", 456, 789L);
		Assert.assertEquals(message.getArgument("First"), "123", "Arguments should be equal.");
	}

	/**
	 * Test the getID method.
	 */
	@Test
	public void testGetId() {
		MessageType messageType = createMessageTypeMock();
		Message message = new Message(messageType);
		Assert.assertEquals(message.getID(), 1234, "Message ID should be equal to mocked id.");
	}

	/**
	 * Test the getArgs method while arguments are not all assigned.
	 */
	@Test(expectedExceptions = IllegalStateException.class)
	public void testGetArgsWhileInvalid() {
		MessageType messageType = createMessageTypeMock();
		Message message = new Message(messageType);
		message.getArgs();
	}

	/**
	 * Test the toString method.
	 */
	@Test
	public void testToString() {
		MessageType messageType = createMessageTypeMock();
		Message message = new Message(messageType);
		Assert.assertEquals(message.toString(), "Message 1234 MockType", "toString incorrect");
	}

	private static MessageType createMessageTypeMock() {
		ArgumentType firstArgType = mock(ArgumentType.class);
		when(firstArgType.getName()).thenReturn("First");
		when(firstArgType.getType()).thenReturn("String");

		ArgumentType secondArgType = mock(ArgumentType.class);
		when(secondArgType.getName()).thenReturn("Second");
		when(secondArgType.getType()).thenReturn("int");

		ArgumentType thirdArgType = mock(ArgumentType.class);
		when(thirdArgType.getName()).thenReturn("Third");
		when(thirdArgType.getType()).thenReturn("long");

		List<ArgumentType> args = new LinkedList<>();
		args.add(firstArgType);
		args.add(secondArgType);
		args.add(thirdArgType);

		MessageType messageType = mock(MessageType.class);
		when(messageType.getArguments()).thenReturn(args);
		when(messageType.getId()).thenReturn((short) 1234);
		when(messageType.getName()).thenReturn("MockType");
		return messageType;
	}
}
