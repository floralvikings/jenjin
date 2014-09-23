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
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	private final World world;
	private Document worldDocument;

	public WorldXmlBuilder(World world) {
		this.world = world;
	}

	/**
	 * Create an XML document from the given world.
	 * @return The XML document from the world
	 * @throws javax.xml.parsers.ParserConfigurationException If there's an error configuring the parser.
	 */
	public Document createWorldDocument() throws ParserConfigurationException {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

		// Add the root "world" tag
		worldDocument = docBuilder.newDocument();
		Element rootElement = worldDocument.createElement(WORLD_TAG_NAME);
		worldDocument.appendChild(rootElement);

		List<Integer> zoneIDs = world.getZoneIDs();
		for (int id : zoneIDs)
		{
			Zone zone = world.getZone(id);
			Element zoneElement = createZoneElement(zone);
			addLocationNodes(zone, zoneElement);
			rootElement.appendChild(zoneElement);
		}

		return worldDocument;
	}

	/**
	 * Add xml for the zone to the specified document.
	 * @param zone The zone.
	 * @return The XML element created for the zone.
	 */
	private Element createZoneElement(Zone zone) {
		Element zoneElement = worldDocument.createElement(ZONE_TAG_NAME);
		zoneElement.setAttribute(ZONE_ID_ATTR, String.valueOf(zone.id));
		zoneElement.setAttribute(ZONE_X_SIZE_ATTR, String.valueOf(zone.xSize));
		zoneElement.setAttribute(ZONE_Y_SIZE_ATTR, String.valueOf(zone.ySize));
		return zoneElement;
	}

	/**
	 * Add location nodes to the specified zone element.
	 * @param zone The zone.
	 * @param zoneElement The zone element.
	 */
	private void addLocationNodes(Zone zone, Element zoneElement) {
		for (int x = 0; x < zone.xSize; x++)
		{
			addRow(zone, x, zoneElement);
		}
	}

	private void addRow(Zone zone, int row, Element zoneElement) {
		for (int y = 0; y < zone.ySize; y++)
		{
			Location location = zone.getLocationOnGrid(row, y);
			Map<String, Object> locationProperties = location.getProperties();
			if (locationProperties.size() > 0)
			{
				Element locationElement = createLocationElement(location);
				zoneElement.appendChild(locationElement);
			}
		}
	}

	/**
	 * Create a location xml element.
	 * @param location The location.
	 * @return The element.
	 */
	private Element createLocationElement(Location location) {
		Element locationElement = worldDocument.createElement(LOC_TAG_NAME);
		locationElement.setAttribute(LOC_X_ATTR, String.valueOf(location.getXCoordinate()));
		locationElement.setAttribute(LOC_Y_ATTR, String.valueOf(location.getYCoordinate()));
		Map<String, Object> locationProperties = location.getProperties();
		if (locationProperties.size() > 0)
		{
			Set<String> propertyNames = locationProperties.keySet();
			for (String property : propertyNames)
			{
				String value = locationProperties.get(property).toString();
				Attr locAttr = worldDocument.createAttribute(property);
				locAttr.setValue(value);
				locationElement.setAttributeNode(locAttr);
			}
		}
		return locationElement;
	}
}
