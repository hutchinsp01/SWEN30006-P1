/**
 * Created by Paul Hutchins - 1160468, Jade Siang - 1170856, Kian Dsouza - 1142463
 * in conjunction with the brief stated by the SWEN3006 assignment 1
 */

package automail;

import simulation.IMailDelivery;
import util.RobotType;

public class RegularRobot extends Robot {
	
	private static int maxDeliveries = 2;
	private static RobotType type = RobotType.REG;

	/**
     * Initiates the robot's location at the start to be at the mailroom
     * also set it to be waiting for mail.
     * @param delivery governs the final delivery
     * @param chargeFee allows the robot to connect to the router and obtain delivery fees
     * @param mailPool is the source of mail items
     * @param id is the robots integer id
     */
	public RegularRobot(IMailDelivery delivery, ChargeFee chargeFee, MailPool mailPool, int number) {
		super(delivery, chargeFee, mailPool, maxDeliveries, "R" + number, type);
	}


}
