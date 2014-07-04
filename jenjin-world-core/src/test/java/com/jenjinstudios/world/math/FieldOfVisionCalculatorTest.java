package com.jenjinstudios.world.math;

import com.jenjinstudios.world.Location;
import com.jenjinstudios.world.Zone;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Properties;

/**
 * @author Caleb Brinkman
 */
public class FieldOfVisionCalculatorTest
{
	private Location[] expectedVisible;
	private Properties props;

	@BeforeClass
	public void setUp() {
		props = new Properties();
		props.setProperty("blocksVision", "true");
		expectedVisible = new Location[]{
			new Location(25, 25), new Location(24, 24), new Location(25, 24), new Location(23, 23),
			new Location(24, 23, props), new Location(22, 22), new Location(21, 21), new Location(25, 23),
			new Location(25, 22), new Location(25, 21), new Location(24, 20), new Location(25, 20),
			new Location(24, 19), new Location(25, 19), new Location(25, 18), new Location(26, 24),
			new Location(25, 24), new Location(27, 23), new Location(26, 23, props), new Location(28, 22),
			new Location(29, 21), new Location(25, 23), new Location(25, 22), new Location(25, 21),
			new Location(26, 20), new Location(25, 20), new Location(26, 19), new Location(25, 19),
			new Location(25, 18), new Location(26, 24), new Location(26, 25), new Location(27, 23),
			new Location(27, 24, props), new Location(28, 22), new Location(29, 21), new Location(27, 25),
			new Location(28, 25), new Location(29, 24), new Location(29, 25), new Location(30, 24),
			new Location(30, 25), new Location(31, 24), new Location(31, 25), new Location(32, 25),
			new Location(26, 26), new Location(26, 25), new Location(27, 27, props), new Location(27, 26, props),
			new Location(27, 25), new Location(28, 25), new Location(29, 25), new Location(30, 26),
			new Location(30, 25), new Location(31, 26), new Location(31, 25), new Location(32, 25),
			new Location(26, 26), new Location(25, 26), new Location(27, 27, props), new Location(26, 27, props),
			new Location(25, 27), new Location(25, 28), new Location(25, 29), new Location(26, 30),
			new Location(25, 30), new Location(26, 31), new Location(25, 31), new Location(25, 32),
			new Location(24, 26), new Location(25, 26), new Location(23, 27), new Location(24, 27, props),
			new Location(22, 28), new Location(21, 29), new Location(25, 27), new Location(25, 28),
			new Location(24, 29), new Location(25, 29), new Location(24, 30), new Location(25, 30),
			new Location(24, 31), new Location(25, 31), new Location(25, 32), new Location(24, 26),
			new Location(24, 25), new Location(23, 27), new Location(23, 26, props), new Location(22, 28),
			new Location(21, 29), new Location(23, 25), new Location(22, 25), new Location(21, 25),
			new Location(20, 26), new Location(20, 25), new Location(19, 26), new Location(19, 25),
			new Location(18, 25), new Location(24, 24), new Location(24, 25), new Location(23, 23),
			new Location(23, 24, props), new Location(22, 22), new Location(21, 21), new Location(23, 25),
			new Location(22, 25), new Location(21, 25), new Location(20, 24), new Location(20, 25),
			new Location(19, 24), new Location(19, 25), new Location(18, 25)
		};
	}

	@Test
	public void testScan() {
		// Test a blocker in each octant
		Location[] locations = new Location[]{
			new Location(24, 23, props),
			new Location(24, 27, props),
			new Location(23, 26, props),
			new Location(23, 24, props),
			new Location(26, 23, props),
			new Location(26, 27, props),
			new Location(27, 24, props),
			new Location(27, 26, props),
			new Location(27, 27, props) // And one that's adjacent to another
		};
		Zone zone = new Zone(0, new Dimension2D(50, 50), locations);

		FieldOfVisionCalculator calculator = new FieldOfVisionCalculator(zone, zone.getLocationOnGrid(25, 25), 7);
		List<Location> visible = calculator.scan();
		System.out.println(visible);
		Location[] visibleArray = new Location[visible.size()];
		visible.toArray(visibleArray);
		Assert.assertEquals(visibleArray, expectedVisible);
	}
}
