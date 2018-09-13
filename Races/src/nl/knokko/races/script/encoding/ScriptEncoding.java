package nl.knokko.races.script.encoding;

import nl.knokko.races.base.ReflectedCause;
import nl.knokko.races.conditions.EntityPresentor;
import nl.knokko.races.conditions.RacePresentor;
import nl.knokko.races.event.parameter.ParameterType;
import nl.knokko.races.potion.ReflectedEffect;
import nl.knokko.races.potion.ReflectedEffectType;

/**
 * A = Action,
 * B = Block,
 * C = Condition,
 * D = ReflectedCause (damage cause),
 * E = Entity (EntityPresentor or RacePresentor),
 * F = Function (double),
 * I = Item,
 * P = Player (RacePresentor),
 * T = Text (String)
 */
public class ScriptEncoding {
	
	static {
		RacePresentor presentor;
	}
	
	public static abstract class Action {
		
		private final byte id;
		
		private final ParameterType[] params;
		
		public Action(byte id, ParameterType...params){
			this.id = id;
			this.params = params;
		}
		
		public byte getID(){
			return id;
		}
		
		public ParameterType[] getParams(){
			return params;
		}
		
		public abstract void execute(Object...args);
	}
	
	public static class Actions {
		
		public static final Action DAMAGE_ENTITY = new Action((byte)-128, ParameterType.ENTITY, ParameterType.DAMAGESOURCE, ParameterType.NUMBER){

			@Override
			public void execute(Object... args) {
				((EntityPresentor) args[0]).attack((ReflectedCause) args[1], (Double) args[2] * 2);
			}
		};
		
		public static final Action HEAL_ENTITY = new Action((byte)-127, ParameterType.ENTITY, ParameterType.NUMBER){

			@Override
			public void execute(Object... args) {
				((EntityPresentor) args[0]).heal((Double) args[1] * 2);
			}
		};
		
		public static final Action TELEPORT_ENTITY = new Action((byte)-126, ParameterType.ENTITY, ParameterType.NUMBER, ParameterType.NUMBER, ParameterType.NUMBER){

			@Override
			public void execute(Object... args) {
				((EntityPresentor) args[0]).teleport((Double)args[1], (Double)args[2], (Double)args[3]);
			}
		};
		
		/**
		 * Launch E; give it F1 m/s in direction X, F2 m/s in direction Y and F3 m/s in direction Z
		 */
		public static final Action LAUNCH_ENTITY = new Action((byte)-125, ParameterType.ENTITY, ParameterType.NUMBER, ParameterType.NUMBER, ParameterType.NUMBER){

			@Override
			public void execute(Object... args) {
				((EntityPresentor) args[0]).launch((Double)args[1], (Double)args[2], (Double)args[3]);
			}
		};
		
		public static final Action ADD_POTION_EFFECT = new Action((byte)-124, ParameterType.ENTITY, ParameterType.POTIONEFFECT, ParameterType.NUMBER){

			@Override
			public void execute(Object... args) {
				((EntityPresentor)args[0]).addPotionEffect(new ReflectedEffect((ReflectedEffectType)args[1], (Double)args[2], 1, true, false,));
			}
		};
		
		/**
		 * Give potion effect of type T and duration F seconds to E
		 */
		public static final byte POTION_EFFECT_ETF = -124;
		
		/**
		 * Give potion effect of type T and duration F1 seconds with level F2 to E
		 */
		public static final byte POTION_EFFECT_ETFF = -123;
		
		/**
		 * Set E on fire for F seconds
		 */
		public static final byte SET_FIRE_EF = -122;
		
		/**
		 * Let E cause an explosion at (F1,F2,F3) with power F4
		 */
		public static final byte EXPLOSION_EFFFF = -121;
	}
	
	public static class Entity {
		
		/**
		 * The main player involved in the event
		 */
		public static final byte THIS_PLAYER = -128;
		
		/**
		 * The other entity involved in the event
		 */
		public static final byte OTHER_ENTITY = -127;
		
		/**
		 * The entity that is the closest to (F1,F2,F3) with a maximum distance of F4 meters.
		 * This player will be ignored if C1
		 * The other entity will be ignored if C2
		 */
		public static final byte NEAREST_ENTITY_FFFFCC = -126;
	}
	
	public static class Text {
		
		/**
		 * The current race of P
		 */
		public static final byte RACE_P = -128;
		
		/**
		 * The name of E
		 */
		public static final byte NAME_E = -127;
		
		/**
		 * The value of choise T that P has chosen
		 */
		public static final byte CHOISE_PT = -126;
	}
}