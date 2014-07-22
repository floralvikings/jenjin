package com.jenjinstudios.demo.client.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.demo.client.DemoWorldClient;
import com.jenjinstudios.demo.client.Score;
import com.jenjinstudios.world.client.WorldClient;
import com.jenjinstudios.world.client.message.WorldClientExecutableMessage;

/**
 * @author Caleb Brinkman
 */
public class ExecutableHighScoreResponse extends WorldClientExecutableMessage
{
	private Score score;

	/**
	 * Construct an ExecutableMessage with the given Message.
	 * @param client The client invoking this message.
	 * @param message The Message.
	 */
	public ExecutableHighScoreResponse(WorldClient client, Message message) { super(client, message); }

	/** Run the synced portion of this message. */
	@Override
	public void runDelayed() {
		((DemoWorldClient) getClient()).setHighScore(score);
	}

	/** Run asynchronous portion of this message. */
	@Override
	public void runImmediate() {
		String name = (String) getMessage().getArgument("name");
		int kills = (int) getMessage().getArgument("kills");
		int deaths = (int) getMessage().getArgument("deaths");
		double ratio = (double) getMessage().getArgument("ratio");
		score = new Score(name, kills, deaths, ratio);
	}
}
