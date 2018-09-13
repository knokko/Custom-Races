package nl.knokko.races.configuration.gui.button;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import nl.knokko.races.configuration.RaceConfigFrame;

public abstract class GuiTextButton extends GuiButton {
	
	private static final Font FONT = new Font("TimesRoman", 0, 30);
	
	protected String text;
	protected Font font;
	
	protected Color textColor;
	protected Color buttonColor;
	protected Color borderColor;

	public GuiTextButton(String text, Font font, Color buttonColor, Color borderColor, Color textColor, int minX, int minY, int maxX, int maxY) {
		super(minX, minY, maxX, maxY);
		this.text = text;
		this.buttonColor = buttonColor;
		this.borderColor = borderColor;
		this.textColor = textColor;
		this.font = font;
	}
	
	public GuiTextButton(String text, Color color, int minX, int minY, int maxX, int maxY){
		this(text, FONT, color, minX, minY, maxX, maxY);
	}
	
	public GuiTextButton(String text, Font font, Color color, int minX, int minY, int maxX, int maxY){
		this(text, font, color, Color.BLACK, Color.BLACK, minX, minY, maxX, maxY);
	}

	@Override
	public void paint(Graphics g) {
		g.setColor(buttonColor);
		g.fillRect(minX(), minY(), width(), height());
		g.setColor(borderColor);
		g.drawRect(minX(), minY(), width(), height());
		g.setColor(textColor);
		g.setFont(font);
		g.drawString(text, minX() + width() / 20, maxY() - height() / 3);
	}
	
	public String getText(){
		return text;
	}
	
	public void setText(String newText){
		text = newText;
		RaceConfigFrame.markChange();
	}
	
	public void setColor(Color newColor){
		buttonColor = newColor;
		setText(text);
	}
}
