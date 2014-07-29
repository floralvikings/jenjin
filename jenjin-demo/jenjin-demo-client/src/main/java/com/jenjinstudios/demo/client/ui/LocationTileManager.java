package com.jenjinstudios.demo.client.ui;

import com.jenjinstudios.world.Location;
import javafx.scene.image.Image;

import java.util.Properties;

/**
 * @author Caleb Brinkman
 */
public class LocationTileManager
{
	private final GroundTileManager groundTileManager;
	private final WallTileManager wallTileManager;
	private final IndoorsTileManager indoorsTileManager;

	public LocationTileManager() {
		groundTileManager = new GroundTileManager();
		wallTileManager = new WallTileManager();
		indoorsTileManager = new IndoorsTileManager();
	}

	public Image getTileForLocation(Location location) {
		Properties properties = location.getProperties();
		boolean walkable = !"false".equals(properties.getProperty("walkable"));
		boolean indoors = "true".equals(properties.get("indoors"));
		Image tile;
		if (!walkable)
		{
			tile = wallTileManager.getTileForLocation(location);
		} else if (indoors)
		{
			tile = indoorsTileManager.getTileForLocation(location);
		} else
		{
			tile = groundTileManager.getTileForLocation(location);
		}
		return tile;
	}
}
