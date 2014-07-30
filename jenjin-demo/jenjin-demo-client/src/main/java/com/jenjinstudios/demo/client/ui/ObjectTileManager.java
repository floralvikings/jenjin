package com.jenjinstudios.demo.client.ui;

import com.jenjinstudios.world.WorldObject;
import javafx.scene.image.Image;

import java.io.InputStream;

/**
 * @author Caleb Brinkman
 */
public class ObjectTileManager
{
	private static final double PLAYER_SCALE = 75;
	private static final double BULLET_SCALE = 15;
	private final Image playerTile;
	private final Image bulletTile;

	public ObjectTileManager() {
		String tankImageFile = "com/jenjinstudios/demo/client/images/tank.png";
		InputStream tankStream = getClass().getClassLoader().getResourceAsStream(tankImageFile);
		playerTile = new Image(tankStream, PLAYER_SCALE * 1.25, PLAYER_SCALE, false, true);

		String bulletImageFile = "com/jenjinstudios/demo/client/images/bullet.png";
		InputStream bulletStream = getClass().getClassLoader().getResourceAsStream(bulletImageFile);
		bulletTile = new Image(bulletStream, BULLET_SCALE, BULLET_SCALE, false, true);
	}

	public Image getObjectTile(WorldObject object) {
		Image tile = null;
		switch (object.getResourceID())
		{
			case 0:
				tile = playerTile;
				break;
			case 1:
				tile = bulletTile;
				break;
		}
		return tile;
	}
}
