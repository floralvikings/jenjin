package com.jenjinstudios.world.json;

import com.google.gson.Gson;
import com.jenjinstudios.world.Location;
import com.jenjinstudios.world.Zone;
import com.jenjinstudios.world.util.ZoneUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author Caleb Brinkman
 */
public class ZoneTest
{
	private static final String VALID_ZONE = "" +
		  "{\n" +
		  "\t\"id\": \"123\",\n" +
		  "\t\"ySize\": \"200\",\n" +
		  "\t\"xSize\": \"100\",\n" +
		  "\t\"locationGrid\": [\n" +
		  "\t\t{\n" +
		  "\t\t\t\"x\": \"10\",\n" +
		  "\t\t\t\"y\": \"10\",\n" +
		  "\t\t\t\"properties\": {\n" +
		  "\t\t\t\t\"foo\": \"bar\"\n" +
		  "\t\t\t}\n" +
		  "\t\t}\n" +
		  "\t]\n" +
		  "}";

	@Test
	public void testDeserialize() throws Exception {
		Gson gson = new Gson();
		Zone zone = gson.fromJson(VALID_ZONE, Zone.class);

		Assert.assertEquals(zone.getId(), 123);
		Assert.assertEquals(zone.getXSize(), 100);
		Assert.assertEquals(zone.getYSize(), 200);

		Location location = ZoneUtils.getLocationOnGrid(zone, 10, 10);

		Assert.assertEquals(location.getX(), 10);
		Assert.assertEquals(location.getY(), 10);
		Assert.assertEquals(location.getProperties().get("foo"), "bar");
	}
}
