package nl.knokko.races.configuration.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import nl.knokko.races.base.RaceFactory;
import nl.knokko.races.base.RaceFactory.SimpleRace;
import nl.knokko.races.base.ReflectedCause;
import nl.knokko.races.configuration.RaceConfigFrame;
import nl.knokko.races.configuration.gui.button.GuiTextButton;
import nl.knokko.races.configuration.replacements.SupportedPermanentEffects;
import nl.knokko.races.potion.PermanentEffect;
import nl.knokko.races.potion.ReflectedEffect;
import nl.knokko.races.potion.ReflectedEffectType;
import nl.knokko.util.bits.ByteArrayBitOutput;

public class GuiSimpleRace extends Gui {
	
	private static final Color BUTTON_COLOR = Color.BLUE;
	private static final Font FONT = new Font("TimesRoman", Font.PLAIN, 35);
	private static final Font ERROR_FONT = new Font("TimesRoman", Font.PLAIN, 15);
	
	private final List<PermanentEffectTab> permanentEffects;
	private final List<OnAttackEffectTab> onAttackEffects;
	private final List<OnHitEffectTab> onHitEffects;
	
	private final TypingButton nameButton;
	
	private final TypingButton healthButton;
	private final TypingButton armorButton;
	private final TypingButton damageButton;
	private final TypingButton strengthButton;
	private final TypingButton attackSpeedButton;
	private final TypingButton speedButton;
	private final TypingButton archeryButton;
	
	private final TypingButton onHitFireButton;
	private final TypingButton onAttackFireButton;
	
	private final TypingButton[] damageResistances;
	
	private String error;
	
	private int scroll;
	
	public GuiSimpleRace(){
		this("new race", 0, 0, 0, 1, 1, 1, 1, 0, 0, new double[ReflectedCause.values().length]);
	}

	public GuiSimpleRace(String name, int health, int armor, int damage, float strength, float attackSpeed, float speed, float archery, int hitFireTicks, int attackFireTicks, double[] damageCauseResistances) {
		int x = 400;
		nameButton = new TypingButton(Type.STRING, name, x, 200, x + 300, 250);
		healthButton = new TypingButton(Type.BYTE, health, x, 300, x + 100, 350);
		armorButton = new TypingButton(Type.BYTE, armor, x, 370, x + 100, 420);
		damageButton = new TypingButton(Type.SHORT, damage, x, 520, x + 100, 570);
		strengthButton = new TypingButton(Type.FLOAT, strength, x, 590, x + 200, 640);
		attackSpeedButton = new TypingButton(Type.FLOAT, attackSpeed, x, 660, x + 200, 710);
		speedButton = new TypingButton(Type.FLOAT, speed, x, 750, x + 200, 800);
		archeryButton = new TypingButton(Type.FLOAT, archery, x, 850, x + 200, 900);
		onHitFireButton = new TypingButton(Type.INT, hitFireTicks, x, 950, x + 100, 1000);
		onAttackFireButton = new TypingButton(Type.INT, attackFireTicks, x, 1020, x + 100, 1070);
		permanentEffects = new ArrayList<PermanentEffectTab>();
		onAttackEffects = new ArrayList<OnAttackEffectTab>();
		onHitEffects = new ArrayList<OnHitEffectTab>();
		damageResistances = new TypingButton[ReflectedCause.values().length];
		for(int i = 0; i < damageResistances.length; i++)
			damageResistances[i] = new TypingButton(Type.DOUBLE, damageCauseResistances[i], x, 1120 + i * 70, x + 200, 1170 + i * 70);
		addButtons();
	}
	
	public GuiSimpleRace(SimpleRace race){
		this(race.getName(), (int) race.getExtraHealth(null), (int) race.getExtraArmor(null), (int) race.getExtraDamage(null),
				(float) race.getStrengthMultiplier(null), (float) race.getAttackSpeedMultiplier(null), (float) race.getSpeedMultiplier(null),
				(float) race.getArcheryFactor(null), race.getOnHitFireTicks(), race.getOnAttackFireTicks(), race.getDamageResistances(null));
		Collection<PermanentEffect> permEffects = race.getPermanentEffects(null);
		int i = 0;
		for(PermanentEffect pe : permEffects){
			permanentEffects.add(new PermanentEffectTab(pe, i));
			i++;
		}
		i = 0;
		Collection<ReflectedEffect> attackEffects = race.getOnAttackEffects();
		for(ReflectedEffect re : attackEffects){
			onAttackEffects.add(new OnAttackEffectTab(re, i));
			i++;
		}
		i = 0;
		Collection<ReflectedEffect> hitEffects = race.getOnHitEffects();
		for(ReflectedEffect re : hitEffects){
			onHitEffects.add(new OnHitEffectTab(re, i));
			i++;
		}
	}
	
