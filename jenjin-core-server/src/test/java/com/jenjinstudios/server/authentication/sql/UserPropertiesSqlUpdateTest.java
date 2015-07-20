package com.jenjinstudios.server.authentication.sql;

import com.jenjinstudios.server.database.DatabaseException;
import com.jenjinstudios.server.database.DatabaseLookup;
import com.jenjinstudios.server.database.DatabaseUpdate;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Collections;
import java.util.Map;

/**
 * Test the UserPropertiesSqlUpdate class.
 *
 * @author Caleb Brinkman
 */
public class UserPropertiesSqlUpdateTest
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
	 * Test the update functionality.
	 *
	 * @throws Exception If there's an exception.
	 */
	@Test
	public void testUpdate() throws Exception {
		Connection connection = testConnectionFactory.createPropertiesTestConnection();
		Map<String, String> updatedProperty = Collections.singletonMap("property1", "newValue");
		DatabaseUpdate<Map<String, String>> update = new UserPropertiesSqlUpdate(connection);
		boolean changed = update.update(updatedProperty, "TestAccount1");
		Assert.assertTrue(changed, "Property should have been changed.");

		DatabaseLookup<Map<String, String>, ResultSet> lookup = new UserPropertiesSqlLookup(connection);
		Map<String, String> lookupProperties = lookup.lookup("TestAccount1");
		Assert.assertEquals(lookupProperties.get("property1"), "newValue", "Values should be equal");
	}

	/**
	 * Test updating a custom property which is not in the database.
	 *
	 * @throws Exception If there's an exception.
	 */
	@Test
	public void testUpdatePropertyNotInDatabase() throws Exception {
		Connection connection = testConnectionFactory.createPropertiesTestConnection();
		Map<String, String> updatedProperty = Collections.singletonMap("nonexisentProperty", "newValue");
		DatabaseUpdate<Map<String, String>> update = new UserPropertiesSqlUpdate(connection);
		boolean changed = update.update(updatedProperty, "TestAccount1");
		Assert.assertFalse(changed, "Since property doesn't exist, change should not have been made");
	}

	/**
	 * Test updating properties without setting a username first.
	 *
	 * @throws Exception If there's an exception.
	 */
	@Test(expectedExceptions = DatabaseException.class)
	public void testUpdateNoUsername() throws Exception {
		Connection connection = testConnectionFactory.createPropertiesTestConnection();
		Map<String, String> updatedProperty = Collections.singletonMap("property1", "newValue");
		DatabaseUpdate<Map<String, String>> update = new UserPropertiesSqlUpdate(connection);
		update.update(updatedProperty);
	}

	/**
	 * Test updating properties when a column is missing from the database.
	 *
	 * @throws Exception If there's an exception
	 */
	@Test(expectedExceptions = DatabaseException.class)
	public void testUpdateMissingColumn() throws Exception {
		Connection connection = testConnectionFactory.createPropertiesTestConnectionInvalid();
		Map<String, String> updatedProperty = Collections.singletonMap("property1", "newValue");
		DatabaseUpdate<Map<String, String>> update = new UserPropertiesSqlUpdate(connection);
		update.update(updatedProperty);
	}
}
