package nl.knokko.races.event;

import nl.knokko.races.conditions.RacePresentor;

public abstract class RaceEvent {
	
	protected final RacePresentor player;

	public RaceEvent(RacePresentor race) {
		this.player = race;
	}
}
