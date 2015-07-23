package com.jenjinstudios.server.serialization;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.jenjinstudios.server.authentication.BasicUser;
import com.jenjinstudios.server.database.DatabaseUpdate;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertTrue;

/**
 * Test the DatabaseUpdateDeserializer class.
 *
 * @author Caleb Brinkman
 */
public class DatabaseUpdateDeserializerTest
{

    /**
     * Test the deserialization.
     *
     * @throws Exception If there's an exception.
     */
    @Test
    public void testDeserialize() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        JsonDeserializer<Connection> connectionDeserializer = mock(JsonDeserializer.class);
        BasicUser basicUser = mock(BasicUser.class);

        when(connectionDeserializer.deserialize(any(), any(), any())).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeUpdate()).thenReturn(1);

        DatabaseUpdateDeserializer<BasicUser> dbUpdateDeserializer = new DatabaseUpdateDeserializer<>();
        Gson gson = new GsonBuilder()
              .registerTypeAdapter(Connection.class, connectionDeserializer)
              .registerTypeAdapter(DatabaseUpdate.class, dbUpdateDeserializer)
              .create();

        String testJson = "{\n" +
              "\"class\":\"com.jenjinstudios.server.authentication.sql.JenjinUserSqlUpdate\",\n" +
              "\"fields\":{\n" +
              "\"connection\":\"foo\"\n" +
              "}\n" +
              "}\n";
        DatabaseUpdate<BasicUser> databaseUpdate = gson.fromJson(testJson, DatabaseUpdate.class);

        boolean update = databaseUpdate.update(basicUser);

        assertTrue(update, "Update will be true if correctly deserialized");
    }
}