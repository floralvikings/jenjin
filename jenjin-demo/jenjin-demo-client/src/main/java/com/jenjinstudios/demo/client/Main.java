package com.jenjinstudios.demo.client;

import com.jenjinstudios.client.authentication.AuthenticationHelper;
import com.jenjinstudios.core.io.MessageRegistry;
import com.jenjinstudios.demo.client.ui.ClientPane;
import com.jenjinstudios.demo.client.ui.LoginPane;
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

import java.io.InputStream;

/**
 * @author Caleb Brinkman
 */
public class Main extends Application implements EventHandler<WindowEvent>
{
	private static final int LOGIN_PANE_WIDTH = 400;
	private static final int LOGIN_PANE_HEIGHT = 175;
	private WorldClient worldClient;
	private Stage stage;

	/**
	 * Launch the application.
	 *
	 * @param args The command line arguments.
	 *
	 * @throws Exception If there's an exception.
	 */
	public static void main(String... args) throws Exception {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		InputStream stream = getClass().getResourceAsStream("/com/jenjinstudios/demo/client/Messages.xml");
		MessageRegistry.getGlobalRegistry().register("Demo Client Messages", stream);
		LoginPane loginPane = new LoginPane(this);
		stage = primaryStage;
		stage.setOnCloseRequest(this);
		stage.setScene(new Scene(loginPane, LOGIN_PANE_WIDTH, LOGIN_PANE_HEIGHT));
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
		GridPane worldPane = new ClientPane(worldClient, new Dimension2D(bounds.getWidth(), bounds.getHeight()));
		stage.getScene().setRoot(worldPane);
		stage.setWidth(bounds.getWidth());
		stage.setHeight(bounds.getHeight());
		stage.show();
	}

	@Override
	public void handle(WindowEvent windowEvent) {
		if (worldClient != null)
		{
			AuthenticationHelper.logoutAndWait(worldClient);
			worldClient.shutdown();
		}
		Platform.exit();
	}
}
