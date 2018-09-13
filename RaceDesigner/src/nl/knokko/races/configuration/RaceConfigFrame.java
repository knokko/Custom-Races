package nl.knokko.races.configuration;

import java.awt.HeadlessException;

import javax.swing.JFrame;

import nl.knokko.races.configuration.gui.Gui;
import nl.knokko.races.configuration.gui.GuiMain;

public class RaceConfigFrame extends JFrame {

	private static final long serialVersionUID = 3354576747407083265L;
	
	public static final int WIDTH = 900;
	public static final int HEIGHT = 600;
	
	private static RaceConfigFrame instance;
	
	public static RaceConfigFrame instance(){
		return instance;
	}
	
	public static void main(String[] arguments){
		instance = new RaceConfigFrame();
		instance.open();
		instance.run();
	}
	
	public static void markChange(){
		instance().gui.markChange();
	}
	
	private final RaceConfigPanel panel;
	private final RaceConfigInput input;
	
	private Gui gui;
	
	private boolean stopping;

	private RaceConfigFrame() throws HeadlessException {
		super();
		panel = new RaceConfigPanel();
		input = new RaceConfigInput();
	}
	
	@Override
	public void dispose(){
		stopping = true;
	}
	
	private void open(){
		setGui(new GuiMain());
		add(panel);
		setUndecorated(true);
		setSize(WIDTH, HEIGHT);
		setTitle("Race Editor");
		setVisible(true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		addKeyListener(input);
		addMouseListener(input);
		addMouseWheelListener(input);
		addMouseMotionListener(input);
	}
	
	private void run(){
		while(!stopping){
			try {
				if(gui.needsRender())
					panel.repaint();
				Thread.sleep(40);
			} catch(Exception ex){
				ex.printStackTrace();
				break;
			}
		}
		terminate();
	}
	
	private void terminate(){
		super.dispose();
	}
	
	public Gui getGui(){
		return gui;
	}
	
	public void setGui(Gui newGui){
		gui = newGui;
		gui.markChange();
	}
	
	public void click(int x, int y){
		gui.click(x, y);
	}
	
	public void scroll(int amount){
		gui.scroll(amount);
	}
	
	public void type(char key){
		gui.type(key);
	}
	
	public void press(int keycode){
		gui.press(keycode);
	}
}
