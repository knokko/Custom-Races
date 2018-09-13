package nl.knokko.races.configuration;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class RaceConfigInput implements MouseListener, KeyListener, MouseWheelListener, MouseMotionListener {
	
	public RaceConfigInput() {
		// TODO Auto-generated constructor stub
	}

	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseWheelMoved(MouseWheelEvent event) {
		RaceConfigFrame.instance().scroll(event.getUnitsToScroll() * 5);
	}

	public void keyTyped(KeyEvent event) {
		RaceConfigFrame.instance().type(event.getKeyChar());
	}

	public void keyPressed(KeyEvent event) {
		if(event.getKeyCode() == KeyEvent.VK_ESCAPE)
			RaceConfigFrame.instance().dispose();
		RaceConfigFrame.instance().press(event.getKeyCode());
	}

	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseClicked(MouseEvent event) {
		RaceConfigFrame.instance().click(event.getX(), event.getY());
	}

	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseEntered(MouseEvent e) {
		RaceConfigFrame.markChange();
	}

	public void mouseExited(MouseEvent e) {
		RaceConfigFrame.markChange();
	}

}
