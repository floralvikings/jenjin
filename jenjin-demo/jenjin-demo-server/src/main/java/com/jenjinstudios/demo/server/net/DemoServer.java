package com.jenjinstudios.demo.server.net;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageRegistry;
import com.jenjinstudios.demo.server.DemoPlayer;
import com.jenjinstudios.server.net.ServerInit;
import com.jenjinstudios.world.io.WorldDocumentException;
import com.jenjinstudios.world.io.WorldDocumentReader;
import com.jenjinstudios.world.server.WorldClientHandler;
import com.jenjinstudios.world.server.WorldServer;
import com.jenjinstudios.world.server.sql.WorldAuthenticator;

import java.io.IOException;

/**
 * @author Caleb Brinkman
 */
public class DemoServer<T extends WorldClientHandler> extends WorldServer<T>
{
	private String highestId = "";
	private double highRatio = 0;
	private int highKills = 0;
	private int highDeaths = 0;

	public DemoServer(ServerInit<T> init, WorldAuthenticator authenticator, WorldDocumentReader reader)
		  throws IOException, WorldDocumentException, NoSuchMethodException
	{
		super(init, authenticator, reader);

		addRepeatedTask(this::updateHighScore);
	}

	private void updateHighScore() {
		for (T handler : getClientHandlers().values())
		{
			DemoPlayer demoPlayer = (DemoPlayer) handler.getPlayer();
			double oldHighRatio = highRatio;
			if (demoPlayer.getKillDeathRatio() > highRatio)
			{
				highRatio = demoPlayer.getKillDeathRatio();
				highDeaths = demoPlayer.getDeaths();
				highKills = demoPlayer.getKills();
				highestId = demoPlayer.getName();
			}
			if (oldHighRatio != highRatio)
			{
				System.out.println("Before");
				sendHighScoreUpdates();
				System.out.println("After");
			}
		}
	}

	private void sendHighScoreUpdates() {
		Message message = MessageRegistry.getInstance().createMessage("HighScoreResponse");
		message.setArgument("name", highestId);
		message.setArgument("kills", highKills);
		message.setArgument("deaths", highDeaths);
		message.setArgument("ratio", highRatio);
		for (T handler : getClientHandlers().values())
		{
			handler.queueOutgoingMessage(message);
		}
	}
}
