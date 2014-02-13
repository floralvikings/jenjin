package test.jenjinstudios.world;

import com.jenjinstudios.world.Actor;
import com.jenjinstudios.world.ClientPlayer;
import com.jenjinstudios.world.math.Vector2D;
import com.jenjinstudios.world.state.MoveState;
import org.junit.Assert;

/**
 * Used to assist in testing World and WorldServer.
 * @author Caleb Brinkman
 */
public class WorldTestUtils
{
	/**
	 * Make the client player stay idle for the given number of steps.
	 * @param i The number of steps.
	 * @param clientPlayer The client player.
	 * @throws InterruptedException If there's an issue waiting for the player to be idle for the given number of steps.
	 */
	public static void idleClientPlayer(int i, ClientPlayer clientPlayer) throws InterruptedException {
		clientPlayer.setNewRelativeAngle(MoveState.IDLE);
		while (clientPlayer.getRelativeAngle() != MoveState.IDLE || clientPlayer.getStepsTaken() < i)
		{
			Thread.sleep(2);
		}
	}

	/**
	 * Move the specified actor to within one STEP_LENGTH of the specified vector.
	 * @param serverActor The actor.
	 * @param newVector The target vector.
	 * @throws InterruptedException If there is an error blocking until the target is reached.
	 */
	public static void moveServerActorToVector(Actor serverActor, Vector2D newVector) throws InterruptedException {
		int stepsTaken = serverActor.getStepsTaken();
		double newAngle = serverActor.getVector2D().getAngleToVector(newVector);
		MoveState newState = new MoveState(newAngle, stepsTaken, 0);
		serverActor.addMoveState(newState);
		double distanceToNewVector = serverActor.getVector2D().getDistanceToVector(newVector);
		while (distanceToNewVector > Actor.STEP_LENGTH && !serverActor.isForcedState())
		{
			Thread.sleep(10);
			distanceToNewVector = serverActor.getVector2D().getDistanceToVector(newVector);
		}
		MoveState idleState = new MoveState(MoveState.IDLE, serverActor.getStepsTaken(), 0);
		serverActor.addMoveState(idleState);
		Thread.sleep(10);
	}

	/**
	 * Move the client and server player to the given vector, by initiating the move client-side.
	 * @param newVector The vector to which to move.
	 * @param clientPlayer The client player.
	 * @param serverPlayer The server player.
	 * @throws InterruptedException If there's an exception.
	 */
	public static void moveClientPlayerTowardVector(Vector2D newVector, ClientPlayer clientPlayer, Actor serverPlayer) throws InterruptedException {
		// Make sure not to send multiple states during the same update.
		idleClientPlayer(1, clientPlayer);
		double newAngle = clientPlayer.getVector2D().getAngleToVector(newVector);
		clientPlayer.setNewRelativeAngle(newAngle);
		double targetDistance = clientPlayer.getVector2D().getDistanceToVector(newVector);
		while (targetDistance >= Actor.STEP_LENGTH && !clientPlayer.isForcedState())
		{
			targetDistance = clientPlayer.getVector2D().getDistanceToVector(newVector);
			Thread.sleep(2);
		}
		int stepsToIdle = Math.abs(clientPlayer.getStepsTaken() - serverPlayer.getStepsTaken()) * 5;
		idleClientPlayer(stepsToIdle, clientPlayer);
		double playersDistance = clientPlayer.getVector2D().getDistanceToVector(serverPlayer.getVector2D());
		Assert.assertEquals(0, playersDistance, .001);
	}
}