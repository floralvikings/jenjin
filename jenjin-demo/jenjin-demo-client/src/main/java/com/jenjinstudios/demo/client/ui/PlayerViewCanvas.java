package com.jenjinstudios.demo.client.ui;

import com.jenjinstudios.world.Actor;
import com.jenjinstudios.world.Location;
import com.jenjinstudios.world.WorldObject;
import com.jenjinstudios.world.actor.Vision;
import com.jenjinstudios.world.event.PreUpdateEvent;
import com.jenjinstudios.world.math.SightCalculator;
import com.jenjinstudios.world.util.LocationUtils;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;

import java.io.InputStream;
import java.util.Map;

/**
 * @author Caleb Brinkman
 */
public class PlayerViewCanvas extends Canvas
{
	private static final double PLAYER_SCALE = 75;
	private static final double BULLET_SCALE = 15;
	private static final double SCALE = 75;
	private final double locScale = (SCALE / Location.SIZE);
	private final Image playerTile;
	private final Image bulletTile;
	private final Actor clientPlayer;
	private final double yOrig = getHeight() / 2;
	private final double xOrig = getWidth() / 2;

	public PlayerViewCanvas(Actor clientPlayer, double width, double height) {
		super(width, height);
		this.clientPlayer = clientPlayer;
		Platform.runLater(this::requestFocus);

		String tankImageFile = "com/jenjinstudios/demo/client/images/tank.png";
		InputStream tankStream = getClass().getClassLoader().getResourceAsStream(tankImageFile);
		playerTile = new Image(tankStream, PLAYER_SCALE * 1.25, PLAYER_SCALE, false, true);

		String bulletImageFile = "com/jenjinstudios/demo/client/images/bullet.png";
		InputStream bulletStream = getClass().getClassLoader().getResourceAsStream(bulletImageFile);
		bulletTile = new Image(bulletStream, BULLET_SCALE, BULLET_SCALE, false, true);
	}

	public void drawWorld() {
		clearBackground();
		drawLocations();
		drawObjects();
	}

	protected void clearBackground() {
		GraphicsContext graphicsContext2D = getGraphicsContext2D();
		graphicsContext2D.setFill(Color.BLACK);
		graphicsContext2D.fillRect(0, 0, getWidth(), getHeight());
	}

	protected void drawLocations() {
		if (LocationUtils.getObjectLocation(clientPlayer) != null)
		{
			SightCalculator.getVisibleLocations(clientPlayer).stream().filter(l -> l != null).
				  forEach(this::drawLocation);
		}
	}

	protected void drawLocation(Location location) {
		Location pLoc = LocationUtils.getObjectLocation(clientPlayer);
		int xDiff = location.getX() - pLoc.getX();
		int yDiff = location.getY() - pLoc.getY();//+ 1;
		double xBuff = clientPlayer.getVector2D().getXCoordinate() % Location.SIZE;
		double yBuff = clientPlayer.getVector2D().getYCoordinate() % Location.SIZE;

		double x = xOrig + (xDiff * SCALE - xBuff * locScale);
		double y = yOrig - (yDiff * SCALE - yBuff * locScale) - SCALE;

		GraphicsContext graphicsContext2D = getGraphicsContext2D();

		Map<String, String> properties = location.getProperties();
		boolean walkable = !"false".equals(properties.get("walkable"));
		boolean indoors = "true".equals(properties.get("indoors"));
		if (!walkable)
		{
			graphicsContext2D.setFill(Color.GRAY);
		} else if (indoors)
		{
			graphicsContext2D.setFill(Color.BROWN);
		} else
		{
			graphicsContext2D.setFill(Color.GREEN);
		}

		graphicsContext2D.fillRect(x, y, SCALE, SCALE);
	}

	protected void drawObjects() {
		PreUpdateEvent event = clientPlayer.getPreUpdateEvent(Vision.EVENT_NAME);
		if (event != null)
		{
			Vision vision = (Vision) event;
			vision.getVisibleObjects().values().forEach(this::drawObject);
			drawObject(clientPlayer);
		}
	}

	private void drawObject(WorldObject o) {
		double xDiff = o.getVector2D().getXCoordinate() - clientPlayer.getVector2D().getXCoordinate();
		double yDiff = o.getVector2D().getYCoordinate() - clientPlayer.getVector2D().getYCoordinate();

		double x = xOrig + (xDiff * locScale);
		double y = yOrig - (yDiff * locScale);

		GraphicsContext graphicsContext2D = getGraphicsContext2D();
		graphicsContext2D.save();
		Rotate r = new Rotate(-Math.toDegrees(o.getAngle().getAbsoluteAngle()), x, y);
		graphicsContext2D.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
		Image objectTile = getObjectTile(o);
		graphicsContext2D.drawImage(objectTile, x - objectTile.getWidth() / 2, y - objectTile.getHeight() / 2);
		graphicsContext2D.restore();
	}

	private Image getObjectTile(WorldObject object) {
		Image tile;
		switch (object.getResourceID())
		{
			case 0:
				tile = playerTile;
				break;
			case 1:
				tile = bulletTile;
				break;
			default:
				tile = playerTile;
				break;
		}
		return tile;
	}
}
