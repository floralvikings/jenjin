package com.jenjinstudios.world;

import com.jenjinstudios.world.ai.Pathfinder;
import com.jenjinstudios.world.math.MathUtil;
import com.jenjinstudios.world.math.Vector2D;
import com.jenjinstudios.world.state.MoveState;

import java.util.LinkedList;

/**
 * This class represents a Non-Player Character.
 * @author Caleb Brinkman
 */
public class NPC extends Actor
{

	/**
	 * Plot a path to the given Location, and begin following it immediately.
	 * @param target The target location.
	 */
	public void plotPath(Location target)
	{
		LinkedList<Location> path = Pathfinder.findPath(getLocation(), target);
		// Start will be current location.
		Location prev = path.pop();
		Vector2D start = getVector2D();
		Vector2D prevCenter = prev.getCenter();
		double absAngle = getVector2D().getAngleToVector(prevCenter);
		// Create a move state to start moving forward, right now, toward the target vector.
		MoveState moveState = new MoveState(MoveState.FRONT, getStepsTaken(), absAngle);
		addMoveState(moveState);

		while(!path.isEmpty())
		{
			Vector2D nextCenter = path.pop().getCenter();
			int stepsToTake = (int) MathUtil.round(start.getDistanceToVector(prevCenter) / Actor.STEP_LENGTH, 0);
			absAngle = prevCenter.getAngleToVector(nextCenter);

			MoveState nextState = new MoveState(MoveState.FRONT, stepsToTake, absAngle);
			addMoveState(nextState);

			start = prevCenter;
			prevCenter = nextCenter;

			if(path.isEmpty())
			{
				stepsToTake = (int) MathUtil.round(start.getDistanceToVector(prevCenter) / Actor.STEP_LENGTH, 0);
				MoveState idleState = new MoveState(MoveState.IDLE, stepsToTake,getCurrentMoveState().absoluteAngle);
				addMoveState(idleState);
			}
		}
	}
}
