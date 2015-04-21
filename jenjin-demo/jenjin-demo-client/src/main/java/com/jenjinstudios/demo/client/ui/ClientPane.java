package com.jenjinstudios.demo.client.ui;

import com.jenjinstudios.world.client.WorldClient;
import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.geometry.Dimension2D;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;

/**
 * Pane responsible for rendering the world client.
 *
 * @author Caleb Brinkman
 */
public class ClientPane extends GridPane
{
	/**
	 * Construct a new ClientPane rendering the world for the given WorldClient and with the given size.
	 *
	 * @param worldClient The WorldClient containing the world and player to render.
	 * @param size The size of the client pane.
	 */
	public ClientPane(WorldClient worldClient, Dimension2D size) {
		PlayerViewCanvas canvas = new PlayerViewCanvas(worldClient, size.getWidth(), size.getHeight());
		EventHandler<KeyEvent> controlHandler = new PlayerControlKeyHandler(worldClient);
		canvas.setOnKeyPressed(controlHandler);
		canvas.setOnKeyReleased(controlHandler);
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