	private void addButtons(){
		addButton(new GuiTextButton("Cancel", FONT, Color.RED, Color.BLACK, Color.BLACK, 100, 100, 200, 150){

			@Override
			public void click() {
				RaceConfigFrame.instance().setGui(new GuiMain());
			}
			
			@Override
			public int minY(){
				return minY - scroll;
			}
			
			@Override
			public int maxY(){
				return maxY - scroll;
			}
		});
		addButton(new GuiTextButton("Save", FONT, Color.GREEN, Color.BLACK, Color.BLACK, 400, 100, 500, 150){

			@Override
			public void click() {
				try {
					GuiChooseRace.getFolder().mkdirs();
					ByteArrayBitOutput output = new ByteArrayBitOutput(2000);
					double[] damageCauseResistances = new double[damageResistances.length];
					for(int index = 0; index < damageResistances.length; index++)
						damageCauseResistances[index] = damageResistances[index].getDoubleValue();
					// TODO save equipment properly
					RaceFactory.saveAsSimpleRace2(output, healthButton.getByteValue(), armorButton.getByteValue(),
							damageButton.getShortValue(), strengthButton.getFloatValue(),
							speedButton.getFloatValue(), attackSpeedButton.getFloatValue(),
							archeryButton.getFloatValue(),
							onHitFireButton.getIntValue(), onAttackFireButton.getIntValue(),
							getPermanentEffects(permanentEffects), getOnHitEffects(onHitEffects),
							getOnAttackEffects(onAttackEffects), damageCauseResistances,
							new HashMap<ReflectedEffectType,Float>(), new SimpleRace.SimpleEquipment(
									true, true, true, true, 
									true, true, true, true, true, 
									true, true, true, true, true, 
									true, true, true, true, 
									true, true, true, true));
					FileOutputStream fileOutput = new FileOutputStream(new File(GuiChooseRace.getFolder() + File.separator + nameButton.getText() + ".race"));
					fileOutput.write(output.getBytes());
					fileOutput.close();
				} catch(Exception ex){
					error = ex.getLocalizedMessage();
					RaceConfigFrame.markChange();
				}
			}
			
			@Override
			public int minY(){
				return minY - scroll;
			}
			
			@Override
			public int maxY(){
				return maxY - scroll;
			}
		});
		addButton(new GuiTextButton("Add", FONT, Color.GREEN, 400, 1300 + damageResistances.length * 70, 500, 1350 + damageResistances.length * 70){

			@Override
			public void click() {
				permanentEffects.add(new PermanentEffectTab(permanentEffects.size()));
				RaceConfigFrame.markChange();
			}
			
			@Override
			public int minY(){
				return minY - scroll;
			}
			
			@Override
			public int maxY(){
				return maxY - scroll;
			}
		});
		addButton(new GuiTextButton("Remove last", FONT, Color.RED, 600, 1300 + damageResistances.length * 70, 800, 1350 + + damageResistances.length * 70){

			@Override
			public void click() {
				permanentEffects.remove(permanentEffects.size() - 1);
				RaceConfigFrame.markChange();
			}
			
			@Override
			public int minY(){
				return minY - scroll;
			}
			
			@Override
			public int maxY(){
				return maxY - scroll;
			}
		});
		addButton(new GuiTextButton("Add", FONT, Color.GREEN, 400, 1400 + damageResistances.length * 70, 500, 1450 + damageResistances.length * 70){

			@Override
			public void click() {
				onAttackEffects.add(new OnAttackEffectTab(onAttackEffects.size()));
				RaceConfigFrame.markChange();
			}
			
			@Override
			public int minY(){
				return minY - scroll + 500 * permanentEffects.size();
			}
			
			@Override
			public int maxY(){
				return maxY - scroll + 500 * permanentEffects.size();
			}
		});
		addButton(new GuiTextButton("Remove last", FONT, Color.RED, 600, 1400 + damageResistances.length * 70, 800, 1450 + damageResistances.length * 70){

			@Override
			public void click() {
				onAttackEffects.remove(onAttackEffects.size() - 1);
				RaceConfigFrame.markChange();
			}
			
			@Override
			public int minY(){
				return minY - scroll + 500 * permanentEffects.size();
			}
			
			@Override
			public int maxY(){
				return maxY - scroll + 500 * permanentEffects.size();
			}
		});
		addButton(new GuiTextButton("Add", FONT, Color.GREEN, 400, 1600 + damageResistances.length * 70, 500, 1650 + damageResistances.length * 70){

			@Override
			public void click() {
				onHitEffects.add(new OnHitEffectTab(onHitEffects.size()));
				RaceConfigFrame.markChange();
			}
			
			@Override
			public int minY(){
				return minY - scroll + 500 * permanentEffects.size() + 550 * onAttackEffects.size();
			}
			
			@Override
			public int maxY(){
				return maxY - scroll + 500 * permanentEffects.size() + 550 * onAttackEffects.size();
			}
		});
		addButton(new GuiTextButton("Remove last", FONT, Color.RED, 600, 1600 + damageResistances.length * 70, 800, 1650 + damageResistances.length * 70){

			@Override
			public void click() {
				onHitEffects.remove(onHitEffects.size() - 1);
				RaceConfigFrame.markChange();
			}
			
			@Override
			public int minY(){
				return minY - scroll + 500 * permanentEffects.size() + 550 * onAttackEffects.size();
			}
			
			@Override
			public int maxY(){
				return maxY - scroll + 500 * permanentEffects.size() + 550 * onAttackEffects.size();
			}
		});
		addButton(nameButton);
		addButton(healthButton);
		addButton(armorButton);
		addButton(damageButton);
		addButton(strengthButton);
		addButton(attackSpeedButton);
		addButton(speedButton);
		addButton(archeryButton);
		addButton(onHitFireButton);
		addButton(onAttackFireButton);
		for(TypingButton dr : damageResistances)
			addButton(dr);
	}

