package nl.knokko.races.plugin.data;

import nl.knokko.races.base.Race;
import nl.knokko.races.progress.RaceProgress;

public abstract class AbstractPlayerData {
	
	public abstract void onJoin();
	
	public abstract void onQuit();
	
	public abstract Race getCurrentRace();
	
	public abstract RaceProgress getProgress(Race race);
	
	public abstract void setCurrentRace(Race race);
	
	public RaceProgress getProgress(){
		return getProgress(getCurrentRace());
	}
}
