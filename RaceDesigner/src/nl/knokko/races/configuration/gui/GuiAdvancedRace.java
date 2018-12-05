package nl.knokko.races.configuration.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import nl.knokko.races.base.RaceFactory;
import nl.knokko.races.base.RaceFactory.AdvancedRace;
import nl.knokko.races.base.RaceFactory.AdvancedRace.AdvancedEquipment;
import nl.knokko.races.base.ReflectedCause;
import nl.knokko.races.condition.*;
import nl.knokko.races.configuration.RaceConfigFrame;
import nl.knokko.races.configuration.gui.button.GuiButton;
import nl.knokko.races.configuration.gui.button.GuiTextButton;
import nl.knokko.races.function.*;
import nl.knokko.races.potion.PermanentPotionFunction;
import nl.knokko.races.potion.PotionFunction;
import nl.knokko.races.potion.ReflectedEffectType;
import nl.knokko.races.progress.ProgressType;
import nl.knokko.races.progress.RaceChoise;
import nl.knokko.races.progress.RaceChoise.Value;
import nl.knokko.races.progress.ValueType;
import nl.knokko.races.utils.BitBuffer;

public class GuiAdvancedRace extends Gui {
	
	private static final Color BACKGROUND = Color.BLUE;
	private static final Color BUTTON = Color.CYAN;
	private static final Font ERROR_FONT = new Font("TimesRoman", Font.PLAIN, 30);
	
	private static void setGui(Gui gui){
		RaceConfigFrame.instance().setGui(gui);
	}
	
	private static String error;
	private final TypeButton nameButton;
	
	private final General general;
	//TODO replace these effects with events
	//private final OnHitEffects onHitEffects;
	//private final OnAttackEffects onAttackEffects;
	private final PermanentEffects permanentEffects;
	private final DamageResistances damageResistances;
	private final EffectResistances effectResistances;
	private final Choises choises;
	private final Fields fields;
	private final Functions functions;
	private final Events events;
	private final Equipment equipment;
	
	public GuiAdvancedRace(AdvancedRace race){
		general = new General(race);
		//onHitEffects = new OnHitEffects(race);
		//onAttackEffects = new OnAttackEffects(race);
		permanentEffects = new PermanentEffects(race);
		damageResistances = new DamageResistances(race);
		effectResistances = new EffectResistances(race);
		choises = new Choises(race);
		fields = new Fields(race);
		functions = new Functions(race);
		events = new Events(race);
		equipment = new Equipment(race);
		nameButton = new TypeButton(race.getName(), Color.ORANGE, 50, 250, 200, 300);
		addButtons();
	}

	public GuiAdvancedRace() {
		general = new General();
		//onHitEffects = new OnHitEffects();
		//onAttackEffects = new OnAttackEffects();
		permanentEffects = new PermanentEffects();
		damageResistances = new DamageResistances();
		effectResistances = new EffectResistances();
		choises = new Choises();
		fields = new Fields();
		functions = new Functions();
		events = new Events();
		equipment = new Equipment();
		nameButton = new TypeButton("Name", Color.ORANGE, 50, 250, 200, 300);
		addButtons();
	}
	
	static void setError(String error){
		GuiAdvancedRace.error = error;
		RaceConfigFrame.markChange();
	}
	
	private void addButtons(){
		addButton(new GuiTextButton("Cancel", Color.RED, 50, 500, 200, 550){

			@Override
			public void click() {
				setGui(new GuiMain());
			}
		});
		addButton(new GuiTextButton("Save", Color.GREEN, 50, 150, 200, 200){

			@Override
			public void click() {
				try {
					BitBuffer buffer = new BitBuffer(16000);
					RaceFactory.saveAsAdvancedRace1(buffer, getUpdateFrequency(), getFields(), getCustomFunctions(), getChoises(), general.health, 
							general.damage, general.strength, general.speed, general.attackSpeed, general.armor, 
							general.archery, general.onHitFire, general.onAttackFire, getHitPotionEffects(), 
							getAttackPotionEffects(), getPermanentEffects(), getDamageResistances(), getEffectResistances(), equipment.equipment);
					//time for a reform...
					buffer.save(new File(GuiChooseRace.getFolder() + File.separator + nameButton.getText() + ".race"));
				} catch(Exception ex){
					error = ex.getLocalizedMessage();
					RaceConfigFrame.markChange();
				}
			}
		});
		addButton(nameButton);
		addButton(new GuiTextButton("General", BUTTON, 250, 50, 450, 100){

			@Override
			public void click() {
				setGui(general);
			}
		});
		/*
		addButton(new GuiTextButton("Effects applied to attackers", BUTTON, 250, 120, 600, 170){

			@Override
			public void click() {
				setGui(onHitEffects);
			}
		});
		addButton(new GuiTextButton("Effects applied to targets", BUTTON, 250, 190, 600, 240){

			@Override
			public void click() {
				setGui(onAttackEffects);
			}
		});
		*/
		addButton(new GuiTextButton("Permanent potion effects", BUTTON, 250, 260, 600, 310){

			@Override
			public void click() {
				setGui(permanentEffects);
			}
		});
		addButton(new GuiTextButton("Damage resistances", BUTTON, 250, 330, 500, 380){
			
			@Override
			public void click() {
				setGui(damageResistances);
			}
		});
		addButton(new GuiTextButton("Effect resistances", BUTTON, 250, 400, 500, 450){
			
			@Override
			public void click() {
				setGui(effectResistances);
			}
		});
		addButton(new GuiTextButton("Player Choises", BUTTON, 650, 50, 880, 100){
			
			@Override
			public void click() {
				setGui(choises);
			}
		});
		addButton(new GuiTextButton("Variables", BUTTON, 650, 120, 880, 170){
			
			@Override
			public void click() {
				setGui(fields);
			}
		});
		addButton(new GuiTextButton("Custom functions", BUTTON, 650, 190, 880, 240){
			
			@Override
			public void click() {
				setGui(functions);
			}
		});
	}

	@Override
	protected Color getBackGroundColor() {
		return BACKGROUND;
	}
	
	@Override
	public void paint(Graphics g){
		super.paint(g);
		if(error != null){
			g.setColor(Color.RED);
			g.setFont(ERROR_FONT);
			g.drawString(error, 230, 30);
		}
	}
	
	//TODO complete all underlying methods
	private double getUpdateFrequency() {
		throw new UnsupportedOperationException(); // TODO implement this
	}
	
	private List<ProgressType> getFields(){
		List<ProgressType> fieldList = fields.fields;
		if(fieldList == null) throw new NullPointerException("fieldList");
		for(ProgressType pt : fieldList)
			if(pt.getName() == null || pt.getType() == null)
				throw new NullPointerException("ProgressType(" + pt.getName() + "," + pt.getType() + ")");
		return fieldList;
	}
	
	private List<NamedFunction> getCustomFunctions(){
		List<NamedFunction> functionList = new ArrayList<NamedFunction>();
		functionList.addAll(functions.functions);
		return functionList;
	}
	
	private List<RaceChoise> getChoises(){
		List<RaceChoise> choiseList = new ArrayList<RaceChoise>();
		choiseList.addAll(choises.choises);
		return choiseList;
	}
	
	private PermanentPotionFunction[] getPermanentEffects(){
		PermanentPotionFunction[] permEffects = new PermanentPotionFunction[0];
		return permEffects;
	}
	
	private PotionFunction[] getHitPotionEffects() {
		throw new UnsupportedOperationException(); // TODO implement this
	}
	
	private PotionFunction[] getAttackPotionEffects() {
		throw new UnsupportedOperationException(); // TODO implement this
	}
	
	private Function[] getDamageResistances(){
		return damageResistances.resistances;
	}
	
	private Map<ReflectedEffectType,Function> getEffectResistances(){
		Map<ReflectedEffectType,Function> map = new HashMap<ReflectedEffectType,Function>();
		for(EffectEntry entry : effectResistances.list)
			map.put(new ReflectedEffectType(entry.type), entry.function);
		return map;
	}
	
	private Collection<Function> getAllFunctions(){
		Collection<Function> functions = new ArrayList<Function>(30);
		addAll(functions, general.health, general.damage, general.strength, general.speed, general.attackSpeed,
				general.armor, general.archery);
		addAll(functions, damageResistances.resistances);
		for(EffectEntry entry : effectResistances.list)
			functions.add(entry.function);
		for(NamedFunction function : this.functions.functions)
			functions.add(function.getFunction());
		return functions;
	}
	
