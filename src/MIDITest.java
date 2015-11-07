import java.io.File;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.Track;

public class MIDITest {
	public static final int NOTE_ON = 0x90;
	public static final int NOTE_OFF = 0x80;
	public static final String[] NOTE_NAMES = { "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#",
			"A", "A#", "B" };

	public static void main(String[] args) throws Exception {
		Sequence sequence = MidiSystem.getSequence(new File("samp/innocenttreasures.mid"));
		float divType = sequence.getDivisionType();
		System.out.println(divType);
		Synthesizer synthesizer = MidiSystem.getSynthesizer();
		synthesizer.open();
//		 Sequencer sequencer = MidiSystem.getSequencer();
//		 sequencer.open();
//		 sequencer.setSequence(sequence);
//		 sequencer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
//		 sequencer.start();
//		 sequencer.getTransmitter().setReceiver(new DumpReceiver(System.out, false));
//		 
		// CHANNEL EVENT NOTE VELOCITY
		int trackNumber = 0;
		try {
			for (Track track : sequence.getTracks()) {
				new Thread() {
					public void run() {
						long lastTick = 0;
						System.out.println("Track " + trackNumber + ": size = " + track.size());
						System.out.println();
						for (int i = 0; i < track.size(); i++) {
							MidiEvent event = track.get(i);
							long tick = event.getTick();
							System.out.print("@" + event.getTick() + " ");
							MidiMessage message = event.getMessage();
							if (message instanceof ShortMessage) {
								ShortMessage sm = (ShortMessage) message;
								int channel = sm.getChannel();
								System.out.print("Channel: " + channel + " ");
								if (sm.getCommand() == NOTE_ON) {
									int key = sm.getData1();
									int octave = (key / 12) - 1;
									int note = key % 12;
									String noteName = NOTE_NAMES[note];
									int velocity = sm.getData2();
									System.out.println("Note on, " + noteName + octave + " key="
											+ key + " velocity: " + velocity);
									synthesizer.getChannels()[trackNumber].noteOn(key, velocity);
								} else if (sm.getCommand() == NOTE_OFF) {
									int key = sm.getData1();
									int octave = (key / 12) - 1;
									int note = key % 12;
									String noteName = NOTE_NAMES[note];
									int velocity = sm.getData2();
									System.out.println("Note off, " + noteName + octave + " key="
											+ key + " velocity: " + velocity);
									synthesizer.getChannels()[trackNumber].noteOff(key);

								} else {
									System.out.println("Command:" + sm.getCommand());
								}
							} else {
								System.out.println("Other message: " + message.getClass());
							}
							try {
								Thread.sleep(tick - lastTick);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							lastTick = tick;
						}
						System.out.println();
					}
				}.start();

			}
		} catch (Exception e) {

		}

	}
}