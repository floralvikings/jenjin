package com.jenjinstudios.world.io;

import com.jenjinstudios.world.World;
import org.w3c.dom.Document;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;

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

	/**
	 * Construct an XML document for the world and write it to the specified file.
	 * @param file The file to which to write the world.
	 * @throws javax.xml.parsers.ParserConfigurationException If there's an error configuring the XML parser
	 * @throws javax.xml.transform.TransformerException If there's an error configuring the xml transformer.
	 */
	public void write(File file) throws ParserConfigurationException, TransformerException {
		Document worldDoc = WorldXmlBuilder.createWorldDocument(world);

		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty("{http://xml.apache.org/xalan}line-separator", "\n\n");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		DOMSource source = new DOMSource(worldDoc);
		StreamResult result = new StreamResult(file);
		transformer.transform(source, result);
	}

}
