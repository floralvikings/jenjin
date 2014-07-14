package com.jenjinstudios.demo.client.ui;

import com.jenjinstudios.client.net.ClientUser;
import com.jenjinstudios.demo.client.JenjinDemoClient;
import com.jenjinstudios.world.client.WorldClient;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import static com.jenjinstudios.demo.client.WorldClientInitUtils.*;

/**
 * @author Caleb Brinkman
 */
public final class LoginPane extends GridPane implements EventHandler<ActionEvent>
{
	private final TextField addressField = new TextField("127.0.0.1");
	private final TextField portField = new TextField("51015");
	private final TextField usernameField = new TextField();
	private final PasswordField passwordField = new PasswordField();
	private final JenjinDemoClient jenjinDemoClient;

	public LoginPane(final JenjinDemoClient jenjinDemoClient) {
		this.jenjinDemoClient = jenjinDemoClient;
		setHgap(10);
		setVgap(10);
		setPadding(new Insets(25, 25, 25, 25));

		Label addressLabel = new Label("Address");
		add(addressLabel, 0, 0);
		add(addressField, 1, 0);
		Label portLabel = new Label("Port");
		add(portLabel, 2, 0);
		add(portField, 3, 0);
		final Label usernameLabel = new Label("Username");
		add(usernameLabel, 0, 1);
		add(usernameField, 1, 1);
		Label passwordLabel = new Label("Password");
		add(passwordLabel, 2, 1);
		add(passwordField, 3, 1);
		Button loginButton = new Button("Login");
		add(loginButton, 3, 2);

		loginButton.setOnAction(this);
	}

	@Override
	public void handle(ActionEvent event) {
		ClientUser clientUser = new ClientUser(usernameField.getText(), passwordField.getText());
		String address = addressField.getText();
		int port = Integer.parseInt(portField.getText());
		WorldClient worldClient = tryCreateWorldClient(address, port, clientUser);
		if (worldClient != null)
		{
			worldClient.start();
			if (tryRequestWorldFile(worldClient))
			{
				if (tryLogin(worldClient))
				{
					if (worldClient.isLoggedIn())
					{
						System.out.println("Successfully logged in!");
						jenjinDemoClient.successfulLogin(worldClient);
					} else
					{
						System.out.println("Login unsuccessful");
						worldClient.shutdown();
					}
				}
			}
		}
	}

}
