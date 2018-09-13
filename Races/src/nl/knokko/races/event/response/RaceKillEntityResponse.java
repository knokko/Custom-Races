package nl.knokko.races.event.response;

import nl.knokko.races.event.RaceKillEntityEvent;

public interface RaceKillEntityResponse {
	
	void execute(RaceKillEntityEvent event);
}