	private void addAll(Collection<Function> allFunctions, Function... functions){
		for(Function function : functions)
			allFunctions.add(function);
	}
	
	private class AdvancedGui extends Gui {
		
		void setError(String error){
			GuiAdvancedRace.error = error;
			RaceConfigFrame.markChange();
		}

		@Override
		protected Color getBackGroundColor() {
			return BACKGROUND;
		}
		
		private Field find(String name) {
			return find(name, AdvancedGui.this.getClass());
		}
		
		private Field find(String name, Class<?> clas) {
			try {
				return clas.getDeclaredField(name);
			} catch(Exception ex){
				return find(name, clas.getSuperclass());
			}
		}
		
		protected void addFunctionButton(final String fieldName, String name, int minX, int minY, int maxX, int maxY){
			addButton(new FunctionButton(new FunctionResult(){

				public void setFunction(Function function) {
					try {
						find(fieldName).set(AdvancedGui.this, function);
					} catch(Exception ex){
						throw new Error(ex);
					}
				}
				
				public Function getCurrent(){
					try {
						return (Function) find(fieldName).get(AdvancedGui.this);
					} catch(Exception ex){
						throw new Error(ex);
					}
				}

				public Gui getParent() {
					return AdvancedGui.this;
				}
				
			}, name, minX, minY, maxX, maxY));
			try {
				find(fieldName);
			} catch(Exception ex){
				throw new Error(ex);
			}
		}
		
		protected void addConditionButton(final String fieldName, String name, int minX, int minY, int maxX, int maxY){
			addButton(new ConditionButton(new ConditionResult(){

				public void setCondition(Condition condition) {
					try {
						find(fieldName).set(AdvancedGui.this, condition);
					} catch(Exception ex){
						throw new Error(ex);
					}
				}
				
				public Condition getCurrent(){
					try {
						return (Condition) find(fieldName).get(AdvancedGui.this);
					} catch(Exception ex){
						throw new Error(ex);
					}
				}

				public Gui getParent() {
					return AdvancedGui.this;
				}
				
			}, name, minX, minY, maxX, maxY));
			try {
				find(fieldName);
			} catch(Exception ex){
				throw new Error(ex);
			}
		}
		
		@Override
		public void paint(Graphics g){
			super.paint(g);
			if(error != null){
				g.setColor(Color.RED);
				g.setFont(ERROR_FONT);
				g.drawString(error, 230, 30);
			}
		}
	}
	
	private class AdvancedMainGui extends AdvancedGui {
		
		private AdvancedMainGui() {
			addButton(new GuiTextButton("Overview", Color.YELLOW, 20, 50, 150, 100){

				@Override
				public void click() {
					setGui(GuiAdvancedRace.this);
				}
			});
		}
	}
	
	private class AdvancedScrollMainGui extends AdvancedScrollGui {
		
		private AdvancedScrollMainGui(){
			addButton(new GuiTextButton("Overview", Color.YELLOW, 20, 50, 150, 100){

				@Override
				public void click() {
					setGui(GuiAdvancedRace.this);
				}
			});
		}
	}
	
	private class AdvancedScrollGui extends AdvancedGui {
		
		private int scroll;
		
		protected int maxY;
		protected boolean buttonChange = true;;
		
		@Override
		public void scroll(int amount){
			scroll += amount;
		}
		
		@Override
		public void paint(Graphics g){
			BufferedImage image = new BufferedImage(RaceConfigFrame.WIDTH, getMaxY(), BufferedImage.TYPE_INT_RGB);
			Graphics2D g2 = image.createGraphics();
			g.setColor(BACKGROUND);
			g.fillRect(0, 0, RaceConfigFrame.WIDTH, RaceConfigFrame.HEIGHT);
			g2.setColor(BACKGROUND);
			g2.fillRect(0, 0, image.getWidth(), image.getHeight());
			super.paint(g2);
			g2.dispose();
			g.drawImage(image, 0, -scroll, null);
			if(error != null){
				g.setColor(Color.RED);
				g.setFont(ERROR_FONT);
				g.drawString(error, 230, 30);
			}
		}
		
		@Override
		public void click(int x, int y){
			super.click(x, y + scroll);
		}
		
		@Override
		protected void addButton(GuiButton button){
			super.addButton(button);
			buttonChange = true;
		}
		
		protected int getMaxY(){
			if(buttonChange){
				for(GuiButton button : buttons){
					if(button.maxY() > maxY)
						maxY = button.maxY();
				}
				buttonChange = false;
			}
			return maxY;
		}
	}
	
	private class FunctionButton extends GuiTextButton {
		
		private final FunctionResult caller;

		public FunctionButton(FunctionResult caller, String text, int minX, int minY, int maxX, int maxY) {
			super(text, caller.getCurrent() != null ? Color.GREEN : Color.WHITE, minX, minY, maxX, maxY);
			this.caller = caller;
		}

		@Override
		public void click() {
			setGui(new GuiFunction(caller, this));
		}
		
		@Override
		public void paint(Graphics g){
			super.paint(g);
			Function current = caller.getCurrent();
			if(current != null)
				g.drawString(current.toString(), maxX() + width() / 20, maxY() - height() / 3);
		}
	}
	
	private class ConditionButton extends GuiTextButton {
		
		private final ConditionResult caller;
		
		public ConditionButton(ConditionResult caller, String text, int minX, int minY, int maxX, int maxY){
			super(text, caller.getCurrent() != null ? Color.GREEN : Color.WHITE, minX, minY, maxX, maxY);
			this.caller = caller;
		}
		
		@Override
		public void click(){
			setGui(new GuiCondition(caller, this));
		}
		
		@Override
		public void paint(Graphics g){
			super.paint(g);
			Condition current = caller.getCurrent();
			if(current != null)
				g.drawString(current.toString(), maxX() + width() / 20, maxY() - height() / 2);
		}
	}
	
	private static class TypeButton extends GuiTextButton {
		
		public TypeButton(String text, Color color, int minX, int minY, int maxX, int maxY) {
			super(text, color, minX, minY, maxX, maxY);
		}

		private boolean enabled;
		
		@Override
		public void click(int x, int y){
			if(isHit(x, y))
				click();
			else if(enabled)
				disable();
		}

		@Override
		public void click() {
			if(enabled)
				disable();
			else
				enable();
		}
		
		@Override
		public void type(char character){
			if(enabled){
				if(isValid(character)){
					text += character;
					RaceConfigFrame.markChange();
				}
			}
		}
		
		@Override
		public void press(int code){
			if(enabled){
				if(code == KeyEvent.VK_BACK_SPACE && text.length() > 0){
					text = text.substring(0, text.length() - 1);
					RaceConfigFrame.markChange();
				}
				else if(code == KeyEvent.VK_ENTER || code == KeyEvent.VK_ESCAPE)
					disable();
			}
		}

		private void enable(){
			enabled = true;
			textColor = Color.YELLOW;
			borderColor = Color.YELLOW;
			RaceConfigFrame.markChange();
		}
		
		private void disable(){
			enabled = false;
			textColor = Color.BLACK;
			borderColor = Color.BLACK;
			RaceConfigFrame.markChange();
		}
	}
	
	private static boolean isValid(char character){
		return (int)character != 0 && (int) character != 8 && character != KeyEvent.CHAR_UNDEFINED;
	}
	
	private class GuiFunction extends AdvancedGui {
		
		private final Color COLOR = Color.MAGENTA;
		
		private final FunctionResult call;
		private final FunctionButton button;
		
		public GuiFunction(FunctionResult caller, FunctionButton button){
			this.call = caller;
			this.button = button;
			addButton(new GuiTextButton("Cancel", Color.ORANGE, 50, 50, 150, 100){

				@Override
				public void click() {
					setGui(call.getParent());
				}
			});
			addSubGui(Constant.class, "Number", 200, 50, 450, 100);
			addDirectButton(FunctionTime.class, "World Time (0 to 24000)", 200, 120, 450, 170);
			addDirectButton(FunctionDimension.class, "Dimension ID", 200, 190, 450, 240);
			addDirectButton(FunctionTemperature.class, "Temperature", 200, 260, 450, 310);
			addSubGui(Fields.class, "Custom Variable", 200, 330, 450, 380);
			addSubGui(Functions.class, "Custom Functions", 200, 400, 450, 450);
			addSubGui(Sum.class, "Sum", 500, 50, 700, 100);
			addSubGui(Substract.class, "Substract", 500, 120, 700, 170);
			addSubGui(Multiply.class, "Multiply", 500, 190, 700, 240);
			addSubGui(Divide.class, "Divide", 500, 260, 700, 310);
			addSubGui(Power.class, "Power", 500, 330, 700, 380);
			addSubGui(Sqrt.class, "Sqrt root", 500, 400, 700, 450);
			addSubGui(MathFunctions.class, "Math Functions", 500, 470, 700, 520);
			addSubGui(MathConstants.class, "Math Constants", 500, 540, 700, 590);
		}
		
