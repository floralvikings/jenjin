package com.jenjinstudios.demo.client.ui;

import com.jenjinstudios.world.Cell;
import com.jenjinstudios.world.client.WorldClient;
import com.jenjinstudios.world.math.SightCalculator;
import com.jenjinstudios.world.object.WorldObject;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.transform.Rotate;

import java.io.InputStream;

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
	private static final double CELL_SCALE = SCALE / Cell.CELL_SIZE;
	private final Image playerTile;
	private final Image bulletTile;
	private final WorldObject clientPlayer;
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
			  "Visible Object Count: " + clientPlayer.getVision()
					.getVisibleObjects().size(),
			  clientPlayer.getName(),
			  clientPlayer.getGeometry().getPosition().toString()
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
		if (clientPlayer.getParent() != null)
		{
			SightCalculator.getVisibleCells(clientPlayer).
				  stream().
				  filter(cell -> cell != null).
				  forEach(this::drawCell);
		}
	}

	private void drawCell(Cell cell) {
		Cell pCell = clientPlayer.getParent();
		int xDiff = cell.getXCoordinate() - pCell.getXCoordinate();
		int yDiff = cell.getYCoordinate() - pCell.getYCoordinate();//+ 1;
		double xBuff = clientPlayer.getGeometry().getPosition().getXValue() % Cell.CELL_SIZE;
		double yBuff = clientPlayer.getGeometry().getPosition().getYValue() % Cell.CELL_SIZE;

		double x = xOrig + ((xDiff * SCALE) - (xBuff * CELL_SCALE));
		double y = yOrig - ((yDiff * SCALE) - (yBuff * CELL_SCALE)) - SCALE;

		GraphicsContext graphicsContext2D = getGraphicsContext2D();

		boolean isBlocked = "false".equals(cell.getProperty("walkable"));
		boolean indoors = "true".equals(cell.getProperty("indoors"));
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
		clientPlayer.getVision().getVisibleObjects().forEach(this::drawObject);
		drawObject(clientPlayer);
	}

	private void drawObject(WorldObject o) {
		double xDiff = o.getGeometry().getPosition().getXValue() - clientPlayer.getGeometry().getPosition()
			  .getXValue();
		double yDiff = o.getGeometry().getPosition().getYValue() - clientPlayer.getGeometry().getPosition()
			  .getYValue();

		double x = xOrig + (xDiff * CELL_SCALE);
		double y = yOrig - (yDiff * CELL_SCALE);

		GraphicsContext graphicsContext2D = getGraphicsContext2D();
		graphicsContext2D.save();
		Rotate r = new Rotate(-Math.toDegrees(o.getGeometry().getOrientation().getAbsoluteAngle()), x, y);
		graphicsContext2D.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
		Image objectTile = getObjectTile(o);
		graphicsContext2D.drawImage(objectTile, x - (objectTile.getWidth() / 2), y - (objectTile.getHeight() / 2));
		graphicsContext2D.restore();
	}

	private Image getObjectTile(WorldObject object) {
		Image tile;
		switch (object.getIdentification().getTypeId())
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
