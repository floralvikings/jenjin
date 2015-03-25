package test.jenjinstudios.integration.message;

import com.jenjinstudios.core.Connection;
import com.jenjinstudios.core.ExecutableMessage;
import com.jenjinstudios.core.io.Message;

/**
 * @author Caleb Brinkman
 */
public class ExecutableTest extends ExecutableMessage
{
	/**
	 * Construct a new ExecutableMessage; this should only ever be invoked reflectively, by a {@code Connection}'s
	 * update cycle.
	 *
	 * @param connection The connection for which this ExecutbleMessage will work.
	 * @param message The message that caused this {@code ExecutableMessage} to be created.
	 */
	@SuppressWarnings("UnusedDeclaration")
	public ExecutableTest(Connection connection, Message message) {
		super(connection, message);
	}

	@Override
	public void runDelayed() {

	}

	@Override
	public void runImmediate() {
		getConnection().setName((String) getMessage().getArgument("encryptedString"));
	}
}
