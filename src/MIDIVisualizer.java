import java.awt.Dimension;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.UIManager;

public class MIDIVisualizer extends JFrame {

	private MIDIPanel mp;

	public static String MIDI_NAME = "samp/smdr08_21medley.mid";

	public MIDIVisualizer() {
		super("MIDI Visualizer: " + MIDI_NAME);
//		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setMinimumSize(new Dimension(1024, 950));
		
		mp = new MIDIPanel();
		this.add(mp);
//		new Thread(){
//			public void run(){
//				try{
//				File[] files = new File("test").listFiles();
//				for(File f : files){
//					mp.loadSequence(f);
//					while(mp.getSequencer().getTickPosition() != 
//							mp.getSequencer().getTickLength()){
//						Thread.sleep(1000);
//					}
//				}
//				}catch(Exception e){
//					
//				}
//			}
//		}.start();
		mp.loadSequence(new File(MIDI_NAME));
		
	}

	public static void main(String[] args) throws Exception {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		MIDIVisualizer mv = new MIDIVisualizer();
		mv.pack();
		mv.setVisible(true);
	}
}
