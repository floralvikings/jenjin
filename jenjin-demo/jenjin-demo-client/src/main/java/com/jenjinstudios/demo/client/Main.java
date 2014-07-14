package com.jenjinstudios.demo.client;

import com.jenjinstudios.world.client.WorldClient;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import static com.jenjinstudios.demo.client.WorldClientInitUtils.*;

/**
 * @author Caleb Brinkman
 */
public class Main extends Application
{
	private WorldClient worldClient;

	public static void main(String[] args) throws Exception {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		GridPane grid = createLoginForm(primaryStage);
		primaryStage.setScene(new Scene(grid, 800, 600));
		primaryStage.show();
	}

	private GridPane createLoginForm(Stage primaryStage) {
		primaryStage.setTitle("Jenjin Demo Client");
		Button loginButton = new Button("Login");
		Label addressLabel = new Label("Address");
		Label portLabel = new Label("Port");
		Label usernameLabel = new Label("Username");
		Label passwordLabel = new Label("Password");
		final TextField addressField = new TextField("127.0.0.1");
		final TextField portField = new TextField("51015");
		final TextField usernameField = new TextField();
		final PasswordField passwordField = new PasswordField();
		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));

		grid.add(addressLabel, 0, 0);
		grid.add(addressField, 1, 0);
		grid.add(portLabel, 2, 0);
		grid.add(portField, 3, 0);
		grid.add(usernameLabel, 0, 1);
		grid.add(usernameField, 1, 1);
		grid.add(passwordLabel, 2, 1);
		grid.add(passwordField, 3, 1);
		grid.add(loginButton, 3, 2);
		loginButton.setOnAction(new EventHandler<ActionEvent>()
		{

			@Override
			public void handle(ActionEvent event) {
				worldClient = tryCreateWorldClient(addressField.getText(), Integer.parseInt
							(portField.getText()),
					  usernameField.getText(), passwordField.getText());
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
							} else
							{
								System.out.println("Login unsuccessful");
								worldClient.shutdown();
							}
						}
					}
				}
			}
		});
		return grid;
	}

}