	@Override
	protected Color getBackGroundColor() {
		return Color.RED;
	}
	
	@Override
	public void scroll(int amount){
		scroll += amount;
	}
	
	@Override
	public void click(int x, int y){
		super.click(x, y);
		for(PermanentEffectTab pet : permanentEffects)
			pet.click(x, y);
		for(OnAttackEffectTab aet : onAttackEffects)
			aet.click(x, y);
		for(OnHitEffectTab aet : onHitEffects)
			aet.click(x, y);
	}
	
	@Override
	public void type(char character){
		super.type(character);
		for(PermanentEffectTab pet : permanentEffects)
			pet.type(character);
		for(OnAttackEffectTab aet : onAttackEffects)
			aet.type(character);
		for(OnHitEffectTab aet : onHitEffects)
			aet.type(character);
	}
	
	@Override
	public void press(int keycode){
		super.press(keycode);
		for(PermanentEffectTab pet : permanentEffects)
			pet.press(keycode);
		for(OnAttackEffectTab aet : onAttackEffects)
			aet.press(keycode);
		for(OnHitEffectTab aet : onHitEffects)
			aet.press(keycode);
	}
	
	@Override
	public void paint(Graphics g){
		super.paint(g);
		for(PermanentEffectTab pet : permanentEffects)
			pet.paint(g);
		for(OnAttackEffectTab aet : onAttackEffects)
			aet.paint(g);
		for(OnHitEffectTab aet : onHitEffects)
			aet.paint(g);
		g.setColor(Color.BLACK);
		g.setFont(FONT);
		int x = 20;
		int dy = 35 - scroll;
		g.drawString("Name:", x, 200 + dy);
		g.drawString("Extra Health:", x, 300 + dy);
		g.drawString("Extra Armor:", x, 370 + dy);
		g.drawString("Extra Damage:", x, 520 + dy);
		g.drawString("Strength:", x, 590 + dy);
		g.drawString("Attack Speed:", x, 660 + dy);
		g.drawString("Movement Speed:", x, 750 + dy);
		g.drawString("Archery Factor:", x, 850 + dy);
		g.drawString("Fire ticks on being hit:", x, 950 + dy);
		g.drawString("Fire ticks on attack:", x, 1020 + dy);
		g.drawString("Resistances against damage causes:", x, 1070 + dy);
		for(int i = 0; i < damageResistances.length; i++)
			g.drawString(ReflectedCause.values()[i].name().toLowerCase(), x, 1120 + i * 70 + dy);
		g.drawString("Permanent effects:", x, 1300 + dy + damageResistances.length * 70);
		g.drawString("On-attack effects:", x, 1400 + dy + 500 * permanentEffects.size() + damageResistances.length * 70);
		g.drawString("On-being hit effects:", x, 1500 + dy + 500 * permanentEffects.size() + 550 * onAttackEffects.size() + damageResistances.length * 70);
		//1300 + permanentEffects.size() * 500 + index * 550 - scroll;
		if(error != null){
			g.setColor(Color.CYAN);
			g.setFont(ERROR_FONT);
			g.drawString(error, 10, 50);
		}
	}
	
