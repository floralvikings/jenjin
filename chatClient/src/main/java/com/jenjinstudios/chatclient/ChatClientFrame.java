package com.jenjinstudios.chatclient;

import com.jenjinstudios.message.BaseMessage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.LinkedList;

/**
 * Displays messages received from the ChatServer, and allows users to send messages of their own.
 *
 * @author Caleb Brinkman
 */
public class ChatClientFrame extends JFrame implements WindowListener, ActionListener
{
	/** The port used to connect to the server. */
	private static int port = 51019;
	/** The address of the server. */
	private static String address = "localhost";
	/** The client with which this frame communicates with the server. */
	private ChatClient client;
	/** The panel used to display broadcasts from the server. */
	private MessagePanel messagePanel;
	/** The panel used to send messages to the server. */
	private MessageInputPanel messageInputPanel;
	/** The username used by the client frame. */
	private String username = "Anonymous";


	/** Construct a new ChatClientFrame with the specified client. */
	public ChatClientFrame()
	{
		super("Chat Client");

		username = JOptionPane.showInputDialog(this, "Please enter a username:", "Username", JOptionPane.PLAIN_MESSAGE);

		client = new ChatClient(address, port, username);
		client.blockingStart();

		addWindowListener(this);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		initGui();
	}

	/**
	 * Start a Chat Client and attempt to connect to a ChatServer.
	 *
	 * @param args Command line arguments.  First argument specifies the server addres.  Second
	 *             argument specifies port number.
	 */
	public static void main(String[] args)
	{
		if (args.length > 0)
			address = args[0];
		if (args.length > 1)
			port = Integer.parseInt(args[1]);

		ChatClientFrame frame = new ChatClientFrame();
		frame.setVisible(true);

		while (frame.isVisible())
		{
			LinkedList<BaseMessage> chatMessages = frame.client.getChatMessages();
			while (!chatMessages.isEmpty())
			{
				BaseMessage baseMessage = chatMessages.pop();
				String message = baseMessage.getArgs()[0] + ": " + baseMessage.getArgs()[1];
				frame.messagePanel.getMessageArea().append(message + "\n");
			}
		}
	}

	/** Initialize the GUI components. */
	private void initGui()
	{
		setLayout(new GridBagLayout());
		// setSize(800, 600);

		messagePanel = new MessagePanel();
		GridBagConstraints messagePanelConstraints = new GridBagConstraints();
		messagePanelConstraints.fill = GridBagConstraints.BOTH;
		messagePanelConstraints.gridx = 0;
		messagePanelConstraints.gridy = 0;
		messagePanelConstraints.anchor = GridBagConstraints.WEST;
		messagePanelConstraints.insets = new Insets(5, 5, 5, 5);
		add(messagePanel, messagePanelConstraints);

		messageInputPanel = new MessageInputPanel();
		messageInputPanel.getSendButton().addActionListener(this);
		GridBagConstraints messageInputPanelConstraints = new GridBagConstraints();
		messageInputPanelConstraints.gridx = 0;
		messageInputPanelConstraints.gridy = 1;
		messageInputPanelConstraints.anchor = GridBagConstraints.WEST;
		messageInputPanelConstraints.insets = new Insets(5, 5, 5, 5);
		add(messageInputPanel, messageInputPanelConstraints);

		pack();
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == messageInputPanel.getSendButton())
		{
			String messageText = messageInputPanel.getMessageField().getText();
			client.sendChatMessage(new BaseMessage(ChatClient.CHAT_MESSAGE_ID, messageText, 0));
			messageInputPanel.getMessageField().setText("");
		}
	}

	@Override
	public void windowClosing(WindowEvent e)
	{
		client.shutdown();
	}

	@Override
	public void windowOpened(WindowEvent e)
	{
	}

	@Override
	public void windowClosed(WindowEvent e)
	{
	}

	@Override
	public void windowIconified(WindowEvent e)
	{
	}

	@Override
	public void windowDeiconified(WindowEvent e)
	{
	}

	@Override
	public void windowActivated(WindowEvent e)
	{
	}

	@Override
	public void windowDeactivated(WindowEvent e)
	{
	}
}
