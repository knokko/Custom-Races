package nl.knokko.races.event;

import nl.knokko.races.conditions.EntityPresentor;
import nl.knokko.races.conditions.RacePresentor;
import nl.knokko.races.item.ReflectedItem;

public class RaceAttackEntityEvent extends RaceEvent {
	
	private final EntityPresentor victim;
	private final ReflectedItem weapon;
	
	private double damage;

	public RaceAttackEntityEvent(RacePresentor attacker, EntityPresentor victim, ReflectedItem weapon, double damage) {
		super(attacker);
		this.victim = victim;
		this.damage = damage;
		this.weapon = weapon;
	}
	
	public RacePresentor getAttackingRace(){
		return player;
	}
	
	public EntityPresentor getAttackedEntity(){
		return victim;
	}
	
	public ReflectedItem getWeapon() {
		return weapon;
	}
	
	public double getDamage(){
		return damage;
	}
	
	public void setDamage(double damage){
		this.damage = damage;
	}
}
