package com.jenjinstudios.server.serialization;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.sql.DriverManager;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.testng.Assert.assertEquals;

/**
 * Tests the ConnectionDeserializer class.
 *
 * @author Caleb Brinkman
 */
@PrepareForTest(ConnectionDeserializer.class)
public class ConnectionDeserializerTest extends PowerMockTestCase
{
    /**
     * Test the deserialize functionality.
     *
     * @throws Exception If there's an exception.
     */
    @Test
    public void testDeserialize() throws Exception {
        Connection connection = mock(Connection.class);
        mockStatic(DriverManager.class);
        when(DriverManager.getConnection(anyString())).thenReturn(connection);
        ConnectionDeserializer connectionDeserializer = new ConnectionDeserializer();
        Gson gson = new GsonBuilder()
              .registerTypeAdapter(Connection.class, connectionDeserializer)
              .create();

        String testJson = "{\n" +
              "\"driver\":\"java.lang.String\",\n" + // Just a dummy so we don't get a ClassNotFoundException
              "\"connectionString\":\"jdbc:foo://connection.string:12345;stuff=here;something=else\"" +
              "}\n";
        Connection deserialized = gson.fromJson(testJson, Connection.class);

        assertEquals(deserialized, connection, "Connections should be the same object");
    }
}