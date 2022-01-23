/**
 * Created by Paul Hutchins - 1160468, Jade Siang - 1170856, Kian Dsouza - 1142463
 * in conjunction with the brief stated by the SWEN3006 assignment 1
 */

package automail;

import simulation.IMailDelivery;
import util.RobotType;

public class FastRobot extends Robot {
	
	private static int maxDeliveries = 1;
	private static final int MOVEDISTANCE = 3;
	private static RobotType type = RobotType.FAST;

	/**
     * Initiates the robot's location at the start to be at the mailroom
     * also set it to be waiting for mail.
     * @param delivery governs the final delivery
     * @param chargeFee allows the robot to connect to the router and obtain delivery fees
     * @param mailPool is the source of mail items
     * @param id is the robots integer id
     */
	public FastRobot(IMailDelivery delivery, ChargeFee chargeFee, MailPool mailPool, int number) {
		super(delivery, chargeFee, mailPool, maxDeliveries, "F" + number, type);
		
	}

	/**
	 * Overrides move method as fast robot moves 3 floors per tick
	 */
	@Override
	protected void moveTowards(int destination) {
		if(current_floor < destination){
			if (Math.abs(current_floor - destination) > MOVEDISTANCE) {
				current_floor += MOVEDISTANCE;
			}
			else {
				current_floor = destination;
			}
        } 
		else {
			if (Math.abs(current_floor - destination) > MOVEDISTANCE) {
				current_floor -= MOVEDISTANCE;
			}
			else {
				current_floor = destination;
			}
		}

	}

	/** hasSpace only checks hand, as fast robot does not have tube */
	@Override
	public boolean hasSpace() {
		return (isHandEmpty());
	}


}