	private static enum Type {
		
		STRING,
		BYTE,
		SHORT,
		INT,
		FLOAT,
		DOUBLE;
	}
	
	private class TypingButton extends GuiTextButton {
		
		private final Type type;
		
		protected boolean enabled;
		
		public TypingButton(Type type, int startValue, int minX, int minY, int maxX, int maxY){
			this(type, startValue + "", minX, minY, maxX, maxY);
		}
		
		public TypingButton(Type type, float startValue, int minX, int minY, int maxX, int maxY){
			this(type, startValue + "", minX, minY, maxX, maxY);
		}
		
		public TypingButton(Type type, double startValue, int minX, int minY, int maxX, int maxY){
			this(type, startValue + "", minX, minY, maxX, maxY);
		}

		public TypingButton(Type type, String text, int minX, int minY, int maxX, int maxY) {
			super(text, FONT, BUTTON_COLOR, Color.BLACK, Color.BLACK, minX, minY, maxX, maxY);
			this.type = type;
		}
		
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
		
		@Override
		public int minY(){
			return minY - scroll;
		}
		
		@Override
		public int maxY(){
			return maxY - scroll;
		}
		
		public byte getByteValue(){
			try {
				return Byte.parseByte(text);
			} catch(NumberFormatException ex){
				return 0;
			}
		}
		
		public short getShortValue(){
			try {
				return Short.parseShort(text);
			} catch(NumberFormatException ex){
				return 0;
			}
		}
		
		public int getIntValue(){
			try {
				return Integer.parseInt(text);
			} catch(NumberFormatException ex){
				return 0;
			}
		}
		
		public float getFloatValue(){
			try {
				return Float.parseFloat(text);
			} catch(NumberFormatException ex){
				return 0;
			}
		}
		
		public double getDoubleValue(){
			try {
				return Double.parseDouble(text);
			} catch(NumberFormatException ex){
				return 0;
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
			if(type == Type.BYTE)
				text = "" + getByteValue();
			if(type == Type.SHORT)
				text = "" + getShortValue();
			if(type == Type.INT)
				text = "" + getIntValue();
			if(type == Type.FLOAT)
				text = "" + getFloatValue();
			if(type == Type.DOUBLE)
				text = "" + getDoubleValue();
			RaceConfigFrame.markChange();
		}
	}
	
	private class PermanentEffectTab {
		
		private int index;
		
		private final PermanentEffectButton effectButton;
		private final TypingButton levelButton;
		private final PermanentBooleanButton ambientButton;
		private final PermanentBooleanButton particlesButton;
		
		private final PermanentColorButton redButton;
		private final PermanentColorButton greenButton;
		private final PermanentColorButton blueButton;

		public PermanentEffectTab(int index) {
			this.index = index;
			effectButton = new PermanentEffectButton();
			levelButton = new PermanentLevelButton();
			ambientButton = new PermanentBooleanButton(true, 160);
			particlesButton = new PermanentBooleanButton(true, 240);
			redButton = new PermanentColorButton(Color.RED, 300);
			greenButton = new PermanentColorButton(Color.GREEN, 500);
			blueButton = new PermanentColorButton(Color.BLUE, 700);
		}
		
		public PermanentEffectTab(PermanentEffect pe, int index){
			this.index = index;
			effectButton = new PermanentEffectButton();
			effectButton.setText(pe.getType().getType().toLowerCase());
			levelButton = new PermanentLevelButton();
			levelButton.setText("" + pe.getLevel());
			ambientButton = new PermanentBooleanButton(pe.isAmbient(), 160);
			particlesButton = new PermanentBooleanButton(pe.hasParticles(), 240);
			redButton = new PermanentColorButton(Color.RED, 300);
			redButton.setText("" + pe.getColor().getRed());
			greenButton = new PermanentColorButton(Color.GREEN, 500);
			greenButton.setText("" + pe.getColor().getGreen());
			blueButton = new PermanentColorButton(Color.BLUE, 700);
			blueButton.setText("" + pe.getColor().getBlue());
		}

		public void paint(Graphics g) {
			effectButton.paint(g);
			levelButton.paint(g);
			ambientButton.paint(g);
			particlesButton.paint(g);
			redButton.paint(g);
			greenButton.paint(g);
			blueButton.paint(g);
			g.setFont(FONT);
			g.setColor(Color.BLACK);
			int x = 20;
			int dy = 35;
			g.drawString("Effect name:", x, minY() + 0 + dy);
			g.drawString("Effect level:", x, minY() + 80 + dy);
			g.drawString("Is ambient?", x, minY() + 160 + dy);
			g.drawString("Show particles?", x, minY() + 240 + dy);
			g.drawString("Particle color:", x, minY() + 320 + dy);
		}
		
