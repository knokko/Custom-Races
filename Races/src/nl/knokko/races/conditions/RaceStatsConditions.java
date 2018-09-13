package nl.knokko.races.conditions;

import nl.knokko.races.base.Race;
import nl.knokko.races.progress.RaceProgress;

public interface RaceStatsConditions extends EntityConditions {
	
	RaceProgress getProgress();
	
	Race getRace();
	
	int getFoodLevel();
}
