package com.jenjinstudios.world.json;

import com.google.gson.Gson;
import com.jenjinstudios.world.Location;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author Caleb Brinkman
 */
public class LocationTest
{
	private static final String VALID_LOCATION = "" +
		  "{\n" +
		  "\t\"x\": \"10\",\n" +
		  "\t\"y\": \"10\",\n" +
		  "\t\"properties\": {\n" +
		  "\t\t\"foo\": \"bar\"\n" +
		  "\t}\n" +
		  "}";

	@Test
	public void testDeserialize() {
		Gson gson = new Gson();
		Location location = gson.fromJson(VALID_LOCATION, Location.class);

		Assert.assertEquals(location.getX(), 10);
		Assert.assertEquals(location.getY(), 10);
		Assert.assertEquals(location.getProperties().get("foo"), "bar");
	}
}
