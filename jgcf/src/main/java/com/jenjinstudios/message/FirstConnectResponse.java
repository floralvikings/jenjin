package com.jenjinstudios.message;

import com.jenjinstudios.io.BaseMessage;

/**
 * Response sent from server verifying a successful connection.
 *
 * @author Caleb Brinkman
 */
public class FirstConnectResponse extends BaseMessage
{
	/** The message registration id for this message type. */
	public static final short ID = 10;
	/** The UPS of the server sending this response. */
	public final int UPS;

	/**
	 * Construct a new FirstConnectResponse.
	 *
	 * @param ups The UPS of the server sending this response.
	 */
	public FirstConnectResponse(Integer ups)
	{
		super(ID, ups);
		UPS = ups;
	}
}
