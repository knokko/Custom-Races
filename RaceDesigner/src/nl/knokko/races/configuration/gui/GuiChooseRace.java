package nl.knokko.races.configuration.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nl.knokko.races.base.Race;
import nl.knokko.races.base.RaceFactory;
import nl.knokko.races.base.RaceFactory.AdvancedRace;
import nl.knokko.races.base.RaceFactory.SimpleRace;
import nl.knokko.races.configuration.RaceConfigFrame;
import nl.knokko.races.configuration.gui.button.GuiTextButton;
import nl.knokko.util.bits.ByteArrayBitInput;

public class GuiChooseRace extends Gui {
	
	private static final Color BUTTON_COLOR = Color.ORANGE;
	private static final Font FONT = new Font("TimesRoman", Font.PLAIN, 40);
	
	private int scroll;

	public GuiChooseRace() {
		File folder = getFolder();
		folder.mkdirs();
		File[] files = folder.listFiles();
		List<File> raceFiles = new ArrayList<File>(files.length);
		for(File file : files)
			if(file.getName().endsWith(".race"))
				raceFiles.add(file);
		addButton(new GuiTextButton("Back", BUTTON_COLOR, 100, 200, 200, 250){

			@Override
			public void click() {
				RaceConfigFrame.instance().setGui(new GuiMain());
			}
			
		});
		int i = 0;
		for(File file : raceFiles){
			addButton(new RaceFileButton(file, i));
			i++;
		}
	}

	@Override
	protected Color getBackGroundColor() {
		return Color.BLUE;
	}
	
	@Override
	public void paint(Graphics g){
		super.paint(g);
		g.setColor(Color.YELLOW);
		g.fillRect(400, 0, 50, 600);
	}
	
	@Override
	public void scroll(int amount){
		scroll += amount;
	}
	
	public static File getFolder(){
		return new File("CustomRaces" + File.separator + "races");
	}
	
	private class RaceFileButton extends GuiTextButton {
		
		private final File file;

		public RaceFileButton(File file, int index) {
			super(file.getName().substring(0, file.getName().length() - 5), FONT, BUTTON_COLOR, 500, 200 + 70 * index, 800, 250 + 70 * index);
			this.file = file;
		}

		@Override
		public void click() {
			try {
				Race race = RaceFactory.loadRace(text, ByteArrayBitInput.fromFile(file));
				if(race instanceof RaceFactory.SimpleRace)
					RaceConfigFrame.instance().setGui(new GuiSimpleRace((SimpleRace) race));
				if(race instanceof RaceFactory.AdvancedRace)
					RaceConfigFrame.instance().setGui(new GuiAdvancedRace((AdvancedRace) race));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		@Override
		public int minY(){
			return minY + scroll;
		}
		
		@Override
		public int maxY(){
			return maxY + scroll;
		}
	}
}
