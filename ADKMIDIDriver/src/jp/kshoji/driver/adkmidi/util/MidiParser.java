package jp.kshoji.driver.adkmidi.util;

import java.io.ByteArrayOutputStream;

/**
 * Parses Legacy MIDI events
 * 
 * @author K.Shoji
 *
 */
public class MidiParser {
	private int midiState = MIDI_STATE_WAIT;
	private int midiEventKind = 0;
	private int midiEventNote = 0;
	private int midiEventVelocity = 0;

	private ByteArrayOutputStream systemExclusiveStream;
	
	private static final int MIDI_STATE_WAIT = 0;
	private static final int MIDI_STATE_SIGNAL_2BYTES_2 = 21;
	private static final int MIDI_STATE_SIGNAL_3BYTES_2 = 31;
	private static final int MIDI_STATE_SIGNAL_3BYTES_3 = 32;
	private static final int MIDI_STATE_SIGNAL_SYSEX = 41;
	
	public MidiParser() {
		midiState = MIDI_STATE_WAIT;
		midiEventKind = 0;
		midiEventNote = 0;
		midiEventVelocity = 0;
	}

	public MidiMessage parseMidiEvent(byte event) {
		MidiMessage midiMsg = null;
		int midiEvent = event & 0xff;
		if (midiState == MIDI_STATE_WAIT) {
			switch (midiEvent & 0xf0) {
			case 0xf0: {
				switch (midiEvent) {
				case 0xf0:
					synchronized (this) {
						if (systemExclusiveStream == null) {
							systemExclusiveStream = new ByteArrayOutputStream();
						}
					}

					synchronized (systemExclusiveStream) {
						systemExclusiveStream.write(midiEvent);
						midiState = MIDI_STATE_SIGNAL_SYSEX;
					}
					break;

				case 0xf1:
				case 0xf3:
					// 0xf1 MIDI Time Code Quarter Frame. : 2bytes
					// 0xf3 Song Select. : 2bytes
					midiEventKind = midiEvent;
					midiState = MIDI_STATE_SIGNAL_2BYTES_2;
					break;

				case 0xf2:
					// 0xf2 Song Position Pointer. : 3bytes
					midiEventKind = midiEvent;
					midiState = MIDI_STATE_SIGNAL_3BYTES_2;
					break;

				case 0xf6:
				case 0xf8:
				case 0xfa:
				case 0xfb:
				case 0xfc:
				case 0xfe:
				case 0xff:
					// 0xf6 Tune Request : 1byte
					// 0xf8 Timing Clock : 1byte
					// 0xfa Start : 1byte
					// 0xfb Continue : 1byte
					// 0xfc Stop : 1byte
					// 0xfe Active Sensing : 1byte
					// 0xff Reset : 1byte
					// handle event
					midiMsg = new MidiMessage(midiEvent, 0, 0);
					midiState = MIDI_STATE_WAIT;
					break;
					
				default:
						break;
				}
			}
				break;
			case 0x80:
			case 0x90:
			case 0xa0:
			case 0xb0:
			case 0xe0:
				// 3bytes pattern
				midiEventKind = midiEvent;
				midiState = MIDI_STATE_SIGNAL_3BYTES_2;
				break;
			case 0xc0: // program change
			case 0xd0: // channel after-touch
				// 2bytes pattern
				midiEventKind = midiEvent;
				midiState = MIDI_STATE_SIGNAL_2BYTES_2;
				break;
			default:
				// 0x00 - 0x70: running status
				if ((midiEventKind & 0xf0) != 0xf0) {
					// previous event kind is multi-bytes pattern
					midiEventNote = midiEvent;
					midiState = MIDI_STATE_SIGNAL_3BYTES_3;
				}
				break;
			}
		} else if (midiState == MIDI_STATE_SIGNAL_2BYTES_2) {
			switch (midiEventKind & 0xf0) {
			case 0xc0: // program change
			case 0xd0: // channel after-touch
			case 0xf0:
				// 2bytes pattern
				midiEventNote = midiEvent;
				// handle event
				midiMsg = new MidiMessage(midiEventKind, midiEventNote, 0);
				midiState = MIDI_STATE_WAIT;
				break;
			default:
				// illegal state
				midiState = MIDI_STATE_WAIT;
				break;
			}
		} else if (midiState == MIDI_STATE_SIGNAL_3BYTES_2) {
			switch (midiEventKind & 0xf0) {
			case 0x80:
			case 0x90:
			case 0xa0:
			case 0xb0:
			case 0xe0:
			case 0xf0:
				// 3bytes pattern
				midiEventNote = midiEvent;
				midiState = MIDI_STATE_SIGNAL_3BYTES_3;
				break;
			default:
				// illegal state
				midiState = MIDI_STATE_WAIT;
				break;
			}
		} else if (midiState == MIDI_STATE_SIGNAL_3BYTES_3) {
			switch (midiEventKind & 0xf0) {
			case 0x80: // note off
			case 0x90: // note on
			case 0xa0: // control polyphonic key pressure
			case 0xb0: // control change
			case 0xe0: // pitch bend
			case 0xf0: // Song Position Pointer.
				// 3bytes pattern
				midiEventVelocity = midiEvent;
				// handle Event
				midiMsg = new MidiMessage(midiEventKind, midiEventNote, midiEventVelocity);
				midiState = MIDI_STATE_WAIT;
				break;
			default:
				// illegal state
				midiState = MIDI_STATE_WAIT;
				break;
			}
		} else if (midiState == MIDI_STATE_SIGNAL_SYSEX) {
			if (midiEvent == 0xf7) {
				if (systemExclusiveStream != null) {
					synchronized (systemExclusiveStream) {
						systemExclusiveStream.write(midiEvent);
						// handle event
						midiMsg = new MidiMessage(midiEventKind, systemExclusiveStream.toByteArray());
					}
				}
				midiState = MIDI_STATE_WAIT;
			} else {
				if (systemExclusiveStream != null) {
					synchronized (systemExclusiveStream) {
						systemExclusiveStream.write(midiEvent);
					}
				} else {
					midiState = MIDI_STATE_WAIT;
				}
			}
		}

		return midiMsg;
	}

}
