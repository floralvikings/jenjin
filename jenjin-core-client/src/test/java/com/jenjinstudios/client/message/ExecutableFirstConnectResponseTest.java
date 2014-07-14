package com.jenjinstudios.client.message;

import com.jenjinstudios.client.net.Client;
import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageRegistry;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * @author Caleb Brinkman
 */
public class ExecutableFirstConnectResponseTest
{
	@Test
	public void testMessageExecution() {
		Message firstConnectResponse = MessageRegistry.getInstance().createMessage("FirstConnectResponse");
		firstConnectResponse.setArgument("ups", 10);

		Client client = mock(Client.class);

		ExecutableFirstConnectResponse executableFirstConnectResponse =
			  new ExecutableFirstConnectResponse(client, firstConnectResponse);
		executableFirstConnectResponse.runImmediate();
		executableFirstConnectResponse.runDelayed();

		verify(client).doPostConnectInit(firstConnectResponse);
	}
}
