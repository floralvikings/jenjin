package com.jenjinstudios.server.serialization;

import com.google.gson.*;
import com.jenjinstudios.server.database.DatabaseLookup;

import java.lang.reflect.Type;

/**
 * This class uses a custom serialization scheme to deserialize objects which implement the DatabaseLookup interface.
 * <p>
 * The expected JSON format is:
 * <p>
 * {@code
 * {
 * "class":"some.class.implementing.DatabaseLookup",
 * "fields": {
 * // fields used to populate object, standard JSON for that object.
 * }
 * }
 * }
 *
 * @author Caleb Brinkman
 */
public class DatabaseLookupDeserializer<T, R> implements JsonDeserializer<DatabaseLookup<T, R>>
{
    @Override
    public DatabaseLookup<T, R> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
          throws JsonParseException
    {
        JsonObject jsonObject = json.getAsJsonObject();
        JsonElement lookupClassElement = jsonObject.get("class");
        if (lookupClassElement == null) {
            throw new JsonParseException("DatabaseLookup must specify class to be deserialized into");
        }
        String className = lookupClassElement.getAsString();
        Class<DatabaseLookup<T, R>> lookupClass;
        try {
            lookupClass = (Class<DatabaseLookup<T, R>>) Class.forName(className);
        } catch (ClassNotFoundException | ClassCastException e) {
            throw new JsonParseException("DatabaseLookup implementation not found or incorrect: ", e);
        }

        JsonElement fieldsElement = jsonObject.get("fields");
        return (fieldsElement != null)
              ? context.deserialize(fieldsElement, lookupClass)
              : context.deserialize(new JsonObject(), lookupClass);
    }
}