		private void addSubGui(final Class<? extends Gui> type, String text, int minX, int minY, int maxX, int maxY){
			addButton(new GuiTextButton(text, COLOR, minX, minY, maxX, maxY){

				@Override
				public void click() {
					try {
						setGui(type.newInstance());
					} catch (Exception e) {
						throw new Error(e);//This should NEVER happen
					} 
				}
			});
		}
		
		private void addDirectButton(final Class<? extends Function> type, String text, int minX, int minY, int maxX, int maxY){
			addButton(new GuiTextButton(text, COLOR, minX, minY, maxX, maxY){

				@Override
				public void click() {
					try {
						set(type.newInstance());
					} catch(Exception e){
						throw new Error(e);//This should NEVER happen
					}
				}
			});
		}
		
		private class ChildFunctionGui extends AdvancedGui {
			
			String error;
			
			private ChildFunctionGui(){
				addButton(new GuiTextButton("Cancel", Color.ORANGE, 20, 50, 120, 100){

					@Override
					public void click() {
						setGui(GuiFunction.this);
					}
				});
			}
			
			@Override
			public void paint(Graphics g){
				super.paint(g);
				if(error != null){
					g.setColor(Color.RED);
					g.drawString(error, 400, 400);
				}
			}
		}
		
		private class ChildFunctionScrollGui extends AdvancedScrollGui {
			
			private String error;
			
			private ChildFunctionScrollGui(){
				addButton(new GuiTextButton("Cancel", Color.ORANGE, 20, 50, 120, 100){

					@Override
					public void click() {
						setGui(GuiFunction.this);
					}
				});
			}
			
			@Override
			public void paint(Graphics g){
				super.paint(g);
				if(error != null){
					g.setColor(Color.RED);
					g.drawString(error, 400, 400);
				}
			}
		}
		
		private void set(Function function){
			call.setFunction(function);
			if(function != null)
				button.setColor(Color.GREEN);
			else
				button.setColor(Color.WHITE);
			setGui(call.getParent());
		}
		
		private class Constant extends ChildFunctionGui {
			
			private String error;
			
			private Constant(){
				final TypeButton typeButton = new TypeButton("", COLOR, 300, 150, 500, 200);
				addButton(typeButton);
				addButton(new GuiTextButton("Done", Color.GREEN, 20, 250, 120, 300){

					@Override
					public void click() {
						try {
							set(FunctionConstant.getConstant(typeButton.getText()));
						} catch(NumberFormatException nfe){
							error = "Invalid number";
						}
					}
				});
			}
			
			@Override
			public void paint(Graphics g){
				super.paint(g);
				g.setColor(Color.BLACK);
				g.drawString("Insert number:", 50, 170);
				if(error != null){
					g.setColor(Color.RED);
					g.drawString(error, 150, 350);
				}
			}
		}
		
		private class Fields extends ChildFunctionScrollGui {
			
			private Fields(){
				List<ProgressType> fields = getFields();
				int index = 0;
				for(final ProgressType field : fields){
					final boolean useable = field.getType() != ValueType.STRING && field.getType() != ValueType.BOOLEAN;
					addButton(new GuiTextButton(field.getName(), useable ? Color.MAGENTA : Color.GRAY, 200, 50 + index * 70, 600, 100 + index * 70){

						@Override
						public void click() {
							if(useable)
								set(new FunctionVariable(field.getName()));
						}
					});
					index++;
				}
			}
		}
		
		private class Functions extends ChildFunctionScrollGui {
			
			private Functions(){
				List<NamedFunction> functions = getCustomFunctions();
				int index = 0;
				for(final NamedFunction function : functions){
					addButton(new GuiTextButton(function.getName(), Color.MAGENTA, 200, 50 + index * 70, 600, 100 + index * 70){

						@Override
						public void click() {
							set(new FunctionFunction(function.getName()));
						}
					});
					index++;
				}
			}
		}
		
		private abstract class DoubleFunctionGui extends ChildFunctionGui {
			
			Function function1;
			Function function2;
			
			private DoubleFunctionGui(){
				addFunctionButton("function1", "Number 1", 200, 50, 400, 100);
				addFunctionButton("function2", "Number 2", 200, 150, 400, 200);
				addButton(new GuiTextButton("Done", Color.GREEN, 20, 250, 120, 300){

					@Override
					public void click() {
						if(function1 == null){
							setError("Number 1 is not specified");
							return;
						}
						if(function2 == null){
							setError("Number 2 is not specified");
							return;
						}
						set(create());
					}
				});
			}
			
			abstract Function create();
		}
		
		private class Sum extends DoubleFunctionGui {

			@Override
			Function create() {
				return new FunctionSum(function1, function2);
			}
		}
		
		private class Substract extends DoubleFunctionGui {

			@Override
			Function create() {
				return new FunctionSubstract(function1, function2);
			}
		}
		
		private class Multiply extends DoubleFunctionGui {

			@Override
			Function create() {
				return new FunctionMultiply(function1, function2);
			}
		}
		
		private class Divide extends DoubleFunctionGui {

			@Override
			Function create() {
				return new FunctionDivide(function1, function2);
			}
		}
		
		private class Power extends ChildFunctionGui {
			
			Function base;
			Function exponent;
			
			private Power(){
				addFunctionButton("base", "Base number", 200, 50, 400, 100);
				addFunctionButton("exponent", "Exponent", 200, 150, 400, 200);
				addButton(new GuiTextButton("Done", Color.GREEN, 20, 250, 120, 300){

					@Override
					public void click() {
						if(base == null){
							Power.this.error = "Base number is not specified";
							return;
						}
						if(exponent == null){
							Power.this.error = "Exponent is not specified";
							return;
						}
						set(new FunctionPower(base, exponent));
					}
				});
			}
		}
		
		private class Sqrt extends ChildFunctionGui {
			
			Function exponent;
			Function function;
			
			private Sqrt(){
				exponent = new FunctionConstant.Byte((byte) 2);
				addFunctionButton("exponent", "Exponent (base 2)", 200, 50, 400, 100);
				addFunctionButton("function", "Number", 200, 150, 400, 200);
				addButton(new GuiTextButton("Done", Color.GREEN, 20, 250, 120, 300){

					@Override
					public void click() {
						if(function == null){
							Sqrt.this.error = "Base number is not specified";
							return;
						}
						if(exponent == null){
							Sqrt.this.error = "Exponent is not specified";
							return;
						}
						set(new FunctionMultiply(function, new FunctionDivide(new FunctionConstant.Byte((byte) 1), exponent)));
					}
				});
			}
		}
		
		private class MathConstants extends ChildFunctionGui {
			
			private MathConstants(){
				addButton(Math.PI, "π", 200, 50, 300, 100);
				addButton(Math.E, "e", 200, 150, 300, 200);
			}
			
			private void addButton(final double value, String text, int minX, int minY, int maxX, int maxY){
				buttons.add(new GuiTextButton(text, COLOR, minX, minY, maxX, maxY){

					@Override
					public void click() {
						set(new FunctionConstant.Double(value));
					}
				});
			}
		}
		
		private class MathFunctions extends ChildFunctionGui {
			
			private MathFunctions(){
				addButton(new Abs(), "abs(x)", 200, 50, 300, 100);
				addButton(new Negate(), "-(x)", 200, 120, 300, 170);
				addButton(new SimpleSqrt(), "sqrt(x)", 200, 190, 300, 240);
				addButton(new Log(), "ylog(x)", 200, 260, 300, 310);
				
				addButton(new Tan(), "tan(x)", 350, 50, 450, 100);
				addButton(new Sin(), "sin(x)", 350, 120, 450, 170);
				addButton(new Cos(), "cos(x)", 350, 190, 450, 240);
				addButton(new ArcTan(), "arctan(x)", 350, 260, 450, 310);
				addButton(new ArcSin(), "arcsin(x)", 350, 330, 450, 380);
				addButton(new ArcCos(), "arccos(x)", 400, 190, 450, 450);
			}
			
