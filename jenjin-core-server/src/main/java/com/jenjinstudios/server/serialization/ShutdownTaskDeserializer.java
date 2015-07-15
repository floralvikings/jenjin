package com.jenjinstudios.server.serialization;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.jenjinstudios.server.concurrency.ShutdownTask;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;

/**
 * Used to deserialized ShutdownTasks using Gson.
 *
 * @author Caleb Brinkman
 */
public class ShutdownTaskDeserializer implements JsonDeserializer<ShutdownTask>
{
	@Override
	public ShutdownTask deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
		  throws JsonParseException
	{
		String className = json.getAsString();
		Class clazz;
		try {
			clazz = Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new JsonParseException(e);
		}
		try {
			return (ShutdownTask) clazz.getConstructor().newInstance();
		} catch (InstantiationException
			  | IllegalAccessException
			  | ClassCastException
			  | NoSuchMethodException
			  | InvocationTargetException e) {
			throw new JsonParseException(e);
		}
	}
}
