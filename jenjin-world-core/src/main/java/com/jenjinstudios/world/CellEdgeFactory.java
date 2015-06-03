package com.jenjinstudios.world;

import org.jgrapht.EdgeFactory;

/**
 * Used to create edges for a cell graph.
 *
 * @author Caleb Brinkman
 */
public class CellEdgeFactory implements EdgeFactory<Cell, CellEdge>
{
	@Override
	public CellEdge createEdge(Cell sourceVertex, Cell targetVertex) {
		return new CellEdge(sourceVertex, targetVertex);
	}
}
