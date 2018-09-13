package nl.knokko.races.configuration.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import nl.knokko.races.configuration.RaceConfigFrame;
import nl.knokko.races.configuration.gui.button.GuiTextButton;

public class GuiMain extends Gui {
	
	private static final Color BUTTON_COLOR = Color.GREEN;
	private static final Font FONT = new Font("TimesRoman", 0, 40);

	public GuiMain() {
		addButton(new GuiTextButton("Edit existing race", FONT, BUTTON_COLOR, 500, 250, 800, 350){

			@Override
			public void click() {
				RaceConfigFrame.instance().setGui(new GuiChooseRace());
			}
		});
		addButton(new GuiTextButton("Create simple race", FONT, BUTTON_COLOR, 50, 250, 450, 300){

			@Override
			public void click() {
				RaceConfigFrame.instance().setGui(new GuiSimpleRace());
			}
		});
		addButton(new GuiTextButton("Create advanced race", FONT, BUTTON_COLOR, 50, 320, 450, 370){

			@Override
			public void click() {
				RaceConfigFrame.instance().setGui(new GuiAdvancedRace());
			}
			
		});
		addButton(new GuiTextButton("Stop", FONT, BUTTON_COLOR, 400, 450, 500, 500){

			@Override
			public void click() {
				RaceConfigFrame.instance().dispose();
			}
		});
	}

	@Override
	protected Color getBackGroundColor() {
		return Color.BLUE;
	}
	
	@Override
	public void paint(Graphics g){
		super.paint(g);
		g.setColor(Color.BLACK);
		g.setFont(FONT);
		g.drawString("What would you like to do?", 240, 150);
	}
}
