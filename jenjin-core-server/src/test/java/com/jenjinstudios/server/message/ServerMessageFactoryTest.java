package com.jenjinstudios.server.message;

import com.jenjinstudios.core.io.Message;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * @author Caleb Brinkman
 */
public class ServerMessageFactoryTest
{

	@Test
	public void testGenerateLogoutResponse() {
		Message message = ServerMessageFactory.generateLogoutResponse(true);

		assertEquals(message.name, "LogoutResponse");
		assertEquals(message.getArgument("success"), true);
	}

}