			private void addButton(final Gui gui, String text, int minX, int minY, int maxX, int maxY){
				addButton(new GuiTextButton(text, COLOR, minX, minY, maxX, maxY){

					@Override
					public void click() {
						setGui(gui);
					}
				});
			}
		}
		
		private abstract class SimpleMathFunction extends ChildFunctionGui {
			
			Function function;
			
			private SimpleMathFunction(){
				addFunctionButton("function", "Function", 200, 50, 400, 100);
				addButton(new GuiTextButton("Done", Color.GREEN, 20, 250, 120, 300){

					@Override
					public void click() {
						if(function == null){
							error = "You need to specify a function.";
							return;
						}
						set(create());
					}
				});
			}
			
			abstract Function create();
		}
		
		private class Abs extends SimpleMathFunction {

			@Override
			Function create() {
				return new FunctionAbs(function);
			}
		}
		
		private class Negate extends SimpleMathFunction {

			@Override
			Function create() {
				return new FunctionNegate(function);
			}
			
		}
		
		private class Tan extends SimpleMathFunction {

			@Override
			Function create() {
				return new FunctionTan(function);
			}
		}
		
		private class Sin extends SimpleMathFunction {

			@Override
			Function create() {
				return new FunctionSin(function);
			}
		}
		
		private class Cos extends SimpleMathFunction {

			@Override
			Function create() {
				return new FunctionCos(function);
			}
		}
		
		private class ArcTan extends SimpleMathFunction {

			@Override
			Function create() {
				return new FunctionArcTan(function);
			}
		}
		
		private class ArcSin extends SimpleMathFunction {

			@Override
			Function create() {
				return new FunctionArcSin(function);
			}
		}
		
		private class ArcCos extends SimpleMathFunction {

			@Override
			Function create() {
				return new FunctionArcCos(function);
			}
		}
		
		private class SimpleSqrt extends SimpleMathFunction {
			
			@Override
			Function create(){
				return new FunctionSqrt(function);
			}
		}
		
		private class Log extends ChildFunctionGui {
			
			Function function;
			Function exponent;
			
			private Log(){
				addFunctionButton("function", "x", 200, 50, 400, 100);
				addFunctionButton("exponent", "y", 200, 150, 400, 200);
				addButton(new GuiTextButton("Done", Color.GREEN, 20, 250, 120, 300){

					@Override
					public void click() {
						if(function == null){
							setError("You need to specify the X (function).");
							return;
						}
						if(exponent == null){
							setError("You need to specify the Y (exponent).");
						}
						set(new FunctionLog(function, exponent));
					}
				});
			}
		}
	}
	
	private class GuiCondition extends AdvancedGui {
		
		final Color COLOR = Color.MAGENTA;
		
		private final ConditionResult call;
		private final GuiTextButton button;
		
		public GuiCondition(ConditionResult caller, GuiTextButton button){
			super();
			call = caller;
			this.button = button;
			addButton(new GuiTextButton("Cancel", Color.ORANGE, 50, 50, 150, 100){

				@Override
				public void click() {
					setGui(call.getParent());
				}
			});
			addConditionButton(new ConditionTrue(), "Always yes", 200, 50, 300, 100);
			addConditionButton(new ConditionFalse(), "Always no", 200, 150, 300, 200);
			addSubGui(And.class, "2 conditions are yes", 300, 50, 500, 100);
			addSubGui(Or.class, "at least 1 of 2 conditions is yes", 300, 120, 500, 170);
			addSubGui(Equal.class, "2 numbers are equal", 300, 190, 500, 240);
			addSubGui(NotEqual.class, "2 numbers are not equal", 300, 260, 500, 410);
			addSubGui(Greater.class, "number 1 > number 2", 300, 430, 500, 480);
			addSubGui(Smaller.class, "number 1 < 2", 300, 500, 500, 550);
			addSubGui(GreaterEqual.class, "number 1 >= number 2", 300, 570, 500, 620);
			addSubGui(SmallerEqual.class, "number 1 <= number 2", 300, 640, 500, 690);
		}
		
		private void addConditionButton(final Condition value, String text, int minX, int minY, int maxX, int maxY){
			addButton(new GuiTextButton(text, COLOR, minX, minY, maxX, maxY){

				@Override
				public void click() {
					set(value);
				}
			});
		}
		
		private void addSubGui(final Class<? extends ChildConditionGui> type, String text, int minX, int minY, int maxX, int maxY){
			addButton(new GuiTextButton(text, COLOR, minX, minY, maxX, maxY){

				@Override
				public void click() {
					try {
						setGui(type.newInstance());
					} catch(Exception ex){
						throw new Error(ex);//This should NOT happen!
					}
				}
			});
		}
		
		void set(Condition condition){
			call.setCondition(condition);
			if(condition == null)
				button.setColor(Color.WHITE);
			else
				button.setColor(Color.GREEN);
			setGui(call.getParent());
		}
		
		private abstract class ChildConditionGui extends AdvancedGui {
			
			String error;
			
			private ChildConditionGui(){
				addButton(new GuiTextButton("Cancel", Color.ORANGE, 20, 50, 120, 100){

					@Override
					public void click() {
						setGui(GuiCondition.this);
					}
				});
				addButton(new GuiTextButton("Done", Color.GREEN, 20, 300, 120, 350){

					@Override
					public void click() {
						complete();
					}
				});
			}
			
			@Override
			public void paint(Graphics g){
				super.paint(g);
				if(error != null){
					g.setColor(Color.RED);
					g.drawString(error, 400, 400);
				}
			}
			
			/**
			 * THIS RETURNS TRUE IF THE OBJECT IS NULL!
			 * @param object The function of condition
			 * @param name The button name of the object
			 * @return true if object is null, false otherwise
			 */
			boolean notNull(Object object, String name){
				if(object == null){
					setError("Click on '" + name + "' to specify it.");
					return true;
				}
				return false;
			}
			
			abstract void complete();
		}
		
		private abstract class DoubleConditionGui extends ChildConditionGui {
			
			Condition condition1;
			Condition condition2;
			
			private DoubleConditionGui(){
				addConditionButton("condition1", "Condition 1", 200, 150, 400, 200);
				addConditionButton("condition2", "Condition 2", 200, 250, 400, 300);
			}

			@Override
			void complete() {
				if(notNull(condition1, "Condition1")) return;
				if(notNull(condition2, "Condition2")) return;
				set(create());
			}
			
			abstract Condition create();
		}
		
		private class And extends DoubleConditionGui {

			@Override
			Condition create() {
				return new ConditionAnd(condition1, condition2);
			}
		}
		
		private class Or extends DoubleConditionGui {

			@Override
			Condition create() {
				return new ConditionOr(condition1, condition2);
			}
		}
		
		private abstract class DoubleFunctionGui extends ChildConditionGui {
			
			Function function1;
			Function function2;
			
			private DoubleFunctionGui(){
				addFunctionButton("function1", "Number 1", 200, 150, 400, 200);
				addFunctionButton("function2", "Number 2", 200, 250, 400, 300);
			}

			@Override
			void complete() {
				if(notNull(function1, "Number 1")) return;
				if(notNull(function2, "Number 2")) return;
				set(create());
			}
			
			abstract Condition create();
		}
		
		private class Equal extends DoubleFunctionGui {

			@Override
			Condition create() {
				return new ConditionEqual(function1, function2);
			}
		}
		
		private class NotEqual extends DoubleFunctionGui {

			@Override
			Condition create() {
				return new ConditionNotEqual(function1, function2);
			}
		}
		
		private class Greater extends DoubleFunctionGui {

			@Override
			Condition create() {
				return new ConditionGreater(function1, function2);
			}
		}
		
		private class Smaller extends DoubleFunctionGui {

			@Override
			Condition create() {
				return new ConditionSmaller(function1, function2);
			}
		}
		
		private class GreaterEqual extends DoubleFunctionGui {

			@Override
			Condition create() {
				return new ConditionEqualGreater(function1, function2);
			}
		}
		
		private class SmallerEqual extends DoubleFunctionGui {

			@Override
			Condition create() {
				return new ConditionEqualSmaller(function1, function2);
			}
		}
	}
	
	private static interface FunctionResult {
		
		void setFunction(Function function);
		
		Function getCurrent();
		
		Gui getParent();
	}
	
	private static interface ConditionResult {
		
		void setCondition(Condition condition);
		
		Condition getCurrent();
		
		Gui getParent();
	}
	
	private class General extends AdvancedMainGui {
		
		Function health;
		Function damage;
		Function strength;
		Function speed;
		Function attackSpeed;
		Function armor;
		Function archery;
		
