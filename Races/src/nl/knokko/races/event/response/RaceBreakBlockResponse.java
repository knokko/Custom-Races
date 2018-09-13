package nl.knokko.races.event.response;

import nl.knokko.races.event.RaceBreakBlockEvent;

public interface RaceBreakBlockResponse {
	
	void execute(RaceBreakBlockEvent event);
}