package com.jenjinstudios.core.io;

import com.jenjinstudios.core.xml.MessageGroup;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.Closeable;
import java.io.IOException;
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
            } catch (JAXBException | RuntimeException ex)
            {
                LOGGER.log(Level.INFO, "Unable to parse XML file", ex);
            } finally
            {
                closeQuietly(inputStream);
            }
        }
        return foundMessages;
	}

    private static void closeQuietly(Closeable inputStream) {
        try
        {
            inputStream.close();
        } catch (IOException ex)
        {
            LOGGER.log(Level.INFO, "Unable to close stream", ex);
        }
    }
}
