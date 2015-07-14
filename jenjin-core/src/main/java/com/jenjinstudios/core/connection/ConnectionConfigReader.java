package com.jenjinstudios.core.connection;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.lang.reflect.Type;

/**
 * Used to read connection configuration from a JSON file.
 *
 * @author Caleb Brinkman
 */
public class ConnectionConfigReader
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
	 * @param configType The type of the configuration to be read in.
	 *
	 * @return The deserialized ConnectionConfig.
	 */
	public <T extends ConnectionConfig> T read(Type configType) {
		Gson gson = new GsonBuilder()
			  .registerTypeAdapter(Class.class, new ClassDeserializer())
			  .create();
		JsonReader reader = new JsonReader(new InputStreamReader(inputStream));
		return gson.fromJson(reader, configType);
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
