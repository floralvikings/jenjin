package com.jenjinstudios.demo.client;

import com.jenjinstudios.client.authentication.AuthenticationHelper;
import com.jenjinstudios.client.authentication.ClientUser;
import com.jenjinstudios.client.authentication.User;
import com.jenjinstudios.core.io.MessageInputStream;
import com.jenjinstudios.core.io.MessageOutputStream;
import com.jenjinstudios.core.io.MessageRegistry;
import com.jenjinstudios.core.io.MessageStreamPair;
import com.jenjinstudios.demo.client.ui.ClientPane;
import com.jenjinstudios.world.client.WorldClient;
import com.jenjinstudios.world.client.WorldClientMessageContext;
import com.jenjinstudios.world.io.WorldDocumentException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Dimension2D;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Demonstrates a very rudimentary game using the jenjin framework and javafx.  Use arrow keys or WASD to move,
 * space to shoot.
 *
 * @author Caleb Brinkman
 */
public class DemoClientApplication extends Application implements EventHandler<WindowEvent>
{
	private static final int LOGIN_PANE_WIDTH = 400;
	private static final int LOGIN_PANE_HEIGHT = 175;
	private WorldClient worldClient;
	private Stage stage;

	/**
	 * Launch the application.
	 *
	 * @param args The command line arguments.
	 */
	public static void main(String... args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		InputStream stream = getClass().getResourceAsStream("/com/jenjinstudios/demo/client/Messages.xml");
		MessageRegistry.getGlobalRegistry().register("Demo Client Messages", stream);
		LoginPane loginPane = new LoginPane(this);
		stage = primaryStage;
		stage.setOnCloseRequest(this);
		stage.setScene(new Scene(loginPane, LOGIN_PANE_WIDTH, LOGIN_PANE_HEIGHT));
		stage.show();
		Platform.runLater(() -> Platform.setImplicitExit(false));
	}

	/**
	 * Called on successful login from a LoginPane.
	 *
	 * @param wc The world client that has been created.
	 */
	public void successfulLogin(WorldClient wc) {
		if (worldClient == null)
		{
			worldClient = wc;
			Screen screen = Screen.getPrimary();
			Rectangle2D bounds = screen.getVisualBounds();
			stage.setX(bounds.getMinX());
			stage.setY(bounds.getMinY());
			GridPane worldPane = new ClientPane(worldClient, new Dimension2D(bounds.getWidth(), bounds.getHeight()));
			stage.getScene().setRoot(worldPane);
			stage.setWidth(bounds.getWidth());
			stage.setHeight(bounds.getHeight());
			stage.show();
		} else
		{
			throw new IllegalStateException("WorldClient already set.");
		}
	}

	@Override
	public void handle(WindowEvent event) {
		if (worldClient != null)
		{
			AuthenticationHelper.logoutAndWait(worldClient);
			worldClient.shutdown();
		}
		Platform.exit();
	}

	/**
	 * Pane that handles logging into a world server.
	 *
	 * @author Caleb Brinkman
	 */
	public static final class LoginPane extends GridPane
	{
		private static final Logger LOGGER = Logger.getLogger(LoginPane.class.getName());
		private static final int PANE_PADDING = 25;
		private final TextField addressField = new TextField("127.0.0.1");
		private final TextField portField = new TextField("51015");
		private final TextField usernameField = new TextField("TestAccount1");
		private final Button loginButton = new Button("Login");
		private final PasswordField passwordField = new PasswordField();
		private final DemoClientApplication demoClientApplication;

		/**
		 * Construct a new LoginPane.
		 *
		 * @param application The application using the login pane.
		 */
		public LoginPane(final DemoClientApplication application) {
			this.demoClientApplication = application;
			setHgap(10);
			setVgap(10);
			setPadding(new Insets(PANE_PADDING, PANE_PADDING, PANE_PADDING, PANE_PADDING));

			createForm();
		}

		private static WorldClient tryCreateWorldClient(String address, int port, User user) {
			WorldClient worldClient;
			try
			{
				worldClient = createWorldClient(address, port, user);
				worldClient.start();
				worldClient.initializeWorldFromServer();
				LOGGER.log(Level.INFO, "Created World Client.");
			} catch (WorldDocumentException e)
			{
				LOGGER.log(Level.SEVERE, "Exception reading world document.", e);
				worldClient = null;
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
			if (event.getCode() == KeyCode.ENTER)
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
					demoClientApplication.successfulLogin(worldClient);
				} else
				{
					LOGGER.log(Level.WARNING, "Login unsuccessful");
					worldClient.shutdown();
				}
			}
		}

	}
}
