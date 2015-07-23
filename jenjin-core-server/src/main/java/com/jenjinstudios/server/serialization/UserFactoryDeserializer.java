package com.jenjinstudios.server.serialization;

import com.google.gson.*;
import com.jenjinstudios.server.authentication.User;
import com.jenjinstudios.server.authentication.UserFactory;

import java.lang.reflect.Type;

/**
 * The expected JSON format is:
 * <p>
 * {@code
 * {
 * "class":"some.class.implementing.UserFactory",
 * "fields": {
 * // fields used to populate object, standard JSON for that object.
 * }
 * }
 * }
 *
 * @author Caleb Brinkman
 */
public class UserFactoryDeserializer<T extends User> implements JsonDeserializer<UserFactory<T>>
{
    @Override
    public UserFactory<T> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
          throws JsonParseException
    {
        JsonObject jsonObject = json.getAsJsonObject();
        JsonElement lookupClassElement = jsonObject.get("class");
        if (lookupClassElement == null) {
            throw new JsonParseException("DatabaseLookup must specify class to be deserialized into");
        }
        String className = lookupClassElement.getAsString();
        Class<UserFactory> lookupClass;
        try {
            lookupClass = (Class<UserFactory>) Class.forName(className);
        } catch (ClassNotFoundException | ClassCastException e) {
            throw new JsonParseException("DatabaseLookup implementation not found or incorrect: ", e);
        }

        JsonElement fieldsElement = jsonObject.get("fields");
        return (fieldsElement != null)
              ? context.deserialize(fieldsElement, lookupClass)
              : context.deserialize(new JsonObject(), lookupClass);
    }
}
