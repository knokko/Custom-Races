package nl.knokko.races.event.response;

import nl.knokko.races.event.RaceAttackEntityEvent;

public interface RaceAttackEntityResponse {
	
	void execute(RaceAttackEntityEvent event);
}