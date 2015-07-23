package com.jenjinstudios.server.net;

import com.jenjinstudios.server.serialization.ServerConfigReader;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;

/**
 * Test reading the ServerConfig class.
 *
 * @author Caleb Brinkman
 */
public class ServerConfigReaderTest
{
    /**
     * Test the read method.
     *
     * @throws Exception If there's an exception.
     */
    @Test
    public void testRead() throws Exception {
        String testJson = "{\n" +
              "\t\"ups\": 50,\n" +
              "\t\"port\": 1234,\n" +
              "\t\"contextClass\": \"com.jenjinstudios.server.net.ServerMessageContext\",\n" +
              "\t\"updateTasks\": [ ],\n" +
              "\t\"shutdownTasks\":[\n" +
              "\t\t\"com.jenjinstudios.server.net.EmergencyLogoutTask\"\n" +
              "\t],\n" +
              "\t\"connectionAddedTasks\": [ ],\n" +
              "\t\"authenticator\": {\n" +
              "\t\t\"databaseLookup\": {\n" +
              "\t\t\t\"class\":\"com.jenjinstudios.server.authentication.sql.JenjinUserSqlLookup\",\n" +
              "\t\t\t\"fields\": {\n" +
              "\t\t\t\t\"connection\": {\n" +
              "\t\t\t\t\t\"driver\":\"java.lang.String\",\n" +
              "\t\t\t\t\t\"connectionString\":\"foo\"\n" +
              "\t\t\t\t},\n" +
              "\t\t\t\t\"userFactory\": {\n" +
              "\t\t\t\t\t\"class\":\"com.jenjinstudios.server.authentication.BasicUserFactory\"\t\n" +
              "\t\t\t\t}\n" +
              "\t\t\t}\n" +
              "\t\t},\n" +
              "\t\t\"databaseUpdate\": {\n" +
              "\t\t\t\"class\":\"com.jenjinstudios.server.authentication.sql.JenjinUserSqlUpdate\",\n" +
              "\t\t\t\"fields\": {\n" +
              "\t\t\t\t\"connection\": {\n" +
              "\t\t\t\t\t\"driver\":\"java.lang.String\",\n" +
              "\t\t\t\t\t\"connectionString\":\"foo\"\n" +
              "\t\t\t\t}\n" +
              "\t\t\t}\n" +
              "\t\t}\n" +
              "\t}\n" +
              '}';
        ByteArrayInputStream inputStream = new ByteArrayInputStream(testJson.getBytes());
        ServerConfigReader reader = new ServerConfigReader(inputStream);
        ServerConfig serverConfig = reader.read();
        Assert.assertFalse(serverConfig.getShutdownTasks().isEmpty(), "Tasks should not be empty");
    }
}
