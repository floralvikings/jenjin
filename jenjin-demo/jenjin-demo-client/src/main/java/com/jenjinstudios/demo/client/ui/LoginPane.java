package com.jenjinstudios.demo.client.ui;

import com.jenjinstudios.client.authentication.AuthenticationHelper;
import com.jenjinstudios.client.authentication.ClientUser;
import com.jenjinstudios.client.authentication.User;
import com.jenjinstudios.core.io.MessageInputStream;
import com.jenjinstudios.core.io.MessageOutputStream;
import com.jenjinstudios.core.io.MessageStreamPair;
import com.jenjinstudios.demo.client.Main;
import com.jenjinstudios.world.client.WorldClient;
import com.jenjinstudios.world.client.WorldClientMessageContext;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Caleb Brinkman
 */
public final class LoginPane extends GridPane
{
	private static final Logger LOGGER = Logger.getLogger(LoginPane.class.getName());
	private static final int PANE_PADDING = 25;
	private final TextField addressField = new TextField("127.0.0.1");
	private final TextField portField = new TextField("51015");
	private final TextField usernameField = new TextField("TestAccount1");
	private final Button loginButton = new Button("Login");
	private final PasswordField passwordField = new PasswordField();
	private final Main main;

	public LoginPane(final Main main) {
		this.main = main;
		setHgap(10);
		setVgap(10);
		setPadding(new Insets(PANE_PADDING, PANE_PADDING, PANE_PADDING, PANE_PADDING));

		createForm();
	}

	public static WorldClient tryCreateWorldClient(String address, int port, User user) {
		WorldClient worldClient;
		try
		{
			worldClient = createWorldClient(address, port, user);
			worldClient.start();
			worldClient.initializeWorldFromServer();
			LOGGER.log(Level.INFO, "Created World Client.");
		} catch (IOException e)
		{
			LOGGER.log(Level.SEVERE, "Exception creating world client.", e);
			worldClient = null;
		}
		return worldClient;
	}

	private static WorldClient createWorldClient(String address, int port, User user) throws IOException {
		String slash = File.separator;
		File worldFile = new File(System.getProperty("user.home") + slash + ".jenjin-demo" + slash + "World.json");
		Socket socket = new Socket(address, port);
		MessageInputStream messageInputStream = new MessageInputStream(socket.getInputStream());
		MessageOutputStream messageOutputStream = new MessageOutputStream(socket.getOutputStream());
		MessageStreamPair messageStreamPair = new MessageStreamPair(messageInputStream, messageOutputStream);
		WorldClientMessageContext context = new WorldClientMessageContext();
		context.setUser(user);
		return new WorldClient<>(messageStreamPair, worldFile, context);
	}

	private void createForm() {
		addressField.setPromptText("Server Address");
		portField.setPromptText("Port Number");
		usernameField.setPromptText("Username");
		passwordField.setPromptText("Password");
		passwordField.setText("testPassword");
		add(addressField, 1, 0);
		add(portField, 3, 0);
		add(usernameField, 1, 1);
		add(passwordField, 3, 1);
		add(loginButton, 3, 2);

		loginButton.setOnAction(this::loginButtonFired);
		setOnKeyPressed(this::keyPressed);
	}

	private void keyPressed(KeyEvent event) {
		if (event.getCode().equals(KeyCode.ENTER))
		{
			loginButton.fire();
		}
	}

	private void loginButtonFired(ActionEvent event) {
		LOGGER.log(Level.FINE, "Login Button Fired: {0}", event);
		User user = new ClientUser();
		user.setUsername(usernameField.getText());
		user.setPassword(passwordField.getText());
		String address = addressField.getText();
		int port = Integer.parseInt(portField.getText());
		WorldClient worldClient = tryCreateWorldClient(address, port, user);
		if (worldClient != null)
		{
			if (AuthenticationHelper.loginAndWait(worldClient))
			{
				LOGGER.log(Level.INFO, "Successfully logged in!");
				main.successfulLogin(worldClient);
			} else
			{
				LOGGER.log(Level.WARNING, "Login unsuccessful");
				worldClient.shutdown();
			}
		}
	}

}
