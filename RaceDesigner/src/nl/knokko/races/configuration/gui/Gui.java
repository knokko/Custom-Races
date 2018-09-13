package nl.knokko.races.configuration.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import nl.knokko.races.configuration.RaceConfigFrame;
import nl.knokko.races.configuration.gui.button.GuiButton;

public abstract class Gui {
	
	protected final List<GuiButton> buttons;
	
	private boolean needsRender;

	public Gui() {
		buttons = new ArrayList<GuiButton>();
	}
	
	public boolean needsRender(){
		return needsRender;
	}
	
	public void markChange(){
		needsRender = true;
	}
	
	public void paint(Graphics g){
		g.setColor(getBackGroundColor());
		g.fillRect(0, 0, RaceConfigFrame.WIDTH, RaceConfigFrame.HEIGHT);
		for(GuiButton button : buttons)
			button.paint(g);
	}
	
	public void click(int x, int y){
		for(GuiButton button : buttons)
			button.click(x, y);
	}
	
	public void scroll(int amount){}
	
	public void type(char key){
		for(GuiButton button : buttons)
			button.type(key);
	}
	
	public void press(int code){
		for(GuiButton button : buttons)
			button.press(code);
	}
	
	protected abstract Color getBackGroundColor();
	
	protected void addButton(GuiButton button){
		buttons.add(button);
	}
	
	protected void clearButtons(){
		buttons.clear();
	}
}