		Function onHitFire;
		Function onAttackFire;
		
		private General(){
			health = new FunctionConstant.Byte((byte) 10);
			damage = new FunctionConstant.Byte((byte) 0);
			strength = new FunctionConstant.Byte((byte) 1);
			speed = new FunctionConstant.Byte((byte) 1);
			attackSpeed = new FunctionConstant.Byte((byte) 1);
			armor = new FunctionConstant.Byte((byte) 0);
			archery = new FunctionConstant.Byte((byte) 1);
			onHitFire = new FunctionConstant.Byte((byte) 0);
			onAttackFire = new FunctionConstant.Byte((byte) 0);
			addButtons();
		}
		
		private General(AdvancedRace race){
			health = race.health();
			damage = race.damage();
			strength = race.strength();
			speed = race.speed();
			attackSpeed = race.attackSpeed();
			armor = race.armor();
			archery = race.archery();
			onHitFire = race.onHitFire();
			onAttackFire = race.onAttackFire();
			addButtons();
		}
		
		private void addButtons(){
			addFunctionButton("health", "Health", 200, 10, 400, 60);
			addFunctionButton("damage", "Extra Damage", 200, 70, 400, 120);
			addFunctionButton("strength", "Strength", 200, 130, 400, 180);
			addFunctionButton("speed", "Speed", 200, 190, 400, 240);
			addFunctionButton("attackSpeed", "Attack Speed", 200, 250, 400, 300);
			addFunctionButton("armor", "Extra Armor", 200, 310, 400, 360);
			addFunctionButton("archery", "Archery", 200, 370, 400, 420);
		}
	}
	
	private class PermanentEffects extends AdvancedScrollMainGui {
		
		private PermanentEffects() {
			
		}
		
		private PermanentEffects(AdvancedRace race) {
			
		}
	}
	
	private class DamageResistances extends AdvancedScrollMainGui {
		
		private Function[] resistances;
		
		private DamageResistances(){
			resistances = new Function[ReflectedCause.values().length];
			for(int i = 0; i < resistances.length; i++)
				resistances[i] = new FunctionConstant.Byte((byte) 0);
			addButtons();
		}
		
		private DamageResistances(AdvancedRace race){
			resistances = new Function[ReflectedCause.values().length];
			Function[] raceResistances = race.getDamageResistanceFunctions();
			System.arraycopy(raceResistances, 0, resistances, 0, Math.min(raceResistances.length, resistances.length));
			for(int i = raceResistances.length; i < resistances.length; i++)
				resistances[i] = new FunctionConstant.Byte((byte) 0);
			addButtons();
		}
		
		private void addButtons(){
			for(int index = 0; index < resistances.length; index++){
				final int i = index;
				addButton(new FunctionButton(new FunctionResult(){

					@Override
					public void setFunction(Function function) {
						resistances[i] = function;
					}

					@Override
					public Function getCurrent() {
						return resistances[i];
					}

					@Override
					public Gui getParent() {
						return DamageResistances.this;
					}}, ReflectedCause.values()[i].name().toLowerCase(), 200, 10 + index * 60, 500, 60 + index * 60));
			}
		}
	}
	
	private class EffectResistances extends AdvancedScrollMainGui {
		
		private final Color COLOR = Color.ORANGE;
		
		private final List<EffectEntry> list;
		
		private int indexToRemove = -1;
		
		private EffectResistances(){
			list = new ArrayList<EffectEntry>();
			addButtons();
		}
		
		private EffectResistances(AdvancedRace race){
			Map<ReflectedEffectType,Function> resistances = race.getEffectResistanceFunctions();
			list = new ArrayList<EffectEntry>(resistances.size());
			Set<Entry<ReflectedEffectType,Function>> entrySet = resistances.entrySet();
			for(Entry<ReflectedEffectType,Function> entry : entrySet)
				list.add(new EffectEntry(entry.getKey().getType(), entry.getValue()));
			addButtons();
		}
		
		private void addButtons(){
			list.subList(1, list.size()).clear();
			addButton(new GuiTextButton("Add new", Color.GREEN, 50, 300, 150, 350){

				@Override
				public void click() {
					setGui(new CreateEntry());
				}
			});
			int index = 0;
			for(EffectEntry entry : list){
				final int finalIndex = index;
				addButton(new GuiTextButton(entry.type, COLOR, 300, 100 + index * 70, 600, 150 + index * 70){

					@Override
					public void click() {
						setGui(new EditEntry(finalIndex));
					}
				});
				addButton(new GuiTextButton("Remove", Color.RED, 650, 100 + index * 70, 750, 150 + index * 70){

					@Override
					public void click() {
						indexToRemove = finalIndex;
					}
				});
				index++;
			}
		}
		
		@Override
		public void click(int x, int y){
			super.click(x, y);
			if(indexToRemove != -1){
				list.remove(2 + indexToRemove * 2);
				list.remove(2 + indexToRemove * 2);
				addButtons();
				indexToRemove = -1;
			}
		}
		
		private abstract class EntryGui extends AdvancedGui {
			
			String type;
			Function resistance;
			
			private EntryGui(EffectEntry entry){
				this(entry.type, entry.function);
			}
			
			private EntryGui(String type, Function resistance){
				this.type = type;
				this.resistance = resistance;
				addButtons();
			}
			
			private EntryGui(){
				addButtons();
			}
			
			private void addButtons(){
				addButton(new GuiTextButton("Cancel", Color.RED, 100, 100, 200, 150){

					@Override
					public void click() {
						setGui(EffectResistances.this);
					}
				});
				addButton(new GuiTextButton("Done", Color.GREEN, 100, 300, 200, 350){

					@Override
					public void click() {
						type = ((GuiTextButton) buttons.get(2)).getText();
						if(type.equals("Select Type...")){
							setError("Click on 'Select Type...' and use the arrow keys.");
							return;
						}
						if(resistance == null){
							setError("Click on 'Select Value' to select the resistance.");
							return;
						}
						for(EffectEntry entry : list){
							if(entry.type != type && entry.type.equals(type)){
								setError("You have assigned another function to this potion effect already.");
								return;
							}
						}
						if(complete())
							setGui(EffectResistances.this);
					}
				});
				addButton(new TypeButton("Select Type...", Color.YELLOW, 300, 150, 600, 200){
					
					@Override
					public void press(int key){
						super.press(key);
						if(key == KeyEvent.VK_LEFT || key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_DOWN || key == KeyEvent.VK_UP){
							int currentIndex = -1;
							for(int index = 0; index < ReflectedEffectType.VALUES.length; index++){
								if(ReflectedEffectType.VALUES[index].getType().equals(text)){
									currentIndex = index;
									break;
								}
							}
							if(currentIndex == -1){
								text = ReflectedEffectType.VALUES[0].getType();
							}
							else {
								if(key == KeyEvent.VK_LEFT || key == KeyEvent.VK_UP){
									if(currentIndex == 0)
										currentIndex = ReflectedEffectType.VALUES.length;
									currentIndex--;
								}
								else {
									currentIndex++;
									if(currentIndex == ReflectedEffectType.VALUES.length)
										currentIndex = 0;
								}
								text = ReflectedEffectType.VALUES[currentIndex].getType();
							}
							RaceConfigFrame.markChange();
						}
					}
				});
				addButton(new FunctionButton(new FunctionResult(){

					@Override
					public void setFunction(Function function) {
						resistance = function;
					}

					@Override
					public Function getCurrent() {
						return resistance;
					}

					@Override
					public Gui getParent() {
						return EntryGui.this;
					}
					
				}, "Select Value", 300, 300, 600, 350));
			}
			
			abstract boolean complete();
		}
		
		private class EditEntry extends EntryGui {
			
			private final int index;
			
			private EditEntry(int index){
				super(list.get(index));
				this.index = index;
			}

			@Override
			boolean complete() {
				list.set(index, new EffectEntry(type, resistance));
				return true;
			}
		}
		
		private class CreateEntry extends EntryGui {

			@Override
			boolean complete() {
				list.add(new EffectEntry(type, resistance));
				return true;
			}
		}
	}
	
	private static class EffectEntry {
		
		private EffectEntry(String type, Function function){
			this.type = type;
			this.function = function;
		}
		
		private String type;
		
		private Function function;
	}
	
	private class Choises extends AdvancedScrollMainGui {
		
		private List<RaceChoise> choises;
		
		private int indexToRemove = -1;
		
