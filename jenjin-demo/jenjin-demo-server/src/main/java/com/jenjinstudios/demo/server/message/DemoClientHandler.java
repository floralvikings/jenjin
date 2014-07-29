package com.jenjinstudios.demo.server.message;

import com.jenjinstudios.core.MessageIO;
import com.jenjinstudios.demo.server.DemoServer;
import com.jenjinstudios.world.server.WorldClientHandler;

import java.io.IOException;

/**
 * @author Caleb Brinkman
 */
public class DemoClientHandler extends WorldClientHandler
{
	public DemoClientHandler(DemoServer<? extends WorldClientHandler> s, MessageIO messageIO) throws IOException {
		super(s, messageIO);
		setPlayer(new DemoPlayer());
	}
}
