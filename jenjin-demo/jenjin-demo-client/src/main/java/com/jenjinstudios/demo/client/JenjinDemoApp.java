package com.jenjinstudios.demo.client;

import com.jenjinstudios.demo.client.ui.LoginPane;
import com.jenjinstudios.demo.client.ui.WorldPane;
import com.jenjinstudios.world.client.WorldClient;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Dimension2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * @author Caleb Brinkman
 */
public class JenjinDemoApp extends Application implements EventHandler<WindowEvent>
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
		stage.setScene(new Scene(loginPane, 400, 175));
		stage.show();
		Platform.runLater(() -> Platform.setImplicitExit(false));
	}

	public void successfulLogin(WorldClient worldClient) {
		if (this.worldClient != null)
		{
			throw new IllegalStateException("WorldClient already set.");
		}
		this.worldClient = worldClient;
		Screen screen = Screen.getPrimary();
		Rectangle2D bounds = screen.getVisualBounds();
		stage.setX(bounds.getMinX());
		stage.setY(bounds.getMinY());
		GridPane worldPane = new WorldPane(worldClient, new Dimension2D(bounds.getWidth(), bounds.getHeight()));
		stage.getScene().setRoot(worldPane);
		stage.setWidth(bounds.getWidth());
		stage.setHeight(bounds.getHeight());
		stage.show();
	}

	@Override
	public void handle(WindowEvent windowEvent) {
		if (worldClient != null)
		{
			worldClient.getLoginTracker().sendLogoutRequestAndWaitForResponse(30000);
			worldClient.shutdown();
		}
		Platform.exit();
	}
}
