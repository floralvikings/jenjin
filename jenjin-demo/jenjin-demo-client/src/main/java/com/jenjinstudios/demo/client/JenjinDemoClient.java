package com.jenjinstudios.demo.client;

import com.jenjinstudios.demo.client.ui.LoginPane;
import com.jenjinstudios.demo.client.ui.WorldPane;
import com.jenjinstudios.world.client.WorldClient;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Dimension2D;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * @author Caleb Brinkman
 */
public class JenjinDemoClient extends Application implements EventHandler<WindowEvent>
{
	private WorldClient worldClient;
	private Stage stage;

	public static void main(String[] args) throws Exception {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		LoginPane loginPane = new LoginPane(this);
		stage = primaryStage;
		stage.setOnCloseRequest(this);
		stage.setScene(new Scene(loginPane, 800, 600));
		stage.show();
	}

	public void successfulLogin(WorldClient worldClient) {
		if (this.worldClient != null)
		{
			throw new IllegalStateException("WorldClient already set.");
		}
		this.worldClient = worldClient;
		final WorldPane worldPane = new WorldPane(worldClient.getPlayer(), new Dimension2D(800, 600));
		stage.setScene(new Scene(worldPane, 800, 600));
		worldPane.drawWorld();
		stage.show();
	}

	@Override
	public void handle(WindowEvent windowEvent) {
		if (worldClient != null)
		{
			worldClient.shutdown();
		}
	}
}
