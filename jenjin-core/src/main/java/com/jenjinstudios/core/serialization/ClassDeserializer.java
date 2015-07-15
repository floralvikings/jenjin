package com.jenjinstudios.core.serialization;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Used for deserializing Class objects using the name of the class.
 *
 * @author Caleb Brinkman
 */
class ClassDeserializer implements JsonDeserializer<Class>
{
	@Override
	public Class deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
		  throws JsonParseException
	{
		String className = json.getAsString();
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new JsonParseException(e);
		}
	}
}
