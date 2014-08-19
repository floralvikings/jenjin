package com.jenjinstudios.demo.client.ui;

import com.jenjinstudios.world.client.WorldClient;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Dimension2D;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;

/**
 * @author Caleb Brinkman
 */
public class WorldPane extends GridPane
{
	public WorldPane(WorldClient worldClient, Dimension2D size) {
		PlayerViewCanvas canvas = new PlayerViewCanvas(worldClient.getPlayer(), size.getWidth(), size.getHeight());
		PlayerControlKeyHandler playerControlKeyHandler = new PlayerControlKeyHandler(worldClient);
		canvas.setOnKeyPressed(playerControlKeyHandler);
		canvas.setOnKeyReleased(playerControlKeyHandler);
		add(canvas, 0, 0);

		final Duration oneFrameAmt = Duration.millis(1000 / (float) 60);
		final KeyFrame oneFrame = new KeyFrame(oneFrameAmt, event -> canvas.drawWorld());
		Timeline timeline = new Timeline(oneFrame);
		timeline.setCycleCount(Timeline.INDEFINITE);
		timeline.play();
	}
}
