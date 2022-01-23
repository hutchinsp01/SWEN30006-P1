/**
 * Edited by Paul Hutchins - 1160468, Jade Siang - 1170856, Kian Dsouza - 1142463
 * in conjunction with the brief stated by the SWEN3006 assignment 1
 */

package automail;

import exceptions.ExcessiveDeliveryException;
import exceptions.ItemTooHeavyException;
import simulation.Clock;
import simulation.IMailDelivery;
import util.RobotType;

/**
 * The robot delivers mail!
 */
public abstract class Robot {

    protected static final int INDIVIDUAL_MAX_WEIGHT = 2000;

    private IMailDelivery delivery;
    protected final String id;
    /** Possible states the robot can be in */
    public enum RobotState { DELIVERING, WAITING, RETURNING }
    private RobotState current_state;
    protected int current_floor;
    private int destination_floor;
    private MailPool mailPool;
    private boolean receivedDispatch;

    protected MailItem deliveryItem = null;
    private MailItem tube = null;

    private int deliveryCounter;
    private RobotType type;
    private int maxDeliveries;
    private double operatingTime = 0;
    private ChargeFee chargeFee;
    

    /**
     * Initiates the robot's location at the start to be at the mailroom
     * also set it to be waiting for mail.
     * @param delivery governs the final delivery
     * @param chargeFee allows the robot to connect to the router and obtain delivery fees
     * @param mailPool is the source of mail items
     * @param maxDeliveries stores max deliveries for each robot type, to throw excessiveDeliveryException
     * @param id integer ID of the robot
     * @param type is the enumerated robot type
     */
	public Robot(IMailDelivery delivery, ChargeFee chargeFee, MailPool mailPool, int maxDeliveries, String id, RobotType type) {
		this.id = id;
        // current_state = RobotState.WAITING;
    	current_state = RobotState.RETURNING;
        current_floor = Building.getInstance().getMailroomLocationFloor();
        this.delivery = delivery;
        this.mailPool = mailPool;
        this.receivedDispatch = false;
        this.deliveryCounter = 0;
        this.maxDeliveries = maxDeliveries;
        this.chargeFee = chargeFee;
        this.type = type;
	}


	/**
     * This is called on every time step
     * @throws ExcessiveDeliveryException if robot delivers more than the capacity of the tube without refilling
     */
    public void operate() throws ExcessiveDeliveryException {   
    	switch(current_state) {
    		/** This state is triggered when the robot is returning to the mailroom after a delivery */
    		case RETURNING:
    			operatingTime++;
    			/** If its current position is at the mailroom, then the robot should change state */
                if(current_floor == Building.getInstance().getMailroomLocationFloor()){
        			/** Tell the sorter the robot is ready */
        			mailPool.registerWaiting(this);
                	changeState(RobotState.WAITING);
                } else {
                	/** If the robot is not at the mailroom floor yet, then move towards it! */
                    moveTowards(Building.getInstance().getMailroomLocationFloor());
                	break;
                }
    		case WAITING:
                /** If the StorageTube is ready and the Robot is waiting in the mailroom then start the delivery */
                if(!isEmpty() && receivedDispatch){
                	receivedDispatch = false;
                	deliveryCounter = 0; // reset delivery counter
                	setDestination();
                	changeState(RobotState.DELIVERING);
                }
                break;
    		case DELIVERING:
    			operatingTime++;
    			if(current_floor == destination_floor){ // If already here drop off either way
                    /** Delivery complete, report this to the simulator! */
    				MailItem temp = deliveryItem;
    				deliveryItem = null;
                    delivery.deliver(this, temp, chargeFee.getServiceFeeLog(this));
                    deliveryCounter++;
                    if(deliveryCounter > maxDeliveries){  // Implies a simulation bug
                    	throw new ExcessiveDeliveryException();
                    }
                    /** Check if want to return, i.e. if there is no item in the tube*/
                    if(isTubeEmpty()){
                    	changeState(RobotState.RETURNING);
                    }
                    else{
                        /** If there is another item, set the robot's route to the location to deliver the item */
                        deliveryItem = getNextItem();
                        setDestination();
                        changeState(RobotState.DELIVERING);
                    }
    			} else {
	        		/** The robot is not at the destination yet, move towards it! */
	                moveTowards(destination_floor);
    			}
                break;
    	}
    }
    
    /**
     * This is called when a robot is assigned the mail items and ready to dispatch for the delivery 
     */
    public void dispatch() {
    	receivedDispatch = true;
    }


    /**
     * Sets the route for the robot
     */
    protected void setDestination() {
        /** Set the destination floor */
        destination_floor = deliveryItem.getDestFloor();
    }

    /**
     * Generic function that moves the robot towards the destination at speed 1
     * @param destination the floor towards which the robot is moving
     */
	protected void moveTowards(int destination) {
        if(current_floor < destination){
            current_floor++;
        } else {
            current_floor--;
        }
    }
    
    public String getIdTube() {
    	return String.format("%s(%1d)", this.id, (tube == null ? 0 : 1));
    }
    
    /**
     * Prints out the change in state
     * @param nextState the state to which the robot is transitioning
     */
    protected void changeState(RobotState nextState){
    	assert(!(deliveryItem == null && tube != null));
    	if (current_state != nextState) {
            System.out.printf("T: %3d > %7s changed from %s to %s%n", Clock.Time(), getIdTube(), current_state, nextState);
    	}
    	current_state = nextState;
    	if(nextState == RobotState.DELIVERING){
            System.out.printf("T: %3d > %7s-> [%s]%n", Clock.Time(), getIdTube(), deliveryItem.toString());
    	}
    }

	public MailItem getTube() {
		return tube;
	}

	public boolean isEmpty() {
		return (deliveryItem == null && tube == null);
	}
	
	public boolean isHandEmpty() {
		return (deliveryItem == null);
	}
	
	public boolean isTubeEmpty() {
		return (tube == null);
	}
	
	public boolean hasSpace() {
		return(isHandEmpty() || isTubeEmpty());
	}
	
	public int getFloor() {
		return current_floor;
	}
	
	public RobotType getType() {
		return type;
	}
	
	public double getOperatingTime() {
		return operatingTime;
	}
	
	protected MailItem getNextItem() {
		MailItem temp = tube;
		tube = null;
		return temp;
	}

	public void addToHand(MailItem mailItem) throws ItemTooHeavyException {
		assert(deliveryItem == null);
		deliveryItem = mailItem;
		if (deliveryItem.weight > INDIVIDUAL_MAX_WEIGHT) throw new ItemTooHeavyException();
	}

	public void addToTube(MailItem mailItem) throws ItemTooHeavyException {
		assert(tube == null);
		tube = mailItem;
		if (tube.weight > INDIVIDUAL_MAX_WEIGHT) throw new ItemTooHeavyException();
	}
	

}
