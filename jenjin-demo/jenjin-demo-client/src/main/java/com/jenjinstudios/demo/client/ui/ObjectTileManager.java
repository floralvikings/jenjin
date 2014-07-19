package com.jenjinstudios.demo.client.ui;

import com.jenjinstudios.world.WorldObject;
import javafx.scene.image.Image;

import java.io.InputStream;

/**
 * @author Caleb Brinkman
 */
public class ObjectTileManager
{
	private final Image playerTile;
	private final Image bulletTile;

	public ObjectTileManager() {
		String tankImageFile = "com/jenjinstudios/demo/client/images/tank.png";
		InputStream tankStream = getClass().getClassLoader().getResourceAsStream(tankImageFile);
		playerTile = new Image(tankStream);

		String bulletImageFile = "com/jenjinstudios/demo/client/images/bullet.png";
		InputStream bulletStream = getClass().getClassLoader().getResourceAsStream(bulletImageFile);
		bulletTile = new Image(bulletStream);
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
