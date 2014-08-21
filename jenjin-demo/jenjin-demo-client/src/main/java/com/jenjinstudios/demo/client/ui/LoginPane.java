package com.jenjinstudios.demo.client.ui;

import com.jenjinstudios.client.net.ClientUser;
import com.jenjinstudios.demo.client.JenjinDemoApp;
import com.jenjinstudios.world.client.WorldClient;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;

import java.util.logging.Level;
import java.util.logging.Logger;

import static com.jenjinstudios.demo.client.WorldClientFactory.tryCreateWorldClient;

/**
 * @author Caleb Brinkman
 */
public final class LoginPane extends GridPane
{
	private static final Logger LOGGER = Logger.getLogger(LoginPane.class.getName());
	private final TextField addressField = new TextField("127.0.0.1");
	private final TextField portField = new TextField("51015");
	private final TextField usernameField = new TextField("TestAccount1");
	private final Button loginButton = new Button("Login");
	private final PasswordField passwordField = new PasswordField();
	private final JenjinDemoApp jenjinDemoApp;
	private WorldClient worldClient;

	public LoginPane(final JenjinDemoApp jenjinDemoApp) {
		this.jenjinDemoApp = jenjinDemoApp;
		setHgap(10);
		setVgap(10);
		setPadding(new Insets(25, 25, 25, 25));

		createForm();
	}

	private void createForm() {
		addressField.setPromptText("Server Address");
		add(addressField, 1, 0);
		portField.setPromptText("Port Number");
		add(portField, 3, 0);
		usernameField.setPromptText("Username");
		add(usernameField, 1, 1);
		passwordField.setPromptText("Password");
		passwordField.setText("testPassword");
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
		ClientUser clientUser = new ClientUser(usernameField.getText(), passwordField.getText());
		String address = addressField.getText();
		int port = Integer.parseInt(portField.getText());
		worldClient = tryCreateWorldClient(address, port, clientUser);
		if (worldClient != null)
		{
			logIn();
		}
	}

	private void logIn() {
		if (worldClient.getLoginTracker().sendLoginRequestAndWaitForResponse())
		{
			System.out.println("Successfully logged in!");
			jenjinDemoApp.successfulLogin(worldClient);
		} else
		{
			System.out.println("Login unsuccessful");
			worldClient.shutdown();
		}
	}
}
