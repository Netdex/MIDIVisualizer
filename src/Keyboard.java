import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.sound.midi.Instrument;
import javax.sound.midi.MidiSystem;

public class Keyboard {

	private int X_OFFSET = 30;
	private int CHANNEL_NUMBER;
	public static final int SHADOW_HEIGHT = 5;
	public static final int KEY_HEIGHT = 37;

	private static final boolean[] SPECIAL_KEYS = { false, true, false, true, false, false, true,
			false, true, false, true, false };

	private int[] isPressed = new int[128];

	private String instName;
	private int program = 0;
	private static final int FADE_LENGTH = 64;
	private int trackIndicatorFade = 0;
	
	private Rectangle muteButton = new Rectangle(0,0,0,0);
	
	private MIDIPanel mp;

	private ChordIdentifier ci = new ChordIdentifier();
	
	public Keyboard(MIDIPanel mp, int trackNumber) {
		this.mp = mp;
		this.CHANNEL_NUMBER = trackNumber;
		this.reset();
	}

	public void render(Graphics2D g, int Y_OFFSET) {
		if (instName.equals("Unused"))
			return;
		
		g.setColor(Color.WHITE);
		g.drawString(instName, X_OFFSET, Y_OFFSET - 2);
		String[] chords = ci.getChordName(this.getAllOnKeys());
		if(chords.length > 0){
			g.setColor(Color.CYAN);
			String crds = "";
			for(String s : chords){
				crds += s + " OR ";
			}
			g.drawString(crds.substring(0, crds.length() - 4), X_OFFSET + 100, Y_OFFSET - 2);
		}
		double keyWidth = 1024 / 70;
		X_OFFSET = (int) ((mp.getWidth() - keyWidth * 70) / 2);

		GradientPaint gp = new GradientPaint(0, Y_OFFSET, Color.LIGHT_GRAY, 0,
				Y_OFFSET + KEY_HEIGHT, Color.WHITE);
		g.setPaint(gp);
		g.fillRect(X_OFFSET, Y_OFFSET, mp.getWidth() - 2 * X_OFFSET, KEY_HEIGHT);
		g.setPaint(Color.BLACK);
		
		this.muteButton = new Rectangle(X_OFFSET - 15, Y_OFFSET, 10, KEY_HEIGHT);
		if(this.isMuted()){
			g.setColor(Color.RED);
		}
		else{
			g.setColor(new Color(0, 255, 0, (int)(255.0 * trackIndicatorFade / FADE_LENGTH)));
		}
		
		g.fill(muteButton);
		
		g.setColor(Color.BLACK);
		g.draw(muteButton);
		double keyPos = 0;
		for (int octave = 0; octave < 10; octave++) {
			for (int key = 0; key < 12; key++) {
				int noteNo = octave * 12 + key;
				if (!SPECIAL_KEYS[key]) {
					if (isPressed[noteNo] != 0) {
						g.setColor(Color.PINK);
						g.fillRect((int) keyPos + X_OFFSET, Y_OFFSET, (int) keyWidth, KEY_HEIGHT);
						g.setColor(Color.BLACK);
						g.fillRect((int) keyPos + X_OFFSET, Y_OFFSET, (int) keyWidth / 6,
								KEY_HEIGHT);
						g.setColor(Color.RED);
						g.drawString(PianoReceiver.getKeyName(noteNo).charAt(0) + "",
								(int) keyPos + X_OFFSET + 5, Y_OFFSET + KEY_HEIGHT - 4);
					} else {
						// g.setColor(Color.WHITE);
						// g.fillRect((int) keyPos + X_OFFSET, Y_OFFSET, (int)
						// keyWidth,
						// Y_HEIGHT - SHADOW_HEIGHT);
						g.setColor(Color.GRAY);
						g.fillRect((int) keyPos + X_OFFSET, Y_OFFSET + KEY_HEIGHT - SHADOW_HEIGHT,
								(int) keyWidth, SHADOW_HEIGHT);
						g.setColor(Color.LIGHT_GRAY);
						g.drawString(PianoReceiver.getKeyName(noteNo).charAt(0) + "",
								(int) keyPos + X_OFFSET + 5, Y_OFFSET + KEY_HEIGHT - 7);
					}
					g.setColor(Color.BLACK);
					g.drawRect((int) keyPos + X_OFFSET, Y_OFFSET, (int) keyWidth, KEY_HEIGHT);
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
						g.fillRect((int) (keyPos - keyWidth / 3) + X_OFFSET, Y_OFFSET,
								(int) (keyWidth * 2 / 3.0), KEY_HEIGHT / 2);
					} else {
						g.setColor(Color.DARK_GRAY);
						g.fillRect((int) (keyPos - keyWidth / 3) + X_OFFSET, Y_OFFSET,
								(int) (keyWidth * 2 / 3.0), KEY_HEIGHT / 2 - SHADOW_HEIGHT / 2);
						g.setColor(Color.BLACK);
						g.fillRect((int) (keyPos - keyWidth / 3) + X_OFFSET,
								Y_OFFSET + KEY_HEIGHT / 2 - SHADOW_HEIGHT / 2,
								(int) (keyWidth * 2 / 3.0), SHADOW_HEIGHT / 2);

					}
					g.setColor(Color.BLACK);
					g.drawRect((int) (keyPos - keyWidth / 3) + X_OFFSET, Y_OFFSET,
							(int) (keyWidth * 2 / 3.0), KEY_HEIGHT / 2);
				} else {
					keyPos += keyWidth;
				}
			}
		}

	}

	public void tick(){
		if(trackIndicatorFade > 0)
			trackIndicatorFade--;
	}
	
	public void reset() {
		this.instName = "Unused";
		for (int i = 0; i < 128; i++)
			isPressed[i] = 0;
		this.program = 0;
	}

	private static String getInstName(int program) {
		try {
			Instrument[] inst = MidiSystem.getSynthesizer().getAvailableInstruments();
			String instName = "";
			for (int i = 0; i < inst.length; i++) {
				if (inst[i].getPatch().getProgram() == program) {
					instName = inst[i].getName();
					break;
				}
			}
			return instName;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "Unknown";
	}

	public boolean isPlaying() {
		for (int i : isPressed)
			if (i > 0)
				return true;
		return false;
	}

	public boolean isUsed() {
		return !this.instName.equals("Unused");
	}

	public void setProgram(int prog) {
		this.program = prog;
		instName = getInstName(program);
	}
	
	public void setMuted(boolean mute){
		mp.getSynthesizer().getChannels()[CHANNEL_NUMBER].setMute(mute);
	}
	
	public boolean isMuted(){
		return mp.getSynthesizer().getChannels()[CHANNEL_NUMBER].getMute();
	}
	
	public Rectangle getMuteButtonBounds(){
		return muteButton.getBounds();
	}
	
	public int[] getAllOnKeys(){
		int c = 0;
		for(int i = 0; i < isPressed.length; i++){
			if(isPressed[i] > 0)
				c++;
		}
		int[] on = new int[c];
		int idx = 0;
		for(int i = 0; i < isPressed.length; i++){
			if(isPressed[i] > 0)
				on[idx++] = i;
		}
		return on;
	}
	public void setPressed(int idx, int pressed) {
		isPressed[idx] = pressed;
		if(pressed > 0){
			trackIndicatorFade = FADE_LENGTH;
		}
		if (this.instName.equals("Unused"))
			this.instName = "Unknown";
	}
}
