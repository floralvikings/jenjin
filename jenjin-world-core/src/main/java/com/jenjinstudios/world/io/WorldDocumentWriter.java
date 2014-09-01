package com.jenjinstudios.world.io;

import com.jenjinstudios.world.World;
import org.w3c.dom.Document;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.OutputStream;

/**
 * Used to write world objects to file.
 * @author Caleb Brinkman
 */
public class WorldDocumentWriter
{
	/** The world to be written to file. */
	private final World world;

	/**
	 * Construct a new WorldFileWriter.
	 * @param world The world to be written to file.
	 */
	public WorldDocumentWriter(World world) { this.world = world; }

	public void write(OutputStream outputStream) throws WorldDocumentException {
		DOMSource source = createWorldDomSource();
		StreamResult result = new StreamResult(outputStream);
		transformSourceIntoStream(source, result);
	}

	private DOMSource createWorldDomSource() throws WorldDocumentException {
		Document worldDoc = createWorldDocument();
		return new DOMSource(worldDoc);
	}

	private Transformer createTransformer() throws WorldDocumentException {
		try
		{
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty("{http://xml.apache.org/xalan}line-separator", "\n\n");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			return transformer;
		} catch (TransformerConfigurationException e)
		{
			throw new WorldDocumentException("Couldn't create transformer.", e);
		}
	}

	private Document createWorldDocument() throws WorldDocumentException {
		try
		{
			return new WorldXmlBuilder(world).createWorldDocument();
		} catch (ParserConfigurationException e)
		{
			throw new WorldDocumentException("Couldn't create world document.", e);
		}
	}

	private void transformSourceIntoStream(DOMSource source, StreamResult result) throws WorldDocumentException {
		try
		{
			Transformer transformer = createTransformer();
			transformer.transform(source, result);
		} catch (TransformerException e)
		{
			throw new WorldDocumentException("Couldn't transform data to stream.", e);
		}
	}

}
