package com.jenjinstudios.chatclient;

import javax.swing.*;
import java.awt.*;

/**
 * Allows users to send messages to the server.
 *
 * @author Caleb Brinkman
 */
public class MessageInputPanel extends JPanel
{
	/** The text field used to get user messages. */
	private JTextField messageField;
	/** The button used to send messages. */
	private JButton sendButton;

	/** Create the message input panel. */
	public MessageInputPanel()
	{
		super();
		initGui();
	}

	/** Initialize the GUI components. */
	private void initGui()
	{
		setLayout(new GridBagLayout());

		messageField = new JTextField(50);
		GridBagConstraints messageFieldConstraints = new GridBagConstraints();
		messageFieldConstraints.gridx = 0;
		messageFieldConstraints.gridy = 0;
		messageFieldConstraints.fill = GridBagConstraints.HORIZONTAL;
		add(messageField, messageFieldConstraints);

		sendButton = new JButton("Send");
		GridBagConstraints sendButtonConstraints = new GridBagConstraints();
		sendButtonConstraints.gridx = 1;
		sendButtonConstraints.gridy = 0;
		add(sendButton, sendButtonConstraints);
	}

	/**
	 * Get the button used to send messages.
	 *
	 * @return The button used to send messages.
	 */
	public JButton getSendButton()
	{
		return sendButton;
	}

	/**
	 * The text field used to get user messages.
	 *
	 * @return The field used to retrieve messages from the user.
	 */
	public JTextField getMessageField()
	{
		return messageField;
	}
}
