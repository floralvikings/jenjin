package com.jenjinstudios.server.net;

import com.jenjinstudios.server.authentication.Authenticator;

import java.io.IOException;

public class TaskedServer extends Server
{

	public TaskedServer(ServerInit initInfo, Authenticator authenticator) throws IOException,
		  NoSuchMethodException {
		super(initInfo, authenticator);
	}

}
