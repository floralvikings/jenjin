package com.jenjinstudios.server.authentication.sql;

import com.jenjinstudios.server.authentication.BasicUser;
import com.jenjinstudios.server.authentication.BasicUserFactory;
import com.jenjinstudios.server.database.DatabaseException;
import com.jenjinstudios.server.database.DatabaseLookup;
import com.jenjinstudios.server.database.DatabaseUpdate;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.sql.ResultSet;

/**
 * Test the BasicUserSqlUpdate class.
 *
 * @author Caleb Brinkman
 */
public class JenjinUserSqlUpdateTest
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
	 * Test the update functionlity.
	 *
	 * @throws Exception If there's an exception.
	 */
	@Test
	public void testUpdateExistingProperties() throws Exception {
		Connection connection = testConnectionFactory.createUserTestConnection();
		DatabaseUpdate<BasicUser> update = new JenjinUserSqlUpdate<>(connection);
		BasicUser user = new BasicUser("TestAccount1");
		user.setPassword("password");
		user.setSalt("salt");
		user.setLoggedIn(true);
		boolean changed = update.update(user);
		Assert.assertTrue(changed, "Database should have changed.");

		DatabaseLookup<BasicUser, ResultSet> lookup = new JenjinUserSqlLookup<>(new BasicUserFactory(), connection);
		BasicUser lookupUser = lookup.lookup("TestAccount1");

		Assert.assertTrue(lookupUser.isLoggedIn(), "User should be logged in.");
	}

	/**
	 * Test the update functionlity when not all properties are set; specifically, properties that cannot be null.
	 *
	 * @throws Exception If there's an exception.
	 */
	@Test(expectedExceptions = DatabaseException.class)
	public void testUpdateNotAllPropertiesSet() throws Exception {
		Connection connection = testConnectionFactory.createUserTestConnection();
		DatabaseUpdate<BasicUser> update = new JenjinUserSqlUpdate<>(connection);
		BasicUser user = new BasicUser("TestAccount1");
		user.setPassword("password");
		update.update(user);
	}
}
