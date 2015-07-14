package com.jenjinstudios.server.net;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.jenjinstudios.core.connection.ConnectionConfig;
import com.jenjinstudios.core.connection.ConnectionConfigReader;
import com.jenjinstudios.server.concurrency.ConnectionAddedTask;
import com.jenjinstudios.server.concurrency.ShutdownTask;
import com.jenjinstudios.server.concurrency.UpdateTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
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
public class ServerConfigReader extends ConnectionConfigReader
{
	/**
	 * Construct a new ServerConfigReader.
	 *
	 * @param path The path to the config file.
	 *
	 * @throws FileNotFoundException If the file doesn't exist.
	 */
	public ServerConfigReader(String path) throws FileNotFoundException { super(path); }

	/**
	 * Construct a new ServerConfigReader.
	 *
	 * @param file The config file.
	 *
	 * @throws FileNotFoundException If the file doesn't exist.
	 */
	public ServerConfigReader(File file) throws FileNotFoundException { super(file); }

	/**
	 * Construct a new ServerConfigReader.
	 *
	 * @param inputStream The stream containing the config file.
	 */
	public ServerConfigReader(InputStream inputStream) { super(inputStream); }

	/**
	 * Read the constructor-supplied JSON data into a ServerConfig object.
	 *
	 * @param configType The type of the configuration to be read in.
	 *
	 * @return The deserialized ServerConfig.
	 */
	@Override
	public <T extends ConnectionConfig> T read(Type configType) {
		return read(configType, Collections.<Type, JsonDeserializer>emptyMap());
	}

	/**
	 * Read the constructor-supplied JSON data into a ServerConfig object.
	 *
	 * @param configType The type of the configuration to be read in.
	 * @param deserializers A Map of TypeAdapters to use when deserializing.
	 *
	 * @return The deserialized ServerConfig.
	 */
	@Override
	public <T extends ConnectionConfig> T read(Type configType, Map<Type, JsonDeserializer> deserializers) {
		Map<Type, JsonDeserializer> adapterMap = new HashMap<>(deserializers);
		adapterMap.put(UpdateTask.class, new UpdateTaskDeserializer());
		adapterMap.put(UpdateTask.class, new ShutdownTaskDeserializer());
		adapterMap.put(UpdateTask.class, new ConnectionAddedTaskDeserializer());
		return super.read(configType, adapterMap);
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
}
