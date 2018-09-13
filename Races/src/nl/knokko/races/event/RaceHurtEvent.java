package nl.knokko.races.event;

import nl.knokko.races.base.ReflectedCause;
import nl.knokko.races.conditions.RacePresentor;

public class RaceHurtEvent extends RaceEvent {
	
	private final ReflectedCause cause;
	private double damage;

	public RaceHurtEvent(RacePresentor hurtPlayer, ReflectedCause cause, double damage) {
		super(hurtPlayer);
		this.cause = cause;
		this.damage = damage;
	}
	
	public RacePresentor getHurtPlayer(){
		return player;
	}
	
	public ReflectedCause getCause(){
		return cause;
	}
	
	public double getDamage(){
		return damage;
	}
	
	public void setDamage(double damage){
		this.damage = damage;
	}
}
