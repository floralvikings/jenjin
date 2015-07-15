package com.jenjinstudios.server.net;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.jenjinstudios.server.authentication.User;
import com.jenjinstudios.server.concurrency.ConnectionAddedTask;
import com.jenjinstudios.server.concurrency.ShutdownTask;
import com.jenjinstudios.server.concurrency.UpdateTask;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Used to help with deserializing server configs.
 *
 * @author Caleb Brinkman
 */
public class ServerConfigReader<U extends User, C extends ServerMessageContext<U>>
{
	private final InputStream inputStream;

	/**
	 * Construct a new ServerConfigReader.
	 *
	 * @param path The path to the config file.
	 *
	 * @throws FileNotFoundException If the file doesn't exist.
	 */
	public ServerConfigReader(String path) throws FileNotFoundException { this(new File(path)); }

	/**
	 * Construct a new ServerConfigReader.
	 *
	 * @param file The config file.
	 *
	 * @throws FileNotFoundException If the file doesn't exist.
	 */
	public ServerConfigReader(File file) throws FileNotFoundException { this(new FileInputStream(file)); }

	/**
	 * Construct a new ServerConfigReader.
	 *
	 * @param inputStream The stream containing the config file.
	 */
	public ServerConfigReader(InputStream inputStream) { this.inputStream = inputStream; }

	/**
	 * Read the constructor-supplied JSON data into a ServerConfig object.
	 *
	 * @return The deserialized ServerConfig.
	 */
	public ServerConfig<U, C> read() {
		return read(Collections.<Type, JsonDeserializer>emptyMap());
	}

	/**
	 * Read the constructor-supplied JSON data into a ServerConfig object.
	 *
	 * @param deserializers A Map of TypeAdapters to use when deserializing.
	 *
	 * @return The deserialized ServerConfig.
	 */
	public ServerConfig<U, C> read(Map<Type, JsonDeserializer> deserializers) {
		Map<Type, JsonDeserializer> adapterMap = new HashMap<>(deserializers);
		adapterMap.put(UpdateTask.class, new UpdateTaskDeserializer());
		adapterMap.put(ShutdownTask.class, new ShutdownTaskDeserializer());
		adapterMap.put(ConnectionAddedTask.class, new ConnectionAddedTaskDeserializer());
		adapterMap.put(Class.class, new ClassDeserializer());
		GsonBuilder builder = new GsonBuilder();
		adapterMap.forEach(builder::registerTypeAdapter);
		Gson gson = builder.create();
		JsonReader reader = new JsonReader(new InputStreamReader(inputStream));
		return gson.fromJson(reader, new TypeToken<ServerConfig<U, C>>() {}.getType());
	}

	private static class UpdateTaskDeserializer implements JsonDeserializer<UpdateTask>
	{
		@Override
		public UpdateTask deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
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
				return (UpdateTask) clazz.getConstructor().newInstance();
			} catch (InstantiationException
				  | IllegalAccessException
				  | ClassCastException
				  | NoSuchMethodException
				  | InvocationTargetException e) {
				throw new JsonParseException(e);
			}
		}
	}

	private static class ShutdownTaskDeserializer implements JsonDeserializer<ShutdownTask>
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

	private static class ConnectionAddedTaskDeserializer implements JsonDeserializer<ConnectionAddedTask>
	{
		@Override
		public ConnectionAddedTask deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
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
				return (ConnectionAddedTask) clazz.getConstructor().newInstance();
			} catch (InstantiationException
				  | IllegalAccessException
				  | ClassCastException
				  | NoSuchMethodException
				  | InvocationTargetException e) {
				throw new JsonParseException(e);
			}
		}
	}

	private static class ClassDeserializer implements JsonDeserializer<Class>
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
}
