package com.jenjinstudios.server.serialization;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * This class is used by Gson to deserialize a connection string and driver class into a database Connection object.
 * <p>
 * The expected JSON format is:
 * <p>
 * {@code
 * {
 * "driver":"some.jdbc.driver",
 * "connectionString":"some://connection:string/here
 * }
 * }
 *
 * @author Caleb Brinkman
 */
public class ConnectionDeserializer implements JsonDeserializer<Connection>
{
    @Override
    public Connection deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
          throws JsonParseException
    {
        JsonObject jsonObject = json.getAsJsonObject();

        if (!jsonObject.has("driver")) {
            throw new JsonParseException("Expected \"driver\"");
        }
        if (!jsonObject.has("connectionString")) {
            throw new JsonParseException("Expected \"connectionString\"");
        }

        String driverClassName = jsonObject.get("driver").getAsString();
        String connectionString = jsonObject.get("connectionString").getAsString();

        // Load the driver
        try {
            Class.forName(driverClassName);
        } catch (ClassNotFoundException e) {
            throw new JsonParseException("Driver class not found", e);
        }

        try {
            return DriverManager.getConnection(connectionString);
        } catch (SQLException e) {
            throw new JsonParseException("Unable to establish database connection when deserializing", e);
        }
    }
}
