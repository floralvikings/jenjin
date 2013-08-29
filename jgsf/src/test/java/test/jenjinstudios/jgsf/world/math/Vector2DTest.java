package test.jenjinstudios.jgsf.world.math;

import com.jenjinstudios.jgcf.world.math.Vector2D;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test the coordinates class.
 *
 * @author Caleb Brinkman
 */
public class Vector2DTest
{
	/** Test the coordinates methods. */
	@Test
	public void testVector2D()
	{
		Vector2D vector2D01 = new Vector2D(5, 7);
		Assert.assertEquals(7, vector2D01.getZCoordinate(), 0);
		Assert.assertEquals(5, vector2D01.getXCoordinate(), 0);

		Vector2D vector2D02 = new Vector2D(vector2D01);
		Assert.assertEquals(7, vector2D02.getZCoordinate(), 0);
		Assert.assertEquals(5, vector2D02.getXCoordinate(), 0);

		vector2D02.setXCoordinate(2);
		vector2D02.setZCoordinate(3);

		Assert.assertEquals(2, vector2D02.getXCoordinate(), 0);
		Assert.assertEquals(3, vector2D02.getZCoordinate(), 0);
	}

	/** Test the direction math methods. */
	@Test
	public void testDirectionMath()
	{
		Vector2D original = new Vector2D(5, 5);

		double right = 0;
		Vector2D stepped = original.getVectorInDirection(1, right);
		Assert.assertEquals(6, stepped.getXCoordinate(), 0.001);
		Assert.assertEquals(5, stepped.getZCoordinate(), 0.001);

		double left = Math.PI;
		stepped = original.getVectorInDirection(1, left);
		Assert.assertEquals(4, stepped.getXCoordinate(), 0.001);
		Assert.assertEquals(5, stepped.getZCoordinate(), 0.001);

		double back = Math.PI * 1.5;
		stepped = original.getVectorInDirection(1, back);
		Assert.assertEquals(5, stepped.getXCoordinate(), 0.001);
		Assert.assertEquals(4, stepped.getZCoordinate(), 0.001);

		double forward = Math.PI * .5;
		stepped = original.getVectorInDirection(1, forward);
		Assert.assertEquals(5, stepped.getXCoordinate(), 0.001);
		Assert.assertEquals(6, stepped.getZCoordinate(), 0.001);

		double forwardRight = Math.PI * .25;
		double expectedX = 5 + Math.sqrt(2) / 2;
		double expectedZ = 5 + Math.sqrt(2) / 2;
		stepped = original.getVectorInDirection(1, forwardRight);
		Assert.assertEquals(expectedX, stepped.getXCoordinate(), 0.001);
		Assert.assertEquals(expectedZ, stepped.getZCoordinate(), 0.001);

		double backRight = Math.PI * -.25;
		expectedX = 5 + Math.sqrt(2) / 2;
		expectedZ = 5 - Math.sqrt(2) / 2;
		stepped = original.getVectorInDirection(1, backRight);
		Assert.assertEquals(expectedX, stepped.getXCoordinate(), 0.001);
		Assert.assertEquals(expectedZ, stepped.getZCoordinate(), 0.001);

		double backLeft = Math.PI * 1.25;
		expectedX = 5 - Math.sqrt(2) / 2;
		expectedZ = 5 - Math.sqrt(2) / 2;
		stepped = original.getVectorInDirection(1, backLeft);
		Assert.assertEquals(expectedX, stepped.getXCoordinate(), 0.001);
		Assert.assertEquals(expectedZ, stepped.getZCoordinate(), 0.001);
	}

}
