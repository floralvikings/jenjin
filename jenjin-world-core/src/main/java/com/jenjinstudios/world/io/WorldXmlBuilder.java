package com.jenjinstudios.world.io;

import com.jenjinstudios.world.Location;
import com.jenjinstudios.world.World;
import com.jenjinstudios.world.Zone;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.util.*;

/**
 * Used to create XML representations of World objects.
 * @author Caleb Brinkman
 */
public class WorldXmlBuilder
{
	/** The tag name for World objects. */
	private static final String WORLD_TAG_NAME = "world";
	/** The tag name for zone objects. */
	private static final String ZONE_TAG_NAME = "zone";
	/** The name of the zone ID attribute. */
	private static final String ZONE_ID_ATTR = "id";
	/** The name of the zone xSize attribute. */
	private static final String ZONE_X_SIZE_ATTR = "xSize";
	/** The name of the zone ySize attribute. */
	private static final String ZONE_Y_SIZE_ATTR = "ySize";
	/** The zone of the location element. */
	private static final String LOC_TAG_NAME = "location";
	/** The name of the location x attribute. */
	private static final String LOC_X_ATTR = "x";
	/** The name of the location y attribute. */
	private static final String LOC_Y_ATTR = "y";

	/**
	 * Create an XML document from the given world.
	 * @param world The world.
	 * @return The XML document from the world
	 * @throws javax.xml.parsers.ParserConfigurationException If there's an error configuring the parser.
	 */
	public static Document createWorldDocument(World world) throws ParserConfigurationException {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

		// Add the root "world" tag
		Document doc = docBuilder.newDocument();
		Element rootElement = doc.createElement(WORLD_TAG_NAME);
		doc.appendChild(rootElement);

		List<Integer> zoneIDs = world.getZoneIDs();
		for (int id : zoneIDs)
		{
			Zone zone = world.getZone(id);
			Element zoneElement = createZoneElement(doc, zone);
			addLocationNodes(doc, zone, zoneElement);
			rootElement.appendChild(zoneElement);
		}

		return doc;
	}

	/**
	 * Add xml for the zone to the specified document.
	 * @param doc The document.
	 * @param zone The zone.
	 * @return The XML element created for the zone.
	 */
	private static Element createZoneElement(Document doc, Zone zone) {
		Element zoneElement = doc.createElement(ZONE_TAG_NAME);
		zoneElement.setAttribute(ZONE_ID_ATTR, String.valueOf(zone.id));
		zoneElement.setAttribute(ZONE_X_SIZE_ATTR, String.valueOf(zone.xSize));
		zoneElement.setAttribute(ZONE_Y_SIZE_ATTR, String.valueOf(zone.ySize));
		return zoneElement;
	}

	/**
	 * Add location nodes to the specified zone element.
	 * @param doc The document.
	 * @param zone The zone.
	 * @param zoneElement The zone element.
	 */
	private static void addLocationNodes(Document doc, Zone zone, Element zoneElement) {
		for (int x = 0; x < zone.xSize; x++)
		{
			for (int y = 0; y < zone.ySize; y++)
			{
				Location location = zone.getLocationOnGrid(x, y);
				Properties locationProperties = location.getProperties();
				if (locationProperties.size() > 0)
				{
					Element locationElement = createLocationElement(doc, location);
					zoneElement.appendChild(locationElement);
				}
			}
		}
	}

	/**
	 * Create a location xml element.
	 * @param doc The document.
	 * @param location The location.
	 * @return The element.
	 */
	private static Element createLocationElement(Document doc, Location location) {
		Element locationElement = doc.createElement(LOC_TAG_NAME);
		locationElement.setAttribute(LOC_X_ATTR, String.valueOf(location.X_COORDINATE));
		locationElement.setAttribute(LOC_Y_ATTR, String.valueOf(location.Y_COORDINATE));
		Properties locationProperties = location.getProperties();
		if (locationProperties.size() > 0)
		{
			Enumeration<?> propertyNames = locationProperties.propertyNames();
			ArrayList<?> list = Collections.list(propertyNames);
			for (Object property : list)
			{
				String name = property.toString();
				String value = locationProperties.getProperty(name);
				Attr locAttr = doc.createAttribute(name);
				locAttr.setValue(value);
				locationElement.setAttributeNode(locAttr);
			}
		}
		return locationElement;
	}
}
