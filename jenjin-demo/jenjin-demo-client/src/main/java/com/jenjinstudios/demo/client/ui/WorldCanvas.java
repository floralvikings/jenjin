package com.jenjinstudios.demo.client.ui;

import com.jenjinstudios.world.Location;
import com.jenjinstudios.world.WorldObject;
import com.jenjinstudios.world.client.ClientPlayer;
import com.jenjinstudios.world.math.Angle;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.TimelineBuilder;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

import java.io.InputStream;

/**
 * @author Caleb Brinkman
 */
public class WorldCanvas extends Canvas
{
	private static final double SCALE = 75;
	private static final double OBJ_SCALE = 75;
	private final ClientPlayer clientPlayer;
	private final LocationTileManager locationTileManager;
	private final Image objectImage;

	public WorldCanvas(ClientPlayer clientPlayer, double width, double height) {
		super(width, height);
		this.clientPlayer = clientPlayer;
		PlayerControlKeyHandler playerControlKeyHandler = new PlayerControlKeyHandler(clientPlayer);
		setOnKeyPressed(playerControlKeyHandler);
		setOnKeyReleased(playerControlKeyHandler);
		Platform.runLater(this::requestFocus);

		final Duration oneFrameAmt = Duration.millis(1000 / (float) 60);
		final KeyFrame oneFrame = new KeyFrame(oneFrameAmt,
			  event -> drawWorld());
		TimelineBuilder.create().cycleCount(Animation.INDEFINITE).keyFrames(oneFrame).build().play();
		locationTileManager = new LocationTileManager();
		String tankImageFile = "com/jenjinstudios/demo/client/images/tank.png";
		InputStream stream = getClass().getClassLoader().getResourceAsStream(tankImageFile);
		objectImage = new Image(stream);
	}

	public void drawWorld() {
		clearBackground();
		drawLocations();
		drawObjects();
		drawPlayer();
	}

	public void clearBackground() {
		GraphicsContext graphicsContext2D = getGraphicsContext2D();
		graphicsContext2D.setFill(Color.BLACK);
		graphicsContext2D.fillRect(0, 0, getWidth(), getHeight());
	}

	public void drawLocations() { clientPlayer.getVisibleLocations().forEach(this::drawLocation); }

	public void drawLocation(Location location) {
		Location pLoc = clientPlayer.getLocation();
		if (location != null && pLoc != null)
		{
			int xDiff = location.X_COORDINATE - pLoc.X_COORDINATE;
			int yDiff = location.Y_COORDINATE - pLoc.Y_COORDINATE;//+ 1;
			double xBuff = clientPlayer.getVector2D().getXCoordinate() % Location.SIZE;
			double yBuff = clientPlayer.getVector2D().getYCoordinate() % Location.SIZE;

			double x = getWidth() / 2 + (xDiff * SCALE - xBuff * (SCALE / Location.SIZE));
			double y = getHeight() / 2 + (yDiff * SCALE - yBuff * (SCALE / Location.SIZE));

			GraphicsContext graphicsContext2D = getGraphicsContext2D();
			Image tile = locationTileManager.getTileForLocation(location);
			graphicsContext2D.drawImage(tile, x, y, SCALE, SCALE);
		}
	}

	public void drawObjects() { clientPlayer.getVisibleObjects().values().forEach(this::drawObject); }

	public void drawPlayer() {
		double x = getWidth() / 2;
		double y = getHeight() / 2;

		GraphicsContext graphicsContext2D = getGraphicsContext2D();
		graphicsContext2D.save();
		Angle pAngle = clientPlayer.getAngle();
		double angle = pAngle.getAbsoluteAngle();
		angle = Math.toDegrees(angle);
		Rotate r = new Rotate(angle, x, y);
		graphicsContext2D.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
		graphicsContext2D.drawImage(objectImage, x - OBJ_SCALE / 2, y - OBJ_SCALE / 2, OBJ_SCALE,
			  OBJ_SCALE);
		graphicsContext2D.restore();
	}

	private void drawObject(WorldObject o) {
		double xDiff = o.getVector2D().getXCoordinate() - clientPlayer.getVector2D().getXCoordinate();
		double yDiff = o.getVector2D().getYCoordinate() - clientPlayer.getVector2D().getYCoordinate();

		double yOrig = getHeight() / 2;
		double xOrig = getWidth() / 2;

		double locScale = (SCALE / Location.SIZE);

		double x = xOrig + (xDiff * locScale);
		double y = yOrig + (yDiff * locScale);

		GraphicsContext graphicsContext2D = getGraphicsContext2D();
		graphicsContext2D.save();
		Rotate r = new Rotate(Math.toDegrees(o.getAngle().getRelativeAngle()), x, y);
		graphicsContext2D.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
		graphicsContext2D.drawImage(objectImage, x - OBJ_SCALE / 2, y - OBJ_SCALE / 2, OBJ_SCALE, OBJ_SCALE);
		graphicsContext2D.restore();
	}
}
