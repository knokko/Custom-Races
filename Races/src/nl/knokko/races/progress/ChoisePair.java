package nl.knokko.races.progress;

import nl.knokko.races.progress.RaceChoise.Value;

public class ChoisePair {
	
	private final RaceChoise choise;
	private Value value;

	public ChoisePair(RaceChoise choise) {
		this.choise = choise;
	}
	
	@Override
	public String toString(){
		return "Choise " + choise.getID() + " is " + value.getName();
	}
	
	@Override
	public int hashCode(){
		return choise.hashCode() + value.hashCode();
	}
	
	@Override
	public boolean equals(Object other){
		if(other instanceof ChoisePair){
			ChoisePair cp = (ChoisePair) other;
			return cp.choise.equals(choise) && cp.value.equals(value);
		}
		return false;
	}
	
	public RaceChoise getChoise(){
		return choise;
	}
	
	public Value getValue(){
		return value;
	}
	
	public void setValue(Value value){
		this.value = value;
	}
}
