package com.jenjinstudios.demo.server.net;

import com.jenjinstudios.core.MessageIO;
import com.jenjinstudios.demo.server.DemoPlayer;
import com.jenjinstudios.world.server.WorldClientHandler;

/**
 * @author Caleb Brinkman
 */
public class DemoClientHandler extends WorldClientHandler
{
	public DemoClientHandler(DemoServer<? extends WorldClientHandler> s, MessageIO messageIO) {
		super(s, messageIO);
		setPlayer(new DemoPlayer());
	}
}
