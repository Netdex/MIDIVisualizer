import java.awt.Color;
import java.awt.Graphics2D;

import javax.sound.midi.Instrument;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Patch;

public class Keyboard {

	private static int X_OFFSET = 30;
	private static final int Y_HEIGHT = 40;

	private static final boolean[] SPECIAL_KEYS = { false, true, false, true, false, false, true,
			false, true, false, true, false };

	private int[] isPressed = new int[128];

	private String instName;
	private int program = 0;

	private int width;
	private int yoffset;

	public Keyboard(int width, int yoffset) {
		this.width = width;
		this.yoffset = yoffset;
		
		this.reset();
	}

	public void render(Graphics2D g) {
		if(instName.equals("Unused"))
			return;
		g.setColor(Color.WHITE);
		g.drawString(instName, X_OFFSET, yoffset - 2);
		double keyWidth = width / 70;
		X_OFFSET = (int) ((width - keyWidth * 70) / 2);
		double keyPos = 0;
		for (int octave = 0; octave < 10; octave++) {
			for (int key = 0; key < 12; key++) {
				int noteNo = octave * 12 + key;
				if (!SPECIAL_KEYS[key]) {
					if (isPressed[noteNo] != 0) {
						g.setColor(Color.PINK);
						g.fillRect((int) keyPos + X_OFFSET, yoffset, (int) keyWidth, Y_HEIGHT);
					}
					else{
						g.setColor(Color.WHITE);
						g.fillRect((int) keyPos + X_OFFSET, yoffset, (int) keyWidth, Y_HEIGHT);
					}
					g.setColor(Color.BLACK);
					g.drawRect((int) keyPos + X_OFFSET, yoffset, (int) keyWidth, Y_HEIGHT);
					keyPos += keyWidth;
				}
			}
		}
		keyPos = 0;
		for (int octave = 0; octave < 10; octave++) {
			for (int key = 0; key < 12; key++) {
				int noteNo = octave * 12 + key;
				if (SPECIAL_KEYS[key]) {
					if (isPressed[noteNo] != 0) {
						g.setColor(Color.RED);
						g.fillRect((int) (keyPos - keyWidth / 3) + X_OFFSET, yoffset,
								(int) (keyWidth * 2 / 3.0), Y_HEIGHT / 2);
					} else {
						g.setColor(Color.DARK_GRAY);
						g.fillRect((int) (keyPos - keyWidth / 3) + X_OFFSET, yoffset,
								(int) (keyWidth * 2 / 3.0), Y_HEIGHT / 2);
					}
					g.setColor(Color.BLACK);
					g.drawRect((int) (keyPos - keyWidth / 3) + X_OFFSET, yoffset,
							(int) (keyWidth * 2 / 3.0), Y_HEIGHT / 2);
				} else {
					keyPos += keyWidth;
				}
			}
		}

	}

	public void reset(){
		this.instName = "Unused";
		for(int i = 0; i < 128; i++)
			isPressed[i] = 0;
	}
	
	private static String getInstName(int program) {
		try {
			Instrument[] inst = MidiSystem.getSynthesizer().getAvailableInstruments();
			String instName = "";
			for (int i = 0; i < inst.length; i++) {
				if (inst[i].getPatch().getProgram() == program){
					instName = inst[i].getName();
					break;
				}
			}
			return instName;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public void setProgram(int prog) {
		this.program = prog;
		instName = getInstName(program);
	}

	public void setPressed(int idx, int pressed) {
		isPressed[idx] = pressed;
	}
}