		private Choises(AdvancedRace race){
			addButtons();
			Collection<RaceChoise> raceChoises = race.getChoises();
			choises = new ArrayList<RaceChoise>(raceChoises.size());
			for(RaceChoise choise : raceChoises)
				addChoise(choise);
		}
		
		private Choises(){
			addButtons();
			choises = new ArrayList<RaceChoise>();
		}
		
		private void addButtons(){
			addButton(new GuiTextButton("Create new choise", Color.GREEN, 400, 50, 700, 100){

				@Override
				public void click() {
					setGui(new CreateChoise());
				}
			});
		}
		
		@Override
		public void click(int x, int y){
			super.click(x, y);
			if(indexToRemove != -1){
				RaceChoise choise = choises.get(indexToRemove);
				Collection<Function> functions = getAllFunctions();
				for(Function function : functions){
					if(function.usesChoise(choise.getID())){
						setError("You can't remove a choise that is used by functions.");
						indexToRemove = -1;
						return;
					}
				}
				buttons.remove(indexToRemove * 2 + 2);
				buttons.remove(indexToRemove * 2 + 2);
				choises.remove(indexToRemove);
				indexToRemove = -1;
			}
		}
		
		private void addChoise(RaceChoise choise){
			for(RaceChoise currentChoise : choises){
				if(choise.getID().equals(currentChoise.getID())){
					setError("A choise with the id " + choise.getID() + " already exists.");
					return;
				}
			}
			final int index = buttons.size() - 3;
			addButton(new GuiTextButton(choise.getDisplayName(), Color.YELLOW, 200, 200 + index * 70, 400, 250 + index * 70){

				@Override
				public void click() {
					setGui(new EditChoise(index));
				}
			});
			addButton(new GuiTextButton("Remove", Color.RED, 500, 200 + index * 70, 650, 250 + index * 70){

				@Override
				public void click() {
					indexToRemove = index;
				}
			});
			choises.add(choise);
			RaceConfigFrame.markChange();
		}
		
		private abstract class EditChoiseGui extends AdvancedScrollGui {
			
			String name;
			
			List<String> options;
			List<Condition> conditions;
			
			int indexToRemove;
			
			private EditChoiseGui(){
				this("Choise name");
			}
			
			private EditChoiseGui(String name, Value... options){
				this.name = name;
				this.options = new ArrayList<String>(options.length);
				addButton(new GuiTextButton("Back", Color.ORANGE, 50, 50, 150, 100){

					@Override
					public void click() {
						setGui(Choises.this);
					}
				});
				addButton(new TypeButton(name, Color.MAGENTA, 200, 200, 400, 250));
				addButton(new GuiTextButton("Add New", Color.GREEN, 200, 300, 350, 350){

					@Override
					public void click() {
						addOption("New option");
					}
				});
				addButton(new GuiTextButton("Done", Color.GREEN, 50, 400, 150, 450){

					@Override
					public void click() {
						if(EditChoiseGui.this.options.isEmpty()){
							setError("You need at least 1 option.");
							return;
						}
						List<String> check = new ArrayList<String>(EditChoiseGui.this.options.size());
						for(String option : EditChoiseGui.this.options){
							if(check.contains(option)){
								setError("2 or more options have the same name!");
								return;
							}
							check.add(option);
						}
						EditChoiseGui.this.name = ((GuiTextButton) buttons.get(1)).getText();
						if(EditChoiseGui.this.name.equals("Choise name")){
							setError("Click on 'Choise name' and type a proper name.");
							return;
						}
						complete();
						setGui(Choises.this);
					}
				});
				for(Value option : options)
					addOption(option.getName());
			}
			
			@Override
			public void click(int x, int y){
				super.click(x, y);
				if(indexToRemove != -1){
					buttons.remove(indexToRemove * 3 + 3);
					buttons.remove(indexToRemove * 3 + 3);
					options.remove(indexToRemove);
					indexToRemove = -1;
				}
			}
			
			void addOption(String option){
				final int index = options.size();
				conditions.add(new ConditionTrue());
				options.add(option);
				addButton(new TypeButton(option, Color.YELLOW, 400, 200 + index * 130, 700, 250 + index * 130));
				addButton(new GuiTextButton("X", Color.RED, 720, 200 + index * 130, 750, 250 + index * 130){

					@Override
					public void click() {
						EditChoiseGui.this.indexToRemove = index;
					}
				});
				addButton(new ConditionButton(new ConditionResult(){

					@Override
					public void setCondition(Condition condition) {
						conditions.set(index, condition);
					}

					@Override
					public Condition getCurrent() {
						return conditions.get(index);
					}

					@Override
					public Gui getParent() {
						return EditChoiseGui.this;
					}
					
				}, "Can choose if...", 400, 270 + index * 130, 700, 320 + index * 130));
				RaceConfigFrame.markChange();
			}
			
			abstract void complete();
		}
		
		private class EditChoise extends EditChoiseGui {
			
			private final int index;
			
			private EditChoise(int index){
				this.index = index;
			}

			@Override
			void complete() {
				for(int i = 0; i < choises.size(); i++){
					RaceChoise choise = choises.get(i);
					if(i != index && choise.getID().equals(EditChoise.this.name)){
						setError("There is already a choise with name " + choise.getID());
						return;
					}
				}
				String[] values = new String[EditChoise.this.options.size()];
				for(int i = 0; i < values.length; i++)
					values[i] = EditChoise.this.options.get(i);
				Condition[] conditions = new Condition[EditChoise.this.conditions.size()];
				for(int i = 0; i < conditions.length; i++)
					conditions[i] = EditChoise.this.conditions.get(i);
				choises.set(index, new RaceChoise(EditChoise.this.name, values, conditions));
			}
		}
		
		private class CreateChoise extends EditChoiseGui {

			@Override
			void complete() {
				for(RaceChoise choise : choises){
					if(choise.getID().equals(name)){
						setError("There is already a choise with name " + choise.getID());
						return;
					}
				}
				String[] values = new String[CreateChoise.this.options.size()];
				for(int i = 0; i < values.length; i++)
					values[i] = CreateChoise.this.options.get(i);
				Condition[] conditions = new Condition[CreateChoise.this.conditions.size()];
				for(int i = 0; i < conditions.length; i++)
					conditions[i] = CreateChoise.this.conditions.get(i);
				addChoise(new RaceChoise(name, null, null));
			}
		}
	}
	
	private class Fields extends AdvancedScrollMainGui {
		
		private List<ProgressType> fields;
		
		private int indexToRemove = -1;
		
		private Fields(AdvancedRace race){
			addButtons();
			List<ProgressType> fields = race.getFields();
			this.fields = new ArrayList<ProgressType>(fields.size());
			for(ProgressType field : fields)
				addField(field);
		}
		
		private Fields(){
			addButtons();
			fields = new ArrayList<ProgressType>();
		}
		
		private void addButtons(){
			addButton(new GuiTextButton("Create new variable", Color.GREEN, 400, 50, 700, 100){

				@Override
				public void click() {
					setGui(new CreateField());
				}
			});
		}
		
		@Override
		public void click(int x, int y){
			super.click(x, y);
			if(indexToRemove != -1){
				ProgressType field = fields.get(indexToRemove);
				Collection<Function> functions = getAllFunctions();
				for(Function function : functions){
					if(function.usesField(field.getName())){
						setError("You can't remove a variable that is used by functions.");
						indexToRemove = -1;
						return;
					}
				}
				buttons.remove(indexToRemove * 2 + 2);
				buttons.remove(indexToRemove * 2 + 2);
				fields.remove(indexToRemove);
				indexToRemove = -1;
			}
		}
		
		private void addField(ProgressType field){
			for(ProgressType currentField : fields){
				if(field.getName().equals(currentField.getName())){
					setError("A variable with the name " + field.getName() + " already exists.");
					return;
				}
			}
			final int index = buttons.size() - 3;
			addButton(new GuiTextButton(field.getName(), Color.YELLOW, 200, 200 + index * 70, 400, 250 + index * 70){

				@Override
				public void click() {
					setGui(new EditField(index));
				}
			});
			addButton(new GuiTextButton("Remove", Color.RED, 500, 200 + index * 70, 650, 250 + index * 70){

				@Override
				public void click() {
					indexToRemove = index;
				}
			});
			fields.add(field);
			RaceConfigFrame.markChange();
		}
		
		private abstract class EditFieldGui extends AdvancedGui {
			
			ValueType type;
			String name;
			Object defaultValue;
			
			private EditFieldGui(){
				this("Variable name", null, null);
			}
			
