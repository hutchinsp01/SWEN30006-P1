/**
 * Created by Paul Hutchins - 1160468, Jade Siang - 1170856, Kian Dsouza - 1142463
 * in conjunction with the brief stated by the SWEN3006 assignment 1
 */

package automail;

import java.util.Arrays;
import java.util.HashMap;

import com.unimelb.swen30006.wifimodem.WifiModem;
import util.RobotType;

//CHANGED

public class ChargeFee {

	private double[] pastLookups;
	private int floors;
	private int lowestFloor;
	private boolean feeCharging;
	private WifiModem modem;
	private Robot[] robots;
	private HashMap<RobotType, Double> maintenanceCosts;
	
	/**
	 * Creates the instance of charge fee, and initiates pastlookups array with 0's
	 * @param lowestFloor is the lowest floor in the building, used in array index
	 * @param numFloors is the number of floors pastLookups array length
	 * @param feeCharging is boolean value of whether we are charging fees currently
	 * @param modem is the wifiModem to connect to obtain the service fees
	 * @param maintenanceCosts is a hashmap of the maintanence cost per step for each robot type
	 */
	public ChargeFee(int lowestFloor, int numFloors, boolean feeCharging, WifiModem modem, HashMap<RobotType, Double> maintenanceCosts) {
		this.floors = numFloors;
		this.lowestFloor = lowestFloor;
		this.feeCharging = feeCharging;
		this.modem = modem;
		this.maintenanceCosts = maintenanceCosts;
		pastLookups = new double[floors];
		Arrays.fill(pastLookups, 0.0);
	}
	
	/** 
	 * Assigns the robot array to chargeFee class, is used in autoMail as robots are created after chargeFee instance
	 * @param robots is an array of all robots in the building
	 */
	public void assignRobots(Robot[] robots) {
		this.robots = robots;
	}
	
	/**
	 * obtains the service fee Log of the robot, using the modem lookup for floor service fee and robot maintenance
	 * @param robot - robot in which we are getting the service fee for
	 * @return the service fee string
	 */
	public String getServiceFeeLog(Robot robot) {
		
		// fee charging not enabled return empty string
		if (!feeCharging) return "";
		
		// Else get robot type, cur service fee and maintenceCost for this robot type
		
		double serviceFee = serviceFee(robot);
		double avgOperatingTime = avgOperatingTime(robot);
		double maintenanceCost = maintenanceCost(robot, avgOperatingTime);
		double totalCharge = serviceFee + maintenanceCost;
		
		
		// return String
		return String.format(" | Service Fee: %.2f | Maintenance: %.2f | Avg. Operating Time: %.2f | Total Charge: %.2f",
									serviceFee, maintenanceCost, avgOperatingTime, totalCharge);
		
	}
	
	/**
	 * Look up modem for service fee for current floor, if call fails look in pastLookups
	 * @param robot - robot we are finding fee for
	 * @return double service fee for robot and floor
	 */
	public double serviceFee(Robot robot) {
		double serviceFee = modem.forwardCallToAPI_LookupPrice(robot.getFloor());
		
		// If service < 0, error occured so use pastLookup table for fee
		if ( serviceFee < 0) {
			serviceFee = pastLookups[robot.getFloor() - lowestFloor];
		}
		// else serviceFee found, update pastLookup tables with cur servicefee
		else {
			pastLookups[robot.getFloor() - lowestFloor] = serviceFee;
		}
		
		return serviceFee;
		
	}
	
	/**
	 * obtain the avgOperatingTime of robots of the same type as robot
	 * @param robot current robot for type
	 * @return avg operating time of robot type
	 */
	public double avgOperatingTime(Robot robot) {
		
		RobotType curRobotType = robot.getType();
		
		// Count operating Time and number of robots of the same type
		double cumOperatingTime = 0.0;
		double numRobots = 0.0;
		
		for (Robot otherRobot : robots) {
			if (otherRobot.getType() == curRobotType) {
				cumOperatingTime += otherRobot.getOperatingTime();
				numRobots++;
			}
		}
		
		return cumOperatingTime / numRobots;
		
	}
	
	/**
	 * Look up maintenanceCost hashmap for current robot maintenanceCost, and calculate maintenanceCost with avgOperatingTime
	 * @param robot - Current robot to obtain maintenanceCost
	 * @param avgOperatingTime - the average operating time of robots of the same type as robot
	 * @return the maintenanceCost of the robot type
	 */
	public double maintenanceCost(Robot robot, double avgOperatingTime) {
		double robotMaintenanceCost = maintenanceCosts.get(robot.getType());
		
		return avgOperatingTime * robotMaintenanceCost;
	}

}
