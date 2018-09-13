package nl.knokko.races.event;

import nl.knokko.races.conditions.RacePresentor;

public class RaceKillRaceEvent extends RaceEvent {
	
	private final RacePresentor target;

	public RaceKillRaceEvent(RacePresentor race, RacePresentor target) {
		super(race);
		this.target = target;
	}
	
	public RacePresentor getKiller(){
		return player;
	}
	
	public RacePresentor getKilledRace(){
		return target;
	}
}
