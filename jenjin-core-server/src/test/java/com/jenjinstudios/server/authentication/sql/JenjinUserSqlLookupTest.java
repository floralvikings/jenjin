package com.jenjinstudios.server.authentication.sql;

import com.jenjinstudios.server.authentication.BasicUser;
import com.jenjinstudios.server.authentication.BasicUserFactory;
import com.jenjinstudios.server.database.DatabaseException;
import com.jenjinstudios.server.database.DatabaseLookup;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.sql.ResultSet;

/**
 * Test the UserPropertiesSqlLookup class.
 *
 * @author Caleb Brinkman
 */
public class JenjinUserSqlLookupTest
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
	 * Test the lookup functionality.
	 *
	 * @throws Exception If there's an exception.
	 */
	@Test
	public void testLookupValidUser() throws Exception
	{
		Connection connection = testConnectionFactory.createUserTestConnection();
		DatabaseLookup<BasicUser, ResultSet> lookup = new JenjinUserSqlLookup<>(new BasicUserFactory(), connection);
		BasicUser user = lookup.lookup("TestAccount1");

		Assert.assertNotNull(user, "User should not be null");
	}

	/**
	 * Test a query against an invalid database.
	 *
	 * @throws Exception If there's an exception.
	 */
	@Test(expectedExceptions = DatabaseException.class)
	public void testLookupWithMissingColumn() throws Exception {
		Connection connection = testConnectionFactory.createUserTestConnectionMissingColumn();
		DatabaseLookup<BasicUser, ResultSet> lookup = new JenjinUserSqlLookup<>(new BasicUserFactory(), connection);
		lookup.lookup("TestAccount1");
	}

	/**
	 * Test a query against a databse with duplicate users.
	 *
	 * @throws Exception If there's an exception.
	 */
	@Test(expectedExceptions = DatabaseException.class)
	public void testLookupDuplicateUser() throws Exception {
		Connection connection = testConnectionFactory.createUserTestConnectionDuplicateUser();
		DatabaseLookup<BasicUser, ResultSet> lookup = new JenjinUserSqlLookup<>(new BasicUserFactory(), connection);
		lookup.lookup("TestAccount1");
	}

	/**
	 * Test looking up a nonexistent user.
	 *
	 * @throws Exception If there's an exception.
	 */
	@Test
	public void testLookupFakeUser() throws Exception {
		Connection connection = testConnectionFactory.createUserTestConnection();
		DatabaseLookup<BasicUser, ResultSet> lookup = new JenjinUserSqlLookup<>(new BasicUserFactory(), connection);
		BasicUser user = lookup.lookup("Not a real user");
		Assert.assertNull(user, "User should not exist");
	}

}
