package nl.knokko.races.configuration;

import java.awt.Graphics;

import javax.swing.JPanel;

public class RaceConfigPanel extends JPanel {

	private static final long serialVersionUID = -8614755464350270513L;

	public RaceConfigPanel() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void paint(Graphics g){
		RaceConfigFrame.instance().getGui().paint(g);
	}
}
