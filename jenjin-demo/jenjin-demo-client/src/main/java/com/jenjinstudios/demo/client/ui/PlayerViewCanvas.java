package com.jenjinstudios.demo.client.ui;

import com.jenjinstudios.world.Actor;
import com.jenjinstudios.world.Location;
import com.jenjinstudios.world.WorldObject;
import com.jenjinstudios.world.actor.Vision;
import com.jenjinstudios.world.client.WorldClient;
import com.jenjinstudios.world.math.SightCalculator;
import com.jenjinstudios.world.util.LocationUtils;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.transform.Rotate;

import java.io.InputStream;
import java.util.Map;

/**
 * Canvas used to render a player view of the world.
 *
 * @author Caleb Brinkman
 */
public class PlayerViewCanvas extends Canvas
{
	private static final double PLAYER_SCALE = 75;
	private static final double BULLET_SCALE = 15;
	private static final double SCALE = 75;
	private static final double TANK_WIDTH_MODIFIER = 1.25;
	private static final int HUD_FONT_SIZE = 14;
	private static final int HUD_VERTICAL_OFFSET = 12;
	private static final double HUD_LINE_OFFSET = 1.5;
	private static final double LOC_SCALE = SCALE / Location.SIZE;
	private final Image playerTile;
	private final Image bulletTile;
	private final Actor clientPlayer;
	private final double yOrig = getHeight() / 2;
	private final double xOrig = getWidth() / 2;
	private final WorldClient worldClient;

	/**
	 * Construct a new PlayerViewCanvas displaying data from the given world client, with the given dimensions.
	 *
	 * @param client The world client containing the player and visible data.
	 * @param width The width of the canvas.
	 * @param height The height of the canvas.
	 */
	public PlayerViewCanvas(WorldClient client, double width, double height) {
		super(width, height);
		this.worldClient = client;
		this.clientPlayer = client.getPlayer();
		Platform.runLater(this::requestFocus);

		String tankImageFile = "com/jenjinstudios/demo/client/images/tank.png";
		InputStream tankStream = getClass().getClassLoader().getResourceAsStream(tankImageFile);
		playerTile = new Image(tankStream, PLAYER_SCALE * TANK_WIDTH_MODIFIER, PLAYER_SCALE, false, true);

		String bulletImageFile = "com/jenjinstudios/demo/client/images/bullet.png";
		InputStream bulletStream = getClass().getClassLoader().getResourceAsStream(bulletImageFile);
		bulletTile = new Image(bulletStream, BULLET_SCALE, BULLET_SCALE, false, true);
	}

	/**
	 * Draw the player's view of the game world.
	 */
	public void drawWorld() {
		clearBackground();
		drawLocations();
		drawObjects();
		drawHUD();
	}

	private void drawHUD() {
		GraphicsContext graphicsContext2D = getGraphicsContext2D();
		graphicsContext2D.setFill(Color.WHITE);
		int fontSize = HUD_FONT_SIZE;
		graphicsContext2D.setFont(Font.font("Arial", fontSize));

		String[] hudStrings = {
			  "UPS: " + worldClient.getAverageUPS(),
			  "Visible Object Count: " + ((Vision) clientPlayer.getPreUpdateEvent(Vision.EVENT_NAME)).
					getVisibleObjects().size(),
			  clientPlayer.getName(),
			  clientPlayer.getVector2D().toString()
		};

		for (int i = 0; i < hudStrings.length; i++)
		{
			graphicsContext2D.fillText(hudStrings[i], 0, HUD_VERTICAL_OFFSET + (i * fontSize * HUD_LINE_OFFSET));
		}
	}

	private void clearBackground() {
		GraphicsContext graphicsContext2D = getGraphicsContext2D();
		graphicsContext2D.setFill(Color.BLACK);
		graphicsContext2D.fillRect(0, 0, getWidth(), getHeight());
	}

	private void drawLocations() {
		if (LocationUtils.getObjectLocation(clientPlayer) != null)
		{
			SightCalculator.getVisibleLocations(clientPlayer).stream().filter(l -> l != null).
				  forEach(this::drawLocation);
		}
	}

	private void drawLocation(Location location) {
		Location pLoc = LocationUtils.getObjectLocation(clientPlayer);
		int xDiff = location.getX() - pLoc.getX();
		int yDiff = location.getY() - pLoc.getY();//+ 1;
		double xBuff = clientPlayer.getVector2D().getXCoordinate() % Location.SIZE;
		double yBuff = clientPlayer.getVector2D().getYCoordinate() % Location.SIZE;

		double x = xOrig + ((xDiff * SCALE) - (xBuff * LOC_SCALE));
		double y = yOrig - ((yDiff * SCALE) - (yBuff * LOC_SCALE)) - SCALE;

		GraphicsContext graphicsContext2D = getGraphicsContext2D();

		Map<String, String> properties = location.getProperties();
		boolean isBlocked = "false".equals(properties.get("walkable"));
		boolean indoors = "true".equals(properties.get("indoors"));
		if (isBlocked)
		{
			graphicsContext2D.setFill(Color.GRAY);
		} else if (indoors)
		{
			graphicsContext2D.setFill(Color.BROWN);
		} else
		{
			graphicsContext2D.setFill(Color.GREEN);
		}

		graphicsContext2D.fillRect(x, y, SCALE - 2, SCALE - 2);
	}

	private void drawObjects() {
		Object object = clientPlayer.getPreUpdateEvent(Vision.EVENT_NAME);
		if (object instanceof Vision)
		{
			Vision vision = (Vision) object;
			vision.getVisibleObjects().forEach(this::drawObject);
			drawObject(clientPlayer);
		}
	}

	private void drawObject(WorldObject o) {
		double xDiff = o.getVector2D().getXCoordinate() - clientPlayer.getVector2D().getXCoordinate();
		double yDiff = o.getVector2D().getYCoordinate() - clientPlayer.getVector2D().getYCoordinate();

		double x = xOrig + (xDiff * LOC_SCALE);
		double y = yOrig - (yDiff * LOC_SCALE);

		GraphicsContext graphicsContext2D = getGraphicsContext2D();
		graphicsContext2D.save();
		Rotate r = new Rotate(-Math.toDegrees(o.getAngle().getAbsoluteAngle()), x, y);
		graphicsContext2D.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
		Image objectTile = getObjectTile(o);
		graphicsContext2D.drawImage(objectTile, x - (objectTile.getWidth() / 2), y - (objectTile.getHeight() / 2));
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