		public void click(int x, int y){
			effectButton.click(x, y);
			levelButton.click(x, y);
			ambientButton.click(x, y);
			particlesButton.click(x, y);
			redButton.click(x, y);
			greenButton.click(x, y);
			blueButton.click(x, y);
		}
		
		public void press(int code){
			effectButton.press(code);
			levelButton.press(code);
			redButton.press(code);
			greenButton.press(code);
			blueButton.press(code);
		}
		
		public void type(char character){
			effectButton.type(character);
			levelButton.type(character);
			redButton.type(character);
			greenButton.type(character);
			blueButton.type(character);
		}
		
		public int minY(){
			return 1400 + index * 500 - scroll + damageResistances.length * 70;
		}

		private class PermanentEffectButton extends EffectButton {

			public PermanentEffectButton() {
				super(300, 0);
			}
			
			@Override
			public int minY(){
				return PermanentEffectTab.this.minY() + minY;
			}
			
			@Override
			public int maxY(){
				return PermanentEffectTab.this.minY() + maxY;
			}
		}
		
		private class PermanentLevelButton extends LevelButton {

			public PermanentLevelButton() {
				super(300, 80);
			}
			
			@Override
			public int minY(){
				return PermanentEffectTab.this.minY() + minY;
			}
			
			@Override
			public int maxY(){
				return PermanentEffectTab.this.minY() + maxY;
			}
		}
		
		private class PermanentBooleanButton extends BooleanButton {

			public PermanentBooleanButton(boolean startValue, int minY) {
				super(startValue, 300, minY);
			}
			
			@Override
			public int minY(){
				return PermanentEffectTab.this.minY() + minY;
			}
			
			@Override
			public int maxY(){
				return PermanentEffectTab.this.minY() + maxY;
			}
		}
		
		private class PermanentColorButton extends ColorButton {

			public PermanentColorButton(Color color, int minX) {
				super(color, minX, 320);
			}
			
			@Override
			public int minY(){
				return PermanentEffectTab.this.minY() + minY;
			}
			
			@Override
			public int maxY(){
				return PermanentEffectTab.this.minY() + maxY;
			}
		}
	}
	
	private class OnAttackEffectTab {
		
		private int index;
		
		private final OnAttackEffectButton effectButton;
		private final OnAttackDurationButton durationButton;
		private final OnAttackLevelButton levelButton;
		private final OnAttackBooleanButton ambientButton;
		private final OnAttackBooleanButton particlesButton;
		
		private final OnAttackColorButton redButton;
		private final OnAttackColorButton greenButton;
		private final OnAttackColorButton blueButton;

		public OnAttackEffectTab(int index) {
			this.index = index;
			effectButton = new OnAttackEffectButton();
			durationButton = new OnAttackDurationButton();
			levelButton = new OnAttackLevelButton();
			ambientButton = new OnAttackBooleanButton(true, 240);
			particlesButton = new OnAttackBooleanButton(true, 320);
			redButton = new OnAttackColorButton(Color.RED, 300);
			greenButton = new OnAttackColorButton(Color.GREEN, 500);
			blueButton = new OnAttackColorButton(Color.BLUE, 700);
		}
		
		public OnAttackEffectTab(ReflectedEffect re, int index){
			this.index = index;
			effectButton = new OnAttackEffectButton();
			effectButton.setText(re.getType().getType().toLowerCase());
			durationButton = new OnAttackDurationButton();
			durationButton.setText("" + re.getDuration());
			levelButton = new OnAttackLevelButton();
			levelButton.setText("" + re.getLevel());
			ambientButton = new OnAttackBooleanButton(re.isAmbient(), 160);
			particlesButton = new OnAttackBooleanButton(re.showParticles(), 240);
			redButton = new OnAttackColorButton(Color.RED, 300);
			redButton.setText("" + re.getColor().getRed());
			greenButton = new OnAttackColorButton(Color.GREEN, 500);
			greenButton.setText("" + re.getColor().getGreen());
			blueButton = new OnAttackColorButton(Color.BLUE, 700);
			blueButton.setText("" + re.getColor().getBlue());
		}

