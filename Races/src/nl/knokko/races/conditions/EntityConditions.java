package nl.knokko.races.conditions;

public interface EntityConditions {
	
	long getWorldTime();
	
	double getTemperature();
	
	int getDimension();
	
	int getLightLevel();
	
	double getHearts();
	
	double getAttackDamage();
	
	double getMaxHearts();
	
	/**
	 * @return The value of the movementSpeed attribute
	 */
	double getMovementSpeed();
	
	/**
	 * @return The actual speed of this entity
	 */
	double getCurrentSpeed();
	
	double getX();
	
	double getY();
	
	double getZ();
	
	double getMotionX();
	
	double getMotionY();
	
	double getMotionZ();
	
	boolean inWater();
	
	boolean inLava();
	
	boolean seeSky();
	
	int getFireTicks();
	
	String getName();
}
