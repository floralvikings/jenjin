package com.jenjinstudios.world.io;

import com.jenjinstudios.world.Location;
import com.jenjinstudios.world.World;
import com.jenjinstudios.world.Zone;
import com.jenjinstudios.world.math.Dimension2D;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * This class handles the reading of and construction from world xml files.
 * @author Caleb Brinkman
 */
public class WorldDocumentReader
{
	private static final String ZONE_TAG_NAME = "zone";
	private static final String LOCATION_TAG_NAME = "location";
	private final InputStream inputStream;
	private byte[] worldFileChecksum;
	private byte[] worldFileBytes;

	public WorldDocumentReader(InputStream inputStream) { this.inputStream = inputStream; }

	public World read() throws WorldDocumentException {
		ByteArrayOutputStream bao = toByteArrayOutputStream(inputStream);
		worldFileBytes = bao.toByteArray();
		worldFileChecksum = createDocumentChecksum();

		ByteArrayInputStream bis = new ByteArrayInputStream(worldFileBytes);

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = getDocumentBuilder(factory);
		Document worldDocument = parseWoldDocument(bis, builder);

		NodeList zoneNodes = worldDocument.getElementsByTagName(ZONE_TAG_NAME);
		Zone[] zones = parseZoneNodes(zoneNodes);

		return new World(zones);
	}

	private ByteArrayOutputStream toByteArrayOutputStream(InputStream inputStream) throws WorldDocumentException {
		try
		{
			int bytesRead;
			byte[] buff = new byte[8000];
			ByteArrayOutputStream bao = new ByteArrayOutputStream();
			while ((bytesRead = inputStream.read(buff)) != -1)
			{
				bao.write(buff, 0, bytesRead);
			}
			return bao;
		} catch (IOException e)
		{
			throw new WorldDocumentException("Unable to read input stream.", e);
		}
	}

	private byte[] createDocumentChecksum() throws WorldDocumentException {
		try
		{
			return ChecksumUtil.getMD5Checksum(worldFileBytes);
		} catch (NoSuchAlgorithmException e)
		{
			throw new WorldDocumentException("Unable to create world file checksum.", e);
		}
	}

	private Document parseWoldDocument(InputStream inputStream, DocumentBuilder builder) throws WorldDocumentException {
		try
		{
			Document worldDocument = builder.parse(inputStream);
			worldDocument.normalize();
			return worldDocument;
		} catch (SAXException | IOException e)
		{
			throw new WorldDocumentException("Unable to parse WorldDocument from input stream", e);
		}
	}

	private static DocumentBuilder getDocumentBuilder(DocumentBuilderFactory factory) throws WorldDocumentException {
		DocumentBuilder builder;
		try
		{
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e)
		{
			throw new WorldDocumentException("Unable to configure document builder.", e);
		}
		return builder;
	}

	public byte[] getWorldFileChecksum() { return worldFileChecksum; }

	public byte[] getWorldFileBytes() { return worldFileBytes; }

	private Zone[] parseZoneNodes(NodeList zoneNodes) {
		Zone[] zones = new Zone[zoneNodes.getLength()];

		for (int i = 0; i < zoneNodes.getLength(); i++)
		{
			Element currentZoneElement = (Element) zoneNodes.item(i);
			NodeList locationNodes = currentZoneElement.getElementsByTagName(LOCATION_TAG_NAME);
			int id = Integer.parseInt(zoneNodes.item(i).getAttributes().getNamedItem("id").getTextContent());
			int xSize = Integer.parseInt(zoneNodes.item(i).getAttributes().getNamedItem("xSize").getTextContent());
			int ySize = Integer.parseInt(zoneNodes.item(i).getAttributes().getNamedItem("ySize").getTextContent());

			zones[i] = new Zone(id, new Dimension2D(xSize, ySize), parseLocationNodes(locationNodes));
		}

		return zones;
	}

	private Location[] parseLocationNodes(NodeList locationNodes) {
		Location[] locations = new Location[locationNodes.getLength()];
		for (int i = 0; i < locationNodes.getLength(); i++)
		{
			Node currentLocationNode = locationNodes.item(i);
			NamedNodeMap attributes = currentLocationNode.getAttributes();
			int x = Integer.parseInt(attributes.getNamedItem("x").getTextContent());
			int y = Integer.parseInt(attributes.getNamedItem("y").getTextContent());
			Map<String, Object> properties = getLocationProperties(attributes);
			locations[i] = new Location(x, y, properties);
		}
		return locations;
	}

	private Map<String, Object> getLocationProperties(NamedNodeMap attributes) {
		Map<String, Object> properties = new HashMap<>();
		for (int j = 0; j < attributes.getLength(); j++)
		{
			Attr item = (Attr) attributes.item(j);
			if (item != null && !"x".equals(item.getName()) && !"y".equals(item.getName()))
			{
				String name = item.getName();
				String value = item.getValue();
				properties.put(name, value);
			}
		}
		return properties;
	}

}
