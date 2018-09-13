package nl.knokko.races.event.response;

import nl.knokko.races.event.RaceKilledByRaceEvent;

public interface RaceKilledByRaceResponse {
	
	void execute(RaceKilledByRaceEvent event);
}