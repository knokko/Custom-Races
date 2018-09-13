package nl.knokko.races.event;

import nl.knokko.races.conditions.RacePresentor;
import nl.knokko.races.item.ReflectedItem;

public class RaceRightClickEvent extends RaceEvent {
	
	private final ReflectedItem item;

	public RaceRightClickEvent(RacePresentor player, ReflectedItem item) {
		super(player);
		this.item = item;
	}
	
	public RacePresentor getPlayer(){
		return player;
	}
	
	public ReflectedItem getItem(){
		return item;
	}
	
	public boolean usesItem(){
		return item != null;
	}
}
