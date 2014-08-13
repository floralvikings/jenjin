package com.jenjinstudios.demo.client.ui;

import com.jenjinstudios.world.Location;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.Properties;

/**
 * @author Caleb Brinkman
 */
public class LocationTileManager
{

	public LocationTileManager() {
	}

	public void setGraphicsColor(Location location, GraphicsContext g) {
		Properties properties = location.getProperties();
		boolean walkable = !"false".equals(properties.getProperty("walkable"));
		boolean indoors = "true".equals(properties.get("indoors"));
		if (!walkable)
		{
			g.setFill(Color.GRAY);
		} else if (indoors)
		{
			g.setFill(Color.BROWN);
		} else
		{
			g.setFill(Color.GREEN);
		}
	}
}