		public void paint(Graphics g) {
			effectButton.paint(g);
			durationButton.paint(g);
			levelButton.paint(g);
			ambientButton.paint(g);
			particlesButton.paint(g);
			redButton.paint(g);
			greenButton.paint(g);
			blueButton.paint(g);
			g.setFont(FONT);
			g.setColor(Color.BLACK);
			int x = 20;
			int dy = 35;
			g.drawString("Effect name:", x, minY() + 0 + dy);
			g.drawString("Duration (ticks):", x, minY() + 80 + dy);
			g.drawString("Effect level:", x, minY() + 160 + dy);
			g.drawString("Is ambient?", x, minY() + 240 + dy);
			g.drawString("Show particles?", x, minY() + 320 + dy);
			g.drawString("Particle color:", x, minY() + 400 + dy);
		}
		
		public void click(int x, int y){
			effectButton.click(x, y);
			durationButton.click(x, y);
			levelButton.click(x, y);
			ambientButton.click(x, y);
			particlesButton.click(x, y);
			redButton.click(x, y);
			greenButton.click(x, y);
			blueButton.click(x, y);
		}
		
		public void press(int code){
			effectButton.press(code);
			durationButton.press(code);
			levelButton.press(code);
			redButton.press(code);
			greenButton.press(code);
			blueButton.press(code);
		}
		
		public void type(char character){
			effectButton.type(character);
			durationButton.type(character);
			levelButton.type(character);
			redButton.type(character);
			greenButton.type(character);
			blueButton.type(character);
		}
		
		public int minY(){
			return 1500 + permanentEffects.size() * 500 + index * 550 - scroll + damageResistances.length * 70;
		}

		private class OnAttackEffectButton extends EffectButton {

			public OnAttackEffectButton() {
				super(300, 0);
			}
			
			@Override
			public int minY(){
				return OnAttackEffectTab.this.minY() + minY;
			}
			
			@Override
			public int maxY(){
				return OnAttackEffectTab.this.minY() + maxY;
			}
		}
		
		private class OnAttackDurationButton extends TypingButton {

			public OnAttackDurationButton() {
				super(Type.INT, 20, 300, 80, 400, 130);
			}
			
			@Override
			public int minY(){
				return OnAttackEffectTab.this.minY() + minY;
			}
			
			@Override
			public int maxY(){
				return OnAttackEffectTab.this.minY() + maxY;
			}
		}
		
		private class OnAttackLevelButton extends LevelButton {

			public OnAttackLevelButton() {
				super(300, 160);
			}
			
			@Override
			public int minY(){
				return OnAttackEffectTab.this.minY() + minY;
			}
			
			@Override
			public int maxY(){
				return OnAttackEffectTab.this.minY() + maxY;
			}
		}
		
		private class OnAttackBooleanButton extends BooleanButton {

			public OnAttackBooleanButton(boolean startValue, int minY) {
				super(startValue, 300, minY);
			}
			
			@Override
			public int minY(){
				return OnAttackEffectTab.this.minY() + minY;
			}
			
			@Override
			public int maxY(){
				return OnAttackEffectTab.this.minY() + maxY;
			}
		}
		
		private class OnAttackColorButton extends ColorButton {

			public OnAttackColorButton(Color color, int minX) {
				super(color, minX, 400);
			}
			
			@Override
			public int minY(){
				return OnAttackEffectTab.this.minY() + minY;
			}
			
			@Override
			public int maxY(){
				return OnAttackEffectTab.this.minY() + maxY;
			}
		}
	}
	
	private class OnHitEffectTab {
		
		private int index;
		
		private final OnAttackEffectButton effectButton;
		private final OnAttackDurationButton durationButton;
		private final OnAttackLevelButton levelButton;
		private final OnAttackBooleanButton ambientButton;
		private final OnAttackBooleanButton particlesButton;
		
		private final OnAttackColorButton redButton;
		private final OnAttackColorButton greenButton;
		private final OnAttackColorButton blueButton;

		public OnHitEffectTab(int index) {
			this.index = index;
			effectButton = new OnAttackEffectButton();
			durationButton = new OnAttackDurationButton();
			levelButton = new OnAttackLevelButton();
			ambientButton = new OnAttackBooleanButton(true, 240);
			particlesButton = new OnAttackBooleanButton(true, 320);
			redButton = new OnAttackColorButton(Color.RED, 300);
			greenButton = new OnAttackColorButton(Color.GREEN, 500);
			blueButton = new OnAttackColorButton(Color.BLUE, 700);
		}
		
