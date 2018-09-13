package nl.knokko.races.event;

import nl.knokko.races.conditions.RacePresentor;

public class RaceAttackRaceEvent extends RaceEvent {
	
	private final RacePresentor victim;
	
	private double damage;

	public RaceAttackRaceEvent(RacePresentor attacker, RacePresentor victim, double damage) {
		super(attacker);
		this.victim = victim;
		this.damage = damage;
	}
	
	public RacePresentor getAttackingRace(){
		return player;
	}
	
	public RacePresentor getAttackedRace(){
		return victim;
	}
	
	public double getDamage(){
		return damage;
	}
	
	public void setDamage(double damage){
		this.damage = damage;
	}
}
