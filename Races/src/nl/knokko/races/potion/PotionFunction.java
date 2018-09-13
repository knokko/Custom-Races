package nl.knokko.races.potion;

import java.awt.Color;

import nl.knokko.races.condition.Condition;
import nl.knokko.races.conditions.RaceStatsConditions;
import nl.knokko.races.function.Function;
import nl.knokko.races.utils.BitBuffer;

public class PotionFunction {
	
	private final ReflectedEffectType type;
	
	private final Function durationFunction;
	private final Function levelFunction;
	
	private final Condition particles;
	private final Condition ambient;
	
	private final Function redFunction;
	private final Function greenFunction;
	private final Function blueFunction;
	
	public static PotionFunction fromBits(BitBuffer buffer){
		return new PotionFunction(new ReflectedEffectType(buffer.readString()), Function.fromBits(buffer), Function.fromBits(buffer),
				Condition.fromBits(buffer), Condition.fromBits(buffer), Function.fromBits(buffer), Function.fromBits(buffer), Function.fromBits(buffer));
	}

	public PotionFunction(ReflectedEffectType type, Function duration, Function level, Condition showParticles, Condition isAmbient, Function red, Function green, Function blue) {
		this.type = type;
		durationFunction = duration;
		levelFunction = level;
		particles = showParticles;
		ambient = isAmbient;
		redFunction = red;
		greenFunction = green;
		blueFunction = blue;
	}
	
	public ReflectedEffectType getType(){
		return type;
	}
	
	public Function getDuration(){
		return durationFunction;
	}
	
	public Function getLevel(){
		return levelFunction;
	}
	
	public Condition getParticles(){
		return particles;
	}
	
	public Condition getAmbient(){
		return ambient;
	}
	
	public Function getRed(){
		return redFunction;
	}
	
	public Function getGreen(){
		return greenFunction;
	}
	
	public Function getBlue(){
		return blueFunction;
	}
	
	public ReflectedEffect getEffect(RaceStatsConditions stats){
		int duration = (int) Math.round(durationFunction.value(stats) * 20);
		int level = levelFunction.intValue(stats);
		if(duration > 0 && level != 0){
			return new ReflectedEffect(type, duration, level, particles.value(stats), ambient.value(stats), 
				new Color(ensureColor(redFunction.floatValue(stats)), ensureColor(greenFunction.floatValue(stats)),
						ensureColor(blueFunction.floatValue(stats))));
			}
		return null;
	}
	
	public void save(BitBuffer buffer){
		buffer.addString(type.getType());
		durationFunction.save(buffer);
		levelFunction.save(buffer);
		particles.save(buffer);
		ambient.save(buffer);
		redFunction.save(buffer);
		greenFunction.save(buffer);
		blueFunction.save(buffer);
	}
	
	private float ensureColor(float value){
		if(value > 1)
			value = 1;
		if(value < 0)
			value = 0;
		return value;
	}
}
