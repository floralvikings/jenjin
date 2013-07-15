package com.jenjinstudios.chatclient;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;

/**
 * Displays messages broadcast from the chat server.
 *
 * @author Caleb Brinkman
 */
public class MessagePanel extends JPanel
{
	/** The TextArea where messages are displayed. */
	private JTextArea messageArea;

	/** Construct a new MessagePanel. */
	public MessagePanel()
	{
		super();
		initGui();
	}

	/** Initialize the GUI components. */
	private void initGui()
	{
		setLayout(new GridBagLayout());

		messageArea = new JTextArea(30, 70);
		messageArea.setBackground(Color.WHITE);
		messageArea.setBorder(new BevelBorder(BevelBorder.LOWERED, Color.DARK_GRAY, Color.BLACK));
		messageArea.setEditable(false);
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.fill = GridBagConstraints.BOTH;
		add(messageArea, constraints);

		repaint();
	}

	/**
	 * Get the JTextArea where messages are displayed.
	 *
	 * @return The JTextArea where messages are displayed.
	 */
	public JTextArea getMessageArea()
	{
		return messageArea;
	}
}
