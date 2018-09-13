package nl.knokko.races.event;

import nl.knokko.races.conditions.EntityPresentor;
import nl.knokko.races.conditions.RacePresentor;

public class RaceAttackEntityEvent extends RaceEvent {
	
	private final EntityPresentor victim;
	
	private double damage;

	public RaceAttackEntityEvent(RacePresentor attacker, EntityPresentor victim, double damage) {
		super(attacker);
		this.victim = victim;
		this.damage = damage;
	}
	
	public RacePresentor getAttackingRace(){
		return player;
	}
	
	public EntityPresentor getAttackedEntity(){
		return victim;
	}
	
	public double getDamage(){
		return damage;
	}
	
	public void setDamage(double damage){
		this.damage = damage;
	}
}
