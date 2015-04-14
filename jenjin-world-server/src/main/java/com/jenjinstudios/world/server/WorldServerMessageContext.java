package com.jenjinstudios.world.server;

import com.jenjinstudios.server.net.ServerMessageContext;
import com.jenjinstudios.world.World;

import java.util.Arrays;

/**
 * Used to maintain context for messages executed on a WorldServer connection.
 *
 * @author Caleb Brinkman
 */
public class WorldServerMessageContext extends ServerMessageContext<Player>
{
	private World world;
	private byte[] worldChecksum;
	private byte[] worldBytes;
	private boolean needsToSendSpeedMessage;

	/**
	 * Get the world managed by this context.
	 *
	 * @return The world.
	 */
	public World getWorld() { return world; }

	/**
	 * Set the world managed by this context.
	 *
	 * @param world The world.
	 */
	public void setWorld(World world) { this.world = world; }

	/**
	 * Get the checksum of the file containing the world.
	 *
	 * @return The checksum.
	 */
	public byte[] getWorldChecksum() { return Arrays.copyOf(worldChecksum, worldChecksum.length); }

	/**
	 * Set the checksum of the file containing the world.
	 *
	 * @param worldChecksum The checksum.
	 */
	public void setWorldChecksum(byte... worldChecksum) { this.worldChecksum = worldChecksum; }

	/**
	 * Get the byte array used to make up the world managed by this context.
	 *
	 * @return The bytes that comprise the world.
	 */
	public byte[] getWorldBytes() { return worldBytes; }

	/**
	 * Set the byte array used to make up the world managed by this context.
	 *
	 * @param worldBytes The bytes.
	 */
	public void setWorldBytes(byte[] worldBytes) { this.worldBytes = worldBytes; }

	/**
	 * Get whether the movement speed message needs to be sent.
	 *
	 * @return Whether the movement speed message needs to be sent.
	 */
	public boolean needsToSendSpeedMessage() { return needsToSendSpeedMessage; }

	/**
	 * Set whether the movement speed message needs to be sent.
	 *
	 * @param needs Whether the movememnt speed message needs to be sent.
	 */
	public void setNeedsToSendSpeedMessage(boolean needs) { this.needsToSendSpeedMessage = needs; }
}
