package com.jenjinstudios.world.io;

import java.io.IOException;

/**
 * @author Caleb Brinkman
 */
public class WorldDocumentException extends IOException
{
	public WorldDocumentException(String message, Throwable cause) { super(message, cause); }

	public WorldDocumentException(String s) { super(s); }
}