			private EditFieldGui(String name, ValueType type, Object defaultValue){
				this.name = name;
				this.type = type;
				this.defaultValue = defaultValue;
				addButton(new GuiTextButton("Back", Color.ORANGE, 50, 50, 150, 100){

					@Override
					public void click() {
						setGui(Fields.this);
					}
				});
				addButton(new TypeButton(name, Color.MAGENTA, 200, 200, 400, 250));
				addButton(new TypeButton("Default Value", Color.MAGENTA, 200, 300, 400, 350));
				addButton(new GuiTextButton("Select Type", type == null ? Color.WHITE : Color.GREEN, 500, 200, 700, 250){

					@Override
					public void click() {
						setGui(new GuiSelectType());
					}
				});
				addButton(new GuiTextButton("Done", Color.GREEN, 50, 300, 150, 350){

					@Override
					public void click() {
						ValueType vt = EditFieldGui.this.type;
						if(vt == null){
							setError("You need to select a type.");
							return;
						}
						EditFieldGui.this.name = ((GuiTextButton) buttons.get(1)).getText();
						if(EditFieldGui.this.name.equals("Variable name")){
							setError("Click on 'Variable name' and type a proper name.");
							return;
						}
						boolean validDef = false;
						String defValue = ((GuiTextButton) buttons.get(2)).getText();
						if(vt == ValueType.STRING)
							validDef = true;
						else if(vt == ValueType.BOOLEAN){
							if(defValue.equalsIgnoreCase("yes") || defValue.equalsIgnoreCase("true")){
								defValue = "true";
								validDef = true;
							}
							else if(defValue.equalsIgnoreCase("no") || defValue.equalsIgnoreCase("false")){
								defValue = "false";
								validDef = true;
							}
							else
								setError("Type 'yes' or 'no' in the default button.");
						}
						else if(vt == ValueType.FLOAT){
							try {
								Float.parseFloat(defValue);
								validDef = true;
							} catch(NumberFormatException ex){
								setError("Type a valid number in the default button.");
							}
						}
						else if(vt == ValueType.DOUBLE){
							try {
								Double.parseDouble(defValue);
								validDef = true;
							} catch(NumberFormatException ex){
								setError("Type a valid number in the default button.");
							}
						}
						else {
							//it is an integer
							try {
								long l = Long.parseLong(defValue);
								if(vt == ValueType.LONG)
									validDef = true;
								if(vt == ValueType.INT)
									validDef = l >= Integer.MIN_VALUE && l <= Integer.MAX_VALUE;
								if(vt == ValueType.SHORT)
									validDef = l >= Short.MIN_VALUE && l <= Short.MAX_VALUE;
								if(vt == ValueType.CHAR)
									validDef = l >= Character.MIN_VALUE && l <= Character.MAX_VALUE;
								if(vt == ValueType.BYTE)
									validDef = l >= Byte.MIN_VALUE && l <= Byte.MAX_VALUE;
							} catch(NumberFormatException ex){
								setError("Type a valid number in the default button.");
							}
						}
						if(validDef && complete())
							setGui(Fields.this);
					}
				});
			}
			
			abstract boolean complete();
			
			private class GuiSelectType extends AdvancedGui {
				
				private GuiSelectType(){
					addButton(new GuiTextButton("Back", Color.ORANGE, 50, 50, 150, 100){

						@Override
						public void click() {
							setGui(EditFieldGui.this);
						}
					});
					addButton(new GuiTextButton("I need an integer", Color.MAGENTA, 150, 150, 500, 200){
						
						@Override
						public void click(){
							setGui(new SelectInteger());
						}
					});
					addButton(new GuiTextButton("I also need half numbers", Color.MAGENTA, 150, 250, 500, 300){

						@Override
						public void click() {
							((GuiTextButton)EditFieldGui.this.buttons.get(2)).setColor(Color.GREEN);
							EditFieldGui.this.type = ValueType.DOUBLE;
							setGui(EditFieldGui.this);
						}
					});
					addButton(new GuiTextButton("I need a text", Color.MAGENTA, 150, 350, 500, 400){

						@Override
						public void click() {
							((GuiTextButton)EditFieldGui.this.buttons.get(2)).setColor(Color.GREEN);
							EditFieldGui.this.type = ValueType.STRING;
							setGui(EditFieldGui.this);
						}
					});
					addButton(new GuiTextButton("I need true/false", Color.MAGENTA, 150, 450, 500, 500){

						@Override
						public void click() {
							((GuiTextButton)EditFieldGui.this.buttons.get(2)).setColor(Color.GREEN);
							EditFieldGui.this.type = ValueType.BOOLEAN;
							setGui(EditFieldGui.this);
						}
					});
				}
				
				private class SelectInteger extends AdvancedGui {
					
					private SelectInteger(){
						addButton(new GuiTextButton("Back", Color.ORANGE, 50, 50, 150, 100){

							@Override
							public void click() {
								setGui(GuiSelectType.this);
							}
						});
						addButton(new TypeButton("", Color.MAGENTA, 500, 150, 700, 200));
						addButton(new TypeButton("", Color.MAGENTA, 500, 250, 700, 300));
						addButton(new GuiTextButton("Done", Color.GREEN, 50, 350, 150, 400){

							@Override
							public void click() {
								long max;
								try {
									max = Long.parseLong(((GuiTextButton) buttons.get(1)).getText());
								} catch(NumberFormatException ex){
									setError("Insert a valid maximum value.");
									return;
								}
								long min;
								try {
									min = Long.parseLong(((GuiTextButton) buttons.get(2)).getText());
								} catch(NumberFormatException ex){
									setError(error = "Insert a valid minimum value.");
									return;
								}
								if(max <= Byte.MAX_VALUE && min >= Byte.MIN_VALUE)
									type = ValueType.BYTE;
								else if(max <= Short.MAX_VALUE && min >= Short.MIN_VALUE)
									type = ValueType.SHORT;
								else if(max <= Character.MAX_VALUE && min >= Character.MIN_VALUE)
									type = ValueType.CHAR;
								else if(max <= Integer.MAX_VALUE && min >= Integer.MIN_VALUE)
									type = ValueType.INT;
								else
									type = ValueType.LONG;
								((GuiTextButton)EditFieldGui.this.buttons.get(2)).setColor(Color.GREEN);
								setGui(EditFieldGui.this);
							}
						});
					}
					
					@Override
					public void paint(Graphics g){
						super.paint(g);
						g.setColor(Color.BLACK);
						g.drawString("Maximum value you might need:", 50, 190);
						g.drawString("Minimum value you might need:", 50, 290);
					}
				}
			}
		}
		
		private class EditField extends EditFieldGui {
			
			private final int index;
			
			private EditField(int index){
				super(fields.get(index).getName(), fields.get(index).getType(), fields.get(index).getDefaultValue());
				this.index = index;
			}

			@Override
			boolean complete() {
				int i = 0;
				for(ProgressType field : fields){
					if(i != index && field.getName().equals(name)){
						setError("There is already a variable with name " + name);
						return false;
					}
					i++;
				}
				ValueType oldType = fields.get(index).getType();
				boolean changeType = false;
				if(oldType == ValueType.BOOLEAN && type != ValueType.BOOLEAN)
					changeType = true;
				if(oldType == ValueType.STRING && type != ValueType.STRING)
					changeType = true;
				if(type == ValueType.BOOLEAN && oldType != ValueType.BOOLEAN)
					changeType = true;
				if(type == ValueType.STRING && oldType != ValueType.STRING)
					changeType = true;
				String oldName = fields.get(index).getName();
				boolean changeName = !name.equals(oldName);
				if(changeName || changeType){
					Collection<Function> functions = getAllFunctions();
					for(Function function : functions){
						if(function.usesField(oldName)){
							if(changeType){
								setError("You can't change the type of a variable that is in use.");
								return false;
							}
							if(changeName)
								function.renameFields(oldName, name);
						}
					}
				}
				fields.set(index, new ProgressType(name, type, defaultValue));
				((GuiTextButton) Fields.this.buttons.get(index)).setText(name);
				return true;
			}
		}
		
		private class CreateField extends EditFieldGui {
			
			private CreateField(){
				super();
			}

			@Override
			boolean complete() {
				addField(new ProgressType(name, type, defaultValue));
				return true;
			}
		}
	}
	
	private class Functions extends AdvancedScrollMainGui {
		
		private List<NamedFunction> functions;
		
		private int indexToRemove = -1;
		
