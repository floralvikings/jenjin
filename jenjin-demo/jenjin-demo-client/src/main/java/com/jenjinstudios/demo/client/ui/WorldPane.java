package com.jenjinstudios.demo.client.ui;

import com.jenjinstudios.world.client.WorldClient;
import javafx.geometry.Dimension2D;
import javafx.scene.Group;

/**
 * @author Caleb Brinkman
 */
public class WorldPane extends Group
{
	public WorldPane(WorldClient worldClient, Dimension2D size) {
		WorldCanvas canvas = new WorldCanvas(worldClient, size.getWidth(), size.getHeight());
		getChildren().add(canvas);
	}
}
