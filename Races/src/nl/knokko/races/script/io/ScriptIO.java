package nl.knokko.races.script.io;

import nl.knokko.races.base.ReflectedCause;
import nl.knokko.races.conditions.EntityPresentor;
import nl.knokko.races.conditions.RacePresentor;

/**
 * The available input and output variables of a script (like players and entities, not files)
 */
public abstract class ScriptIO {
	
	public abstract boolean hasSecondEntity();
	
	public abstract EntityPresentor getSecondEntity() throws UnsupportedOperationException;
	
	public abstract boolean hasSecondPlayer();
	
	public abstract RacePresentor getSecondPlayer() throws UnsupportedOperationException;
	
	public abstract boolean hasDamageCause();
	
	public abstract ReflectedCause getDamageCause() throws UnsupportedOperationException;
	
	public abstract boolean hasDamage();
	
	public abstract double getDamage() throws UnsupportedOperationException;
	
	public abstract void setDamage(double damage) throws UnsupportedOperationException;
}
