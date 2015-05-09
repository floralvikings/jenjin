package com.jenjinstudios.server.authentication.sql;

import com.jenjinstudios.server.database.DatabaseException;
import com.jenjinstudios.server.database.DatabaseLookup;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.util.Map;

/**
 * Test the UserPropertiesLookup class.
 *
 * @author Caleb Brinkman
 */
public class UserPropertiesSqlLookupTest
{
	private final TestConnectionFactory testConnectionFactory = new TestConnectionFactory();

	/**
	 * Close all test connections after testing.
	 *
	 * @throws Exception If there's an exception.
	 */
	@AfterClass
	public void closeTestConnections() throws Exception {
		testConnectionFactory.closeAll();
	}

	/**
	 * Test the user properties lookup functionality.
	 *
	 * @throws Exception If there's an exception.
	 */
	@Test
	public void testLookupUserProperties() throws Exception {
		Connection connection = testConnectionFactory.createPropertiesTestConnection();
		String username = "TestAccount1";

		DatabaseLookup<Map<String, String>> lookup = new UserPropertiesSqlLookup(connection);
		Map<String, String> properties = lookup.lookup(username);
		Assert.assertEquals(properties.size(), 2, "Two properties should exist");
		String property1 = properties.get("property1");
		Assert.assertEquals(Integer.parseInt(property1), 1, "Property should be equal to one");
	}

	/**
	 * Test the user properties lookup functionality with an incomplete database.
	 *
	 * @throws Exception If there's an exception.
	 */
	@Test(expectedExceptions = DatabaseException.class)
	public void testLookupUserPropertiesInvalidData() throws Exception {
		Connection connection = testConnectionFactory.createPropertiesTestConnectionInvalid();
		String username = "TestAccount1";

		DatabaseLookup<Map<String, String>> lookup = new UserPropertiesSqlLookup(connection);
		lookup.lookup(username);
	}

	/**
	 * Test the user properties lookup functionality for a nonexistent username.
	 *
	 * @throws Exception If there's an exception.
	 */
	@Test
	public void testLookupUserPropertiesInvlalidUser() throws Exception {
		Connection connection = testConnectionFactory.createPropertiesTestConnection();
		String username = "Fake account.";

		DatabaseLookup<Map<String, String>> lookup = new UserPropertiesSqlLookup(connection);
		Map<String, String> properties = lookup.lookup(username);
		Assert.assertNull(properties, "Should not have found any properties.");
	}
}
