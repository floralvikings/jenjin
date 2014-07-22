package com.jenjinstudios.demo.server.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageRegistry;
import com.jenjinstudios.world.server.WorldClientHandler;
import com.jenjinstudios.world.server.message.WorldExecutableMessage;

import java.util.Map;

/**
 * @author Caleb Brinkman
 */
public class ExecutableHighScoreRequest extends WorldExecutableMessage
{
	/**
	 * Construct a new ExecutableMessage.  Must be implemented by subclasses.
	 * @param handler The handler using this ExecutableMessage.
	 * @param message The message.
	 */
	public ExecutableHighScoreRequest(WorldClientHandler handler, Message message) { super(handler, message); }

	/** Run the synced portion of this message. */
	@Override
	public void runDelayed() {
		Map clientHandlers = getClientHandler().getServer().getClientHandlers();
		double highestRatio = Double.MIN_VALUE;
		int highestKills = Integer.MIN_VALUE;
		int highestDeaths = Integer.MIN_VALUE;
		String highestId = "";
		for (Object o : clientHandlers.values())
		{
			if (o == null) continue;
			Object killsProp = ((WorldClientHandler) o).getPlayer().getProperty("kills");
			Object deathsProp = ((WorldClientHandler) o).getPlayer().getProperty("deaths");
			int kills = killsProp != null ? (int) killsProp : 0;
			int deaths = deathsProp != null ? (int) deathsProp : 0;
			double ratio = deaths != 0 ? (double) kills / (double) deaths : kills;
			if (ratio > highestRatio)
			{
				highestRatio = ratio;
				highestKills = kills;
				highestDeaths = deaths;
				highestId = ((WorldClientHandler) o).getPlayer().getName();
			}
		}
		Message response = MessageRegistry.getInstance().createMessage("HighScoreResponse");
		response.setArgument("name", highestId);
		response.setArgument("kills", highestKills);
		response.setArgument("deaths", highestDeaths);
		response.setArgument("ratio", highestRatio);
		getClientHandler().queueOutgoingMessage(response);
	}

	/** Run asynchronous portion of this message. */
	@Override
	public void runImmediate() {

	}
}