		public OnHitEffectTab(ReflectedEffect re, int index){
			this.index = index;
			effectButton = new OnAttackEffectButton();
			effectButton.setText(re.getType().getType().toLowerCase());
			durationButton = new OnAttackDurationButton();
			durationButton.setText("" + re.getDuration());
			levelButton = new OnAttackLevelButton();
			levelButton.setText("" + re.getLevel());
			ambientButton = new OnAttackBooleanButton(re.isAmbient(), 160);
			particlesButton = new OnAttackBooleanButton(re.showParticles(), 240);
			redButton = new OnAttackColorButton(Color.RED, 300);
			redButton.setText("" + re.getColor().getRed());
			greenButton = new OnAttackColorButton(Color.GREEN, 500);
			greenButton.setText("" + re.getColor().getGreen());
			blueButton = new OnAttackColorButton(Color.BLUE, 700);
			blueButton.setText("" + re.getColor().getBlue());
		}

		public void paint(Graphics g) {
			effectButton.paint(g);
			durationButton.paint(g);
			levelButton.paint(g);
			ambientButton.paint(g);
			particlesButton.paint(g);
			redButton.paint(g);
			greenButton.paint(g);
			blueButton.paint(g);
			g.setFont(FONT);
			g.setColor(Color.BLACK);
			int x = 20;
			int dy = 35;
			g.drawString("Effect name:", x, minY() + 0 + dy);
			g.drawString("Duration (ticks):", x, minY() + 80 + dy);
			g.drawString("Effect level:", x, minY() + 160 + dy);
			g.drawString("Is ambient?", x, minY() + 240 + dy);
			g.drawString("Show particles?", x, minY() + 320 + dy);
			g.drawString("Particle color:", x, minY() + 400 + dy);
		}
		
		public void click(int x, int y){
			effectButton.click(x, y);
			durationButton.click(x, y);
			levelButton.click(x, y);
			ambientButton.click(x, y);
			particlesButton.click(x, y);
			redButton.click(x, y);
			greenButton.click(x, y);
			blueButton.click(x, y);
		}
		
		public void press(int code){
			effectButton.press(code);
			durationButton.press(code);
			levelButton.press(code);
			redButton.press(code);
			greenButton.press(code);
			blueButton.press(code);
		}
		
		public void type(char character){
			effectButton.type(character);
			durationButton.type(character);
			levelButton.type(character);
			redButton.type(character);
			greenButton.type(character);
			blueButton.type(character);
		}
		
		public int minY(){
			return 1700 + permanentEffects.size() * 500 + onAttackEffects.size() * 550 + index * 550 - scroll + damageResistances.length * 70;
		}

		private class OnAttackEffectButton extends EffectButton {

			public OnAttackEffectButton() {
				super(300, 0);
			}
			
			@Override
			public int minY(){
				return OnHitEffectTab.this.minY() + minY;
			}
			
			@Override
			public int maxY(){
				return OnHitEffectTab.this.minY() + maxY;
			}
		}
		
		private class OnAttackDurationButton extends TypingButton {

			public OnAttackDurationButton() {
				super(Type.INT, 20, 300, 80, 400, 130);
			}
			
			@Override
			public int minY(){
				return OnHitEffectTab.this.minY() + minY;
			}
			
			@Override
			public int maxY(){
				return OnHitEffectTab.this.minY() + maxY;
			}
		}
		
		private class OnAttackLevelButton extends LevelButton {

			public OnAttackLevelButton() {
				super(300, 160);
			}
			
			@Override
			public int minY(){
				return OnHitEffectTab.this.minY() + minY;
			}
			
			@Override
			public int maxY(){
				return OnHitEffectTab.this.minY() + maxY;
			}
		}
		
		private class OnAttackBooleanButton extends BooleanButton {

			public OnAttackBooleanButton(boolean startValue, int minY) {
				super(startValue, 300, minY);
			}
			
			@Override
			public int minY(){
				return OnHitEffectTab.this.minY() + minY;
			}
			
			@Override
			public int maxY(){
				return OnHitEffectTab.this.minY() + maxY;
			}
		}
		
		private class OnAttackColorButton extends ColorButton {

			public OnAttackColorButton(Color color, int minX) {
				super(color, minX, 400);
			}
			
			@Override
			public int minY(){
				return OnHitEffectTab.this.minY() + minY;
			}
			
			@Override
			public int maxY(){
				return OnHitEffectTab.this.minY() + maxY;
			}
		}
	}
	
	private class EffectButton extends TypingButton {

		public EffectButton(int minX, int minY) {
			super(Type.STRING, getEffectName(SupportedPermanentEffects.values()[0]), minX, minY, minX + 300, minY + 50);
		}
		
