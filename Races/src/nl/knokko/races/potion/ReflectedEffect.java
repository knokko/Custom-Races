package nl.knokko.races.potion;

import java.awt.Color;

public class ReflectedEffect {
	
	private ReflectedEffectType type;
	
	private int duration;
	private byte amplifier;
	
	private boolean showParticles;
	private boolean isAmbient;
	
	private Color color;

	public ReflectedEffect(ReflectedEffectType type, int duration, int level, boolean showParticles, boolean isAmbient, Color color) {
		this.type = type;
		this.duration = duration;
		this.amplifier = (byte) (level - 1);
		this.showParticles = showParticles;
		this.isAmbient = isAmbient;
		this.color = color;
	}
	
	@Override
	public String toString(){
		return "reflected effect(" + type.toString() + "," + duration + "," + getLevel() + "," + showParticles + "," + isAmbient + "," + color + ")";
	}
	
	@Override
	public int hashCode(){
		return type.hashCode() * amplifier - duration;
	}
	
	@Override
	public boolean equals(Object other){
		if(other instanceof ReflectedEffect){
			ReflectedEffect re = (ReflectedEffect) other;
			return re.type.equals(type) && re.duration == duration && re.amplifier == amplifier && re.showParticles == showParticles && re.isAmbient == isAmbient && re.color.equals(color);
		}
		return false;
	}
	
	public ReflectedEffectType getType(){
		return type;
	}
	
	public int getDuration(){
		return duration;
	}
	
	public byte getAmplifier(){
		return amplifier;
	}
	
	public int getLevel(){
		return amplifier + 1;
	}
	
	public boolean showParticles(){
		return showParticles;
	}
	
	public boolean isAmbient(){
		return isAmbient;
	}
	
	public Color getColor(){
		return color;
	}
}
