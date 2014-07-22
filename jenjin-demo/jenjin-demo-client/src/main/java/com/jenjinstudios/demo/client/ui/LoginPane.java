package com.jenjinstudios.demo.client.ui;

import com.jenjinstudios.client.net.ClientUser;
import com.jenjinstudios.demo.client.DemoWorldClient;
import com.jenjinstudios.demo.client.JenjinDemoApp;
import com.jenjinstudios.world.io.WorldDocumentException;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;

import static com.jenjinstudios.demo.client.WorldClientInitUtils.tryCreateWorldClient;
import static com.jenjinstudios.demo.client.WorldClientInitUtils.tryRequestWorldFile;

/**
 * @author Caleb Brinkman
 */
public final class LoginPane extends GridPane
{
	private final TextField addressField = new TextField("127.0.0.1");
	private final TextField portField = new TextField("51015");
	private final TextField usernameField = new TextField();
	private final PasswordField passwordField = new PasswordField();

	public LoginPane(final JenjinDemoApp jenjinDemoApp) {
		setHgap(10);
		setVgap(10);
		setPadding(new Insets(25, 25, 25, 25));

		createForm(jenjinDemoApp);
	}

	private void createForm(JenjinDemoApp jenjinDemoApp) {
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

		setLoginActionEvent(jenjinDemoApp, loginButton);

		setEnterKeyActionEvent(loginButton);
	}

	private void setEnterKeyActionEvent(Button loginButton) {
		setOnKeyPressed(event -> {
			if (event.getCode().equals(KeyCode.ENTER))
			{
				loginButton.fire();
			}
		});
	}

	private void setLoginActionEvent(JenjinDemoApp jenjinDemoApp, Button loginButton) {
		loginButton.setOnAction(event -> {
			ClientUser clientUser = new ClientUser(usernameField.getText(), passwordField.getText());
			String address = addressField.getText();
			int port = Integer.parseInt(portField.getText());
			DemoWorldClient worldClient = tryCreateWorldClient(address, port, clientUser);
			if (worldClient != null)
			{
				worldClient.start();
				if (tryRequestWorldFile(worldClient))
				{
					try
					{
						worldClient.readWorldFile();
					} catch (WorldDocumentException e)
					{
						e.printStackTrace();
					}
					if (worldClient.getLoginTracker().sendLoginRequestAndWaitForResponse(30000))
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
		});
	}


}
