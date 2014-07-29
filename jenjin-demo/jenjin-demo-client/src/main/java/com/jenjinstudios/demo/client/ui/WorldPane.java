package com.jenjinstudios.demo.client.ui;

import com.jenjinstudios.demo.client.DemoWorldClient;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Dimension2D;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.util.Duration;

/**
 * @author Caleb Brinkman
 */
public class WorldPane extends GridPane
{
	public WorldPane(DemoWorldClient worldClient, Dimension2D size) {
		WorldCanvas canvas = new WorldCanvas(worldClient, size.getWidth(), size.getHeight() - 48);
		add(canvas, 0, 0);
		Font font = new Font("Courier", 24);
		Label highScoreLabel = new Label("High Score: ");
		highScoreLabel.setFont(font);
		add(highScoreLabel, 0, 1);

		final Duration oneFrameAmt = Duration.millis(1000 / (float) 60);
		final KeyFrame oneFrame = new KeyFrame(oneFrameAmt, event -> canvas.drawWorld());
		Timeline timeline = new Timeline(oneFrame);
		timeline.setCycleCount(Timeline.INDEFINITE);
		timeline.play();
	}
}
