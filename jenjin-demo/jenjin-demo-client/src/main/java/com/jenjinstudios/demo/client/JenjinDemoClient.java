package com.jenjinstudios.demo.client;

import com.jenjinstudios.demo.client.ui.LoginPane;
import com.jenjinstudios.world.client.WorldClient;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @author Caleb Brinkman
 */
public class JenjinDemoClient extends Application
{
	private WorldClient worldClient;

	public static void main(String[] args) throws Exception {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		LoginPane loginPane = new LoginPane(this);
		primaryStage.setScene(new Scene(loginPane, 800, 600));
		primaryStage.show();
	}

	public void successfulLogin(WorldClient worldClient) {
		if (this.worldClient != null)
		{
			throw new IllegalStateException("WorldClient already set.");
		}
		this.worldClient = worldClient;
	}
}
