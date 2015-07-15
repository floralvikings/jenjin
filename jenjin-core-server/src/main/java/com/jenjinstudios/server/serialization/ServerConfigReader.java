package com.jenjinstudios.server.serialization;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.jenjinstudios.core.serialization.ClassDeserializer;
import com.jenjinstudios.server.authentication.User;
import com.jenjinstudios.server.concurrency.ConnectionAddedTask;
import com.jenjinstudios.server.concurrency.ShutdownTask;
import com.jenjinstudios.server.concurrency.UpdateTask;
import com.jenjinstudios.server.net.ServerConfig;
import com.jenjinstudios.server.net.ServerMessageContext;

import java.io.*;
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

}