		@Override
		public void press(int code){
			super.press(code);
			if(enabled){
				if(code == KeyEvent.VK_LEFT || code == KeyEvent.VK_DOWN){
					try {
						byte ordinal = (byte) SupportedPermanentEffects.valueOf(text.toUpperCase()).ordinal();
						ordinal--;
						if(ordinal < 0)
							ordinal = (byte) (SupportedPermanentEffects.values().length - 1);
						text = getEffectName(SupportedPermanentEffects.values()[ordinal]);
					} catch(IllegalArgumentException iae){
						text = getEffectName(SupportedPermanentEffects.values()[0]);
					}
					RaceConfigFrame.markChange();
				}
				else if(code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_UP){
					try {
						byte ordinal = (byte) SupportedPermanentEffects.valueOf(text.toUpperCase()).ordinal();
						ordinal++;
						if(ordinal >= SupportedPermanentEffects.values().length)
							ordinal = 0;
						text = getEffectName(SupportedPermanentEffects.values()[ordinal]);
					} catch(IllegalArgumentException iae){
						text = getEffectName(SupportedPermanentEffects.values()[0]);
					}
					RaceConfigFrame.markChange();
				}
			}
		}
	}
	
	private class LevelButton extends TypingButton {

		public LevelButton(int minX, int minY) {
			super(Type.SHORT, 1, minX, minY, minX + 100, minY + 50);
		}
		
		@Override
		public short getShortValue(){
			try {
				short s = Short.parseShort(text);
				if(s < -127)
					s = -127;
				if(s > 128)
					s = 128;
				return s;
			} catch(NumberFormatException ex){
				return 1;
			}
		}
	}
	
	private class BooleanButton extends GuiTextButton {

		public BooleanButton(boolean startValue, int minX, int minY) {
			super(startValue ? "Yes" : "No", FONT, startValue ? Color.GREEN : Color.RED, minX, minY, minX + 100, minY + 50);
		}

		@Override
		public void click() {
			if(getValue()){
				text = "No";
				buttonColor = Color.RED;
			}
			else {
				text = "Yes";
				buttonColor = Color.GREEN;
			}
			RaceConfigFrame.markChange();
		}
		
		public boolean getValue(){
			return text.equals("Yes");
		}
	}
	
	private class ColorButton extends TypingButton {

		public ColorButton(Color color, int minX, int minY) {
			super(Type.SHORT, 0, minX, minY, minX + 100, minY + 50);
			buttonColor = color;
		}
		
		@Override
		public short getShortValue(){
			try {
				short s = Short.parseShort(text);
				if(s < 0)
					s = 0;
				if(s > 255)
					s = 255;
				return s;
			} catch(NumberFormatException ex){
				return 0;
			}
		}
	}
	
	private static String getEffectName(SupportedPermanentEffects se){
		return se.name().toLowerCase();
	}
	
	private static boolean isValid(char character){
		return (int)character != 0 && (int) character != 8 && character != KeyEvent.CHAR_UNDEFINED;
	}
	
	private static List<PermanentEffect> getPermanentEffects(List<PermanentEffectTab> tabs){
		List<PermanentEffect> list = new ArrayList<PermanentEffect>(tabs.size());
		for(PermanentEffectTab tab : tabs)
			list.add(new PermanentEffect(new ReflectedEffectType(tab.effectButton.getText()), tab.levelButton.getShortValue(), tab.ambientButton.getValue(), tab.particlesButton.getValue(), new Color(tab.redButton.getShortValue(), tab.greenButton.getShortValue(), tab.blueButton.getShortValue())));
		return list;
	}
	
	private static List<ReflectedEffect> getOnAttackEffects(List<OnAttackEffectTab> tabs){
		List<ReflectedEffect> list = new ArrayList<ReflectedEffect>(tabs.size());
		for(OnAttackEffectTab tab : tabs)
			list.add(new ReflectedEffect(new ReflectedEffectType(tab.effectButton.getText()), tab.durationButton.getIntValue(), tab.levelButton.getShortValue(), tab.particlesButton.getValue(), tab.ambientButton.getValue(), new Color(tab.redButton.getShortValue(), tab.greenButton.getShortValue(), tab.blueButton.getShortValue())));
		return list;
	}
	
	private static List<ReflectedEffect> getOnHitEffects(List<OnHitEffectTab> tabs){
		List<ReflectedEffect> list = new ArrayList<ReflectedEffect>(tabs.size());
		for(OnHitEffectTab tab : tabs)
			list.add(new ReflectedEffect(new ReflectedEffectType(tab.effectButton.getText()), tab.durationButton.getIntValue(), tab.levelButton.getShortValue(), tab.particlesButton.getValue(), tab.ambientButton.getValue(), new Color(tab.redButton.getShortValue(), tab.greenButton.getShortValue(), tab.blueButton.getShortValue())));
		return list;
	}
}
