package com.jenjinstudios.core.serialization;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.jenjinstudios.core.concurrency.MessageContext;
import com.jenjinstudios.core.connection.ConnectionConfig;

import java.io.*;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Map;

/**
 * Used to read connection configuration from a JSON file.
 *
 * @author Caleb Brinkman
 */
public class ConnectionConfigReader<C extends MessageContext>
{
	private final InputStream inputStream;

	/**
	 * Construct a new ConnectionConfigReader.
	 *
	 * @param path The path to the config file.
	 *
	 * @throws FileNotFoundException If the file doesn't exist.
	 */
	public ConnectionConfigReader(String path) throws FileNotFoundException {
		this(new File(path));
	}

	/**
	 * Construct a new ConnectionConfigReader.
	 *
	 * @param file The config file.
	 *
	 * @throws FileNotFoundException If the file doesn't exist.
	 */
	public ConnectionConfigReader(File file) throws FileNotFoundException {
		this(new FileInputStream(file));
	}

	/**
	 * Construct a new ConnectionConfigReader.
	 *
	 * @param inputStream The stream containing the config file.
	 */
	public ConnectionConfigReader(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	/**
	 * Read the constructor-supplied JSON data into a ConnectionConfig object.
	 *
	 * @return The deserialized ConnectionConfig.
	 */
	public ConnectionConfig<C> read() {
		return read(Collections.<Type, JsonDeserializer>emptyMap());
	}

	/**
	 * Read the constructor-supplied JSON data into a ConnectionConfig object.
	 *
	 * @param deserializers A Map of TypeAdapters to use when deserializing.
	 *
	 * @return The deserialized ConnectionConfig.
	 */
	public ConnectionConfig<C> read(Map<Type, JsonDeserializer> deserializers) {
		GsonBuilder builder = new GsonBuilder().registerTypeAdapter(Class.class, new ClassDeserializer());
		if (deserializers != null) { deserializers.forEach(builder::registerTypeAdapter); }
		Gson gson = builder.create();
		JsonReader reader = new JsonReader(new InputStreamReader(inputStream));
		return gson.fromJson(reader, new TypeToken<ConnectionConfig<C>>() {}.getType());
	}

}