		private Functions(AdvancedRace race){
			addButtons();
			List<NamedFunction> functions = race.getFunctions();
			this.functions = new ArrayList<NamedFunction>(functions.size());
			for(NamedFunction function : functions)
				addFunction(function);
		}
		
		private Functions(){
			addButtons();
			functions = new ArrayList<NamedFunction>();
		}
		
		@Override
		public void click(int x, int y){
			super.click(x, y);
			if(indexToRemove != -1){
				NamedFunction functionToRemove = this.functions.get(indexToRemove);
				Collection<Function> functions = getAllFunctions();
				for(Function function : functions){
					if(function.usesFunction(functionToRemove.getName())){
						setError("You can't remove a function that is used by other functions.");
						indexToRemove = -1;
						return;
					}
				}
				buttons.remove(indexToRemove * 2 + 2);
				buttons.remove(indexToRemove * 2 + 2);
				this.functions.remove(indexToRemove);
				indexToRemove = -1;
			}
		}
		
		private void addButtons(){
			addButton(new GuiTextButton("Create new function", Color.GREEN, 400, 50, 700, 100){

				@Override
				public void click() {
					setGui(new CreateFunction());
				}
			});
		}
		
		private void addFunction(NamedFunction function){
			for(NamedFunction currentFunction : functions){
				if(function.getName().equals(currentFunction.getName())){
					setError("A function with the name " + function.getName() + " already exists.");
					return;
				}
			}
			final int index = buttons.size() - 2;
			addButton(new GuiTextButton(function.getName(), Color.YELLOW, 200, 200 + index * 70, 400, 250 + index * 70){

				@Override
				public void click() {
					setGui(new EditFunction(index));
				}
			});
			addButton(new GuiTextButton("Remove", Color.RED, 500, 200 + index * 70, 650, 250 + index * 70){

				@Override
				public void click() {
					indexToRemove = index;
				}
			});
			functions.add(function);
		}
		
		private abstract class EditFunctionGui extends AdvancedGui {
			
			String name;
			Function function;
			
			private EditFunctionGui(){
				this("Function Name", null);
			}
			
			private EditFunctionGui(String name, Function function){
				this.name = name;
				this.function = function;
				addButton(new GuiTextButton("Back", Color.ORANGE, 50, 50, 150, 100){

					@Override
					public void click() {
						setGui(Functions.this);
					}
				});
				addButton(new TypeButton(name, Color.MAGENTA, 200, 200, 400, 250));
				addFunctionButton("function", "Select Function", 200, 300, 400, 350);
				addButton(new GuiTextButton("Done", Color.GREEN, 50, 300, 150, 350){

					@Override
					public void click() {
						if(EditFunctionGui.this.function == null){
							setError("You need to select a function.");
							return;
						}
						EditFunctionGui.this.name = ((GuiTextButton) buttons.get(1)).getText();
						if(EditFunctionGui.this.name.equals("Function Name")){
							setError("Click on 'Function Name' and type a proper name.");
							return;
						}
						complete();
						setGui(Functions.this);
					}
				});
			}
			
			abstract void complete();
		}
		
		private class EditFunction extends EditFunctionGui {
			
			private final int index;
			
			private EditFunction(int index){
				super(functions.get(index).getName(), functions.get(index).getFunction());
				this.index = index;
			}

			@Override
			void complete() {
				int i = 0;
				for(NamedFunction function : functions){
					if(i != index && function.getName().equals(name)){
						setError("There is already a function with name " + name);
						return;
					}
					i++;
				}
				String oldName = functions.get(index).getName();
				boolean changeName = !name.equals(oldName);
				if(changeName){
					Collection<Function> functions = getAllFunctions();
					for(Function function : functions){
						if(function.usesFunction(oldName)){
							if(changeName)
								function.renameFunctions(oldName, name);
						}
					}
				}
				functions.set(index, new NamedFunction(name, function));
				((GuiTextButton) Functions.this.buttons.get(index)).setText(name);
			}
		}
		
		private class CreateFunction extends EditFunctionGui {
			
			private CreateFunction(){
				super();
			}

			@Override
			void complete() {
				addFunction(new NamedFunction(name, function));
			}
		}
	}
	
	private class Events extends AdvancedMainGui {
		
		//private final Update update;
		//private final BeingHit beingHit;
		//private final Hit hit;
		//private final Kill kill;
		//private final Die die;
		
		private Events(){
			//update = new Update();
			//beingHit = new BeingHit();
			//hit = new Hit();
			//kill = new Kill();
			//die = new Die();
			addButtons();
		}
		
		private Events(AdvancedRace race){
			//update = new Update(race);
			//beingHit = new BeingHit(race);
			//hit = new Hit(race);
			//kill = new Kill(race);
			//die = new Die(race);
			addButtons();
		}
		
		private void addButtons(){
			
		}
		
		private abstract class EventGui extends AdvancedScrollGui {
			
			private EventGui(){
				addButton(new GuiTextButton("Back", Color.YELLOW, 20, 50, 150, 100){

					@Override
					public void click() {
						setGui(Events.this);
					}
				});
			}
		}
		
		private class Update extends EventGui {
			
		}
		
		private class BeingHit extends EventGui {
			
		}
		
		private class Hit extends EventGui {
			
		}
		
		private class Kill extends EventGui {
			
		}
		
		private class Die extends EventGui {
			
		}
	}
	
	private class Equipment extends AdvancedScrollMainGui {
		
		private AdvancedEquipment equipment;
		
		private Equipment(){
			// TODO Initialise this properly
			equipment = new AdvancedEquipment(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
			try {
				Field[] fields = equipment.getClass().getDeclaredFields();
				for(Field field : fields)
					field.set(equipment, new ConditionTrue());
			} catch(Exception ex){
				throw new Error(ex);
			}
			addButtons();
		}
		
		private Equipment(AdvancedRace race){
			equipment = race.getAllowedEquipment();
			addButtons();
		}
		
		private void addButtons(){
			addArmorButton("leatherBoots", "Leather Boots", 300, 100, 500, 150);
			addArmorButton("leatherLeggings", "Leather Pants", 300, 170, 500, 220);
			addArmorButton("leatherChestplate", "Leather Tunic", 300, 240, 500, 290);
			addArmorButton("leatherHelmet", "Leather Cap", 300, 310, 500, 360);
			addArmorButton("goldBoots", "Golden Boots", 300, 410, 500, 460);
			addArmorButton("goldLeggings", "Golden Leggings", 300, 480, 500, 530);
			addArmorButton("goldChestplate", "Golden Chestplate", 300, 550, 500, 600);
			addArmorButton("goldHelmet", "Golden Helmet", 300, 620, 500, 670);
			addArmorButton("chainBoots", "Chainmail Boots", 300, 720, 500, 770);
			addArmorButton("chainLeggings", "Chainmail Leggings", 300, 790, 500, 840);
			addArmorButton("chainChestplate", "Chainmail Chestplate", 300, 860, 500, 910);
			addArmorButton("chainHelmet", "Chainmail Helmet", 300, 930, 500, 980);
			addArmorButton("ironBoots", "Iron Boots", 300, 1030, 500, 1080);
			addArmorButton("ironLeggings", "Iron Leggings", 300, 1100, 500, 1150);
			addArmorButton("ironChestplate", "Iron Chestplate", 300, 1170, 500, 1220);
			addArmorButton("ironHelmet", "Iron Helmet", 300, 1240, 500, 1290);
			addArmorButton("diamondBoots", "Diamond Boots", 300, 1340, 500, 1390);
			addArmorButton("diamondLeggings", "Diamond Leggings", 300, 1410, 500, 1460);
			addArmorButton("diamondChestplate", "Diamond Chestplate", 300, 1480, 500, 1530);
			addArmorButton("diamondHelmet", "Diamond Helmet", 300, 1550, 500, 1600);
		}
		
		protected void addArmorButton(final String fieldName, String text, int minX, int minY, int maxX, int maxY){
			addButton(new ConditionButton(new ConditionResult(){

				@Override
				public void setCondition(Condition condition) {
					try {
						equipment.getClass().getDeclaredField(fieldName).set(equipment, condition);
					} catch(Exception ex){
						throw new Error(ex);
					}
				}

				@Override
				public Condition getCurrent() {
					try {
						return (Condition) equipment.getClass().getDeclaredField(fieldName).get(equipment);
					} catch(Exception ex){
						throw new Error(ex);
					}
				}

				@Override
				public Gui getParent() {
					return Equipment.this;
				}
				
			}, text, minX, minY, maxX, maxY));
		}
	}
}
