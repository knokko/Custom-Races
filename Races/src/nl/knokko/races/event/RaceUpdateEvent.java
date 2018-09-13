package nl.knokko.races.event;

import nl.knokko.races.conditions.RacePresentor;

public class RaceUpdateEvent extends RaceEvent {

	public RaceUpdateEvent(RacePresentor player) {
		super(player);
	}
	
	public RacePresentor getPlayer(){
		return player;
	}
}
