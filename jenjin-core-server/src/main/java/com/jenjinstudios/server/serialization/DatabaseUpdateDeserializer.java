package com.jenjinstudios.server.serialization;

import com.google.gson.*;
import com.jenjinstudios.server.database.DatabaseUpdate;

import java.lang.reflect.Type;

/**
 * This class uses a custom serialization scheme to deserialize objects which implement the DatabaseUpdate interface.
 * <p>
 * The expected JSON format is:
 * <p>
 * {@code
 * {
 * "class":"some.class.implementing.DatabaseUpdate",
 * "fields": {
 * // fields used to populate object, standard JSON for that object.
 * }
 * }
 * }
 *
 * @author Caleb Brinkman
 */
public class DatabaseUpdateDeserializer<T> implements JsonDeserializer<DatabaseUpdate<T>>
{
    @Override
    public DatabaseUpdate<T> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
          throws JsonParseException
    {
        JsonObject jsonObject = json.getAsJsonObject();
        JsonElement lookupClassElement = jsonObject.get("class");
        if (lookupClassElement == null) {
            throw new JsonParseException("DatabaseUpdate must specify class to be deserialized into");
        }
        String className = lookupClassElement.getAsString();
        Class<DatabaseUpdate<T>> lookupClass;
        try {
            lookupClass = (Class<DatabaseUpdate<T>>) Class.forName(className);
        } catch (ClassNotFoundException | ClassCastException e) {
            throw new JsonParseException("DatabaseUpdate implementation not found or incorrect: ", e);
        }

        if (!DatabaseUpdate.class.isAssignableFrom(lookupClass)) {
            throw new JsonParseException(className + " does not implement DatabaseUpdate");
        }

        JsonElement fieldsElement = jsonObject.get("fields");
        return (fieldsElement != null)
              ? context.deserialize(fieldsElement, lookupClass)
              : context.deserialize(new JsonObject(), lookupClass);
    }
}
