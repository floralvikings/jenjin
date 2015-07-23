package com.jenjinstudios.server.serialization;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.jenjinstudios.server.authentication.User;
import com.jenjinstudios.server.authentication.UserFactory;
import com.jenjinstudios.server.database.DatabaseLookup;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import static org.mockito.Mockito.*;

/**
 * Used to test the DatabaseLookupDeserializer.
 *
 * @author Caleb Brinkman
 */
public class DatabaseLookupDeserializerTest
{
    /**
     * Test the deserialize method.
     *
     * @throws Exception If there is an unexpected exception.
     */
    @Test
    public void testDeserialize() throws Exception {
        // Have to do a lot of mocking to get this right; this is due to the caching of result sets so that they can
        // be properly closed before deserialized.
        Connection connection = mock(Connection.class);
        UserFactory userFactory = mock(UserFactory.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet resultSet = mock(ResultSet.class);
        ResultSetMetaData metaData = mock(ResultSetMetaData.class);
        JsonDeserializer<Connection> connectionDeserializer = mock(JsonDeserializer.class);
        JsonDeserializer<UserFactory> userFactoryDeserializer = mock(JsonDeserializer.class);

        when(connectionDeserializer.deserialize(any(), any(), any())).thenReturn(connection);
        when(userFactoryDeserializer.deserialize(any(), any(), any())).thenReturn(userFactory);
        when(connection.prepareStatement(any())).thenReturn(statement);
        when(statement.executeQuery()).thenReturn(resultSet);
        when(resultSet.getMetaData()).thenReturn(metaData);
        when(metaData.getColumnCount()).thenReturn(1);

        DatabaseLookupDeserializer databaseLookupDeserializer = new DatabaseLookupDeserializer();

        Gson gson = new GsonBuilder()
              .registerTypeAdapter(Connection.class, connectionDeserializer)
              .registerTypeAdapter(UserFactory.class, userFactoryDeserializer)
              .registerTypeAdapter(DatabaseLookup.class, databaseLookupDeserializer)
              .create();

        String testJson = "{\n" +
              "\"class\":\"com.jenjinstudios.server.authentication.sql.JenjinUserSqlLookup\",\n" +
              "\"fields\":{\n" +
              "\"connection\":\"foo\",\n" +
              "\"userFactory\":\"bar\"\n" +
              "}\n" +
              "}\n";
        DatabaseLookup<User, ResultSet> databaseLookup = gson.fromJson(testJson, DatabaseLookup.class);

        User user = databaseLookup.lookup("foo");
        Assert.assertNull(user, "Due to mocking, user should be null.");
        verify(connection).prepareStatement(anyString());
    }
}
