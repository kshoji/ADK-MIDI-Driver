package jp.kshoji.driver.adkmidi.device;

import jp.kshoji.driver.adkmidi.listener.OnMidiEventListener;
import jp.kshoji.driver.adkmidi.util.MidiMessage;
import android.os.Handler;
import android.os.Message;

public class MidiInputDevice extends Handler {
	private final OnMidiEventListener onMidiEventListener;
	public MidiInputDevice(OnMidiEventListener onMidiEventListener) {
		this.onMidiEventListener = onMidiEventListener;
	}
	
	@Override
	public void handleMessage(Message msg) {
		MidiMessage o = (MidiMessage) msg.obj;
		switch (o.getByte1() & 0xf0) {
		case 0x80: // note off
			onMidiEventListener.onMidiNoteOff(o.getByte1() & 0xf, o.getByte2(), o.getByte3());
			break;
		case 0x90: // note on
			onMidiEventListener.onMidiNoteOn(o.getByte1() & 0xf, o.getByte2(), o.getByte3());
			break;
		case 0xa0: // control polyphonic key pressure
			onMidiEventListener.onMidiPolyphonicAftertouch(o.getByte1() & 0xf, o.getByte2(), o.getByte3());
			break;
		case 0xb0: // control change
			onMidiEventListener.onMidiControlChange(o.getByte1() & 0xf, o.getByte2(), o.getByte3());
			break;
		case 0xc0: // program change:2bytes
			onMidiEventListener.onMidiProgramChange(o.getByte1() & 0xf, o.getByte2());
			break;
		case 0xd0: // channel after-touch:2bytes
			onMidiEventListener.onMidiChannelAftertouch(o.getByte1() & 0xf, o.getByte2());
			break;
		case 0xe0: // pitch bend
			onMidiEventListener.onMidiPitchWheel(o.getByte1() & 0xf, o.getByte2() | (o.getByte3() << 7));
			break;
		case 0xf0: // sysex
			onMidiEventListener.onMidiSystemExclusive(o.getSystemExclusive());
			break;
		default:
			// illegal state
			break;
		}
	}
}
