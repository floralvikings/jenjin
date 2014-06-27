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

	public void write(OutputStream outputStream) throws ParserConfigurationException, TransformerException {
		Document worldDoc = WorldXmlBuilder.createWorldDocument(world);

		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty("{http://xml.apache.org/xalan}line-separator", "\n\n");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		DOMSource source = new DOMSource(worldDoc);
		StreamResult result = new StreamResult(outputStream);
		transformer.transform(source, result);
	}

}
