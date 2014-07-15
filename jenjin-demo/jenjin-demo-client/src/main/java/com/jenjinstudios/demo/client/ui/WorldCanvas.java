package com.jenjinstudios.demo.client.ui;

import com.jenjinstudios.world.Location;
import com.jenjinstudios.world.WorldObject;
import com.jenjinstudios.world.client.ClientPlayer;
import com.jenjinstudios.world.math.Angle;
import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;

/**
 * @author Caleb Brinkman
 */
public class WorldCanvas extends Canvas implements EventHandler<KeyEvent>
{
	private static final double SCALE = 50;
	private static final double OBJECT_SCALE = 25;
	private final ClientPlayer clientPlayer;
	private final MovementKeyTracker movementKeyTracker;

	public WorldCanvas(ClientPlayer clientPlayer, double width, double height) {
		super(width, height);
		this.clientPlayer = clientPlayer;
		movementKeyTracker = new MovementKeyTracker();
		setOnKeyPressed(this);
		setOnKeyReleased(this);
	}

	public void drawWorld() {
		Canvas canvas = new Canvas(getWidth(), getHeight());
		canvas.getGraphicsContext2D().setFill(Color.BEIGE);
		canvas.getGraphicsContext2D().fillText(clientPlayer.getVector2D().toString(), 0, 24);
		clearBackground(canvas);
		drawLocations(canvas);
		drawObjects(canvas);
		drawPlayer(canvas);
	}

	public void clearBackground(Canvas canvas) {
		GraphicsContext graphicsContext2D = canvas.getGraphicsContext2D();
		graphicsContext2D.setFill(Color.BLACK);
		graphicsContext2D.fillRect(0, 0, canvas.getWidth(), getHeight());
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

			double x = canvas.getWidth() / 2 + (xDiff * SCALE - xBuff * (SCALE / Location.SIZE));
			double y = canvas.getHeight() / 2 - (yDiff * SCALE - yBuff * (SCALE / Location.SIZE));

			GraphicsContext graphicsContext2D = canvas.getGraphicsContext2D();
			graphicsContext2D.setFill(Color.WHITE);
			graphicsContext2D.setStroke(Color.LIGHTSLATEGRAY);
			graphicsContext2D.fillRect(x, y, SCALE, SCALE);
			graphicsContext2D.strokeRect(x, y, SCALE, SCALE);
		}
	}

	public void drawObjects(Canvas canvas) {
		for (WorldObject o : clientPlayer.getVisibleObjects().values())
		{
			drawObject(canvas, o);
		}
	}

	private void drawObject(Canvas canvas, WorldObject o) {
		double xDiff = o.getVector2D().getXCoordinate() - clientPlayer.getVector2D().getXCoordinate();
		double yDiff = o.getVector2D().getYCoordinate() - clientPlayer.getVector2D().getYCoordinate();

		double x = (canvas.getWidth() / 2) + (xDiff * (SCALE / Location.SIZE));
		double y = (canvas.getWidth() / 2) - (yDiff * (SCALE / Location.SIZE)) -
			  SCALE * ((Location.SIZE * Location.SIZE) / SCALE);

		GraphicsContext graphicsContext2D = canvas.getGraphicsContext2D();
		graphicsContext2D.setFill(Color.DARKGREEN);
		graphicsContext2D.fillRect(x - OBJECT_SCALE / 2, y - OBJECT_SCALE / 2, OBJECT_SCALE, OBJECT_SCALE);
	}

	public void drawPlayer(Canvas canvas) {
		GraphicsContext graphicsContext2D = canvas.getGraphicsContext2D();
		graphicsContext2D.setFill(Color.INDIGO);
		double x = getWidth() / 2 - OBJECT_SCALE / 2;
		double y = getHeight() / 2 - OBJECT_SCALE / 2;
		graphicsContext2D.fillRect(x, y, OBJECT_SCALE, OBJECT_SCALE);
	}

	@Override
	public void handle(KeyEvent keyEvent) {
		movementKeyTracker.setKeyFlags(keyEvent);
		setNewAngle();
		keyEvent.consume();
	}

	private void setNewAngle() {
		Angle angle = clientPlayer.getAngle().asIdle();
		angle = movementKeyTracker.getMoveAngle(angle);
		clientPlayer.setAngle(angle);
	}
}
