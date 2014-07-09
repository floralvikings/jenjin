package com.jenjinstudios.world.server.io;

import com.jenjinstudios.world.Location;
import com.jenjinstudios.world.World;
import com.jenjinstudios.world.Zone;
import com.jenjinstudios.world.math.Vector2D;
import com.jenjinstudios.world.server.NPC;
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
public class NPCDocumentReader
{
	/** The tag name for the root "zone" tags. */
	private static final String NPC_TAG_NAME = "npc";
	/** The world in which to look for locations. */
	private final World world;
	private final InputStream inputStream;
	/** The XML document storing the NPC data. */
	private Document npcDocument;

	/**
	 * Construct a new NPCFileReader for the given input stream.
	 * @param inputStream The stream containing the NPC XML.
	 * @param world The world which will be used to retrieve location references; this object is not modified, only
	 * read.
	 */
	public NPCDocumentReader(World world, InputStream inputStream) {
		this.inputStream = inputStream;
		this.world = world;
	}

	/**
	 * Consume the document and return all NPCs read from it.
	 * @return A list of NPCs parsed from the XML input.
	 */
	public List<NPC> read() throws NPCDocumentException {
		buildNPCDocument();
		return parseNPCNodes();
	}

	private LinkedList<NPC> parseNPCNodes() {
		LinkedList<NPC> r = new LinkedList<>();
		NodeList npcNodes = npcDocument.getElementsByTagName(NPC_TAG_NAME);
		for (int i = 0; i < npcNodes.getLength(); i++)
		{
			Element npcElement = (Element) npcNodes.item(i);
			NPC currentNPC = parseNPCFromElement(npcElement);
			r.add(currentNPC);
		}
		return r;
	}

	private NPC parseNPCFromElement(Element npcElement) {
		String name = npcElement.getAttribute("name");
		int zoneID = Integer.parseInt(npcElement.getAttribute("zoneID"));
		double xCoordinate = Double.parseDouble(npcElement.getAttribute("xCoordinate"));
		double yCoordinate = Double.parseDouble(npcElement.getAttribute("yCoordinate"));
		Vector2D vector2D = new Vector2D(xCoordinate, yCoordinate);
		TreeMap<String, Boolean> behaviors = parseBehaviorElements(npcElement.getElementsByTagName("behaviors"));
		List<Location> wanderTargets = parseWanderTargets(zoneID, npcElement.getElementsByTagName("wander_targets"));

		NPC currentNPC = new NPC(name, behaviors);
		currentNPC.setVector2D(vector2D);
		currentNPC.setZoneID(zoneID);

		addWanderTargets(currentNPC, wanderTargets);
		return currentNPC;
	}

	private void addWanderTargets(NPC currentNPC, List<Location> wanderTargets) {
		for (Location location : wanderTargets)
		{
			currentNPC.addWanderTarget(location);
		}
	}

	private void buildNPCDocument() throws NPCDocumentException {
		DocumentBuilder builder = createDocumentBuilder();
		parseInputStreamIntoNpcDocument(builder);
	}

	private DocumentBuilder createDocumentBuilder() throws NPCDocumentException {
		try
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			return factory.newDocumentBuilder();
		} catch (ParserConfigurationException e)
		{
			throw new NPCDocumentException("Unable to create XML Document Builder", e);
		}
	}

	private void parseInputStreamIntoNpcDocument(DocumentBuilder builder) throws NPCDocumentException {
		try
		{
			npcDocument = builder.parse(inputStream);
			npcDocument.getDocumentElement().normalize();
		} catch (SAXException | IOException e)
		{
			throw new NPCDocumentException("Cannot parse InputStream into XML document.", e);
		}
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
