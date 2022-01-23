/**
 * Edited by Paul Hutchins - 1160468, Jade Siang - 1170856, Kian Dsouza - 1142463
 * in conjunction with the brief stated by the SWEN3006 assignment 1
 */

package automail;

import simulation.IMailDelivery;

public class Automail {

    private Robot[] robots;
    private MailPool mailPool;
    
    public Automail(MailPool mailPool, IMailDelivery delivery, ChargeFee chargeFee, int numRegRobots, int numFastRobots, int numBulkRobots) {  	
    	/** Initialize the MailPool */
    	int totalRobots = numRegRobots + numFastRobots + numBulkRobots;
    	int robotCount = 0;
    	this.mailPool = mailPool;
    	
    	/** Initialize robots */
    	robots = new Robot[totalRobots];
    	
    	for (int i = 0; i < numRegRobots; i++) {
    		robots[robotCount] = new RegularRobot(delivery, chargeFee, mailPool, robotCount);
    		robotCount++;
    	}
    	
    	for (int i = 0; i < numFastRobots; i++) {
    		robots[robotCount] = new FastRobot(delivery, chargeFee, mailPool, robotCount);
    		robotCount++;
    	}
    	
    	for (int i = 0; i < numBulkRobots; i++) {
    		robots[robotCount] = new BulkRobot(delivery, chargeFee, mailPool, robotCount);
    		robotCount++;
    	}
    	
    	// Passes robot array to chargeFee so operations and type information are accessible
    	chargeFee.assignRobots(robots);
    	
    }

    public Robot[] getRobots() {
        return robots;
    }

    public MailPool getMailPool() {
        return mailPool;
    }
}
