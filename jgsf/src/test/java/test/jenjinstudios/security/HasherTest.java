package test.jenjinstudios.security;

import com.jenjinstudios.security.Hasher;
import org.junit.Assert;
import org.junit.Test;

/** @author Caleb Brinkman */
public class HasherTest
{
	@Test
	public void testGetFNV1aString() throws Exception
	{
		String sampleString = "The quick brown fox jumps over the lazy dog.";
		String correctHash = "ECAF981A";
		String testHash = Hasher.getFNV1aString(sampleString);
		Assert.assertTrue(correctHash + " == " + testHash, correctHash.equals(testHash));
	}
}
