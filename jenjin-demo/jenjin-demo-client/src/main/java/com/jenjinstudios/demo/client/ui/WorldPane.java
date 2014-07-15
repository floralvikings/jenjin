package com.jenjinstudios.demo.client.ui;

import com.jenjinstudios.world.Location;
import com.jenjinstudios.world.client.ClientPlayer;
import com.jenjinstudios.world.math.Angle;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Dimension2D;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;

/**
 * @author Caleb Brinkman
 */
public class WorldPane extends Group implements EventHandler<KeyEvent>
{
	private static final double SCALE = 100;
	private static final double OBJECT_SCALE = 25;
	private final Dimension2D canvasSize;
	private final ClientPlayer clientPlayer;
	private Canvas canvas;
	private boolean upKey;

	public WorldPane(ClientPlayer clientPlayer, Dimension2D size) {
		this.canvasSize = size;
		this.canvas = new Canvas(size.getWidth(), size.getHeight());
		this.clientPlayer = clientPlayer;
		getChildren().add(canvas);
		setOnKeyPressed(this);
		setOnKeyReleased(this);
		Platform.runLater(new Runnable()
		{
			@Override
			public void run() {
				requestFocus();
			}
		});
	}

	public void drawWorld() {
		Canvas canvas = new Canvas(canvasSize.getWidth(), canvasSize.getHeight());
		canvas.getGraphicsContext2D().setFill(Color.BEIGE);
		canvas.getGraphicsContext2D().fillText(clientPlayer.getVector2D().toString(), 0, 24);
		clearBackground(canvas);
		drawLocations(canvas);
		drawObjects(canvas);
		drawPlayer(canvas);
		getChildren().remove(this.canvas);
		this.canvas = canvas;
		getChildren().add(canvas);
	}

	public void clearBackground(Canvas canvas) {
		GraphicsContext graphicsContext2D = canvas.getGraphicsContext2D();
		graphicsContext2D.setFill(Color.BLACK);
		graphicsContext2D.fillRect(0, 0, canvas.getWidth(), this.canvas.getHeight());
	}

	public void drawLocations(Canvas canvas) {
		for (Location loc : clientPlayer.getVisibleLocations())
		{
			drawLocation(canvas, loc);
		}
	}

	public void drawLocation(Canvas canvas, Location location) {
		Location pLoc = clientPlayer.getLocation();
		if (location != null && pLoc != null)
		{
			int xDiff = location.X_COORDINATE - pLoc.X_COORDINATE;
			int yDiff = location.Y_COORDINATE - pLoc.Y_COORDINATE + 1;
			double xBuff = clientPlayer.getVector2D().getXCoordinate() % Location.SIZE;
			double yBuff = clientPlayer.getVector2D().getYCoordinate() % Location.SIZE;

			double x = canvas.getWidth() / 2 + (xDiff * SCALE - xBuff);
			double y = canvas.getHeight() / 2 - (yDiff * SCALE - yBuff);

			GraphicsContext graphicsContext2D = canvas.getGraphicsContext2D();
			graphicsContext2D.setFill(Color.WHITE);
			graphicsContext2D.fillRect(x, y, SCALE, SCALE);
		}
	}

	public void drawObjects(Canvas canvas) {

	}

	public void drawPlayer(Canvas canvas) {
		GraphicsContext graphicsContext2D = canvas.getGraphicsContext2D();
		graphicsContext2D.setFill(Color.INDIGO);
		double x = this.canvas.getWidth() / 2 - OBJECT_SCALE / 2;
		double y = this.canvas.getHeight() / 2 - OBJECT_SCALE / 2;
		graphicsContext2D.fillRect(x, y, OBJECT_SCALE, OBJECT_SCALE);
	}

	@Override
	public void handle(KeyEvent keyEvent) {
		if (keyEvent.getCode().equals(KeyCode.UP))
		{
			if (keyEvent.getEventType() == KeyEvent.KEY_PRESSED && !upKey)
			{
				upKey = true;
				Angle angle = new Angle(0.0, Angle.LEFT);
				if (!clientPlayer.getAngle().equals(angle))
				{
					clientPlayer.setAngle(angle);
				}
			} else if (keyEvent.getEventType() == KeyEvent.KEY_RELEASED && upKey)
			{
				upKey = false;
				clientPlayer.setAngle(clientPlayer.getAngle().asIdle());
			}
			keyEvent.consume();
		}
	}
}
