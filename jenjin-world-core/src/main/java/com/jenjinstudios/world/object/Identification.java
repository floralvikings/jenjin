package com.jenjinstudios.world.object;

/**
 * Encapsulates data used to identify a WorldObject across machines.
 *
 * @author Caleb Brinkman
 */
public class Identification
{
	private int typeId;
	private String id = "";
	private String zoneId;

	/**
	 * Get the "type" represented in this Identification.  This can be used for
	 * things like loading resources, appearance, etc...
	 *
	 * @return The type identifier for this Identification.
	 */
	public int getTypeId() { return typeId; }

	/**
	 * Set the "type" represented in this Identification.  This can be used for
	 * things like loading resources, appearance, etc...
	 *
	 * @param typeId The new type identifier for this Identification.
	 */
	public void setTypeId(int typeId) { this.typeId = typeId; }

	/**
	 * Get the unique identifier of this Idenfitication.
	 *
	 * @return The unique identifier of this Identification.
	 */
	public String getId() { return id; }

	/**
	 * Set the unique identifier of this Identification.
	 *
	 * @param id The unique identifier of this Identification.
	 */
	public void setId(String id) { this.id = id; }

	/**
	 * Set the machine-agnostic zone id.
	 *
	 * @param zoneId The id of the zone.
	 */
	public void setZoneId(String zoneId) { this.zoneId = zoneId; }

	/**
	 * Get the zone id of the object across machines.
	 *
	 * @return The zone id.
	 */
	public String getZoneId() { return zoneId; }
}
