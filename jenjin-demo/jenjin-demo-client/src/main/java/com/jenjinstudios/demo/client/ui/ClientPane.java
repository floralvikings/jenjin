package com.jenjinstudios.demo.client.ui;

import com.jenjinstudios.world.client.WorldClient;
import javafx.animation.AnimationTimer;
import javafx.geometry.Dimension2D;
import javafx.scene.layout.GridPane;

/**
 * @author Caleb Brinkman
 */
public class ClientPane extends GridPane
{
	public ClientPane(WorldClient worldClient, Dimension2D size) {
		PlayerViewCanvas canvas = new PlayerViewCanvas(worldClient.getPlayer(), size.getWidth(), size.getHeight());
		PlayerControlKeyHandler playerControlKeyHandler = new PlayerControlKeyHandler(worldClient);
		canvas.setOnKeyPressed(playerControlKeyHandler);
		canvas.setOnKeyReleased(playerControlKeyHandler);
		add(canvas, 0, 0);

		AnimationTimer animationTimer = new AnimationTimer()
		{
			@Override
			public void handle(long now) {
				canvas.drawWorld();
			}
		};

		animationTimer.start();
	}
}
