package nl.knokko.races.event;

import nl.knokko.races.conditions.EntityPresentor;
import nl.knokko.races.conditions.RacePresentor;

public class RaceKillEntityEvent extends RaceEvent {
	
	private final EntityPresentor target;

	public RaceKillEntityEvent(RacePresentor race, EntityPresentor target) {
		super(race);
		this.target = target;
	}
	
	public RacePresentor getKiller(){
		return player;
	}
	
	public EntityPresentor getKilledEntity(){
		return target;
	}
}
