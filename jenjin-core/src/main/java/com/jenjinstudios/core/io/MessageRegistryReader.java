package com.jenjinstudios.core.io;

import com.jenjinstudios.core.xml.MessageGroup;

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

	static Collection<MessageGroup> readXmlStreams(Iterable<InputStream> streamsToRead) {
		Collection<MessageGroup> foundMessages = new LinkedList<>();
		for (InputStream inputStream : streamsToRead)
		{
			try
			{
				JAXBContext jaxbContext = JAXBContext.newInstance(MessageGroup.class);
				Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
				MessageGroup collection = (MessageGroup) jaxbUnmarshaller.unmarshal(inputStream);
				foundMessages.add(collection);
			} catch (Exception ex)
			{
				LOGGER.log(Level.INFO, "Unable to parse XML file", ex);
			}
		}
		return foundMessages;
	}
}
