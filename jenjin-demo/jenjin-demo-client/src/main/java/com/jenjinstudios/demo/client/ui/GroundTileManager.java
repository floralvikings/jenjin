package com.jenjinstudios.demo.client.ui;

import com.jenjinstudios.world.Location;
import javafx.scene.image.Image;

import java.io.InputStream;

/**
 * @author Caleb Brinkman
 */
public class GroundTileManager
{
	private final Image[][] tiles;

	public GroundTileManager() {
		tiles = new Image[10][10];
		for (int x = 0; x < tiles.length; x++)
		{
			fillColumnTiles(x);
		}
	}

	private void fillColumnTiles(int x) {
		for (int y = 0; y < tiles[x].length; y++)
		{
			String imageName = "com/jenjinstudios/demo/client/images/ground/ground" + (x + 1) + (y + 1) + ".jpg";
			InputStream inputStream = getClass().getClassLoader().getResourceAsStream(imageName);
			tiles[x][y] = new Image(inputStream);
		}
	}

	public Image getTileForLocation(Location loc) {
		int x = loc.X_COORDINATE % tiles.length;
		int y = tiles[x].length - (loc.Y_COORDINATE % tiles[x].length) - 1;
		return tiles[y][x];
	}
}
