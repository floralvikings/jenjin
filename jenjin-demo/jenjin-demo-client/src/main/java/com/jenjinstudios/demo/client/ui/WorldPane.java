package com.jenjinstudios.demo.client.ui;

import com.jenjinstudios.world.Location;
import com.jenjinstudios.world.client.ClientPlayer;
import javafx.geometry.Dimension2D;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * @author Caleb Brinkman
 */
public class WorldPane extends Group
{
	private static final double SCALE = 100;
	private static final double OBJECT_SCALE = 25;
	private final ClientPlayer clientPlayer;
	private final Canvas canvas;

	public WorldPane(ClientPlayer clientPlayer, Dimension2D size) {
		this.canvas = new Canvas(size.getWidth(), size.getHeight());
		this.clientPlayer = clientPlayer;
		getChildren().add(canvas);
	}

	public void drawWorld() {
		clearBackground();
		drawLocations();
		drawObjects();
		drawPlayer();
	}

	public void clearBackground() {
		GraphicsContext graphicsContext2D = canvas.getGraphicsContext2D();
		graphicsContext2D.setFill(Color.BLACK);
		graphicsContext2D.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
	}

	public void drawLocations() {
		for (Location loc : clientPlayer.getVisibleLocations())
		{
			drawLocation(loc);
		}
	}

	public void drawLocation(Location location) {
		Location pLoc = clientPlayer.getLocation();
		int xDiff = location.X_COORDINATE - pLoc.X_COORDINATE;
		int yDiff = location.Y_COORDINATE - pLoc.Y_COORDINATE;
		double xBuff = clientPlayer.getVector2D().getXCoordinate() % Location.SIZE;
		double yBuff = clientPlayer.getVector2D().getYCoordinate() % Location.SIZE;

		double x = canvas.getWidth() / 2 + (xDiff * SCALE - xBuff);
		double y = canvas.getHeight() / 2 + (yDiff * SCALE - yBuff);

		GraphicsContext graphicsContext2D = canvas.getGraphicsContext2D();
		graphicsContext2D.setFill(Color.WHITE);
		graphicsContext2D.fillRect(x, y, SCALE, SCALE);
	}

	public void drawObjects() {

	}

	public void drawPlayer() {
		GraphicsContext graphicsContext2D = canvas.getGraphicsContext2D();
		graphicsContext2D.setFill(Color.INDIGO);
		double x = canvas.getWidth() / 2 - OBJECT_SCALE / 2;
		double y = canvas.getHeight() / 2 - OBJECT_SCALE / 2;
		graphicsContext2D.fillRect(x, y, OBJECT_SCALE, OBJECT_SCALE);
	}

}
