package com.jenjinstudios.demo.client;

import com.jenjinstudios.client.net.ClientUser;
import com.jenjinstudios.core.MessageIO;
import com.jenjinstudios.world.client.WorldClient;
import com.jenjinstudios.world.io.WorldDocumentException;

import java.io.File;

/**
 * @author Caleb Brinkman
 */
public class DemoWorldClient extends WorldClient
{
	private Score highScore;

	public DemoWorldClient(MessageIO messageIO, ClientUser clientUser, File worldFile) throws WorldDocumentException {
		super(messageIO, clientUser, worldFile);
		this.highScore = new Score("None", 0, 0);
	}

	public Score getHighScore() { return highScore; }

	public void setHighScore(Score highScore) { this.highScore = highScore; }
}
