package com.jenjinstudios.world.math;

import com.jenjinstudios.world.Location;
import com.jenjinstudios.world.Zone;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Properties;

/**
 * @author Caleb Brinkman
 */
public class FieldOfVisionCalculatorTest
{
	private final Location[] expectedVisible = {
			new Location(25, 25), new Location(24, 24),	new Location(25, 24), new Location(23, 23),
			new Location(24, 23), new Location(25, 23), new Location(22, 22), new Location(23, 22),
			new Location(24, 22), new Location(25, 22), new Location(24, 21), new Location(25, 21),
			new Location(25, 20), new Location(26, 24), new Location(25, 24), new Location(27, 23),
			new Location(26, 23), new Location(25, 23), new Location(28, 22), new Location(27, 22),
			new Location(26, 22), new Location(25, 22), new Location(28, 21), new Location(27, 21),
			new Location(26, 21), new Location(25, 21), new Location(25, 20), new Location(26, 24),
			new Location(26, 25), new Location(27, 23), new Location(27, 24), new Location(27, 25),
			new Location(28, 22), new Location(28, 23), new Location(28, 24), new Location(28, 25),
			new Location(29, 22), new Location(29, 23), new Location(29, 24), new Location(29, 25),
			new Location(30, 25), new Location(26, 26), new Location(26, 25), new Location(27, 27),
			new Location(27, 26), new Location(27, 25), new Location(28, 28), new Location(28, 27),
			new Location(28, 26), new Location(28, 25), new Location(29, 28), new Location(29, 27),
			new Location(29, 26), new Location(29, 25), new Location(30, 25), new Location(26, 26),
			new Location(25, 26), new Location(27, 27), new Location(26, 27), new Location(25, 27),
			new Location(28, 28), new Location(27, 28), new Location(26, 28), new Location(25, 28),
			new Location(28, 29), new Location(27, 29), new Location(26, 29), new Location(25, 29),
			new Location(25, 30), new Location(24, 26), new Location(25, 26), new Location(23, 27),
			new Location(24, 27), new Location(25, 27), new Location(22, 28), new Location(23, 28),
			new Location(24, 28), new Location(25, 28), new Location(22, 29), new Location(23, 29),
			new Location(24, 29), new Location(25, 29), new Location(25, 30), new Location(24, 26),
			new Location(24, 25), new Location(23, 27), new Location(23, 26), new Location(23, 25),
			new Location(22, 28), new Location(22, 27), new Location(22, 26), new Location(22, 25),
			new Location(21, 28), new Location(21, 27), new Location(21, 26), new Location(21, 25),
			new Location(20, 25), new Location(24, 24), new Location(24, 25), new Location(23, 23),
			new Location(23, 24), new Location(23, 25), new Location(22, 22), new Location(22, 23),
			new Location(22, 24), new Location(22, 25), new Location(21, 22), new Location(21, 23),
			new Location(21, 24), new Location(21, 25), new Location(20, 25)
	};

	@Test
	public void testScan() {
		Properties props = new Properties();
		props.setProperty("blocksVision", "true");
		Location[] locations = new Location[]{
				new Location(23, 22, props),
				new Location(24, 19, props),
				new Location(27, 31, props)
		};
		Zone zone = new Zone(0, new Dimension2D(50, 50), locations);

		FieldOfVisionCalculator calculator = new FieldOfVisionCalculator(zone, zone.getLocationOnGrid(25, 25), 5);
		List<Location> visible = calculator.scan();
		Location[] visibleArray = new Location[visible.size()];
		visible.toArray(visibleArray);
		Assert.assertEquals(visibleArray, expectedVisible);
	}
}
