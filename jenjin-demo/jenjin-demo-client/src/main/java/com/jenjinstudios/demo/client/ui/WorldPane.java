package com.jenjinstudios.demo.client.ui;

import com.jenjinstudios.world.client.ClientPlayer;
import javafx.geometry.Dimension2D;
import javafx.scene.Group;

/**
 * @author Caleb Brinkman
 */
public class WorldPane extends Group
{
	public WorldPane(ClientPlayer clientPlayer, Dimension2D size) {
		WorldCanvas canvas = new WorldCanvas(clientPlayer, size.getWidth(), size.getHeight());
		getChildren().add(canvas);
	}
}
