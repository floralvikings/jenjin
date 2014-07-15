package com.jenjinstudios.demo.client.ui;

import com.jenjinstudios.world.client.ClientPlayer;
import javafx.application.Platform;
import javafx.geometry.Dimension2D;
import javafx.scene.Group;

/**
 * @author Caleb Brinkman
 */
public class WorldPane extends Group
{
	private WorldCanvas canvas;

	public WorldPane(ClientPlayer clientPlayer, Dimension2D size) {
		this.canvas = new WorldCanvas(clientPlayer, size.getWidth(), size.getHeight());
		getChildren().add(canvas);
		Platform.runLater(new Runnable()
		{
			@Override
			public void run() {
				requestFocus();
			}
		});
	}

	public void drawWorld() {
		canvas.drawWorld();
	}

}
