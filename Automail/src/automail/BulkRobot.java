/**
 * Created by Paul Hutchins - 1160468, Jade Siang - 1170856, Kian Dsouza - 1142463
 * in conjunction with the brief stated by the SWEN3006 assignment 1
 */

package automail;

import exceptions.ItemTooHeavyException;
import simulation.IMailDelivery;
import util.RobotType;

/**
 * Bulk robot extends the robot class, but overrides a lot of the methods
 * to do with the hand and tube as the bulk robot is set up to have no hands and a 5 item tube
 * Due to needed a hand to deliver we imitate this by have the hand hold the most recent item
 * put into the tube, and the tube to hold 4 items. Which imitates a length 5 tube with last in first out
 */
public class BulkRobot extends Robot {
	
	private static int maxDeliveries = 5;
	private MailItem[] tube = {null, null, null, null};
	private static RobotType type = RobotType.BULK;

	/**
     * Initiates the robot's location at the start to be at the mailroom
     * also set it to be waiting for mail.
     * @param delivery governs the final delivery
     * @param chargeFee allows the robot to connect to the router and obtain delivery fees
     * @param mailPool is the source of mail items
     * @param id is the robots integer id
     */
	public BulkRobot(IMailDelivery delivery, ChargeFee chargeFee, MailPool mailPool, int id) {
		super(delivery, chargeFee, mailPool, maxDeliveries, "B" + id, type);
	}
	
	/**
	 * Iterates through tube backwards, to get the next item from the tube to be delivered
	 * Operates in a last in first out stack order
	 */
	@Override
	protected MailItem getNextItem() {
		MailItem nextItem = null;
		for (int i = tube.length - 1; i >=0; i--) {
			if (tube[i] != null && nextItem == null) {
				nextItem = tube[i];
				tube[i] = null;
			}
		}
		return nextItem;
	}

	/**
	 * The most recent item given to the robot always goes into the hand/deliveryItem slot
	 * then the item currently in the hand gets moved the the first empty slot in the tube array
	 * in order to maintain the last in first out functionality of the tube
	 * @param MailItem - MailItem is the item to be added to the tube
	 */
	@Override
	public void addToTube(MailItem mailItem) throws ItemTooHeavyException {
		MailItem temp = deliveryItem;
		deliveryItem = mailItem;
		if (mailItem.weight > INDIVIDUAL_MAX_WEIGHT) throw new ItemTooHeavyException();
		
		for (int i=0; i<tube.length; i++) {
			if (tube[i] == null) {
				tube[i] = temp;
				if (mailItem.weight > INDIVIDUAL_MAX_WEIGHT) throw new ItemTooHeavyException();
				return;
			}
		}
		return;
		
	}
	
	/**
	 * Similar to Robot, just has to iterate through tube as tube is now an array
	 */
	@Override
	public boolean isEmpty() {
		if (deliveryItem != null) return false;
		
		for (MailItem item : tube) {
			if (item != null) return false;
		}
		
		return true;
	}
	
	/**
	 * As we use the hand to store item to be delivered, isTubeEmpty() checks all 4 tube slots and the hand spot
	 */
	@Override
	public boolean isTubeEmpty() {
		for (MailItem item : tube) {
			if (item != null) {
				return false;
			}
		}
		
		return (isHandEmpty());
	}
	
	/**
	 * Checks if a bulk robot has space to fit another item
	 * @return boolean value of if a bulk robot can fit an item
	 */
	@Override
	public boolean hasSpace() {
		for (MailItem item : tube) {
			if (item == null) return true;
		}
		
		return (deliveryItem == null);
	}
	
	/**
	 * Overrides robot.getIDTube() to include getNumItems() for bulk robot
	 */
	@Override
	public String getIdTube() {
    	return String.format("%s(%1d)", this.id, getNumItems());
    }
	
	/**
	 * is called to get the number of items the robot currently contains, used for print report
	 * @return count of items in robot
	 */
	public int getNumItems() {
		int count = 0;
		if (deliveryItem != null) count++;
		for (MailItem item : tube) {
			if (item != null) count++;
		}
		return count;
	}
	

}
