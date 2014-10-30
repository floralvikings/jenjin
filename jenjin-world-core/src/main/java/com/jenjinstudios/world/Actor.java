package com.jenjinstudios.world;

import com.jenjinstudios.world.actor.StateChangeStack;
import com.jenjinstudios.world.state.MoveState;
import com.jenjinstudios.world.util.ActorUtils;


/**
 * Implement a WorldObject which is capable of movement. </p> Actors start with a {@code MoveState} with {@code
 * MoveState.IDLE}.  Each update, the Actor checks to see if there are any MoveStates in the queue.  If there are, it
 * checks the first state in line for the number of steps needed before the state changes.  Once the number of steps has
 * been reached, the state switches to that of the first position in the queue, and the Actor's step counter is reset.
 * If an Actor "oversteps," which is determined if the Actor has taken more than the required number of steps to change
 * state, the Actor is moved back by the "overstepped" number of states, the Actor's state is updated, and the Actor
 * then takes the number of extra steps in the correct relativeAngle. </p> An Actor's state is considered "changed" when
 * the Actor is facing a new relativeAngle or moving in a new relativeAngle. An actor's state is considered "forced"
 * when the Actor attempts to make an illegal move, and the world forces the actor to halt.  The actor's forced state
 * will always be facing the angle of the most recently added move state (even if the state causes an illegal move) and
 * IDLE. The "steps until change" value is determined from the number of steps that were taken until the state was
 * forced.
 *
 * @author Caleb Brinkman
 */
public class Actor extends WorldObject
{
	public static double DEFAULT_MOVE_SPEED = 30.0d;
	private double moveSpeed;
	private MoveState forcedState;

	public Actor(String name) {
		super(name);
		setMoveSpeed(DEFAULT_MOVE_SPEED);
		addEventStack(StateChangeStack.STACK_NAME, new StateChangeStack(this));
	}

	@Override
	public void preUpdate() {
		super.preUpdate();
		setForcedState(null);
	}

	@Override
	public void update() {
		super.update();
		ActorUtils.stepForward(this);
	}

	public MoveState getForcedState() { return forcedState; }

	public void setForcedState(MoveState forcedState) { this.forcedState = forcedState; }

	public double getMoveSpeed() { return moveSpeed; }

	public void setMoveSpeed(double moveSpeed) { this.moveSpeed = moveSpeed; }

}
