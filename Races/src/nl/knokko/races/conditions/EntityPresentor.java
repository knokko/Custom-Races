package nl.knokko.races.conditions;

import nl.knokko.races.base.ReflectedCause;
import nl.knokko.races.potion.ReflectedEffect;

public interface EntityPresentor extends EntityConditions{
	
	void attack(ReflectedCause cause, double amount);
	
	void heal(double amount);
	
	void teleport(double x, double y, double z);
	
	void launch(double motionX, double motionY, double motionZ);
	
	void addPotionEffect(ReflectedEffect effect);
	
	void setFire(int ticks);
}