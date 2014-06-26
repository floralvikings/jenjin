package com.jenjinstudios.world.io;

import com.jenjinstudios.world.Location;
import com.jenjinstudios.world.NPC;
import com.jenjinstudios.world.World;
import com.jenjinstudios.world.Zone;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

/**
 * The class responsible for reading NPCs from an xml file.
 * @author Caleb Brinkman
 */
public class NpcFileReader
{
	/** The tag name for the root "zone" tags. */
	private static final String NPC_TAG_NAME = "npc";
	/** The XML document storing the NPC data. */
	private final Document npcDocument;
	/** The world in which to look for locations. */
	private final World world;

	/**
	 * Construct a new NPCFileReader for the given input stream.
	 * @param inputStream The stream containing the NPC XML.
	 * @param world The world which will be used to retrieve location references; this object is not modified, only
	 * read.
	 * @throws ParserConfigurationException If there's an error parsing the XML.
	 * @throws IOException If there's an error reading the stream.
	 * @throws SAXException If there's an error validating the XML.
	 */
	public NpcFileReader(World world, InputStream inputStream) throws ParserConfigurationException, IOException, SAXException {
		this.world = world;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		npcDocument = builder.parse(inputStream);
		npcDocument.getDocumentElement().normalize();
	}

	/**
	 * Consume the document and return all NPCs read from it.
	 * @return A list of NPCs parsed from the XML input.
	 */
	public List<NPC> read() {
		LinkedList<NPC> r = new LinkedList<>();
		NodeList npcNodes = npcDocument.getElementsByTagName(NPC_TAG_NAME);
		for (int i = 0; i < npcNodes.getLength(); i++)
		{
			Element npcElement = (Element) npcNodes.item(i);
			String name = npcElement.getAttribute("name");
			int zoneID = Integer.parseInt(npcElement.getAttribute("zoneID"));
			double xCoordinate = Double.parseDouble(npcElement.getAttribute("xCoordinate"));
			double yCoordinate = Double.parseDouble(npcElement.getAttribute("yCoordinate"));
			TreeMap<String, Boolean> behaviors = parseBehaviorElements(npcElement.getElementsByTagName("behaviors"));

			NPC currentNPC = new NPC(name, behaviors);
			currentNPC.setVector2D(xCoordinate, yCoordinate);
			currentNPC.setZoneID(zoneID);

			List<Location> wanderTargets = parseWanderTargets(zoneID, npcElement.getElementsByTagName("wander_targets"));
			for (Location location : wanderTargets)
			{
				currentNPC.addWanderTarget(location);
			}
			r.add(currentNPC);
		}
		return r;
	}

	/**
	 * Parse a list of "wander_targets" elements.
	 * @param zoneID The id of the zone in which the NPC is being created.
	 * @param wanderTargetsLists The NodeList containing the "wander_targets" elements.
	 * @return The list of parsed target locations.
	 */
	private List<Location> parseWanderTargets(int zoneID, NodeList wanderTargetsLists) {
		LinkedList<Location> targetList = new LinkedList<>();
		Zone targetZone = world.getZone(zoneID);
		for (int i = 0; i < wanderTargetsLists.getLength(); i++)
		{
			Element wanderTargetsList = (Element) wanderTargetsLists.item(i);
			NodeList wanderTargets = wanderTargetsList.getElementsByTagName("wander_target");
			for (int j = 0; j < wanderTargets.getLength(); j++)
			{
				Element wanderTarget = (Element) wanderTargets.item(j);
				int xLoc = Integer.parseInt(wanderTarget.getAttribute("xLoc"));
				int yLoc = Integer.parseInt(wanderTarget.getAttribute("yLoc"));
				targetList.add(targetZone.getLocationOnGrid(xLoc, yLoc));
			}
		}
		return targetList;
	}

	/**
	 * Parse the "behaviors" XML element for NPC behaviors.
	 * @param behaviorsElements The NodeList containing each "behaviors" element.
	 * @return The list of boolean attributes parsed from the behaviors elements.
	 */
	private TreeMap<String, Boolean> parseBehaviorElements(NodeList behaviorsElements) {
		TreeMap<String, Boolean> behaviors = new TreeMap<>();
		for (int i = 0; i < behaviorsElements.getLength(); i++)
		{
			Element behaviorsElement = (Element) behaviorsElements.item(i);
			NodeList behaviorElements = behaviorsElement.getElementsByTagName("behavior");
			for (int j = 0; j < behaviorElements.getLength(); j++)
			{
				Element behaviorElement = (Element) behaviorElements.item(i);
				String name = behaviorElement.getAttribute("name");
				Boolean value = Boolean.valueOf(behaviorElement.getAttribute("value"));
				behaviors.put(name, value);
			}
		}
		return behaviors;
	}
}
