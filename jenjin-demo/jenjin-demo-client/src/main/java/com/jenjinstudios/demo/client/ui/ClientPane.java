package com.jenjinstudios.demo.client.ui;

import com.jenjinstudios.world.client.WorldClient;
import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.geometry.Dimension2D;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;

/**
 * @author Caleb Brinkman
 */
public class ClientPane extends GridPane
{
	public ClientPane(WorldClient worldClient, Dimension2D size) {
		PlayerViewCanvas canvas = new PlayerViewCanvas(worldClient, size.getWidth(), size.getHeight());
		EventHandler<KeyEvent> playerControlKeyHandler = new PlayerControlKeyHandler(worldClient);
		canvas.setOnKeyPressed(playerControlKeyHandler);
		canvas.setOnKeyReleased(playerControlKeyHandler);
		add(canvas, 0, 0);

		AnimationTimer animationTimer = new WorldDrawTimer(canvas);

		animationTimer.start();
	}

	private static class WorldDrawTimer extends AnimationTimer
	{
		private final PlayerViewCanvas canvas;

		private WorldDrawTimer(PlayerViewCanvas canvas) {this.canvas = canvas;}

		@Override
		public void handle(long now) {
			canvas.drawWorld();
		}
	}
}
