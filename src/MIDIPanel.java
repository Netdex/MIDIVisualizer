import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Synthesizer;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class MIDIPanel extends JPanel {

	private Keyboard[] keyboards;
	private String status = "";

	private MIDIPanel mp = this;
	private Sequencer seq;

	public static int KEYBOARD_HEIGHT = Keyboard.KEY_HEIGHT + 18;

	public MIDIPanel() {
		this.setPreferredSize(new Dimension(1024, 900));
		this.setMinimumSize(new Dimension(1024, 900));
		this.setBackground(Color.GRAY);
		this.setFocusable(true);

		keyboards = new Keyboard[16];
		for (int i = 0; i < 16; i++) {
			keyboards[i] = new Keyboard(this);
		}

		this.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent event) {
				int keycode = event.getKeyCode();
				switch (keycode) {
				case KeyEvent.VK_UP:
					String in = JOptionPane.showInputDialog(null, "Enter seek tick:");
					try {
						int tick = Integer.parseInt(in);
						seq.setTickPosition(tick);
					} catch (NumberFormatException e) {
						JOptionPane.showMessageDialog(null, "Not a number!", "Seek Error",
								JOptionPane.ERROR_MESSAGE);
					}
					break;
				case KeyEvent.VK_SPACE:
					String fname = JOptionPane.showInputDialog(null, "Enter MIDI name:");
					mp.loadSequence(new File(fname));
					break;
				case KeyEvent.VK_ENTER:
					// new Thread(){
					// public void run(){
					// if(mp.getSequencer().isRunning())
					// mp.getSequencer().stop();
					// else{
					// System.out.println("WUT");
					// mp.getSequencer().start();
					//
					// }
					// }
					// }.start();

					break;
				case KeyEvent.VK_LEFT:
					mp.getSequencer().setTempoInBPM(mp.getSequencer().getTempoInBPM() - 10);
					break;
				case KeyEvent.VK_RIGHT:
					mp.getSequencer().setTempoInBPM(mp.getSequencer().getTempoInBPM() + 10);
					break;
				}
			}
		});

		try {
			Synthesizer synthesizer = MidiSystem.getSynthesizer();
			synthesizer.open();

			seq = MidiSystem.getSequencer();
			seq.open();
			PianoReceiver pr = new PianoReceiver(System.out, mp, true);
			seq.getTransmitter().setReceiver(pr);
			seq.addMetaEventListener(pr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		new Thread() {
			public void run() {
				while (true) {
					try {
						mp.repaint();
						Thread.sleep(15);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
	}

	public void loadSequence(File file) {
		try {
			((JFrame) this.getParent().getParent().getParent().getParent())
					.setTitle("MIDI Visualizer: " + file.getPath());
			loadSequence(MidiSystem.getSequence(file));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void loadSequence(Sequence sequence) {
		try {
			seq.setSequence(sequence);
			seq.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (int i = 0; i < 16; i++) {
			keyboards[i].reset();
		}
	}

	public void render(Graphics2D g) {
		// g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		// RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(Color.WHITE);
		g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 12));

		int used = 0;
		for (Keyboard k : keyboards)
			if (k.isUsed())
				used++;
		int offset = (this.getHeight() - used * KEYBOARD_HEIGHT) / 2 + 10;
		for (Keyboard k : keyboards) {
			if (k.isUsed()) {
				k.render(g, offset);
				offset += KEYBOARD_HEIGHT;
			}
		}
		g.setColor(Color.GREEN);
		int strlen = g.getFontMetrics().stringWidth(status);
		g.drawString(status, this.getWidth() - strlen - 30, 15);
		String length = "TOTAL LENGTH: "
				+ timeInSecondsToString((int) (seq.getMicrosecondLength() / 1000000))
				+ "           CURRENT TIME: "
				+ timeInSecondsToString((int) (seq.getMicrosecondPosition() / 1000000))
				+ "           BPM: " + seq.getTempoInBPM();
		g.setColor(Color.YELLOW);
		g.drawString(length, 10, this.getHeight() - 3);
	}

	private static String timeInSecondsToString(int totalSeconds) {

		final int MINUTES_IN_AN_HOUR = 60;
		final int SECONDS_IN_A_MINUTE = 60;

		int seconds = totalSeconds % SECONDS_IN_A_MINUTE;
		int totalMinutes = totalSeconds / SECONDS_IN_A_MINUTE;
		int minutes = totalMinutes % MINUTES_IN_AN_HOUR;
		int hours = totalMinutes / MINUTES_IN_AN_HOUR;

		return hours + " hours " + minutes + " minutes " + seconds + " seconds";
	}

	public Keyboard[] getKeyboards() {
		return keyboards;
	}

	public Sequencer getSequencer() {
		return seq;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public void paintComponent(Graphics gr) {
		super.paintComponent(gr);
		Graphics2D g = (Graphics2D) gr;
		render(g);
	}
}
