package com.jenjinstudios.core.io;

import com.jenjinstudios.core.xml.Messages;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.util.Collection;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Reads message XML registries.
 *
 * @author Caleb Brinkman
 */
public class MessageRegistryReader
{
	private static final Logger LOGGER = Logger.getLogger(MessageRegistryReader.class.getName());

	static Collection<Messages> readXmlStreams(Iterable<InputStream> streamsToRead) {
		Collection<Messages> foundMessages = new LinkedList<>();
		for (InputStream inputStream : streamsToRead)
		{
			try
			{
				JAXBContext jaxbContext = JAXBContext.newInstance(Messages.class);
				Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
				Messages collection = (Messages) jaxbUnmarshaller.unmarshal(inputStream);
				foundMessages.add(collection);
			} catch (Exception ex)
			{
				LOGGER.log(Level.INFO, "Unable to parse XML file", ex);
			}
		}
		return foundMessages;
	}
}
