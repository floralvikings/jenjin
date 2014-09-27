package com.jenjinstudios.world.json;

import com.google.gson.Gson;
import com.jenjinstudios.world.World;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author Caleb Brinkman
 */
public class WorldTest
{
	private static final String VALID_WORLD = "" +
		  "{\n" +
		  "\t\"zones\": {\n" +
		  "\t\t\"123\": {\n" +
		  "\t\t\t\"id\": \"123\",\n" +
		  "\t\t\t\"ySize\": \"200\",\n" +
		  "\t\t\t\"xSize\": \"100\",\n" +
		  "\t\t\t\"locationGrid\": [\n" +
		  "\t\t\t\t{\n" +
		  "\t\t\t\t\t\"x\": \"10\",\n" +
		  "\t\t\t\t\t\"y\": \"10\",\n" +
		  "\t\t\t\t\t\"properties\": {\n" +
		  "\t\t\t\t\t\t\"foo\": \"bar\"\n" +
		  "\t\t\t\t\t}\n" +
		  "\t\t\t\t}\n" +
		  "\t\t\t]\n" +
		  "\t\t}\n" +
		  "\t}\n" +
		  "}";

	@Test
	public void testDeserialize() {
		Gson gson = new Gson();
		World world = gson.fromJson(VALID_WORLD, World.class);

		Assert.assertEquals(world.getZones().get(123).getXSize(), 100);
	}
}
