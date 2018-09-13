package nl.knokko.races.configuration.gui.button;

import java.awt.Graphics;

public abstract class GuiButton {
	
	protected int minX;
	protected int minY;
	
	protected int maxX;
	protected int maxY;

	public GuiButton(int minX, int minY, int maxX, int maxY) {
		this.minX = minX;
		this.minY = minY;
		this.maxX = maxX;
		this.maxY = maxY;
	}
	
	public int minX(){
		return minX;
	}
	
	public int minY(){
		return minY;
	}
	
	public int maxX(){
		return maxX;
	}
	
	public int maxY(){
		return maxY;
	}
	
	public int width(){
		return maxX() - minX();
	}
	
	public int height(){
		return maxY() - minY();
	}
	
	public boolean isHit(int x, int y){
		return x >= minX() && x <= maxX() && y >= minY() && y <= maxY();
	}
	
	public abstract void paint(Graphics g);
	
	public void click(int x, int y){
		if(isHit(x, y))
			click();
	}
	
	public abstract void click();
	
	public void type(char key){}
	
	public void press(int keycode){}
}
